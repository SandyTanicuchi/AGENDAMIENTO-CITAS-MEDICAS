package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO completo de Doctores: Insertar, Actualizar, Eliminar y Listar.
 * Todas las operaciones se realizan contra la BD MySQL CITAS_MEDICAS.
 */
public class DoctoresDAO {

    private final ConexioDashboard conexionManager = new ConexioDashboard();

    // ----------------------------------------------------------------
    // INSERTAR
    // ----------------------------------------------------------------

    /**
     * Registra un nuevo doctor en la base de datos.
     * @return el ID autogenerado si se insertó correctamente; -1 en caso de error.
     */
    public int registrarDoctor(Doctores doctor) {
        String query = "INSERT INTO DOCTORES (nombre, apellido, especialidad, telefono, correo, estado) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexionManager.conectar()) {
            if (conn == null) return -1;
            
            try (PreparedStatement ps = conn.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, doctor.getNombre());
                ps.setString(2, doctor.getApellido());
                ps.setString(3, doctor.getEspecialidad());
                ps.setString(4, doctor.getTelefono());
                ps.setString(5, doctor.getCorreo());
                ps.setString(6, doctor.getEstado() != null ? doctor.getEstado() : "Activo");

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
            System.err.println("[DoctoresDAO] Error al registrar doctor: " + e.getMessage());
        }
        return -1;
    }

    // ----------------------------------------------------------------
    // ACTUALIZAR
    // ----------------------------------------------------------------

    /**
     * Actualiza los datos de un doctor existente identificado por id_doctor.
     */
    public boolean actualizarDoctor(Doctores doctor) {
        String query = "UPDATE DOCTORES " +
                       "SET nombre=?, apellido=?, especialidad=?, telefono=?, correo=?, estado=? " +
                       "WHERE id_doctor=?";

        try (Connection conn = conexionManager.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, doctor.getNombre());
                ps.setString(2, doctor.getApellido());
                ps.setString(3, doctor.getEspecialidad());
                ps.setString(4, doctor.getTelefono());
                ps.setString(5, doctor.getCorreo());
                ps.setString(6, doctor.getEstado() != null ? doctor.getEstado() : "Activo");
                ps.setInt   (7, doctor.getId());

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[DoctoresDAO] Error al actualizar doctor: " + e.getMessage());
            return false;
        }
    }

    // ----------------------------------------------------------------
    // ELIMINAR
    // ----------------------------------------------------------------

    /**
     * Elimina un doctor por su id_doctor.
     * NOTA: Si el doctor tiene citas activas la BD lanzará error de FK.
     * En ese caso se recomienda cambar su estado a 'Inactivo'.
     */
    public boolean eliminarDoctor(int id) {
        String query = "DELETE FROM DOCTORES WHERE id_doctor=?";

        try (Connection conn = conexionManager.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[DoctoresDAO] Error al eliminar doctor: " + e.getMessage());
            return false;
        }
    }

    // ----------------------------------------------------------------
    // LISTAR
    // ----------------------------------------------------------------

    /**
     * Retorna todos los doctores ordenados por apellido y nombre.
     */
    /**
     * Retorna TODOS los doctores (para el panel de gestión del Admin).
     */
    public ObservableList<Doctores> obtenerListaDoctores() {
        ObservableList<Doctores> lista = FXCollections.observableArrayList();
        String query = "SELECT id_doctor, nombre, apellido, especialidad, telefono, correo, estado " +
                       "FROM DOCTORES ORDER BY apellido, nombre";

        try (Connection conn = conexionManager.conectar()) {
            if (conn == null) return lista;

            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

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
        } catch (SQLException e) {
            System.err.println("[DoctoresDAO] Error al listar doctores: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Retorna solo doctores ACTIVOS (para el ComboBox del portal de pacientes).
     */
    public ObservableList<Doctores> obtenerDoctoresActivos() {
        ObservableList<Doctores> lista = FXCollections.observableArrayList();
        String query = "SELECT id_doctor, nombre, apellido, especialidad, telefono, correo, estado " +
                       "FROM DOCTORES WHERE estado = 'Activo' ORDER BY apellido, nombre";

        try (Connection conn = conexionManager.conectar()) {
            if (conn == null) return lista;
            
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

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
        } catch (SQLException e) {
            System.err.println("[DoctoresDAO] Error al listar doctores: " + e.getMessage());
        }

        return lista;
    }
}
