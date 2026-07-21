package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

public class Usuario {
    private int id;
    private String usuario;
    private String rol;

    public Usuario(int id, String usuario, String rol) {
        this.id = id;
        this.usuario = usuario;
        this.rol = rol;
    }

    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getRol() { return rol; }
}