package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Sesion;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private ComboBox<String> comboRol;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private final String URL_BD = "jdbc:mysql://localhost:3306/CITAS_MEDICAS";
    private final String USUARIO_BD = "root";
    private final String CLAVE_BD = "";
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
        String rol = comboRol.getValue();
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();
        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor complete todos los campos.");
            return;
        }
        try {
            Connection con = DriverManager.getConnection(
                    URL_BD,
                    USUARIO_BD,
                    CLAVE_BD
            );
            String sql = "SELECT * FROM USUARIOS "
                    + "WHERE usuario=? "
                    + "AND clave=? "
                    + "AND rol=? "
                    + "AND estado='activo'";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, password);
            ps.setString(3, rol);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {

                if (rol.equals("Cliente")) {
                    Sesion.iniciarSesion(
                            usuario,
                            Sesion.Rol.CLIENTE);

                } else if (rol.equals("Médico")) {
                    Sesion.iniciarSesion(
                            usuario,
                            Sesion.Rol.MEDICO);
                } else if (rol.equals("Administrador")) {
                    Sesion.iniciarSesion(
                            usuario,
                            Sesion.Rol.ADMINISTRADOR);
                }
                ocultarError();
                irADashboard();
            } else {
                System.out.println("Usuario o contraseña incorrectos" );
            }
            con.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void irADashboard() {
        try {
            String vista = "";
            if (Sesion.esAdministrador()) {
                vista = "dashboard.fxml";
            } else if (Sesion.esMedico()) {
                vista = "medico.fxml";
            } else if (Sesion.getRol() == Sesion.Rol.CLIENTE) {
                vista = "pacientes.fxml";
            }
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(vista));
            Parent root = loader.load();
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root,1280,800));
            stage.setTitle("Sistema de Gestión de Citas Médicas");
            stage.centerOnScreen();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleAbrirRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("registro.fxml"));
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
            System.out.println("No se pudo abrir la ventana de registro.");
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
