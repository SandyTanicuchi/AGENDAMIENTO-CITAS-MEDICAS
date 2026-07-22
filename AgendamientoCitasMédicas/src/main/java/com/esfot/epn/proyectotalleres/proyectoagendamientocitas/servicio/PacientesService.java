package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.servicio;

import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Pacientes;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.PacientesDAO;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Capa de Servicios para Pacientes.
 * Aplica validaciones de seguridad basadas en el rol.
 */
public class PacientesService {

    private final PacientesDAO pacientesDAO;

    public PacientesService() {
        this.pacientesDAO = new PacientesDAO();
    }

    /**
     * Retorna la lista de pacientes, con reglas de visibilidad según el rol.
     */
    public ObservableList<Pacientes> obtenerPacientesPermitidos() {
        if (Sesion.esAdministrador() || Sesion.esMedico()) {
            return pacientesDAO.obtenerListaPacientes();
        }

        // Si es cliente, solo debería poder ver su propio registro
        ObservableList<Pacientes> todos = pacientesDAO.obtenerListaPacientes();
        ObservableList<Pacientes> filtrado = FXCollections.observableArrayList();
        
        int miIdPaciente = Sesion.getIdEntidadVinculada();
        for (Pacientes p : todos) {
            if (p.getId() == miIdPaciente) {
                filtrado.add(p);
                break;
            }
        }
        return filtrado;
    }

    public int registrarPaciente(Pacientes paciente) {
        if (!Sesion.esAdministrador()) {
            System.err.println("[PacientesService] Solo un administrador puede registrar pacientes directamente.");
            return -1;
        }
        return pacientesDAO.registrarPaciente(paciente);
    }

    public int crearPerfilClienteAutenticado(Pacientes paciente) {
        if (!Sesion.esCliente()) {
            return -1;
        }
        return pacientesDAO.registrarPaciente(paciente);
    }

    public boolean actualizarPaciente(Pacientes paciente) {
        if (!Sesion.esAdministrador()) {
            // Un cliente podría actualizar sus propios datos si habilitamos esa opción después
            if (Sesion.esCliente() && paciente.getId() == Sesion.getIdEntidadVinculada()) {
                return pacientesDAO.actualizarPaciente(paciente);
            }
            System.err.println("[PacientesService] Permisos insuficientes para actualizar paciente.");
            return false;
        }
        return pacientesDAO.actualizarPaciente(paciente);
    }

    public boolean eliminarPaciente(int idPaciente) {
        if (!Sesion.esAdministrador()) {
            System.err.println("[PacientesService] Solo un administrador puede eliminar (desactivar) pacientes.");
            return false;
        }
        return pacientesDAO.eliminarPaciente(idPaciente);
    }
}
