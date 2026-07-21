package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistroController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> comboRol;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private Label lblError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboRol.setItems(FXCollections.observableArrayList(
                "Administrador",
                "Médico",
                "Recepcionista"
        ));
    }

    @FXML
    private void handleRegistrar() {
        String nombre = txtNombre.getText().trim();
        String rol = comboRol.getValue();
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();
        String confirmarPassword = txtConfirmarPassword.getText();

        if (nombre.isEmpty() || rol == null || usuario.isEmpty()
                || password.isEmpty() || confirmarPassword.isEmpty()) {
            mostrarError("Por favor complete todos los campos.");
            return;
        }

        if (!password.equals(confirmarPassword)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }

        if (password.length() < 4) {
            mostrarError("La contraseña debe tener al menos 4 caracteres.");
            return;
        }

        // TODO: reemplazar con inserción real en la BD (tabla Usuario / LoginDAO)
        ocultarError();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro exitoso");
        alert.setHeaderText(null);
        alert.setContentText("Cuenta creada correctamente. Ya puedes iniciar sesión.");
        alert.showAndWait();

        cerrarVentana();
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
