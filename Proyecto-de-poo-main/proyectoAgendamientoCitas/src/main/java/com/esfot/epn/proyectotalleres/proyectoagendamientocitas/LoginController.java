package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private ComboBox<String> comboRol;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboRol.setItems(FXCollections.observableArrayList(
                "Administrador",
                "Médico",
                "Recepcionista"
        ));
        comboRol.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleLogin() {
        String rol      = comboRol.getValue();
        String usuario  = txtUsuario.getText().trim();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor complete todos los campos.");
            return;
        }

        // TODO: reemplazar con consulta a la BD
        if (usuario.equals("admin") && password.equals("1234")) {
            ocultarError();
            System.out.println("Login exitoso → " + rol);
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }

    private void ocultarError() {
        lblError.setVisible(false);
    }
}