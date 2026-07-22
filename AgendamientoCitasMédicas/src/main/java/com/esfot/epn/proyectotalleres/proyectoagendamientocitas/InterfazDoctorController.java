package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Doctores;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.DoctoresDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;


public class InterfazDoctorController implements Initializable {

    // Campos del formulario
    @FXML private TextField        txtId;
    @FXML private TextField        txtNombres;
    @FXML private TextField        txtApellidos;
    @FXML private ComboBox<String> comboEspecialidad;
    @FXML private TextField        txtTelefono;
    @FXML private TextField        txtCorreo;
    @FXML private ComboBox<String> comboEstado;

    // Tabla
    @FXML private TableView<Doctores>           tablaDoctores;
    @FXML private TableColumn<Doctores, Integer> colId;
    @FXML private TableColumn<Doctores, String>  colNombres;
    @FXML private TableColumn<Doctores, String>  colApellidos;
    @FXML private TableColumn<Doctores, String>  colEspecialidad;
    @FXML private TableColumn<Doctores, String>  colTelefono;
    @FXML private TableColumn<Doctores, String>  colCorreo;
    @FXML private TableColumn<Doctores, String>  colEstado;

    private final DoctoresDAO dao = new DoctoresDAO();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tablaDoctores.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        comboEspecialidad.setItems(FXCollections.observableArrayList(
                "Cardiología", "Pediatría", "Dermatología", "Ginecología",
                "Medicina General", "Traumatología", "Neurología",
                "Oftalmología", "Odontología"
        ));
        comboEspecialidad.getSelectionModel().selectFirst();

        comboEstado.setItems(FXCollections.observableArrayList(
                "Activo", "Inactivo", "Vacaciones"
        ));
        comboEstado.getSelectionModel().selectFirst();

        // Configurar columnas
        colId.setCellValueFactory          (new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory     (new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory   (new PropertyValueFactory<>("apellido"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colTelefono.setCellValueFactory    (new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory      (new PropertyValueFactory<>("correo"));
        colEstado.setCellValueFactory      (new PropertyValueFactory<>("estado"));

        txtId.setEditable(false);
        txtId.setDisable(true);

        cargarDoctores();

        tablaDoctores.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, doctor) -> {
                    if (doctor != null) {
                        txtId.setText(String.valueOf(doctor.getId()));
                        txtNombres.setText(doctor.getNombre());
                        txtApellidos.setText(doctor.getApellido());
                        comboEspecialidad.setValue(doctor.getEspecialidad());
                        txtTelefono.setText(doctor.getTelefono());
                        txtCorreo.setText(doctor.getCorreo());
                        comboEstado.setValue(doctor.getEstado());
                    }
                }
        );
    }

    private void cargarDoctores() {
        tablaDoctores.setItems(dao.obtenerListaDoctores());
    }

    @FXML
    private void handleInsertar() {
        if (!validarCampos()) return;

        // El id_doctor lo asigna la BD (AUTO_INCREMENT), usamos 0 como placeholder
        Doctores doctor = new Doctores(
                0,
                txtNombres.getText().trim(),
                txtApellidos.getText().trim(),
                comboEspecialidad.getValue(),
                txtTelefono.getText().trim(),
                txtCorreo.getText().trim(),
                comboEstado.getValue()
        );

        if (dao.registrarDoctor(doctor) > 0) {
            mostrarInfo("Doctor registrado correctamente.");
            cargarDoctores();
            limpiarCampos();
        } else {
            mostrarError("No se pudo registrar el doctor. Verifique que el correo no esté duplicado o revise la conexión a la BD.");
        }
    }

    @FXML
    private void handleModificar() {
        Doctores seleccionado = tablaDoctores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Seleccione un doctor de la tabla para modificar.");
            return;
        }
        if (!validarCampos()) return;
        
        seleccionado.setNombre(txtNombres.getText().trim());
        seleccionado.setApellido(txtApellidos.getText().trim());
        seleccionado.setEspecialidad(comboEspecialidad.getValue());
        seleccionado.setTelefono(txtTelefono.getText().trim());
        seleccionado.setCorreo(txtCorreo.getText().trim());
        seleccionado.setEstado(comboEstado.getValue());

        if (dao.actualizarDoctor(seleccionado)) {
            mostrarInfo("Doctor actualizado correctamente.");
            cargarDoctores();
            limpiarCampos();
        } else {
            mostrarError("No se pudo actualizar el doctor. Revise la conexión a la BD.");
        }
    }

    @FXML
    private void handleMostrar() {
        tablaDoctores.getSelectionModel().clearSelection();
        limpiarCampos();
    }

    @FXML
    private void handleEliminar() {
        Doctores seleccionado = tablaDoctores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Seleccione un doctor de la tabla para eliminar.");
            return;
        }

        // Confirmación antes de eliminar
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar al Dr. " + seleccionado.getNombre() + " " + seleccionado.getApellido() + "?");
        confirm.setContentText("Si el doctor tiene citas registradas no se podrá eliminar (integridad referencial). En ese caso márquelo como 'Inactivo'.");
        confirm.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                if (dao.eliminarDoctor(seleccionado.getId())) {
                    mostrarInfo("Doctor eliminado correctamente.");
                    cargarDoctores();
                    limpiarCampos();
                } else {
                    mostrarError("No se pudo eliminar el doctor. Es posible que tenga citas registradas. Márquelo como 'Inactivo'.");
                }
            }
        });
    }
    private void limpiarCampos() {
        txtId.clear();
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        comboEspecialidad.getSelectionModel().selectFirst();
        comboEstado.getSelectionModel().selectFirst();
        tablaDoctores.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        if (txtNombres.getText().trim().isEmpty() ||
            txtApellidos.getText().trim().isEmpty() ||
            txtTelefono.getText().trim().isEmpty() ||
            txtCorreo.getText().trim().isEmpty() ||
            comboEspecialidad.getValue() == null ||
            comboEstado.getValue() == null) {
            mostrarError("Por favor complete todos los campos requeridos.");
            return false;
        }
        return true;
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
