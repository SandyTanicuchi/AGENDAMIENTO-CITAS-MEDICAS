package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

/**
 * Controlador para el formulario de Gestión de Citas Médicas (citas.fxml).
 *
 * CORRECCIONES PRINCIPALES:
 *  - Usa CitasDAO para persistir en BD (ya no trabaja solo en memoria)
 *  - Paciente y Médico ahora son ComboBox con datos reales de BD
 *  - El ID se obtiene de la fila seleccionada en la tabla (no como TextField)
 *  - Validación de conflictos de horario a nivel de BD (constraints UNIQUE)
 *  - La fecha se formatea como yyyy-MM-dd para MySQL
 */
public class CitasController implements Initializable {

    // Formulario
    @FXML private TextField           txtId;
    @FXML private ComboBox<Pacientes> comboPaciente;
    @FXML private ComboBox<Doctores>  comboMedico;
    @FXML private DatePicker          dpFecha;
    @FXML private TextField           txtHora;
    @FXML private ComboBox<String>    comboEstado;
    @FXML private TextArea            txtMotivo;

    // Tabla
    @FXML private TableView<Citas>           tablaCitas;
    @FXML private TableColumn<Citas, Integer> colId;
    @FXML private TableColumn<Citas, String>  colPaciente;
    @FXML private TableColumn<Citas, String>  colMedico;
    @FXML private TableColumn<Citas, String>  colFecha;
    @FXML private TableColumn<Citas, String>  colHora;
    @FXML private TableColumn<Citas, String>  colEstado;
    @FXML private TableColumn<Citas, String>  colMotivo;

    private final CitasDAO   citasDAO   = new CitasDAO();
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter formatoHora  = DateTimeFormatter.ofPattern("HH:mm");

    // ----------------------------------------------------------------
    // INICIALIZACIÓN
    // ----------------------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tablaCitas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Estados disponibles para la cita
        comboEstado.setItems(FXCollections.observableArrayList(
                "Pendiente", "Confirmada", "Cancelada", "Completada"
        ));
        comboEstado.getSelectionModel().selectFirst();

        // Configurar DatePicker: sólo fechas futuras o de hoy
        dpFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate fecha, boolean vacio) {
                super.updateItem(fecha, vacio);
                if (fecha != null && fecha.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #b0b0b0;");
                }
            }
        });

        // Cargar pacientes y doctores activos desde BD
        cargarCombosPacientesMedicos();

        // Configurar cómo se muestra cada ítem en los ComboBox
        configurarComboBoxDisplay();

        // Configurar columnas de la tabla
        colId.setCellValueFactory      (new PropertyValueFactory<>("id"));
        colPaciente.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        colMedico.setCellValueFactory  (new PropertyValueFactory<>("doctor"));
        colFecha.setCellValueFactory   (new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory    (new PropertyValueFactory<>("hora"));
        colEstado.setCellValueFactory  (new PropertyValueFactory<>("estadoCita"));
        colMotivo.setCellValueFactory  (new PropertyValueFactory<>("motivo"));

        txtId.setEditable(false);
        txtId.setDisable(true);

        cargarCitas();

        // Al seleccionar una cita de la tabla, rellena el formulario parcialmente
        tablaCitas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, cita) -> {
                    if (cita != null) {
                        txtId.setText(String.valueOf(cita.getId()));
                        // Se rellena solo el estado y motivo (los ComboBox de paciente/médico
                        // requieren buscar por nombre; aquí mostramos solo info de lectura)
                        comboEstado.setValue(cita.getEstadoCita());
                        txtMotivo.setText(cita.getMotivo());
                        // Intentar parsear la fecha para el DatePicker
                        try {
                            dpFecha.setValue(LocalDate.parse(cita.getFecha(), formatoFecha));
                        } catch (DateTimeParseException e) {
                            dpFecha.setValue(null);
                        }
                        txtHora.setText(cita.getHora());
                    }
                }
        );
    }

    // ----------------------------------------------------------------
    // CARGAR DATOS
    // ----------------------------------------------------------------

    private void cargarCombosPacientesMedicos() {
        ObservableList<Pacientes> pacientes = citasDAO.obtenerPacientesActivos();
        ObservableList<Doctores>  doctores  = citasDAO.obtenerDoctoresActivos();

        // Datos mock si la BD no está disponible
        if (pacientes.isEmpty()) {
            pacientes = FXCollections.observableArrayList(
                    new Pacientes(1, "1723456789", "Juan",   "Pérez",    "", "", "", "Activo"),
                    new Pacientes(2, "1712345678", "María",  "Gómez",    "", "", "", "Activo"),
                    new Pacientes(3, "1709876543", "Carlos", "Andrade",  "", "", "", "Activo"),
                    new Pacientes(4, "1755566778", "Ana",    "Martínez", "", "", "", "Activo")
            );
        }
        if (doctores.isEmpty()) {
            doctores = FXCollections.observableArrayList(
                    new Doctores(1, "Fernando", "Ríos",    "Cardiología",     "", "", "Activo"),
                    new Doctores(2, "Elena",    "Salazar", "Pediatría",       "", "", "Activo"),
                    new Doctores(3, "Javier",   "Mendoza", "Dermatología",    "", "", "Activo"),
                    new Doctores(5, "Roberto",  "Mora",    "Medicina General","", "", "Activo")
            );
        }

        comboPaciente.setItems(pacientes);
        comboMedico.setItems(doctores);
    }

    private void configurarComboBoxDisplay() {
        // Mostrar "Apellido, Nombre (Cédula)" en el ComboBox de pacientes
        comboPaciente.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pacientes p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null :
                        p.getApellido() + ", " + p.getNombre() + " (" + p.getCedula() + ")");
            }
        });
        comboPaciente.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Pacientes p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? "Seleccione paciente..." :
                        p.getApellido() + ", " + p.getNombre());
            }
        });

        // Mostrar "Apellido, Nombre – Especialidad" en el ComboBox de médicos
        comboMedico.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Doctores d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? null :
                        "Dr. " + d.getApellido() + ", " + d.getNombre() + " – " + d.getEspecialidad());
            }
        });
        comboMedico.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Doctores d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? "Seleccione médico..." :
                        "Dr. " + d.getApellido() + " – " + d.getEspecialidad());
            }
        });
    }

    private void cargarCitas() {
        ObservableList<Citas> lista = citasDAO.obtenerListaCitas();
        if (lista.isEmpty()) {
            // Datos mock si la BD no está disponible
            lista = FXCollections.observableArrayList(
                    new Citas(1, "Juan Pérez",    "Fernando Ríos",  "2026-07-25", "09:00", "Chequeo general de cardiología",   "Pendiente"),
                    new Citas(2, "María Gómez",   "Elena Salazar",  "2026-07-25", "10:30", "Consulta pediátrica de control",   "Completada"),
                    new Citas(3, "Ana Martínez",  "Javier Mendoza", "2026-07-26", "11:15", "Consulta dermatológica por acné",  "Pendiente"),
                    new Citas(4, "Carlos Andrade","Gabriela Vargas","2026-07-27", "14:00", "Control rutinario ginecológico",   "Cancelada")
            );
        }
        tablaCitas.setItems(lista);
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------

    @FXML
    private void handleInsertar() {
        Pacientes paciente = comboPaciente.getValue();
        Doctores  medico   = comboMedico.getValue();

        if (paciente == null || medico == null || dpFecha.getValue() == null || txtHora.getText().isEmpty()) {
            mostrarAlerta("Por favor complete: paciente, médico, fecha y hora.");
            return;
        }

        String fechaStr = dpFecha.getValue().format(formatoFecha);
        String horaStr  = normalizarHora(txtHora.getText());
        if (horaStr == null) {
            mostrarAlerta("La hora debe tener el formato HH:MM (ej. 09:30).");
            return;
        }

        // Verificar conflictos a nivel de BD
        if (citasDAO.existeConflictoMedico(medico.getId(), fechaStr, horaStr, -1)) {
            mostrarAlerta("El médico " + medico.getNombre() + " " + medico.getApellido() +
                          " ya tiene una cita en esa fecha y hora.");
            return;
        }
        if (citasDAO.existeConflictoPaciente(paciente.getId(), fechaStr, horaStr, -1)) {
            mostrarAlerta("El paciente " + paciente.getNombre() + " " + paciente.getApellido() +
                          " ya tiene otra cita en esa fecha y hora.");
            return;
        }

        // Estado "Pendiente" = id_estado 1 (según el INSERT del script SQL)
        int idEstado = mapearEstadoAId(comboEstado.getValue());

        if (citasDAO.registrarCita(paciente.getId(), medico.getId(), fechaStr, horaStr,
                                   txtMotivo.getText().trim(), idEstado)) {
            mostrarInfo("Cita registrada correctamente.");
            cargarCitas();
            limpiarCampos();
        } else {
            mostrarAlerta("No se pudo registrar la cita. Verifique conflictos de horario o la conexión a la BD.");
        }
    }

    @FXML
    private void handleModificar() {
        Citas citaSeleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (citaSeleccionada == null) {
            mostrarAlerta("Seleccione una cita de la tabla para modificar su estado.");
            return;
        }

        // Desde la tabla se puede actualizar el estado de la cita
        boolean actualizado = citasDAO.actualizarEstadoCita(
                citaSeleccionada.getId(),
                comboEstado.getValue()
        );

        if (actualizado) {
            mostrarInfo("Estado de la cita actualizado a: " + comboEstado.getValue());
            cargarCitas();
            limpiarCampos();
        } else {
            mostrarAlerta("No se pudo actualizar el estado. Revise la conexión a la BD.");
        }
    }

    @FXML
    private void handleMostrar() {
        tablaCitas.getSelectionModel().clearSelection();
        limpiarCampos();
    }

    @FXML
    private void handleEliminar() {
        Citas citaSeleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (citaSeleccionada == null) {
            mostrarAlerta("Seleccione una cita de la tabla para cancelar/eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar la cita #" + citaSeleccionada.getId() + "?");
        confirm.setContentText("Paciente: " + citaSeleccionada.getPaciente() +
                               "\nDoctor: "  + citaSeleccionada.getDoctor() +
                               "\nFecha: "   + citaSeleccionada.getFecha() +
                               " a las " + citaSeleccionada.getHora() +
                               "\n\nSi solo desea cancelarla, use el botón Modificar y cambie el estado a 'Cancelada'.");
        confirm.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                if (citasDAO.eliminarCita(citaSeleccionada.getId())) {
                    mostrarInfo("Cita eliminada correctamente.");
                    cargarCitas();
                    limpiarCampos();
                } else {
                    mostrarAlerta("No se pudo eliminar la cita. Revise la conexión a la BD.");
                }
            }
        });
    }

    @FXML
    private void handleBuscar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            mostrarAlerta("El campo ID se completa automáticamente al seleccionar una cita de la tabla.");
            return;
        }
    }

    // ----------------------------------------------------------------
    // AUXILIARES
    // ----------------------------------------------------------------

    private void limpiarCampos() {
        txtId.clear();
        comboPaciente.getSelectionModel().clearSelection();
        comboMedico.getSelectionModel().clearSelection();
        dpFecha.setValue(null);
        txtHora.clear();
        txtMotivo.clear();
        comboEstado.getSelectionModel().selectFirst();
        tablaCitas.getSelectionModel().clearSelection();
    }

    /** Normaliza la hora a formato HH:mm; retorna null si el formato es inválido */
    private String normalizarHora(String horaTexto) {
        try {
            String[] partes = horaTexto.trim().split(":");
            if (partes.length != 2) return null;
            int h = Integer.parseInt(partes[0].trim());
            int m = Integer.parseInt(partes[1].trim());
            if (h < 0 || h > 23 || m < 0 || m > 59) return null;
            return java.time.LocalTime.of(h, m).format(formatoHora);
        } catch (Exception e) {
            return null;
        }
    }

    /** Mapea el nombre del estado a su id en la tabla ESTADOS */
    private int mapearEstadoAId(String nombreEstado) {
        return switch (nombreEstado) {
            case "Pendiente"  -> 1;
            case "Confirmada" -> 2;
            case "Cancelada"  -> 3;
            case "Completada" -> 4;
            default           -> 1;
        };
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}