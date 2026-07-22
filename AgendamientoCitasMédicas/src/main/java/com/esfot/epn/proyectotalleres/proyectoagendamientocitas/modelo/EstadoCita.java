package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

public enum EstadoCita {

    PENDIENTE   ("Pendiente"),
    CONFIRMADA  ("Confirmada"),
    EN_ATENCION ("En_Atencion"),
    COMPLETADA  ("Completada"),
    CANCELADA   ("Cancelada"),
    NO_ASISTIO  ("No_Asistio");
    private final String nombreEnBD;

    EstadoCita(String nombreEnBD) {
        this.nombreEnBD = nombreEnBD;
    }

    public String getNombreEnBD() { return nombreEnBD; }

    /**Convierte el String de la BD al enum correspondiente.
     * @throws IllegalArgumentException si el valor no coincide con ningún estado conocido
     */
    public static EstadoCita desdeBD(String nombre) {
        for (EstadoCita e : values()) {
            if (e.nombreEnBD.equalsIgnoreCase(nombre)) return e;
        }
        throw new IllegalArgumentException("Estado de cita no reconocido: '" + nombre + "'");
    }
    public boolean puedeTransicionarA(EstadoCita siguiente) {
        if (this == siguiente) return true;
        return switch (this) {
            case PENDIENTE    -> true; // Desde PENDIENTE se puede pasar a cualquier otro estado
            case CONFIRMADA   -> siguiente == EN_ATENCION || siguiente == COMPLETADA || siguiente == CANCELADA || siguiente == NO_ASISTIO;
            case EN_ATENCION  -> siguiente == COMPLETADA  || siguiente == NO_ASISTIO || siguiente == CANCELADA;
            case COMPLETADA, CANCELADA, NO_ASISTIO -> false; // Estados finales, no regresan
        };
    }

    @Override
    public String toString() { return nombreEnBD; }
}
