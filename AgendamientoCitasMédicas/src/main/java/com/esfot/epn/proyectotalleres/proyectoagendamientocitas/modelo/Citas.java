package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

/**
 * Representa una cita médica tal como llega del JOIN en la BD.
 *
 * Fase 2 – Bloque 1:
 *  - Se agregan idPacienteRef e idDoctorRef para control de propiedad por rol.
 *  - Se agrega notasMedicas para el diagnóstico registrado durante la atención.
 */
public class Citas {

    private int    id;
    private int    idPacienteRef;   // control de propiedad: "¿es cita del cliente logueado?"
    private int    idDoctorRef;     // filtro para la vista del médico
    private String paciente;        // nombre completo (del JOIN)
    private String doctor;          // nombre completo (del JOIN)
    private String fecha;           // formato yyyy-MM-dd
    private String hora;            // formato HH:mm
    private String motivo;
    private String notasMedicas;    // diagnóstico registrado por el médico
    private String estadoCita;      // nombre desde la tabla ESTADOS

    /** Constructor completo — usado por los DAOs con resultado de JOIN. */
    public Citas(int id, int idPacienteRef, int idDoctorRef,
                 String paciente, String doctor,
                 String fecha, String hora, String motivo,
                 String notasMedicas, String estadoCita) {
        this.id            = id;
        this.idPacienteRef = idPacienteRef;
        this.idDoctorRef   = idDoctorRef;
        this.paciente      = paciente;
        this.doctor        = doctor;
        this.fecha         = fecha;
        this.hora          = hora;
        this.motivo        = motivo;
        this.notasMedicas  = notasMedicas;
        this.estadoCita    = estadoCita;
    }

    /**
     * Constructor reducido para datos mock en controladores.
     * Compatibilidad temporal — se elimina en el Bloque 3 al limpiar los mocks.
     */
    public Citas(int id, String paciente, String doctor,
                 String fecha, String hora, String motivo, String estadoCita) {
        this(id, 0, 0, paciente, doctor, fecha, hora, motivo, null, estadoCita);
    }

    // ---- Getters ----
    public int    getId()            { return id; }
    public int    getIdPacienteRef() { return idPacienteRef; }
    public int    getIdDoctorRef()   { return idDoctorRef; }
    public String getPaciente()      { return paciente; }
    public String getDoctor()        { return doctor; }
    public String getFecha()         { return fecha; }
    public String getHora()          { return hora; }
    public String getMotivo()        { return motivo; }
    public String getNotasMedicas()  { return notasMedicas; }
    public String getEstadoCita()    { return estadoCita; }

    // ---- Setters (para actualización de estado y notas desde controladores) ----
    public void setEstadoCita(String estadoCita)  { this.estadoCita  = estadoCita; }
    public void setMotivo(String motivo)          { this.motivo      = motivo; }
    public void setNotasMedicas(String notas)     { this.notasMedicas = notas; }
}
