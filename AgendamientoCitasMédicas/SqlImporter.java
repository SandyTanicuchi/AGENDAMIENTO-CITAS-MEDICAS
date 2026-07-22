import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SqlImporter {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=America/Guayaquil&allowMultiQueries=true";
        String user = "root";
        String pass = "admin";

        try {
            System.out.println("Leyendo archivo SQL...");
            File file = new File("database/CITAS_MEDICAS.sql");
            String sqlContent = Files.readString(file.toPath());

            System.out.println("Conectando a MySQL local (root/admin)...");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 Statement stmt = conn.createStatement()) {
                
                System.out.println("Ejecutando script de actualización de Base de Datos...");
                stmt.execute(sqlContent);
                System.out.println("¡BASE DE DATOS ACTUALIZADA CON ÉXITO!");
            }
        } catch (Exception e) {
            System.err.println("Error ejecutando el script: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
