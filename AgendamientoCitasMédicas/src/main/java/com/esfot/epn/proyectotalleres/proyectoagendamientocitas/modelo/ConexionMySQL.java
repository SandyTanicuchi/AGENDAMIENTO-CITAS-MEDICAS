package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Provee conexiones JDBC a MySQL leyendo las credenciales desde config.properties.
 * Clase utilitaria: solo abre conexiones, no las gestiona.
 *
 * Corrige H-2 (credenciales hardcodeadas) y H-12 (nombre incorrecto de ConexioDashboard).
 *
 * Uso: try (Connection conn = ConexionMySQL.conectar()) { ... }
 */
public final class ConexionMySQL {

    private static final String URL;
    private static final String USUARIO;
    private static final String CLAVE;

    static {
        Properties props = cargarPropiedades();
        URL     = props.getProperty("db.url",     "jdbc:mysql://localhost:3306/CITAS_MEDICAS");
        USUARIO = props.getProperty("db.usuario", "root");
        CLAVE   = props.getProperty("db.clave",   "");
    }

    private ConexionMySQL() {}

    private static Properties cargarPropiedades() {
        Properties props = new Properties();
        try (InputStream entrada = ConexionMySQL.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (entrada == null) {
                System.err.println("[ConexionMySQL] config.properties no encontrado en classpath. " +
                                   "Verifique src/main/resources/config.properties");
                return props;
            }
            props.load(entrada);
        } catch (IOException e) {
            System.err.println("[ConexionMySQL] Error leyendo config.properties: " + e.getMessage());
        }
        return props;
    }

    /**
     * Abre y devuelve una nueva conexión a la BD.
     * El llamador es responsable de cerrarla con try-with-resources.
     *
     * @return Connection activa, o null si el motor no está disponible.
     */
    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (SQLException e) {
            System.err.println("[ConexionMySQL] Fallo al conectar a la BD: " + e.getMessage());
            return null;
        }
    }
}
