package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDAO {
    private final ConexioDashboard conexionManager = new ConexioDashboard();

    public Usuario validarLogin(String usuario, String clave) {
        String query = "SELECT id_usuario, usuario, rol FROM USUARIOS WHERE usuario = ? AND clave = ? AND estado = 'Activo'";

        try (Connection conn = conexionManager.conectar();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, usuario);
            ps.setString(2, clave);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("usuario"),
                            rs.getString("rol")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error validando login: " + e.getMessage());
        }
        return null;
    }
}