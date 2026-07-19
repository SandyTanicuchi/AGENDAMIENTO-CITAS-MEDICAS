package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class CitasController implements Initializable {

    @FXML private TextField txtId;
    @FXML private TextField txtPaciente;
    @FXML private TextField txtMedico;
    @FXML private TextField txtFecha;
    @FXML private ComboBox<String> comboEstado;
    @FXML private TableView<Cita> tablaCitas;
    @FXML private TableColumn<Cita, Integer> colId;
    @FXML private TableColumn<Cita, String> colPaciente;
    @FXML private TableColumn<Cita, String> colMedico;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colEstado;

    private ObservableList<Cita> listaCitas = FXCollections.observableArrayList();
    private int contadorId = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tablaCitas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Cargar estados
        comboEstado.setItems(FXCollections.observableArrayList(
                "Pendiente", "Confirmada", "Cancelada", "Completada"
        ));
        comboEstado.getSelectionModel().selectFirst();

        // Configurar columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPaciente.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        colMedico.setCellValueFactory(new PropertyValueFactory<>("medico"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaCitas.setItems(listaCitas);

        // Al seleccionar fila, llenar campos
        tablaCitas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        txtId.setText(String.valueOf(newVal.getId()));
                        txtPaciente.setText(newVal.getPaciente());
                        txtMedico.setText(newVal.getMedico());
                        txtFecha.setText(newVal.getFecha());
                        comboEstado.setValue(newVal.getEstado());
                    }
                }
        );
    }

    @FXML
    private void handleInsertar() {
        if (txtPaciente.getText().isEmpty() || txtMedico.getText().isEmpty() || txtFecha.getText().isEmpty()) {
            mostrarAlerta("Por favor complete todos los campos.");
            return;
        }
        Cita cita = new Cita(
                contadorId++,
                txtPaciente.getText(),
                txtMedico.getText(),
                txtFecha.getText(),
                comboEstado.getValue()
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
        seleccionada.setPaciente(txtPaciente.getText());
        seleccionada.setMedico(txtMedico.getText());
        seleccionada.setFecha(txtFecha.getText());
        seleccionada.setEstado(comboEstado.getValue());
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
        int id = Integer.parseInt(idTexto);
        for (Cita c : listaCitas) {
            if (c.getId() == id) {
                tablaCitas.getSelectionModel().select(c);
                return;
            }
        }
        mostrarAlerta("No se encontró una cita con ese ID.");
    }

    private void limpiarCampos() {
        txtId.clear();
        txtPaciente.clear();
        txtMedico.clear();
        txtFecha.clear();
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
        private String paciente, medico, fecha, estado;

        public Cita(int id, String paciente, String medico, String fecha, String estado) {
            this.id = id; this.paciente = paciente;
            this.medico = medico; this.fecha = fecha; this.estado = estado;
        }

        public int getId() { return id; }
        public String getPaciente() { return paciente; }
        public String getMedico() { return medico; }
        public String getFecha() { return fecha; }
        public String getEstado() { return estado; }
        public void setPaciente(String p) { this.paciente = p; }
        public void setMedico(String m) { this.medico = m; }
        public void setFecha(String f) { this.fecha = f; }
        public void setEstado(String e) { this.estado = e; }
    }
}
