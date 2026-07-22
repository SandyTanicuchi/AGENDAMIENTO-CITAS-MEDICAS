package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

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
    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (SQLException e) {
            System.err.println("[ConexionMySQL] Fallo al conectar a la BD: " + e.getMessage());
            return null;
        }
    }
}
