# VitalSched
VitalSched es un aplicacion de escritorio interactiva e intuitiva que te permite adminitrar tu clinica.
---

## Características
- **Administra citas**: registra, edita, elimina, crea y valida horarios.
- **Gestiona Doctores y Pacientes**: registra, edita, elimina y crea.
- **Ingresa sesion**: con tus credenciales.

---

## Arquitectura: MVC con DAO y servicio
Src

  |--java
  
  |  |--AgendamientoCitasMédicas/src/main/java/com/esfot/epn/proyectotalleres
  
  |    	|--modelo
  
  |    	|--servicio
  
  |--resources
  
- **Vista**:

![img.png](imagenes/Vista.png)
- **Controlador**:

![img_1.png](imagenes/Controlador.png)
- **Modelo**:

![img_2.png](imagenes/Modelo.png)
- **Servicio**:

![img_2.png](imagenes/Servicio.png)

---
## Clases principales
- **Cita**

![alt text](imagenes/Cita.png)

- **Paciente**

![alt text](imagenes/Paciente.png)

- **Doctor**

![alt text](imagenes/Doctor.png)

- **Usuario**

![alt text](imagenes/Usuario.png)
---

## Requisitos
- **Java Development Kit (JDK)**: 17 o superior.
- **MySQL Server**: 8.0 o superior. Tambien puede usarse el servicio de MySQL de xammp.
- **IDE de preferencia**: Compatible con Java

---

## Interfaz Grafica
- **Login**

![alt text](imagenes/Login.png)

- **Agendamiento de citas**

![alt text](imagenes/Agendamiento.png)

- **Historial de citas**

![alt text](imagenes/Historial.png)

---

## Tecnologias
|Componente|Tecnologia|Proposito|
|----------|----------|---------|
|Lenguaje de programación|Java 17|Desarrollo de la lógica de la aplicación.|
|Gestor de proyectos|Apache Maven|Administración de dependencias, compilación y empaquetado del proyecto.|
|Interfaz gráfica|JavaFX 21.0.6|Desarrollo de la interfaz de usuario.|
|Diseño de interfaces|JavaFX FXML|Separación del diseño gráfico de la lógica de la aplicación.|
|Base de datos|MySQL|Almacenamiento de la información del sistema.|
|Conector de base de datos|MySQL Connector/J 9.4.0|Comunicación entre la aplicación Java y MySQL mediante JDBC.|
|Compilador|Maven Compiler Plugin 3.13.0|Compilación del proyecto utilizando Java 17.|
|Ejecución JavaFX|JavaFX Maven Plugin 0.0.8|Ejecución y generación de la aplicación JavaFX desde Maven.|

---

## Licencia
Proyecto académico — EPN (Escuela Politécnica Nacional)

---
##Desarrolladores
- **Emilio Gavilánez**
- ** Claudia Coello**
- **Dennes Molina**
- **Sandy Tanicuchi**
