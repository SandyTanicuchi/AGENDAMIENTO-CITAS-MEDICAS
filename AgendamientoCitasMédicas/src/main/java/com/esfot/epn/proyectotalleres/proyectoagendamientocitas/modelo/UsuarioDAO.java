package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    public Usuario validarLogin(String nombreUsuario, String clave) {
        String query = "SELECT id_usuario, usuario, rol, id_paciente, id_doctor " +
                       "FROM USUARIOS WHERE usuario = ? AND clave = ? AND estado = 'Activo'";

        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return null;
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, nombreUsuario);
                ps.setString(2, clave);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String rol = rs.getString("rol");
                        int idEntidadVinculada = 0;
            
                        if ("Cliente".equals(rol)) {
                            idEntidadVinculada = rs.getInt("id_paciente");
                        } else if ("Médico".equals(rol)) {
                            idEntidadVinculada = rs.getInt("id_doctor");
                        }

                        return new Usuario(
                                rs.getInt("id_usuario"),
                                rs.getString("usuario"),
                                rol,
                                idEntidadVinculada
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[UsuarioDAO] Error validando login: " + e.getMessage());
        }

        return null;
    }
    public boolean registrarUsuario(String nombre, String usuario, String clave, String rol) {
        // Verificar que el nombre de usuario no exista
        if (existeUsuario(usuario)) {
            System.err.println("[UsuarioDAO] El nombre de usuario '" + usuario + "' ya está en uso o la BD no responde.");
            return false;
        }

        String query = "INSERT INTO USUARIOS (nombre, usuario, clave, rol, estado) VALUES (?, ?, ?, ?, 'Activo')";

        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, nombre);
                ps.setString(2, usuario);
                ps.setString(3, clave);
                ps.setString(4, rol);

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }
    private boolean existeUsuario(String usuario) {
        String query = "SELECT COUNT(*) FROM USUARIOS WHERE usuario = ?";
        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return true; // Asumir que existe si no hay BD para evitar registros falsos
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, usuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error verificando usuario: " + e.getMessage());
            return true; // Asumir que existe por seguridad en caso de error
        }
        return false;
    }

    public boolean vincularPacienteAUsuario(int idUsuario, int idPaciente) {
        String query = "UPDATE USUARIOS SET id_paciente = ? WHERE id_usuario = ?";
        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, idPaciente);
                ps.setInt(2, idUsuario);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error vinculando paciente: " + e.getMessage());
            return false;
        }
    }

    public boolean vincularDoctorAUsuario(int idUsuario, int idDoctor) {
        String query = "UPDATE USUARIOS SET id_doctor = ? WHERE id_usuario = ?";
        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, idDoctor);
                ps.setInt(2, idUsuario);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UsuarioDAO] Error vinculando doctor: " + e.getMessage());
            return false;
        }
    }
}
