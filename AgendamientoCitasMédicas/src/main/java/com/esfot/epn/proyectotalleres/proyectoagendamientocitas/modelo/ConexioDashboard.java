package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import java.sql.Connection;

/**
 * @deprecated Reemplazada por {@link ConexionMySQL} (corrige H-2 y H-12).
 *             Esta clase delega todas las llamadas a ConexionMySQL y se mantiene
 *             únicamente para que el compilador no falle si existe alguna referencia residual.
 *             Eliminar en cuanto no haya ningún DAO que la referencie.
 */
@Deprecated(forRemoval = true)
public class ConexioDashboard {

    /** @deprecated Usar directamente {@link ConexionMySQL#conectar()} */
    @Deprecated(forRemoval = true)
    public Connection conectar() {
        return ConexionMySQL.conectar();
    }
}
