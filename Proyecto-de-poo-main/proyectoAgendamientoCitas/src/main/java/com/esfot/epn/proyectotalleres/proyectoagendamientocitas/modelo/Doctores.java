package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

public class Doctores {
    private int id;
    private String nombre;
    private String apellido;
    private String especialidad;
    private String telefono;
    private String correo;
    private String estado;

    public Doctores(int id, String nombre, String apellido, String especialidad, String telefono, String correo, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
        this.telefono = telefono;
        this.correo = correo;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEspecialidad() { return especialidad; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getEstado() { return estado; }
}
