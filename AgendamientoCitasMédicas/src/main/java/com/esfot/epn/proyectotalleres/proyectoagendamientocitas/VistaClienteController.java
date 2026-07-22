package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Citas;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.ConexionMySQL;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Doctores;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Pacientes;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Sesion;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.servicio.CitasService;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.servicio.DoctoresService;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.servicio.PacientesService;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.servicio.UsuarioService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class VistaClienteController implements Initializable {

    @FXML private Label lblBienvenida;
    @FXML private Label lblMensaje;

    // Campos formulario Agendar
    @FXML private ComboBox<Doctores> comboDoctor;
    @FXML private DatePicker         dpFecha;
    @FXML private ComboBox<String>   comboHora;
    @FXML private TextArea           txtMotivo;

    // Tabla Historial
    @FXML private TableView<Citas>   tablaCitas;
    @FXML private TableColumn<Citas, String> colDoctor;
    @FXML private TableColumn<Citas, String> colFecha;
    @FXML private TableColumn<Citas, String> colHora;
    @FXML private TableColumn<Citas, String> colMotivo;
    @FXML private TableColumn<Citas, String> colEstado;

    private final CitasService    citasService    = new CitasService();
    private final DoctoresService doctoresService = new DoctoresService();
    private final UsuarioService  usuarioService  = new UsuarioService();
    private final PacientesService pacientesService = new PacientesService();

    private int idPacienteResuelto = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblBienvenida.setText("Hola, " + Sesion.getNombreUsuario());
        
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoCita"));

        // Resuelve el idPaciente
        idPacienteResuelto = Sesion.getIdEntidadVinculada();

        if (idPacienteResuelto <= 0) {
            idPacienteResuelto = recuperarIdPacienteDesdeDB(Sesion.getIdUsuario());

            if (idPacienteResuelto > 0) {
                Sesion.iniciarSesion(
                        Sesion.getNombreUsuario(),
                        Sesion.getRol(),
                        Sesion.getIdUsuario(),
                        idPacienteResuelto
                );
                System.out.println("[VistaClienteController] idPaciente recuperado de BD: " + idPacienteResuelto);
            }
        }

        if (idPacienteResuelto <= 0) {
            solicitarPerfilCliente();
        } else {
            cargarDatosTabla();
            cargarFormulario();
        }
    }
    private void solicitarPerfilCliente() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Completar Perfil");
        dialog.setHeaderText("Bienvenido, complete sus datos personales para continuar.");

        ButtonType btnGuardar = new ButtonType("Guardar y Continuar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 50, 10, 10));

        TextField txtCedula = new TextField();
        txtCedula.setPromptText("Ej. 1712345678");
        TextField txtApellido = new TextField();
        txtApellido.setPromptText("Sus apellidos");
        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("Ej. 0991234567");
        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("Ej. correo@ejemplo.com");
        TextField txtDireccion = new TextField();
        txtDireccion.setPromptText("Dirección domiciliaria");

        grid.add(new Label("Cédula:"), 0, 0);
        grid.add(txtCedula, 1, 0);
        grid.add(new Label("Apellidos:"), 0, 1);
        grid.add(txtApellido, 1, 1);
        grid.add(new Label("Teléfono:"), 0, 2);
        grid.add(txtTelefono, 1, 2);
        grid.add(new Label("Correo:"), 0, 3);
        grid.add(txtCorreo, 1, 3);
        grid.add(new Label("Dirección:"), 0, 4);
        grid.add(txtDireccion, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                if (txtCedula.getText().isEmpty() || txtApellido.getText().isEmpty()) {
                    mostrarMensaje("Cédula y Apellidos son obligatorios.", true);
                    return null;
                }

                Pacientes p = new Pacientes(
                        0,
                        txtCedula.getText(),
                        Sesion.getNombreUsuario(), // Usa el nombre de la cuenta
                        txtApellido.getText(),
                        txtTelefono.getText(),
                        txtCorreo.getText(),
                        txtDireccion.getText(),
                        "Activo"
                );

                int nuevoId = pacientesService.crearPerfilClienteAutenticado(p);
                if (nuevoId > 0) {
                    boolean vinculado = usuarioService.vincularPaciente(Sesion.getIdUsuario(), nuevoId);
                    if (vinculado) {
                        idPacienteResuelto = nuevoId;
                        Sesion.iniciarSesion(
                                Sesion.getNombreUsuario(),
                                Sesion.getRol(),
                                Sesion.getIdUsuario(),
                                nuevoId
                        );
                        mostrarMensaje("Perfil completado con éxito. Ya puede agendar.", false);
                        cargarDatosTabla();
                        cargarFormulario();
                    } else {
                        mostrarMensaje("Perfil creado pero falló la vinculación. Consulte al Admin.", true);
                    }
                } else {
                    mostrarMensaje("Falló al crear perfil (Cédula duplicada o error).", true);
                }
            }
            return dialogButton;
        });

        dialog.showAndWait();
    }
    private int recuperarIdPacienteDesdeDB(int idUsuario) {
        String query = "SELECT id_paciente FROM USUARIOS WHERE id_usuario = ? AND id_paciente IS NOT NULL";
        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return 0;
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id_paciente");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[VistaClienteController] Error recuperando id_paciente: " + e.getMessage());
        }
        return 0;
    }

    private void cargarDatosTabla() {
        ObservableList<Citas> citas = citasService.obtenerCitasPermitidas();
        tablaCitas.setItems(citas);
    }

    private void cargarFormulario() {
        ObservableList<Doctores> listaDocs = doctoresService.obtenerDoctoresActivos();
        comboDoctor.setItems(listaDocs);
        comboDoctor.setConverter(new StringConverter<Doctores>() {
            @Override
            public String toString(Doctores d) {
                if (d == null) return null;
                return "Dr. " + d.getNombre() + " " + d.getApellido() + " - " + d.getEspecialidad();
            }

            @Override
            public Doctores fromString(String s) {
                return null;
            }
        });

        // Cargar combobox horas (horario referencial de 08:00 a 18:00)
        ObservableList<String> horas = FXCollections.observableArrayList(
                "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
                "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
                "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
                "17:00", "17:30", "18:00"
        );
        comboHora.setItems(horas);
    }

    @FXML
    private void handleAgendarCita() {
        Doctores doctorSel = comboDoctor.getValue();
        LocalDate fechaSel = dpFecha.getValue();
        String horaSel     = comboHora.getValue();
        String motivo      = txtMotivo.getText();

        if (doctorSel == null || fechaSel == null || horaSel == null || motivo == null || motivo.trim().isEmpty()) {
            mostrarMensaje("Complete todos los campos para agendar.", true);
            return;
        }

        // Regla: no agendar en fechas pasadas
        if (fechaSel.isBefore(LocalDate.now())) {
            mostrarMensaje("La fecha seleccionada no puede ser pasada.", true);
            return;
        }

        // Verificar que el paciente esté vinculado
        if (idPacienteResuelto <= 0) {
            mostrarMensaje("Su cuenta no tiene un paciente vinculado. Contacte al administrador para asociar su perfil.", true);
            return;
        }

        String fechaStr = fechaSel.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        boolean exito = citasService.registrarCita(idPacienteResuelto, doctorSel.getId(), fechaStr, horaSel, motivo);

        if (exito) {
            mostrarMensaje("¡Cita agendada con éxito!", false);
            limpiarFormulario();
            cargarDatosTabla();
        } else {
            mostrarMensaje("Horario no disponible o error al agendar. Intente con otra fecha/hora.", true);
        }
    }

    @FXML
    private void handleCancelarCita() {
        Citas seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Seleccione una cita de la tabla para cancelar.", true);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de cancelar esta cita?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar cancelación");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean exito = citasService.cancelarCita(seleccionada);
            if (exito) {
                mostrarMensaje("Cita cancelada correctamente.", false);
                cargarDatosTabla();
            } else {
                mostrarMensaje("No se pudo cancelar la cita (estado no permitido).", true);
            }
        }
    }

    @FXML
    private void handleLogout() {
        usuarioService.cerrarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Login – VitalSched");
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
        }
    }

    private void mostrarMensaje(String msg, boolean esError) {
        lblMensaje.setText(msg);
        if (esError) {
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
        } else {
            lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
        }
    }

    private void limpiarFormulario() {
        comboDoctor.getSelectionModel().clearSelection();
        dpFecha.setValue(null);
        comboHora.getSelectionModel().clearSelection();
        txtMotivo.clear();
    }
}
