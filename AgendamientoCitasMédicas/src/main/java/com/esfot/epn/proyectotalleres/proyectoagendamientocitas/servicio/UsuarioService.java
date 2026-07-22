package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.servicio;

import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Sesion;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.Usuario;
import com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo.UsuarioDAO;

import java.text.Normalizer;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    private static String normalizar(String texto) {
        if (texto == null) return "";
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sinAcentos.toLowerCase().trim();
    }
    public static Sesion.Rol mapearRol(String rolStr) {
        String normalizado = normalizar(rolStr);
        return switch (normalizado) {
            case "administrador" -> Sesion.Rol.ADMINISTRADOR;
            case "medico"        -> Sesion.Rol.MEDICO;
            case "cliente"       -> Sesion.Rol.CLIENTE;
            default              -> null;
        };
    }
    public boolean iniciarSesion(String nombreUsuario, String claveTextoPlano, String rolSeleccionado) {
        if (nombreUsuario == null || claveTextoPlano == null
                || nombreUsuario.trim().isEmpty() || claveTextoPlano.trim().isEmpty()) {
            System.err.println("[UsuarioService] Credenciales vacías.");
            return false;
        }

        Usuario u = usuarioDAO.validarLogin(nombreUsuario, claveTextoPlano);

        if (u == null) {
            System.err.println("[UsuarioService] Login fallido para usuario: " + nombreUsuario);
            return false;
        }

        if (!normalizar(u.getRol()).equals(normalizar(rolSeleccionado))) {
            System.err.println("[UsuarioService] El rol seleccionado (" + rolSeleccionado
                    + ") no coincide con la BD (" + u.getRol() + ").");
            return false;
        }

        Sesion.Rol rolEnum = mapearRol(u.getRol());
        if (rolEnum == null) {
            System.err.println("[UsuarioService] Rol desconocido en BD: " + u.getRol());
            return false;
        }

        // Inicializa la sesión con todos los datos
        Sesion.iniciarSesion(
                u.getUsuario(),
                rolEnum,
                u.getId(),
                u.getIdEntidadVinculada()
        );

        System.out.println("[UsuarioService] Sesión iniciada: usuario=" + u.getUsuario()
                + ", rol=" + rolEnum + ", idVinculado=" + u.getIdEntidadVinculada());
        return true;
    }
    public void cerrarSesion() {
        Sesion.cerrarSesion();
    }
    public boolean registrarNuevoUsuario(String nombre, String usuario, String clave, String rol) {
        if (nombre == null || usuario == null || clave == null || rol == null) {
            System.err.println("[UsuarioService] Datos incompletos para registro.");
            return false;
        }

        if ("administrador".equals(normalizar(rol))) {
            if (!Sesion.esAdministrador()) {
                System.err.println("[UsuarioService] DENEGADO: registro de Administrador desde acceso público.");
                return false;
            }
        }

        return usuarioDAO.registrarUsuario(nombre, usuario, clave, rol);
    }

    public boolean vincularPaciente(int idUsuario, int idPaciente) {
        return usuarioDAO.vincularPacienteAUsuario(idUsuario, idPaciente);
    }

    public boolean vincularDoctor(int idUsuario, int idDoctor) {
        return usuarioDAO.vincularDoctorAUsuario(idUsuario, idDoctor);
    }
}
