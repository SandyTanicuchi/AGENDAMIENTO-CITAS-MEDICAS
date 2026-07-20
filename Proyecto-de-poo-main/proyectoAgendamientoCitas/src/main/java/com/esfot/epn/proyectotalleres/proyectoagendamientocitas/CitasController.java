package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

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

public class CitasController implements Initializable {

    @FXML private TextField txtId;
    @FXML private TextField txtPaciente;
    @FXML private TextField txtMedico;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private ComboBox<String> comboEstado;
    @FXML private TextArea txtMotivo;

    @FXML private TableView<Cita> tablaCitas;
    @FXML private TableColumn<Cita, Integer> colId;
    @FXML private TableColumn<Cita, String> colPaciente;
    @FXML private TableColumn<Cita, String> colMedico;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colHora;
    @FXML private TableColumn<Cita, String> colEstado;
    @FXML private TableColumn<Cita, String> colMotivo;

    private final ObservableList<Cita> listaCitas = FXCollections.observableArrayList();
    private int contadorId = 1;
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tablaCitas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        comboEstado.setItems(FXCollections.observableArrayList(
                "Pendiente", "Confirmada", "Cancelada", "Completada"
        ));
        comboEstado.getSelectionModel().selectFirst();

        configurarCalendario();

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPaciente.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        colMedico.setCellValueFactory(new PropertyValueFactory<>("medico"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));

        tablaCitas.setItems(listaCitas);

        tablaCitas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        txtId.setText(String.valueOf(newVal.getId()));
                        txtPaciente.setText(newVal.getPaciente());
                        txtMedico.setText(newVal.getMedico());
                        dpFecha.setValue(LocalDate.parse(newVal.getFecha(), formatoFecha));
                        txtHora.setText(newVal.getHora());
                        comboEstado.setValue(newVal.getEstado());
                        txtMotivo.setText(newVal.getMotivo());
                    }
                }
        );
    }

    /**
     * Deshabilita visualmente los días anteriores a hoy en el calendario,
     * para que no se puedan agendar citas en fechas pasadas.
     */
    private void configurarCalendario() {
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
    }

    @FXML
    private void handleInsertar() {
        if (txtPaciente.getText().isEmpty() || txtMedico.getText().isEmpty()
                || dpFecha.getValue() == null || txtHora.getText().isEmpty()) {
            mostrarAlerta("Por favor complete todos los campos.");
            return;
        }

        String fechaTexto = dpFecha.getValue().format(formatoFecha);
        String horaTexto = normalizarHora(txtHora.getText());
        if (horaTexto == null) {
            mostrarAlerta("La hora debe tener el formato HH:MM (ej. 09:30).");
            return;
        }

        // Validación: mismo médico o mismo paciente ya ocupado en esa fecha/hora
        String mensajeCruce = tipoDeCruce(fechaTexto, horaTexto, txtPaciente.getText(), txtMedico.getText(), -1);
        if (mensajeCruce != null) {
            mostrarAlerta(mensajeCruce);
            return;
        }

        Cita cita = new Cita(
                contadorId++,
                txtPaciente.getText(),
                txtMedico.getText(),
                fechaTexto,
                horaTexto,
                comboEstado.getValue(),
                txtMotivo.getText()
        );
        listaCitas.add(cita);
        limpiarCampos();
    }

    @FXML
    private void handleModificar() {
        Cita seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Seleccione una cita de la tabla.");
            return;
        }
        if (dpFecha.getValue() == null || txtHora.getText().isEmpty()) {
            mostrarAlerta("Por favor complete fecha y hora.");
            return;
        }

        String fechaTexto = dpFecha.getValue().format(formatoFecha);
        String horaTexto = normalizarHora(txtHora.getText());
        if (horaTexto == null) {
            mostrarAlerta("La hora debe tener el formato HH:MM (ej. 09:30).");
            return;
        }

        // Validación: excluye la propia cita que se está modificando (por eso el id)
        String mensajeCruce = tipoDeCruce(fechaTexto, horaTexto, txtPaciente.getText(), txtMedico.getText(), seleccionada.getId());
        if (mensajeCruce != null) {
            mostrarAlerta(mensajeCruce);
            return;
        }

        seleccionada.setPaciente(txtPaciente.getText());
        seleccionada.setMedico(txtMedico.getText());
        seleccionada.setFecha(fechaTexto);
        seleccionada.setHora(horaTexto);
        seleccionada.setEstado(comboEstado.getValue());
        seleccionada.setMotivo(txtMotivo.getText());
        tablaCitas.refresh();
        limpiarCampos();
    }

    @FXML
    private void handleMostrar() {
        tablaCitas.getSelectionModel().clearSelection();
        limpiarCampos();
    }

    @FXML
    private void handleEliminar() {
        Cita seleccionada = tablaCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Seleccione una cita de la tabla.");
            return;
        }
        listaCitas.remove(seleccionada);
        limpiarCampos();
    }

    @FXML
    private void handleBuscar() {
        String idTexto = txtId.getText().trim();
        if (idTexto.isEmpty()) {
            mostrarAlerta("Ingrese un ID para buscar.");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idTexto);
        } catch (NumberFormatException e) {
            mostrarAlerta("El ID debe ser un número.");
            return;
        }
        for (Cita c : listaCitas) {
            if (c.getId() == id) {
                tablaCitas.getSelectionModel().select(c);
                return;
            }
        }
        mostrarAlerta("No se encontró una cita con ese ID.");
    }

    /**
     * Revisa si ya existe un cruce en la misma fecha y hora:
     *  - mismo MÉDICO ya tiene otra cita a esa hora, o
     *  - mismo PACIENTE ya tiene otra cita a esa hora (aunque sea con otro médico).
     * idExcluir sirve para no comparar la cita consigo misma al modificar.
     */
    private String tipoDeCruce(String fecha, String hora, String paciente, String medico, int idExcluir) {
        for (Cita c : listaCitas) {
            if (c.getId() == idExcluir) continue;
            if (!c.getFecha().equals(fecha) || !c.getHora().equals(hora)) continue;

            boolean mismoMedico = c.getMedico().equalsIgnoreCase(medico);
            boolean mismoPaciente = c.getPaciente().equalsIgnoreCase(paciente);

            if (mismoMedico && mismoPaciente) {
                return "Ese paciente ya tiene una cita con ese médico en esa fecha y hora.";
            } else if (mismoMedico) {
                return "Ya existe una cita para ese médico en la misma fecha y hora.";
            } else if (mismoPaciente) {
                return "Ese paciente ya tiene otra cita (con otro médico) en la misma fecha y hora.";
            }
        }
        return null;
    }

    /**
     * Valida que la hora tenga formato HH:mm (24h) y la normaliza (ej. "9:5" -> "09:05").
     * Devuelve null si el formato no es válido.
     */
    private String normalizarHora(String horaTexto) {
        String texto = horaTexto.trim();
        try {
            String[] partes = texto.split(":");
            if (partes.length != 2) return null;
            int horas = Integer.parseInt(partes[0].trim());
            int minutos = Integer.parseInt(partes[1].trim());
            if (horas < 0 || horas > 23 || minutos < 0 || minutos > 59) return null;
            return java.time.LocalTime.of(horas, minutos).format(formatoHora);
        } catch (NumberFormatException | DateTimeParseException e) {
            return null;
        }
    }

    private void limpiarCampos() {
        txtId.clear();
        txtPaciente.clear();
        txtMedico.clear();
        dpFecha.setValue(null);
        txtHora.clear();
        txtMotivo.clear();
        comboEstado.getSelectionModel().selectFirst();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Clase interna modelo
    public static class Cita {
        private int id;
        private String paciente, medico, fecha, hora, estado, motivo;

        public Cita(int id, String paciente, String medico, String fecha, String hora, String estado, String motivo) {
            this.id = id;
            this.paciente = paciente;
            this.medico = medico;
            this.fecha = fecha;
            this.hora = hora;
            this.estado = estado;
            this.motivo = motivo;
        }

        public int getId() { return id; }
        public String getPaciente() { return paciente; }
        public String getMedico() { return medico; }
        public String getFecha() { return fecha; }
        public String getHora() { return hora; }
        public String getEstado() { return estado; }
        public String getMotivo() { return motivo; }

        public void setPaciente(String p) { this.paciente = p; }
        public void setMedico(String m) { this.medico = m; }
        public void setFecha(String f) { this.fecha = f; }
        public void setHora(String h) { this.hora = h; }
        public void setEstado(String e) { this.estado = e; }
        public void setMotivo(String mo) { this.motivo = mo; }
    }
}