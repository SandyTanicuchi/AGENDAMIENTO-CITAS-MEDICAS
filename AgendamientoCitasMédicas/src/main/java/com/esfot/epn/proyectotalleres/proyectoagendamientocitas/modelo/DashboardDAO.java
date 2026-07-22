package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardDAO {
    public ObservableList<Pacientes> obtenerPacientes() {
        ObservableList<Pacientes> lista = FXCollections.observableArrayList();
        String query = "SELECT id_paciente, cedula, nombre, apellido, telefono, correo, direccion, estado " +
                       "FROM PACIENTES ORDER BY apellido, nombre";

        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) {
                System.err.println("[DashboardDAO] Sin conexión a BD – modo demostración PACIENTES");
                return obtenerPacientesMock();
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs   = stmt.executeQuery(query)) {
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
            System.err.println("[DashboardDAO] Error consultando PACIENTES: " + e.getMessage());
            return obtenerPacientesMock();
        }

        return lista.isEmpty() ? obtenerPacientesMock() : lista;
    }

    private ObservableList<Pacientes> obtenerPacientesMock() {
        ObservableList<Pacientes> lista = FXCollections.observableArrayList();
        lista.add(new Pacientes(1, "1723456789", "Juan",   "Pérez",    "0987654321", "juan.perez@email.com",    "Av. Amazonas N24-12",    "Activo"));
        lista.add(new Pacientes(2, "1712345678", "María",  "Gómez",    "0998877665", "maria.gomez@email.com",   "Calle Larga y Solano",   "Activo"));
        lista.add(new Pacientes(3, "1709876543", "Carlos", "Andrade",  "0976543210", "carlos.andrade@email.com","La Prensa y El Inca",    "Activo"));
        lista.add(new Pacientes(4, "1755566778", "Ana",    "Martínez", "0955511223", "ana.martinez@email.com",  "Cumbayá, San Juan",      "Activo"));
        return lista;
    }
    public ObservableList<Doctores> obtenerDoctores() {
        ObservableList<Doctores> lista = FXCollections.observableArrayList();
        String query = "SELECT id_doctor, nombre, apellido, especialidad, telefono, correo, estado " +
                       "FROM DOCTORES ORDER BY apellido, nombre";

        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) {
                System.err.println("[DashboardDAO] Sin conexión a BD – modo demostración DOCTORES");
                return obtenerDoctoresMock();
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs   = stmt.executeQuery(query)) {
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
            System.err.println("[DashboardDAO] Error consultando DOCTORES: " + e.getMessage());
            return obtenerDoctoresMock();
        }

        return lista.isEmpty() ? obtenerDoctoresMock() : lista;
    }

    private ObservableList<Doctores> obtenerDoctoresMock() {
        ObservableList<Doctores> lista = FXCollections.observableArrayList();
        lista.add(new Doctores(1, "Fernando", "Ríos",    "Cardiología",      "0991234567", "fernando.rios@hospital.com",   "Activo"));
        lista.add(new Doctores(2, "Elena",    "Salazar", "Pediatría",        "0992345678", "elena.salazar@hospital.com",   "Activo"));
        lista.add(new Doctores(3, "Javier",   "Mendoza", "Dermatología",     "0993456789", "javier.mendoza@hospital.com",  "Activo"));
        lista.add(new Doctores(4, "Gabriela", "Vargas",  "Ginecología",      "0994567890", "gabriela.vargas@hospital.com", "Inactivo"));
        lista.add(new Doctores(5, "Roberto",  "Mora",    "Medicina General", "0995678901", "roberto.mora@hospital.com",    "Activo"));
        return lista;
    }
    public ObservableList<Citas> obtenerCitas() {
        ObservableList<Citas> lista = FXCollections.observableArrayList();
        String query = "SELECT id_cita, id_paciente, id_doctor, paciente, doctor, " +
                       "DATE_FORMAT(fecha, '%Y-%m-%d') AS fecha_fmt, " +
                       "TIME_FORMAT(hora, '%H:%i') AS hora_fmt, " +
                       "motivo, notas_medicas, estado " +
                       "FROM VW_CITAS_COMPLETAS " +
                       "ORDER BY fecha DESC, hora";

        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) {
                System.err.println("[DashboardDAO] Sin conexión a BD – modo demostración CITAS");
                return obtenerCitasMock();
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs   = stmt.executeQuery(query)) {
                while (rs.next()) {
                    lista.add(new Citas(
                            rs.getInt("id_cita"),
                            rs.getInt("id_paciente"),
                            rs.getInt("id_doctor"),
                            rs.getString("paciente"),
                            rs.getString("doctor"),
                            rs.getString("fecha_fmt"),
                            rs.getString("hora_fmt"),
                            rs.getString("motivo"),
                            rs.getString("notas_medicas"),
                            rs.getString("estado")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("[DashboardDAO] Error consultando CITAS: " + e.getMessage());
            return obtenerCitasMock();
        }

        return lista.isEmpty() ? obtenerCitasMock() : lista;
    }

    private ObservableList<Citas> obtenerCitasMock() {
        ObservableList<Citas> lista = FXCollections.observableArrayList();
        // Usa el constructor reducido para datos mock
        lista.add(new Citas(1, "Juan Pérez",    "Fernando Ríos",    "2026-07-25", "09:00", "Chequeo general de cardiología",    "Pendiente"));
        lista.add(new Citas(2, "María Gómez",   "Elena Salazar",    "2026-07-25", "10:30", "Consulta pediátrica de control",    "Completada"));
        lista.add(new Citas(3, "Ana Martínez",  "Javier Mendoza",   "2026-07-26", "11:15", "Consulta dermatológica por acné",   "Pendiente"));
        lista.add(new Citas(4, "Carlos Andrade","Gabriela Vargas",  "2026-07-27", "14:00", "Control rutinario ginecológico",    "Cancelada"));
        return lista;
    }
}
