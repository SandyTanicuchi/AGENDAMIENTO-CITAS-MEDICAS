package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
public class CitasDAO {

    public boolean registrarCita(int idPaciente, int idDoctor,
                                  String fecha, String hora,
                                  String motivo, int idEstado) {
        String query = "INSERT INTO CITAS (id_paciente, id_doctor, fecha, hora, motivo, id_estado) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt   (1, idPaciente);
                ps.setInt   (2, idDoctor);
                ps.setString(3, fecha);
                ps.setString(4, hora);
                ps.setString(5, motivo);
                ps.setInt   (6, idEstado);

                return ps.executeUpdate() > 0;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("[CitasDAO] Conflicto de horario (médico o paciente ya ocupado): " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("[CitasDAO] Error al registrar cita: " + e.getMessage());
            return false;
        }
    }
    public boolean actualizarEstadoCita(int idCita, String nombreEstado) {
        String query = "UPDATE CITAS c " +
                       "JOIN ESTADOS e ON e.nombre_estado = ? " +
                       "SET c.id_estado = e.id_estado " +
                       "WHERE c.id_cita = ?";

        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, nombreEstado);
                ps.setInt   (2, idCita);

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[CitasDAO] Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarNotasMedicas(int idCita, String notasMedicas) {
        String query = "UPDATE CITAS SET notas_medicas = ? WHERE id_cita = ?";
        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, notasMedicas);
                ps.setInt(2, idCita);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[CitasDAO] Error al actualizar notas médicas: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarCita(int idCita) {
        return actualizarEstadoCita(idCita, EstadoCita.CANCELADA.getNombreEnBD());
    }
    public ObservableList<Citas> obtenerListaCitas() {
        ObservableList<Citas> lista = FXCollections.observableArrayList();
        String query = "SELECT id_cita, id_paciente, id_doctor, paciente, doctor, " +
                       "DATE_FORMAT(fecha, '%Y-%m-%d') AS fecha_fmt, " +
                       "TIME_FORMAT(hora, '%H:%i') AS hora_fmt, " +
                       "motivo, notas_medicas, estado " +
                       "FROM VW_CITAS_COMPLETAS";

        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return lista;
            
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

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
        } catch (SQLException e) {
            System.err.println("[CitasDAO] Error al listar citas: " + e.getMessage());
        }

        return lista;
    }

    public ObservableList<Pacientes> obtenerPacientesActivos() {
        ObservableList<Pacientes> lista = FXCollections.observableArrayList();
        String query = "SELECT id_paciente, cedula, nombre, apellido, telefono, correo, direccion, estado " +
                       "FROM PACIENTES WHERE estado='Activo' ORDER BY apellido, nombre";

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
            System.err.println("[CitasDAO] Error obteniendo pacientes activos: " + e.getMessage());
        }
        return lista;
    }

    public ObservableList<Doctores> obtenerDoctoresActivos() {
        ObservableList<Doctores> lista = FXCollections.observableArrayList();
        String query = "SELECT id_doctor, nombre, apellido, especialidad, telefono, correo, estado " +
                       "FROM DOCTORES WHERE estado='Activo' ORDER BY apellido, nombre";

        try (Connection conn = ConexionMySQL.conectar()) {
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
            System.err.println("[CitasDAO] Error obteniendo doctores activos: " + e.getMessage());
        }
        return lista;
    }

    public boolean existeConflictoMedico(int idDoctor, String fecha, String hora, int idCitaExcluir) {
        // Un médico puede tener citas en estado Cancelada a esa hora, esas no generan conflicto
        String query = "SELECT COUNT(*) FROM VW_CITAS_COMPLETAS " +
                       "WHERE id_doctor=? AND fecha=? AND hora=? AND id_cita != ? AND estado != 'Cancelada' AND estado != 'No_Asistio'";
        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt   (1, idDoctor);
                ps.setString(2, fecha);
                ps.setString(3, hora);
                ps.setInt   (4, idCitaExcluir);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[CitasDAO] Error verificando conflicto médico: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verifica si ya existe una cita para un paciente en esa fecha/hora.
     */
    public boolean existeConflictoPaciente(int idPaciente, String fecha, String hora, int idCitaExcluir) {
        String query = "SELECT COUNT(*) FROM VW_CITAS_COMPLETAS " +
                       "WHERE id_paciente=? AND fecha=? AND hora=? AND id_cita != ? AND estado != 'Cancelada' AND estado != 'No_Asistio'";
        try (Connection conn = ConexionMySQL.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt   (1, idPaciente);
                ps.setString(2, fecha);
                ps.setString(3, hora);
                ps.setInt   (4, idCitaExcluir);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[CitasDAO] Error verificando conflicto paciente: " + e.getMessage());
        }
        return false;
    }
}
