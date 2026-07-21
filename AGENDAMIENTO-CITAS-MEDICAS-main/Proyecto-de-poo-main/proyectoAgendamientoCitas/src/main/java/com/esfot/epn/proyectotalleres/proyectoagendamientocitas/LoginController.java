package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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
                "Cliente"
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
            irADashboard();
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
        }
    }

    /**
     * Carga dashboard.fxml y lo pone en el mismo Stage que ya está abierto,
     * reemplazando la ventana de login.
     */
    private void irADashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();

            // Obtiene el Stage actual a partir de cualquier nodo de la escena de login
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800));
            stage.setTitle("Sistema de Gestión de Citas Médicas");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar el panel principal.");
        }
    }

    @FXML
    private void handleAbrirRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("registro.fxml"));
            Parent root = loader.load();

            Stage stageLogin = (Stage) txtUsuario.getScene().getWindow();

            Stage stageRegistro = new Stage();
            stageRegistro.setTitle("Crear cuenta");
            stageRegistro.initOwner(stageLogin);
            stageRegistro.initModality(Modality.APPLICATION_MODAL);
            stageRegistro.setResizable(false);
            stageRegistro.setScene(new Scene(root));
            stageRegistro.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir la ventana de registro.");
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