package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

/**
 * Representa un usuario autenticado del sistema.
 * Fase 2 – Bloque 1: se agrega idEntidadVinculada (H-14) para poder
 * filtrar recursos por rol sin consultas adicionales.
 */
public class Usuario {

    private int    id;
    private String usuario;
    private String rol;
    /** id_paciente si rol=Cliente; id_doctor si rol=Médico; 0 si Administrador. */
    private int    idEntidadVinculada;

    public Usuario(int id, String usuario, String rol, int idEntidadVinculada) {
        this.id                 = id;
        this.usuario            = usuario;
        this.rol                = rol;
        this.idEntidadVinculada = idEntidadVinculada;
    }

    /** Constructor de compatibilidad mientras se migran los llamadores. */
    public Usuario(int id, String usuario, String rol) {
        this(id, usuario, rol, 0);
    }

    public int    getId()                  { return id; }
    public String getUsuario()             { return usuario; }
    public String getRol()                 { return rol; }
    public int    getIdEntidadVinculada()  { return idEntidadVinculada; }
}