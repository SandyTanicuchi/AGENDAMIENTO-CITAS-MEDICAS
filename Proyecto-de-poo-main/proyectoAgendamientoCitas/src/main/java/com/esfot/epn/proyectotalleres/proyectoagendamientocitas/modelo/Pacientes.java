package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

public class Pacientes {
    private int id;
    private String cedula;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private String direccion;
    private String estado;

    public Pacientes(int id, String cedula, String nombre, String apellido, String telefono, String correo, String direccion, String estado) {
        this.id = id;
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getDireccion() { return direccion; }
    public String getEstado() { return estado; }
}
