package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Sesion;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.servicio.UsuarioService;
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
    @FXML private TextField        txtUsuario;
    @FXML private PasswordField    txtPassword;
    @FXML private Label            lblError;

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboRol.setItems(FXCollections.observableArrayList(
                "Administrador",
                "Médico",
                "Cliente"
        ));
        comboRol.getSelectionModel().selectFirst();
        txtPassword.setOnAction(e -> handleLogin());
    }
    @FXML
    private void handleLogin() {
        String rolSeleccionado = comboRol.getValue();
        String usuario         = txtUsuario.getText().trim();
        String password        = txtPassword.getText();

        // Validación de campos vacíos
        if (usuario.isEmpty() || password.isEmpty() || rolSeleccionado == null) {
            mostrarError("Por favor complete todos los campos.");
            return;
        }
        boolean exito = usuarioService.iniciarSesion(usuario, password, rolSeleccionado);

        if (!exito) {
            mostrarError("Usuario/contraseña incorrectos o rol no corresponde.");
            return;
        }

        ocultarError();
        irADashboard();
    }


    private void irADashboard() {
        String vista = switch (Sesion.getRol()) {
            case ADMINISTRADOR -> "dashboard.fxml";
            case MEDICO        -> "vistaMedico.fxml";
            case CLIENTE       -> "vistaCliente.fxml";
        };

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));
            Parent root = loader.load();
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root, 1280, 800));
            stage.setTitle("VitalSched – Sistema de Gestión de Citas Médicas");
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("[LoginController] Error al cargar la vista '" + vista + "': " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar la interfaz principal. Posiblemente la vista aún no está creada.");
        }
    }
    @FXML
    private void handleAbrirRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("registro.fxml"));
            Parent root = loader.load();

            Stage stageLogin    = (Stage) txtUsuario.getScene().getWindow();
            Stage stageRegistro = new Stage();
            stageRegistro.setTitle("Crear cuenta – VitalSched");
            stageRegistro.initOwner(stageLogin);
            stageRegistro.initModality(Modality.APPLICATION_MODAL);
            stageRegistro.setResizable(false);
            stageRegistro.setScene(new Scene(root));
            stageRegistro.showAndWait();

        } catch (IOException e) {
            System.err.println("[LoginController] No se pudo abrir la ventana de registro: " + e.getMessage());
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

