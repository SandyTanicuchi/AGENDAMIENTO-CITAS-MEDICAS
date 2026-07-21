package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

public class Citas {
    private int id;
    private String paciente;     // String para mostrar el nombre directo en la tabla
    private String doctor;       // String para mostrar el nombre directo en la tabla
    private String fecha;
    private String hora;
    private String motivo;
    private String estadoCita;   // El texto desde la tabla ESTADOS ('Pendiente', etc)

    public Citas(int id, String paciente, String doctor, String fecha, String hora, String motivo, String estadoCita) {
        this.id = id;
        this.paciente = paciente;
        this.doctor = doctor;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.estadoCita = estadoCita;
    }

    public int getId() { return id; }
    public String getPaciente() { return paciente; }
    public String getDoctor() { return doctor; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getMotivo() { return motivo; }
    public String getEstadoCita() { return estadoCita; }
}
