package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import java.sql.Connection;

@Deprecated(forRemoval = true)
public class ConexioDashboard {

    /** @deprecated Usar directamente {@link ConexionMySQL#conectar()} */
    @Deprecated(forRemoval = true)
    public Connection conectar() {
        return ConexionMySQL.conectar();
    }
}
