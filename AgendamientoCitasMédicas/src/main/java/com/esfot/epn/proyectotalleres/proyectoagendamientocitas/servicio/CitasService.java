package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.servicio;

import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Citas;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.CitasDAO;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.EstadoCita;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Capa de Servicios para Citas.
 * Intermediario entre los Controladores (Vistas) y los DAOs.
 * Aquí se validan las reglas de negocio y los permisos según el rol activo.
 */
public class CitasService {

    private final CitasDAO citasDAO;

    public CitasService() {
        this.citasDAO = new CitasDAO();
    }

    /**
     * Retorna únicamente las citas que el usuario actual tiene permitido ver.
     */
    public ObservableList<Citas> obtenerCitasPermitidas() {
        ObservableList<Citas> todas = citasDAO.obtenerListaCitas();
        
        if (Sesion.esAdministrador()) {
            return todas; // Administrador ve todo
        }

        ObservableList<Citas> filtradas = FXCollections.observableArrayList();
        int idVinculado = Sesion.getIdEntidadVinculada();

        for (Citas c : todas) {
            if (Sesion.esCliente() && c.getIdPacienteRef() == idVinculado) {
                filtradas.add(c);
            } else if (Sesion.esMedico() && c.getIdDoctorRef() == idVinculado) {
                filtradas.add(c);
            }
        }
        return filtradas;
    }

    /**
     * Registra una cita previa validación de conflictos de horario.
     */
    public boolean registrarCita(int idPaciente, int idDoctor, String fecha, String hora, String motivo) {
        // Regla: El cliente solo puede agendar para sí mismo
        if (Sesion.esCliente() && idPaciente != Sesion.getIdEntidadVinculada()) {
            System.err.println("[CitasService] Operación denegada. El cliente no puede agendar citas a nombre de terceros.");
            return false;
        }

        if (citasDAO.existeConflictoMedico(idDoctor, fecha, hora, 0)) {
            System.err.println("[CitasService] El médico ya tiene una cita asignada en ese horario.");
            return false;
        }
        if (citasDAO.existeConflictoPaciente(idPaciente, fecha, hora, 0)) {
            System.err.println("[CitasService] El paciente ya tiene otra cita en ese horario.");
            return false;
        }

        // Por defecto, toda cita nueva nace en estado PENDIENTE (id_estado = 1)
        return citasDAO.registrarCita(idPaciente, idDoctor, fecha, hora, motivo, 1);
    }

    /**
     * Lógica segura para cambiar estados.
     */
    public boolean cambiarEstadoCita(Citas cita, EstadoCita nuevoEstado) {
        if (cita == null || nuevoEstado == null) return false;

        EstadoCita estadoActual = EstadoCita.desdeBD(cita.getEstadoCita());
        
        // Validación 1: ¿Es una transición válida según las reglas de negocio?
        if (estadoActual != null && !estadoActual.puedeTransicionarA(nuevoEstado)) {
            System.err.println("[CitasService] Transición inválida de " + estadoActual.name() + " a " + nuevoEstado.name());
            return false;
        }

        // Validación 2: ¿El usuario tiene permiso para este cambio?
        if (!tienePermisoParaCambiarEstado(cita, nuevoEstado)) {
            System.err.println("[CitasService] Usuario sin permisos para aplicar este estado.");
            return false;
        }

        // Si pasa todas las validaciones, actualizamos en BD
        boolean exito = citasDAO.actualizarEstadoCita(cita.getId(), nuevoEstado.getNombreEnBD());
        if (exito) {
            cita.setEstadoCita(nuevoEstado.getNombreEnBD()); // Reflejar en memoria
        }
        return exito;
    }

    /**
     * Permite cancelar una cita. 
     * Encapsula las reglas específicas de cancelación.
     */
    public boolean cancelarCita(Citas cita) {
        return cambiarEstadoCita(cita, EstadoCita.CANCELADA);
    }

    /**
     * Permite actualizar el diagnóstico/notas de una cita.
     * Solo el médico asignado (o el admin) puede hacer esto.
     */
    public boolean guardarNotasMedicas(Citas cita, String notas) {
        if (Sesion.esCliente()) {
            System.err.println("[CitasService] Un cliente no puede modificar notas médicas.");
            return false;
        }

        if (Sesion.esMedico() && cita.getIdDoctorRef() != Sesion.getIdEntidadVinculada()) {
            System.err.println("[CitasService] El médico no está autorizado a editar notas de una cita ajena.");
            return false;
        }

        boolean exito = citasDAO.actualizarNotasMedicas(cita.getId(), notas);
        if (exito) {
            cita.setNotasMedicas(notas); // Actualiza la instancia en memoria
        }
        return exito;
    }

    // --- Métodos privados de validación ---

    private boolean tienePermisoParaCambiarEstado(Citas cita, EstadoCita nuevoEstado) {
        if (Sesion.esAdministrador()) return true;

        if (Sesion.esCliente()) {
            // Regla: El cliente solo modifica sus citas
            if (cita.getIdPacienteRef() != Sesion.getIdEntidadVinculada()) return false;
            // Regla: Cliente solo puede cancelar
            return nuevoEstado == EstadoCita.CANCELADA;
        }

        if (Sesion.esMedico()) {
            // Regla: Médico solo modifica sus citas
            if (cita.getIdDoctorRef() != Sesion.getIdEntidadVinculada()) return false;
            // El médico puede confirmar, iniciar atención, completar o cancelar/marcar inasistencia.
            // La validación estricta de transición (puedeTransicionarA) ya filtró absurdos,
            // pero le permitimos cualquier estado si es su cita.
            return true;
        }

        return false;
    }
}
