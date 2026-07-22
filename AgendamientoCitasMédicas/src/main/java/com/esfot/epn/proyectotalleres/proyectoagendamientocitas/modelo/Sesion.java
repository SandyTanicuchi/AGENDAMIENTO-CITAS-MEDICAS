package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

/**
 * Singleton estático que almacena el estado de la sesión activa.
 * Fase 2 – Bloque 1: se agregan idUsuario e idEntidadVinculada (H-14).
 * Con idEntidadVinculada el controlador de cada rol puede filtrar
 * "sus propias" citas sin hacer consultas adicionales de vinculación.
 */
public class Sesion {

    public enum Rol { ADMINISTRADOR, MEDICO, CLIENTE }

    private static String nombreUsuario;
    private static Rol    rolActual;
    private static int    idUsuario;
    /** id_paciente si Cliente; id_doctor si Médico; 0 si Administrador. */
    private static int    idEntidadVinculada;

    private Sesion() {}

    /**
     * Inicia la sesión con todos los datos del usuario autenticado.
     *
     * @param nombreUsuario       Login del usuario
     * @param rol                 Rol asignado
     * @param idUsuario           PK en la tabla USUARIOS
     * @param idEntidadVinculada  id_paciente (Cliente) | id_doctor (Médico) | 0 (Admin)
     */
    public static void iniciarSesion(String nombreUsuario, Rol rol,
                                     int idUsuario, int idEntidadVinculada) {
        Sesion.nombreUsuario       = nombreUsuario;
        Sesion.rolActual           = rol;
        Sesion.idUsuario           = idUsuario;
        Sesion.idEntidadVinculada  = idEntidadVinculada;
    }

    /**
     * @deprecated Usar {@link #iniciarSesion(String, Rol, int, int)}.
     *             Mantenido temporalmente para no romper LoginController hasta el Bloque 3.
     */
    @Deprecated
    public static void iniciarSesion(String nombreUsuario, Rol rol) {
        iniciarSesion(nombreUsuario, rol, 0, 0);
    }

    public static String getNombreUsuario()       { return nombreUsuario; }
    public static Rol    getRol()                 { return rolActual; }
    public static int    getIdUsuario()           { return idUsuario; }
    public static int    getIdEntidadVinculada()  { return idEntidadVinculada; }

    public static boolean esAdministrador() { return rolActual == Rol.ADMINISTRADOR; }
    public static boolean esMedico()        { return rolActual == Rol.MEDICO; }
    public static boolean esCliente()       { return rolActual == Rol.CLIENTE; }

    /** Limpia todos los datos de sesión al cerrar o hacer logout. */
    public static void cerrarSesion() {
        nombreUsuario      = null;
        rolActual          = null;
        idUsuario          = 0;
        idEntidadVinculada = 0;
    }
}
