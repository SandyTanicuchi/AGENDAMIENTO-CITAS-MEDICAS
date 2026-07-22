module com.esfot.epn.proyectotalleres.proyectoagendamientocitas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Permite usar JDBC y conectarte a XAMPP

    // Agrega estas dos líneas
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    // Abre y exporta el paquete principal para las vistas FXML y controladores
    opens com.esfot.epn.proyectotalleres.proyectoagendamientocitas to javafx.fxml;
    exports com.esfot.epn.proyectotalleres.proyectoagendamientocitas;

    // 🔥 AGREGA ESTAS LÍNEAS PARA TU CAPA MODELO 🔥
    // Da permiso a 'javafx.base' para que las TableView puedan leer los atributos de tus clases mediante reflexión
    opens com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo to javafx.base, javafx.fxml;
    exports com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;
}