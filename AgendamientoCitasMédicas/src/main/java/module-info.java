module com.esfot.epn.proyectotalleres.proyectoagendamientocitas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Permite usar JDBC y conectarte a XAMPP
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    opens com.esfot.epn.proyectotalleres.proyectoagendamientocitas to javafx.fxml;
    exports com.esfot.epn.proyectotalleres.proyectoagendamientocitas;
    opens com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo to javafx.base, javafx.fxml;
    exports com.esfot.epn.proyectotalleres.proyectoagendamientocitas.modelo;
}
