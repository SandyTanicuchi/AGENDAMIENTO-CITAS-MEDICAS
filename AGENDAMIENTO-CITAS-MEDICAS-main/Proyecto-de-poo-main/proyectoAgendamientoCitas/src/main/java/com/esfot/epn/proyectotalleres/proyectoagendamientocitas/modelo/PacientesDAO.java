package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PacientesDAO {
    private final ConexioDashboard conexionManager = new ConexioDashboard();

    public boolean registrarPaciente(Pacientes pacientes) {
        String query = "INSERT INTO PACIENTES (cedula, nombre, apellido, telefono, correo, direccion, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexionManager.conectar();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, pacientes.getCedula());
            ps.setString(2, pacientes.getNombre());
            ps.setString(3, pacientes.getApellido());
            ps.setString(4, pacientes.getTelefono());
            ps.setString(5, pacientes.getCorreo());
            ps.setString(6, pacientes.getDireccion());
            ps.setString(7, pacientes.getEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar paciente: " + e.getMessage());
            return false;
        }
    }
    public boolean actualizarPaciente(Pacientes paciente) {

        String query = "UPDATE PACIENTES SET cedula=?, nombre=?, apellido=?, telefono=?, correo=?, direccion=?, estado=? WHERE id_paciente=?";

        try (Connection conn = conexionManager.conectar();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, paciente.getCedula());
            ps.setString(2, paciente.getNombre());
            ps.setString(3, paciente.getApellido());
            ps.setString(4, paciente.getTelefono());
            ps.setString(5, paciente.getCorreo());
            ps.setString(6, paciente.getDireccion());
            ps.setString(7, paciente.getEstado());
            ps.setInt(8, paciente.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar paciente: " + e.getMessage());
            return false;
        }
    }
    public boolean eliminarPaciente(int id) {

        String query = "DELETE FROM PACIENTES WHERE id_paciente=?";

        try (Connection conn = conexionManager.conectar();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar paciente: " + e.getMessage());
            return false;
        }
    }

    public ObservableList<Pacientes> obtenerListaPacientes() {
        ObservableList<Pacientes> listaPacientes = FXCollections.observableArrayList();
        String query = "SELECT id_paciente, cedula, nombre, apellido, telefono, correo, direccion, estado FROM PACIENTES";

        try (Connection conn = conexionManager.conectar();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pacientes pacientes = new Pacientes(
                        rs.getInt("id_paciente"),
                        rs.getString("cedula"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("correo"),
                        rs.getString("direccion"),
                        rs.getString("estado")
                );
                listaPacientes.add(pacientes);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener pacientes: " + e.getMessage());
        }
        return listaPacientes;
    }
}