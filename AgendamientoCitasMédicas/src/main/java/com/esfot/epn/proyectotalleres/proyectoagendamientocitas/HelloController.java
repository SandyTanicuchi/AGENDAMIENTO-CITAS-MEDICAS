package com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.DashboardDAO;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Pacientes;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Doctores;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Citas;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    // Tablas de la Interfaz
    @FXML private TableView<Pacientes> tblPacientes;
    @FXML private TableView<Doctores> tblDoctores;
    @FXML private TableView<Citas> tblCitas;

    // Columnas Pacientes
    @FXML private TableColumn<Pacientes, Integer> colId;
    @FXML private TableColumn<Pacientes, String> colPaciente;
    @FXML private TableColumn<Pacientes, String> colCedula;
    @FXML private TableColumn<Pacientes, String> colTelefono;
    @FXML private TableColumn<Pacientes, String> colCorreoPaciente;
    @FXML private TableColumn<Pacientes, String> colEstadoPaciente;

    // Columnas Doctores
    @FXML private TableColumn<Doctores, Integer> colIdDoctor;
    @FXML private TableColumn<Doctores, String> colDoctor;
    @FXML private TableColumn<Doctores, String> colEspecialidades;
    @FXML private TableColumn<Doctores, String> colCorreoDoctor;
    @FXML private TableColumn<Doctores, String> colEstadoDoctor;

    // Columnas Citas
    @FXML private TableColumn<Citas, Integer> colIdCita;
    @FXML private TableColumn<Citas, String> colPacienteCita;
    @FXML private TableColumn<Citas, String> colDoctorCita;
    @FXML private TableColumn<Citas, String> colFechaCita;
    @FXML private TableColumn<Citas, String> colHoraCita;
    @FXML private TableColumn<Citas, String> colMotivoCita;
    @FXML private TableColumn<Citas, String> colEstadoCita;

    // Paneles de Contenido
    @FXML private VBox panelDashboard;
    @FXML private VBox panelPacientes;
    @FXML private VBox panelDoctores;
    @FXML private VBox panelCitas;
    @FXML private VBox panelConfiguracion;

    // Botones del Menú Lateral
    @FXML private Button btnMenuDashboard;
    @FXML private Button btnMenuPacientes;
    @FXML private Button btnMenuDoctores;
    @FXML private Button btnMenuCitas;
    @FXML private Button btnMenuConfiguracion;

    // Etiquetas de estadísticas en el Dashboard
    @FXML private Label lblCantPacientes;
    @FXML private Label lblCantDoctores;
    @FXML private Label lblCantCitas;

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        mostrarDatosDashboard();
        activarPanel(panelDashboard, btnMenuDashboard);
    }

    private void configurarColumnas() {
        // Enlace Pacientes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPaciente.setCellValueFactory(new PropertyValueFactory<>("nombre")); // Mapeado a nombre del Paciente
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreoPaciente.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colEstadoPaciente.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Enlace Doctores
        colIdDoctor.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEspecialidades.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colCorreoDoctor.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colEstadoDoctor.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Enlace Citas
        colIdCita.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPacienteCita.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        colDoctorCita.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        colFechaCita.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHoraCita.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colMotivoCita.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colEstadoCita.setCellValueFactory(new PropertyValueFactory<>("estadoCita"));
    }

    public void mostrarDatosDashboard() {
        tblPacientes.setItems(dashboardDAO.obtenerPacientes());
        tblDoctores.setItems(dashboardDAO.obtenerDoctores());
        tblCitas.setItems(dashboardDAO.obtenerCitas());

        if (lblCantPacientes != null) {
            lblCantPacientes.setText(String.valueOf(tblPacientes.getItems().size()));
        }
        if (lblCantDoctores != null) {
            lblCantDoctores.setText(String.valueOf(tblDoctores.getItems().size()));
        }
        if (lblCantCitas != null) {
            lblCantCitas.setText(String.valueOf(tblCitas.getItems().size()));
        }
    }
    @FXML
    private void mostrarPanelDashboard(ActionEvent event) {
        activarPanel(panelDashboard, btnMenuDashboard);
    }

    @FXML
    private void mostrarPanelPacientes(ActionEvent event) {
        activarPanel(panelPacientes, btnMenuPacientes);
    }

    @FXML
    private void mostrarPanelDoctores(ActionEvent event) {
        activarPanel(panelDoctores, btnMenuDoctores);
    }

    @FXML
    private void mostrarPanelCitas(ActionEvent event) {
        activarPanel(panelCitas, btnMenuCitas);
    }

    @FXML
    private void mostrarPanelConfiguracion(ActionEvent event) {
        activarPanel(panelConfiguracion, btnMenuConfiguracion);
    }

    private void activarPanel(VBox panelActivo, Button botonActivo) {
        // Ocultar todos los paneles
        panelDashboard.setVisible(false);
        panelDashboard.setManaged(false);
        panelPacientes.setVisible(false);
        panelPacientes.setManaged(false);
        panelDoctores.setVisible(false);
        panelDoctores.setManaged(false);
        panelCitas.setVisible(false);
        panelCitas.setManaged(false);
        panelConfiguracion.setVisible(false);
        panelConfiguracion.setManaged(false);

        // Mostrar panel activo
        panelActivo.setVisible(true);
        panelActivo.setManaged(true);

        // Remover clase activa de todos los botones
        btnMenuDashboard.getStyleClass().remove("menu-button-active");
        btnMenuPacientes.getStyleClass().remove("menu-button-active");
        btnMenuDoctores.getStyleClass().remove("menu-button-active");
        btnMenuCitas.getStyleClass().remove("menu-button-active");
        btnMenuConfiguracion.getStyleClass().remove("menu-button-active");

        // Agregar clase activa al botón presionado
        botonActivo.getStyleClass().add("menu-button-active");
    }

    @FXML
    private void abrirFormularioCita() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("citas.fxml")
            );
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Gestión de Citas");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

            mostrarDatosDashboard(); // refresca la tabla al cerrar el formulario
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirFormularioPacientes() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("pacientes.fxml")
            );
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Gestión de Pacientes");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

            mostrarDatosDashboard();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirFormularioDoctores() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("InterfazDoctor.fxml")
            );
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Gestión de Doctores");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

            mostrarDatosDashboard();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cierra la sesión del Administrador y regresa a la pantalla de Login.
     * Vinculado al botón "Salir" en el header del dashboard.fxml.
     */
    @FXML
    private void cerrarSesion() {
        Sesion.cerrarSesion();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) panelDashboard.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.setTitle("Login – VitalSched");
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("[HelloController] Error al cerrar sesión: " + e.getMessage());
        }
    }
}