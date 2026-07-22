package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Pacientes;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.PacientesDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la vista de Gestión de Pacientes (pacientes.fxml).
 *
 * CORRECCIONES:
 *  - Movido al paquete principal (no al sub-paquete modelo)
 *  - Se eliminó el uso del 8° argumento en el constructor incorrecto
 *  - Se agregan importaciones correctas al modelo
 *  - Se añade validación de campos antes de guardar/modificar
 *  - Se muestra alerta de confirmación antes de eliminar
 */
public class PacientesController implements Initializable {

    // Campos del formulario
    @FXML private TextField        txtId;
    @FXML private TextField        txtCedula;
    @FXML private TextField        txtNombre;
    @FXML private TextField        txtApellido;
    @FXML private TextField        txtTelf;
    @FXML private TextField        txtCorreo;
    @FXML private TextField        txtDireccion;
    @FXML private ComboBox<String> cmbEstado;

    // Tabla
    @FXML private TableView<Pacientes>           tablePacientes;
    @FXML private TableColumn<Pacientes, Integer> colId;
    @FXML private TableColumn<Pacientes, String>  colCedula;
    @FXML private TableColumn<Pacientes, String>  colNombre;
    @FXML private TableColumn<Pacientes, String>  colApellido;
    @FXML private TableColumn<Pacientes, String>  colTelefono;
    @FXML private TableColumn<Pacientes, String>  colCorreo;
    @FXML private TableColumn<Pacientes, String>  colDireccion;
    @FXML private TableColumn<Pacientes, String>  colEstado;

    private final PacientesDAO dao = new PacientesDAO();

    // ----------------------------------------------------------------
    // INICIALIZACIÓN
    // ----------------------------------------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbEstado.getItems().addAll("Activo", "Inactivo");
        cmbEstado.getSelectionModel().selectFirst();

        // Configurar columnas de la tabla
        colId.setCellValueFactory      (new PropertyValueFactory<>("id"));
        colCedula.setCellValueFactory  (new PropertyValueFactory<>("cedula"));
        colNombre.setCellValueFactory  (new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory  (new PropertyValueFactory<>("correo"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colEstado.setCellValueFactory  (new PropertyValueFactory<>("estado"));

        txtId.setEditable(false);

        cargarPacientes();

        // Al seleccionar una fila, rellenar el formulario
        tablePacientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, paciente) -> {
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
                }
        );
    }

    // ----------------------------------------------------------------
    // CARGAR TABLA
    // ----------------------------------------------------------------

    private void cargarPacientes() {
        tablePacientes.setItems(dao.obtenerListaPacientes());
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------

    @FXML
    private void guardarPaciente() {
        if (!validarCamposObligatorios()) return;

        Pacientes paciente = construirPacienteDesdeFormulario(0);

        if (dao.registrarPaciente(paciente) > 0) {
            mostrarInfo("Paciente registrado correctamente.");
            cargarPacientes();
            limpiarCampos();
        } else {
            mostrarError("No se pudo registrar el paciente. Verifique que la cédula no esté duplicada o revise la conexión a la BD.");
        }
    }

    @FXML
    private void modificarPaciente() {
        if (txtId.getText().isEmpty()) {
            mostrarError("Seleccione un paciente de la tabla para modificar.");
            return;
        }
        if (!validarCamposObligatorios()) return;

        int id;
        try {
            id = Integer.parseInt(txtId.getText());
        } catch (NumberFormatException e) {
            mostrarError("ID inválido.");
            return;
        }

        Pacientes paciente = construirPacienteDesdeFormulario(id);

        if (dao.actualizarPaciente(paciente)) {
            mostrarInfo("Paciente actualizado correctamente.");
            cargarPacientes();
            limpiarCampos();
        } else {
            mostrarError("No se pudo actualizar el paciente. Revise la conexión a la BD.");
        }
    }

    @FXML
    private void eliminarPaciente() {
        Pacientes paciente = tablePacientes.getSelectionModel().getSelectedItem();
        if (paciente == null) {
            mostrarError("Seleccione un paciente de la tabla para eliminar.");
            return;
        }

        // Confirmación antes de eliminar
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar al paciente " + paciente.getNombre() + " " + paciente.getApellido() + "?");
        confirm.setContentText("Esta acción no se puede deshacer. Si el paciente tiene citas registradas, se recomienda marcarlo como 'Inactivo' en lugar de eliminarlo.");
        confirm.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                if (dao.eliminarPaciente(paciente.getId())) {
                    mostrarInfo("Paciente eliminado correctamente.");
                    cargarPacientes();
                    limpiarCampos();
                } else {
                    mostrarError("No se pudo eliminar el paciente. Es posible que tenga citas registradas (integridad referencial). Márquelo como 'Inactivo'.");
                }
            }
        });
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
        cmbEstado.getSelectionModel().selectFirst();
        tablePacientes.getSelectionModel().clearSelection();
    }

    // ----------------------------------------------------------------
    // AUXILIARES
    // ----------------------------------------------------------------

    /** Construye un objeto Pacientes con los datos del formulario */
    private Pacientes construirPacienteDesdeFormulario(int id) {
        return new Pacientes(
                id,
                txtCedula.getText().trim(),
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                txtTelf.getText().trim(),
                txtCorreo.getText().trim(),
                txtDireccion.getText().trim(),
                cmbEstado.getValue()
        );
    }

    /** Valida que los campos obligatorios no estén vacíos */
    private boolean validarCamposObligatorios() {
        if (txtCedula.getText().trim().isEmpty() ||
            txtNombre.getText().trim().isEmpty() ||
            txtApellido.getText().trim().isEmpty()) {
            mostrarError("Los campos Cédula, Nombre y Apellido son obligatorios.");
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