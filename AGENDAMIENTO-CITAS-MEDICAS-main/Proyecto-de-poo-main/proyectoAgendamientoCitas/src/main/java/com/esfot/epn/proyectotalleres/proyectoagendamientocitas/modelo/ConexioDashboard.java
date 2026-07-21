package com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexioDashboard {
    private final String SERVIDOR = "jdbc:mysql://localhost:3306/CITAS_MEDICAS";
    private final String USUARIO = "root";
    private final String CLAVE = "";

    public Connection conectar() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(SERVIDOR, USUARIO, CLAVE);
            System.out.println("Conexión establecida con éxito a CITAS_MEDICAS");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error en la conexio a CITAS_MEDICAS" + e.getMessage());
        }
        return conn;
    }
}
