package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

public class Sesion {

    public enum Rol { ADMINISTRADOR, MEDICO, CLIENTE }

    private static String nombreUsuario;
    private static Rol    rolActual;
    private static int    idUsuario;
    private static int    idEntidadVinculada;

    private Sesion() {}

    public static void iniciarSesion(String nombreUsuario, Rol rol,
                                     int idUsuario, int idEntidadVinculada) {
        Sesion.nombreUsuario       = nombreUsuario;
        Sesion.rolActual           = rol;
        Sesion.idUsuario           = idUsuario;
        Sesion.idEntidadVinculada  = idEntidadVinculada;
    }
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
