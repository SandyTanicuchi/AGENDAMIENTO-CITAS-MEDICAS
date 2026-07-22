package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class PacientesDAO {
    /**
     * Registra un nuevo paciente en la BD.
     */
    public int registrarPaciente(Pacientes paciente) {
        String query = "INSERT INTO PACIENTES (cedula, nombre, apellido, telefono, correo, direccion, estado) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return -1;
            
            try (PreparedStatement ps = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, paciente.getCedula());
                ps.setString(2, paciente.getNombre());
                ps.setString(3, paciente.getApellido());
                ps.setString(4, paciente.getTelefono());
                ps.setString(5, paciente.getCorreo());
                ps.setString(6, paciente.getDireccion());
                ps.setString(7, paciente.getEstado() != null ? paciente.getEstado() : "Activo");

                int afectadas = ps.executeUpdate();
                if (afectadas > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            return rs.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[PacientesDAO] Error al registrar paciente: " + e.getMessage());
        }
        return -1;
    }
    /**
     * Actualiza los datos de un paciente existente identificado por id_paciente.
     */
    public boolean actualizarPaciente(Pacientes paciente) {
        String query = "UPDATE PACIENTES " +
                       "SET cedula=?, nombre=?, apellido=?, telefono=?, correo=?, direccion=?, estado=? " +
                       "WHERE id_paciente=?";

        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, paciente.getCedula());
                ps.setString(2, paciente.getNombre());
                ps.setString(3, paciente.getApellido());
                ps.setString(4, paciente.getTelefono());
                ps.setString(5, paciente.getCorreo());
                ps.setString(6, paciente.getDireccion());
                ps.setString(7, paciente.getEstado() != null ? paciente.getEstado() : "Activo");
                ps.setInt   (8, paciente.getId());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[PacientesDAO] Error al actualizar paciente: " + e.getMessage());
            return false;
        }
    }

  
    /**
     * Elimina lógicamente a un paciente 
     */
    public boolean eliminarPaciente(int id) {
        String query = "UPDATE PACIENTES SET estado = 'Inactivo' WHERE id_paciente = ?";

        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[PacientesDAO] Error al eliminar (desactivar) paciente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retorna todos los pacientes ordenados por apellido y nombre.
     */
    public ObservableList<Pacientes> obtenerListaPacientes() {
        ObservableList<Pacientes> lista = FXCollections.observableArrayList();
        String query = "SELECT id_paciente, cedula, nombre, apellido, telefono, correo, direccion, estado " +
                       "FROM PACIENTES ORDER BY apellido, nombre";

        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return lista;
            
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

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
        } catch (SQLException e) {
            System.err.println("[PacientesDAO] Error al listar pacientes: " + e.getMessage());
        }

        return lista;
    }
}
