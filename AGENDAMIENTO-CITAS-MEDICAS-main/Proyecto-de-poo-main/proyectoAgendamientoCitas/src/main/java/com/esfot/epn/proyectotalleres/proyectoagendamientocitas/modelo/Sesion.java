package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

public class Sesion {

    public enum Rol { ADMINISTRADOR, MEDICO, CLIENTE }

    private static String nombreUsuario;
    private static Rol rolActual;

    private Sesion() {}

    public static void iniciarSesion(String nombreUsuario, Rol rol) {
        Sesion.nombreUsuario = nombreUsuario;
        Sesion.rolActual = rol;
    }

    public static String getNombreUsuario() { return nombreUsuario; }
    public static Rol getRol() { return rolActual; }

    public static boolean esAdministrador() { return rolActual == Rol.ADMINISTRADOR; }
    public static boolean esMedico() { return rolActual == Rol.MEDICO; }
    public static boolean esRecepcionista() { return rolActual == Rol.CLIENTE; }
}