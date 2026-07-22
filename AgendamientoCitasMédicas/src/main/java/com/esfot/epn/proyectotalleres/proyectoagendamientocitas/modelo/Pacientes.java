package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

/**
 * Clase que representa un Paciente en el sistema de agendamiento.
 * Incluye el campo 'estado' para control de pacientes activos/inactivos.
 */
public class Pacientes {
    private int    id;
    private String cedula;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private String direccion;
    private String estado;    // Activo | Inactivo

    /** Constructor vacío requerido por algunos frameworks */
    public Pacientes() {}

    /** Constructor completo con estado */
    public Pacientes(int id, String cedula, String nombre, String apellido,
                     String telefono, String correo, String direccion, String estado) {
        this.id        = id;
        this.cedula    = cedula;
        this.nombre    = nombre;
        this.apellido  = apellido;
        this.telefono  = telefono;
        this.correo    = correo;
        this.direccion = direccion;
        this.estado    = estado;
    }

    /** Constructor sin estado (compatibilidad; asigna 'Activo' por defecto) */
    public Pacientes(int id, String cedula, String nombre, String apellido,
                     String telefono, String correo, String direccion) {
        this(id, cedula, nombre, apellido, telefono, correo, direccion, "Activo");
    }

    // ---- Getters ----
    public int    getId()        { return id; }
    public String getCedula()    { return cedula; }
    public String getNombre()    { return nombre; }
    public String getApellido()  { return apellido; }
    public String getTelefono()  { return telefono; }
    public String getCorreo()    { return correo; }
    public String getDireccion() { return direccion; }
    public String getEstado()    { return estado; }

    // ---- Setters ----
    public void setId(int id)               { this.id        = id; }
    public void setCedula(String cedula)    { this.cedula    = cedula; }
    public void setNombre(String nombre)    { this.nombre    = nombre; }
    public void setApellido(String apellido){ this.apellido  = apellido; }
    public void setTelefono(String telefono){ this.telefono  = telefono; }
    public void setCorreo(String correo)    { this.correo    = correo; }
    public void setDireccion(String dir)    { this.direccion = dir; }
    public void setEstado(String estado)    { this.estado    = estado; }
}
