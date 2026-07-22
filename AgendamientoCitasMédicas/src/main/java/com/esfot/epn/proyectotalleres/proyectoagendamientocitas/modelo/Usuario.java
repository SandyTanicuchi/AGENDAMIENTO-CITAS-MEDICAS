package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

public class Usuario {

    private int    id;
    private String usuario;
    private String rol;
    private int    idEntidadVinculada;

    public Usuario(int id, String usuario, String rol, int idEntidadVinculada) {
        this.id                 = id;
        this.usuario            = usuario;
        this.rol                = rol;
        this.idEntidadVinculada = idEntidadVinculada;
    }
    public Usuario(int id, String usuario, String rol) {
        this(id, usuario, rol, 0);
    }

    public int    getId()                  { return id; }
    public String getUsuario()             { return usuario; }
    public String getRol()                 { return rol; }
    public int    getIdEntidadVinculada()  { return idEntidadVinculada; }
}
