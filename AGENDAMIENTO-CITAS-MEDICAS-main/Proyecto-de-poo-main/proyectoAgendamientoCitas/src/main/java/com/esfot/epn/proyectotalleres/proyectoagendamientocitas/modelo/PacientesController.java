package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PacientesController {

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtCedula;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtTelf;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtDireccion;
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private TableView<Pacientes> tablePacientes;
    @FXML
    private TableColumn<Pacientes, Integer> colId;
    @FXML
    private TableColumn<Pacientes, String> colCedula;
    @FXML
    private TableColumn<Pacientes, String> colNombre;
    @FXML
    private TableColumn<Pacientes, String> colApellido;
    @FXML
    private TableColumn<Pacientes, String> colTelefono;
    @FXML
    private TableColumn<Pacientes, String> colCorreo;
    @FXML
    private TableColumn<Pacientes, String> colDireccion;
    @FXML
    private TableColumn<Pacientes, String> colEstado;
    private PacientesDAO dao = new PacientesDAO();

    @FXML
    public void initialize() {
        cmbEstado.getItems().addAll("Activo", "Inactivo");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarPacientes();

        tablePacientes.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, paciente) -> {

            if (paciente != null) {

                txtId.setText(String.valueOf(paciente.getId()));
                txtCedula.setText(paciente.getCedula());
                txtNombre.setText(paciente.getNombre());
                txtApellido.setText(paciente.getApellido());
                txtTelf.setText(paciente.getTelefono());
                txtCorreo.setText(paciente.getCorreo());
                txtDireccion.setText(paciente.getDireccion());
                cmbEstado.setValue(paciente.getEstado());

            }
        });
    }

    private void cargarPacientes() {
        tablePacientes.setItems(dao.obtenerListaPacientes());
    }

    @FXML
    private void guardarPaciente() {

        Pacientes paciente = new Pacientes(
                0,
                txtCedula.getText(),
                txtNombre.getText(),
                txtApellido.getText(),
                txtTelf.getText(),
                txtCorreo.getText(),
                txtDireccion.getText(),
                cmbEstado.getValue()
        );

        if (dao.registrarPaciente(paciente)) {
            cargarPacientes();
            limpiarCampos();
        }
    }

    @FXML
    private void modificarPaciente() {

        Pacientes paciente = new Pacientes(
                Integer.parseInt(txtId.getText()),
                txtCedula.getText(),
                txtNombre.getText(),
                txtApellido.getText(),
                txtTelf.getText(),
                txtCorreo.getText(),
                txtDireccion.getText(),
                cmbEstado.getValue()
        );

        if (dao.actualizarPaciente(paciente)) {
            cargarPacientes();
            limpiarCampos();
        }
    }

    @FXML
    private void eliminarPaciente() {
        Pacientes paciente = tablePacientes.getSelectionModel().getSelectedItem();
        if (paciente != null) {
            if (dao.eliminarPaciente(paciente.getId())) {
                cargarPacientes();
                limpiarCampos();
            }
        }
    }

    @FXML
    private void limpiarCampos() {
        txtId.clear();
        txtCedula.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtTelf.clear();
        txtCorreo.clear();
        txtDireccion.clear();

        cmbEstado.getSelectionModel().clearSelection();
        tablePacientes.getSelectionModel().clearSelection();
    }
}