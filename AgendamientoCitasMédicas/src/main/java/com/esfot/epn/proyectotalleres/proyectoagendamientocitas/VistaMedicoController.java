package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Citas;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.ConexionMySQL;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Doctores;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.EstadoCita;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Sesion;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.servicio.CitasService;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.servicio.DoctoresService;
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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

/**
 * Controlador de la vista principal para los usuarios con rol Médico.
 *
 * CORRECCIÓN: Si idEntidadVinculada es 0, se recupera el id_doctor
 * directamente desde la BD para garantizar que el filtro de citas
 * funcione correctamente.
 */
public class VistaMedicoController implements Initializable {

    @FXML private Label lblBienvenida;
    @FXML private Label lblMensaje;

    // Tabla Historial
    @FXML private TableView<Citas>   tablaCitas;
    @FXML private TableColumn<Citas, String> colFecha;
    @FXML private TableColumn<Citas, String> colHora;
    @FXML private TableColumn<Citas, String> colPaciente;
    @FXML private TableColumn<Citas, String> colMotivo;
    @FXML private TableColumn<Citas, String> colEstado;

    // Detalles
    @FXML private TextField txtPaciente;
    @FXML private ComboBox<EstadoCita> comboEstado;
    @FXML private TextArea txtNotas;

    private final CitasService   citasService   = new CitasService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final DoctoresService doctoresService = new DoctoresService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblBienvenida.setText("Dr. " + Sesion.getNombreUsuario());

        // Inicializar columnas de la tabla
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colPaciente.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoCita"));

        // Poblar ComboBox de Estados
        comboEstado.setItems(FXCollections.observableArrayList(EstadoCita.values()));

        // Si la sesión no tiene idDoctor vinculado, recuperarlo desde la BD
        if (Sesion.getIdEntidadVinculada() <= 0) {
            int idDoctor = recuperarIdDoctorDesdeDB(Sesion.getIdUsuario());
            if (idDoctor > 0) {
                Sesion.iniciarSesion(
                        Sesion.getNombreUsuario(),
                        Sesion.getRol(),
                        Sesion.getIdUsuario(),
                        idDoctor
                );
                System.out.println("[VistaMedicoController] idDoctor recuperado de BD: " + idDoctor);
            } else {
                solicitarPerfilMedico();
            }
        }

        cargarDatosTabla();

        // Listener para detectar selección en la tabla
        tablaCitas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetallesCita(newSelection);
            }
        });
    }

    /**
     * Consulta id_doctor de la tabla USUARIOS para el usuario logueado.
     */
    private int recuperarIdDoctorDesdeDB(int idUsuario) {
        String query = "SELECT id_doctor FROM USUARIOS WHERE id_usuario = ? AND id_doctor IS NOT NULL";
        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return 0;
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id_doctor");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[VistaMedicoController] Error recuperando id_doctor: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Muestra un Dialog modal para crear el perfil de doctor
     * y vincularlo al usuario actual en caso de que no lo tenga.
     */
    private void solicitarPerfilMedico() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Completar Perfil Médico");
        dialog.setHeaderText("Bienvenido Dr/Dra. " + Sesion.getNombreUsuario() + ", complete sus datos profesionales.");

        ButtonType btnGuardar = new ButtonType("Guardar y Continuar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 50, 10, 10));

        TextField txtApellido = new TextField();
        txtApellido.setPromptText("Sus apellidos");
        
        ComboBox<String> comboEspecialidad = new ComboBox<>();
        comboEspecialidad.setItems(FXCollections.observableArrayList(
                "Cardiología", "Pediatría", "Dermatología", "Ginecología", 
                "Medicina General", "Traumatología", "Neurología", 
                "Oftalmología", "Odontología"
        ));
        
        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("Ej. 0991234567");
        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("Ej. correo@hospital.com");

        grid.add(new Label("Apellidos:"), 0, 0);
        grid.add(txtApellido, 1, 0);
        grid.add(new Label("Especialidad:"), 0, 1);
        grid.add(comboEspecialidad, 1, 1);
        grid.add(new Label("Teléfono:"), 0, 2);
        grid.add(txtTelefono, 1, 2);
        grid.add(new Label("Correo:"), 0, 3);
        grid.add(txtCorreo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                if (txtApellido.getText().isEmpty() || comboEspecialidad.getValue() == null) {
                    mostrarMensaje("Apellidos y Especialidad son obligatorios.", true);
                    return null;
                }

                Doctores d = new Doctores(
                        0,
                        Sesion.getNombreUsuario(), // Usa el nombre de la cuenta
                        txtApellido.getText(),
                        comboEspecialidad.getValue(),
                        txtTelefono.getText(),
                        txtCorreo.getText(),
                        "Activo"
                );

                int nuevoId = doctoresService.crearPerfilMedicoAutenticado(d);
                if (nuevoId > 0) {
                    boolean vinculado = usuarioService.vincularDoctor(Sesion.getIdUsuario(), nuevoId);
                    if (vinculado) {
                        Sesion.iniciarSesion(
                                Sesion.getNombreUsuario(),
                                Sesion.getRol(),
                                Sesion.getIdUsuario(),
                                nuevoId
                        );
                        mostrarMensaje("Perfil completado. Ahora los clientes pueden verle.", false);
                        cargarDatosTabla();
                    } else {
                        mostrarMensaje("Perfil creado pero falló la vinculación. Consulte al Admin.", true);
                    }
                } else {
                    mostrarMensaje("Falló al crear perfil.", true);
                }
            }
            return dialogButton;
        });

        dialog.showAndWait();
    }

    private void cargarDatosTabla() {
        ObservableList<Citas> citas = citasService.obtenerCitasPermitidas();
        tablaCitas.setItems(citas);

        if (citas.isEmpty()) {
            lblMensaje.setText("No tiene citas asignadas actualmente.");
            lblMensaje.setStyle("-fx-text-fill: #7f8c8d;");
        } else {
            lblMensaje.setText("");
        }
    }

    private void mostrarDetallesCita(Citas cita) {
        txtPaciente.setText(cita.getPaciente());
        txtNotas.setText(cita.getNotasMedicas() != null ? cita.getNotasMedicas() : "");

        EstadoCita estadoActual = EstadoCita.desdeBD(cita.getEstadoCita());
        if (estadoActual != null) {
            comboEstado.getSelectionModel().select(estadoActual);
        }
        lblMensaje.setText(""); // Limpiar mensajes previos
    }

    @FXML
    private void handleActualizarEstado() {
        Citas seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        EstadoCita nuevoEstado = comboEstado.getValue();

        if (seleccionada == null) {
            mostrarMensaje("Seleccione una cita de la tabla.", true);
            return;
        }

        if (nuevoEstado == null) {
            mostrarMensaje("Seleccione un estado válido.", true);
            return;
        }

        boolean exito = citasService.cambiarEstadoCita(seleccionada, nuevoEstado);

        if (exito) {
            mostrarMensaje("Estado actualizado a: " + nuevoEstado.getNombreEnBD(), false);
            tablaCitas.refresh();
        } else {
            mostrarMensaje("Transición de estado no permitida o error en BD.", true);
            comboEstado.getSelectionModel().select(EstadoCita.desdeBD(seleccionada.getEstadoCita()));
        }
    }

    @FXML
    private void handleActualizarTabla() {
        cargarDatosTabla();
        mostrarMensaje("Tabla de citas actualizada.", false);
    }

    @FXML
    private void handleGuardarNotas() {
        Citas seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        String notas = txtNotas.getText();

        if (seleccionada == null) {
            mostrarMensaje("Seleccione una cita de la tabla.", true);
            return;
        }

        boolean exito = citasService.guardarNotasMedicas(seleccionada, notas);

        if (exito) {
            mostrarMensaje("Historial / Notas guardadas exitosamente.", false);
            tablaCitas.refresh();
        } else {
            mostrarMensaje("No se pudo guardar el diagnóstico.", true);
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
            System.err.println("Error al cerrar sesión (médico): " + e.getMessage());
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
}
