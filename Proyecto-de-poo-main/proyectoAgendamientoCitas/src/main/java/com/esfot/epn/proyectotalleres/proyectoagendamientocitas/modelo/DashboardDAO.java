package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardDAO {
    private final ConexioDashboard conexionManager = new ConexioDashboard();

    public ObservableList<Pacientes> obtenerPacientes() {
        ObservableList<Pacientes> lista = FXCollections.observableArrayList();
        String query = "SELECT id_paciente, cedula, nombre, apellido, telefono, correo, direccion, estado FROM PACIENTES";

        try (Connection conn = conexionManager.conectar()) {
            if (conn == null) {
                System.err.println("Error: No se pudo obtener la conexión para consultar PACIENTES. Cargando datos de demostración...");
                return obtenerPacientesMock();
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    lista.add(new Pacientes(
                            rs.getInt("id_paciente"),
                            rs.getString("cedula"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("telefono"),
                            rs.getString("correo"),
                            rs.getString("direccion"),
                            rs.getString("estado")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error consultando PACIENTES: " + e.getMessage() + ". Cargando datos de demostración...");
            return obtenerPacientesMock();
        }

        if (lista.isEmpty()) {
            return obtenerPacientesMock();
        }
        return lista;
    }

    private ObservableList<Pacientes> obtenerPacientesMock() {
        ObservableList<Pacientes> lista = FXCollections.observableArrayList();
        lista.add(new Pacientes(1, "1723456789", "Juan", "Pérez", "0987654321", "juan.perez@email.com", "Av. Amazonas N24-12", "Activo"));
        lista.add(new Pacientes(2, "1712345678", "María", "Gómez", "0998877665", "maria.gomez@email.com", "Calle Larga y Solano", "Activo"));
        lista.add(new Pacientes(3, "1709876543", "Carlos", "Andrade", "0976543210", "carlos.andrade@email.com", "La Prensa y El Inca", "Inactivo"));
        lista.add(new Pacientes(4, "1755566778", "Ana", "Martínez", "0955511223", "ana.martinez@email.com", "Cumbayá, San Juan", "Activo"));
        return lista;
    }

    public ObservableList<Doctores> obtenerDoctores() {
        ObservableList<Doctores> lista = FXCollections.observableArrayList();
        String query = "SELECT id_doctor, nombre, apellido, especialidad, telefono, correo, estado FROM DOCTORES";

        try (Connection conn = conexionManager.conectar()) {
            if (conn == null) {
                System.err.println("Error: No se pudo obtener la conexión para consultar DOCTORES. Cargando datos de demostración...");
                return obtenerDoctoresMock();
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    lista.add(new Doctores(
                            rs.getInt("id_doctor"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("especialidad"),
                            rs.getString("telefono"),
                            rs.getString("correo"),
                            rs.getString("estado")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error consultando DOCTORES: " + e.getMessage() + ". Cargando datos de demostración...");
            return obtenerDoctoresMock();
        }

        if (lista.isEmpty()) {
            return obtenerDoctoresMock();
        }
        return lista;
    }

    private ObservableList<Doctores> obtenerDoctoresMock() {
        ObservableList<Doctores> lista = FXCollections.observableArrayList();
        lista.add(new Doctores(1, "Dr. Fernando", "Ríos", "Cardiología", "0991234567", "fernando.rios@clinica.com", "Activo"));
        lista.add(new Doctores(2, "Dra. Elena", "Salazar", "Pediatría", "0992345678", "elena.salazar@clinica.com", "Activo"));
        lista.add(new Doctores(3, "Dr. Javier", "Mendoza", "Dermatología", "0993456789", "javier.mendoza@clinica.com", "Activo"));
        lista.add(new Doctores(4, "Dra. Gabriela", "Vargas", "Ginecología", "0994567890", "gabriela.vargas@clinica.com", "Inactivo"));
        return lista;
    }

    public ObservableList<Citas> obtenerCitas() {
        ObservableList<Citas> lista = FXCollections.observableArrayList();
        String query = "SELECT c.id_cita, CONCAT(p.nombre, ' ', p.apellido) AS paciente, " +
                "CONCAT(d.nombre, ' ', d.apellido) AS doctor, c.fecha, c.hora, c.motivo, e.nombre_estado AS estado " +
                "FROM CITAS c " +
                "JOIN PACIENTES p ON c.id_paciente = p.id_paciente " +
                "JOIN DOCTORES d ON c.id_doctor = d.id_doctor " +
                "JOIN ESTADOS e ON c.id_estado = e.id_estado";

        try (Connection conn = conexionManager.conectar()) {
            if (conn == null) {
                System.err.println("Error: No se pudo obtener la conexión para consultar CITAS. Cargando datos de demostración...");
                return obtenerCitasMock();
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    lista.add(new Citas(
                            rs.getInt("id_cita"),
                            rs.getString("paciente"),
                            rs.getString("doctor"),
                            rs.getString("fecha"),
                            rs.getString("hora"),
                            rs.getString("motivo"),
                            rs.getString("estado")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error consultando CITAS: " + e.getMessage() + ". Cargando datos de demostración...");
            return obtenerCitasMock();
        }

        if (lista.isEmpty()) {
            return obtenerCitasMock();
        }
        return lista;
    }

    private ObservableList<Citas> obtenerCitasMock() {
        ObservableList<Citas> lista = FXCollections.observableArrayList();
        lista.add(new Citas(1, "Juan Pérez", "Dr. Fernando Ríos", "2026-07-18", "09:00", "Chequeo general de cardiología", "Pendiente"));
        lista.add(new Citas(2, "María Gómez", "Dra. Elena Salazar", "2026-07-18", "10:30", "Consulta pediátrica de control", "Completada"));
        lista.add(new Citas(3, "Ana Martínez", "Dr. Javier Mendoza", "2026-07-19", "11:15", "Consulta dermatológica por acné", "Pendiente"));
        lista.add(new Citas(4, "Carlos Andrade", "Dra. Gabriela Vargas", "2026-07-20", "14:00", "Control rutinario ginecológico", "Cancelada"));
        return lista;
    }
}