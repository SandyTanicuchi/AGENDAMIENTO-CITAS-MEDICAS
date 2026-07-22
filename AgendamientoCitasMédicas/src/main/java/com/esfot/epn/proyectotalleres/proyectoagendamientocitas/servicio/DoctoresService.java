package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.servicio;

import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Doctores;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.DoctoresDAO;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Sesion;
import javafx.collections.ObservableList;

/**
 * Capa de Servicios para Doctores.
 * Aplica validaciones de seguridad basadas en el rol.
 */
public class DoctoresService {

    private final DoctoresDAO doctoresDAO;

    public DoctoresService() {
        this.doctoresDAO = new DoctoresDAO();
    }

    /**
     * Retorna la lista de doctores (Todos, para uso de Admin).
     */
    public ObservableList<Doctores> obtenerListaDoctores() {
        return doctoresDAO.obtenerListaDoctores();
    }

    /**
     * Retorna solo los doctores activos (Para que los clientes agenden citas).
     */
    public ObservableList<Doctores> obtenerDoctoresActivos() {
        return doctoresDAO.obtenerDoctoresActivos();
    }

    public int registrarDoctor(Doctores doctor) {
        if (!Sesion.esAdministrador()) {
            System.err.println("[DoctoresService] Solo un administrador puede registrar doctores.");
            return -1;
        }
        return doctoresDAO.registrarDoctor(doctor);
    }

    public int crearPerfilMedicoAutenticado(Doctores doctor) {
        if (!Sesion.esMedico()) {
            return -1;
        }
        return doctoresDAO.registrarDoctor(doctor);
    }

    public boolean actualizarDoctor(Doctores doctor) {
        if (!Sesion.esAdministrador()) {
            System.err.println("[DoctoresService] Permisos insuficientes para actualizar doctor.");
            return false;
        }
        return doctoresDAO.actualizarDoctor(doctor);
    }

    public boolean eliminarDoctor(int idDoctor) {
        if (!Sesion.esAdministrador()) {
            System.err.println("[DoctoresService] Solo un administrador puede eliminar (desactivar) doctores.");
            return false;
        }
        return doctoresDAO.eliminarDoctor(idDoctor);
    }
}
