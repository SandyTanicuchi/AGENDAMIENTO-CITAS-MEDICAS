package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.servicio.UsuarioService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
public class RegistroController implements Initializable {

    @FXML private TextField     txtNombre;
    @FXML private ComboBox<String> comboRol;
    @FXML private TextField     txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private Label         lblError;

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // RESTRICCIÓN: Se elimina "Administrador" del registro público.
        comboRol.setItems(FXCollections.observableArrayList(
                "Médico",
                "Cliente"
        ));
        comboRol.getSelectionModel().select("Cliente"); // Valor por defecto más común
    }

    @FXML
    private void handleRegistrar() {
        String nombre            = txtNombre.getText().trim();
        String rol               = comboRol.getValue();
        String usuario           = txtUsuario.getText().trim();
        String password          = txtPassword.getText();
        String confirmarPassword = txtConfirmarPassword.getText();

        if (nombre.isEmpty() || rol == null || usuario.isEmpty()
                || password.isEmpty() || confirmarPassword.isEmpty()) {
            mostrarError("Por favor complete todos los campos.");
            return;
        }

        // --- Validar longitud mínima de usuario ---
        if (usuario.length() < 4) {
            mostrarError("El nombre de usuario debe tener al menos 4 caracteres.");
            return;
        }

        // --- Validar que las contraseñas coincidan ---
        if (!password.equals(confirmarPassword)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }

        // --- Validar longitud mínima de contraseña ---
        if (password.length() < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        // --- Intentar registrar a través del servicio ---
        boolean registrado = usuarioService.registrarNuevoUsuario(nombre, usuario, password, rol);

        if (registrado) {
            ocultarError();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro exitoso");
            alert.setHeaderText(null);
            alert.setContentText("Cuenta creada correctamente.\nYa puede iniciar sesión con el usuario: " + usuario);
            alert.showAndWait();
            cerrarVentana();
        } else {
            mostrarError("No se pudo crear la cuenta. El nombre de usuario ya existe o permisos insuficientes.");
        }
    }
    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }
    private void cerrarVentana() {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }

    private void ocultarError() {
        lblError.setVisible(false);
    }
}
