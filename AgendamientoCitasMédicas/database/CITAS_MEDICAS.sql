-- ============================================================
--   SCRIPT DE CREACIÓN DE BASE DE DATOS: CITAS_MEDICAS
--   Sistema de Agendamiento de Citas Hospitalarias
--   Motor: MySQL 8.x / MariaDB 10.x
-- ============================================================

DROP DATABASE IF EXISTS CITAS_MEDICAS;
CREATE DATABASE CITAS_MEDICAS
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE CITAS_MEDICAS;

-- ------------------------------------------------------------
-- TABLA: ESTADOS
-- Catálogo de estados para las citas médicas.
-- Los valores deben coincidir exactamente con EstadoCita.getNombreEnBD().
-- ------------------------------------------------------------
CREATE TABLE ESTADOS (
    id_estado    INT          NOT NULL AUTO_INCREMENT,
    nombre_estado VARCHAR(50) NOT NULL UNIQUE,
    PRIMARY KEY (id_estado)
) ENGINE=InnoDB;

-- Fase 2 – Bloque 1: se agregan En_Atencion y No_Asistio (requeridos por los requisitos de negocio)
INSERT INTO ESTADOS (nombre_estado) VALUES
    ('Pendiente'),
    ('Confirmada'),
    ('Cancelada'),
    ('Completada'),
    ('En_Atencion'),   -- el médico inicia la atención presencial
    ('No_Asistio');    -- el paciente no se presentó a la cita

-- ------------------------------------------------------------
-- TABLA: ESPECIALIDADES
-- Catálogo de especialidades médicas
-- ------------------------------------------------------------
CREATE TABLE ESPECIALIDADES (
    id_especialidad INT         NOT NULL AUTO_INCREMENT,
    nombre          VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY KEY (id_especialidad)
) ENGINE=InnoDB;

INSERT INTO ESPECIALIDADES (nombre) VALUES
    ('Cardiología'),
    ('Pediatría'),
    ('Dermatología'),
    ('Ginecología'),
    ('Medicina General'),
    ('Traumatología'),
    ('Neurología'),
    ('Oftalmología'),
    ('Odontología');

-- ------------------------------------------------------------
-- TABLA: DOCTORES
-- Personal médico del hospital
-- ------------------------------------------------------------
CREATE TABLE DOCTORES (
    id_doctor    INT          NOT NULL AUTO_INCREMENT,
    nombre       VARCHAR(100) NOT NULL,
    apellido     VARCHAR(100) NOT NULL,
    especialidad VARCHAR(100) NOT NULL,
    telefono     VARCHAR(20),
    correo       VARCHAR(150) UNIQUE,
    estado       VARCHAR(30)  NOT NULL DEFAULT 'Activo',  -- Activo | Inactivo | Vacaciones
    PRIMARY KEY (id_doctor),
    CONSTRAINT chk_estado_doctor CHECK (estado IN ('Activo','Inactivo','Vacaciones'))
) ENGINE=InnoDB;

INSERT INTO DOCTORES (nombre, apellido, especialidad, telefono, correo, estado) VALUES
    ('Fernando', 'Ríos',    'Cardiología',     '0991234567', 'fernando.rios@hospital.com',    'Activo'),
    ('Elena',    'Salazar', 'Pediatría',       '0992345678', 'elena.salazar@hospital.com',    'Activo'),
    ('Javier',   'Mendoza', 'Dermatología',    '0993456789', 'javier.mendoza@hospital.com',   'Activo'),
    ('Gabriela', 'Vargas',  'Ginecología',     '0994567890', 'gabriela.vargas@hospital.com',  'Inactivo'),
    ('Roberto',  'Mora',    'Medicina General','0995678901', 'roberto.mora@hospital.com',     'Activo');

-- ------------------------------------------------------------
-- TABLA: PACIENTES
-- Registro de pacientes del sistema
-- ------------------------------------------------------------
CREATE TABLE PACIENTES (
    id_paciente INT          NOT NULL AUTO_INCREMENT,
    cedula      VARCHAR(20)  NOT NULL UNIQUE,
    nombre      VARCHAR(100) NOT NULL,
    apellido    VARCHAR(100) NOT NULL,
    telefono    VARCHAR(20),
    correo      VARCHAR(150),
    direccion   VARCHAR(250),
    estado      VARCHAR(20)  NOT NULL DEFAULT 'Activo',  -- Activo | Inactivo
    PRIMARY KEY (id_paciente),
    CONSTRAINT chk_estado_paciente CHECK (estado IN ('Activo','Inactivo'))
) ENGINE=InnoDB;

INSERT INTO PACIENTES (cedula, nombre, apellido, telefono, correo, direccion, estado) VALUES
    ('1723456789', 'Juan',   'Pérez',    '0987654321', 'juan.perez@email.com',    'Av. Amazonas N24-12, Quito',  'Activo'),
    ('1712345678', 'María',  'Gómez',    '0998877665', 'maria.gomez@email.com',   'Calle Larga y Solano, Quito', 'Activo'),
    ('1709876543', 'Carlos', 'Andrade',  '0976543210', 'carlos.andrade@email.com','La Prensa y El Inca, Quito',  'Activo'),
    ('1755566778', 'Ana',    'Martínez', '0955511223', 'ana.martinez@email.com',  'Cumbayá, San Juan, Quito',    'Activo');

-- ------------------------------------------------------------
-- TABLA: CITAS
-- Agendamiento de citas médicas
-- Fase 2 – Bloque 1: se agregan notas_medicas (diagnóstico del médico)
--                     y fecha_modificacion (trazabilidad de cambios).
-- ------------------------------------------------------------
CREATE TABLE CITAS (
    id_cita              INT       NOT NULL AUTO_INCREMENT,
    id_paciente          INT       NOT NULL,
    id_doctor            INT       NOT NULL,
    fecha                DATE      NOT NULL,
    hora                 TIME      NOT NULL,
    motivo               TEXT,
    notas_medicas        TEXT,                                      -- diagnóstico/notas del médico durante la atención
    id_estado            INT       NOT NULL DEFAULT 1,              -- FK a ESTADOS; 1=Pendiente por defecto
    fecha_registro       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion   TIMESTAMP NULL     ON UPDATE CURRENT_TIMESTAMP,  -- se actualiza automáticamente
    PRIMARY KEY (id_cita),
    CONSTRAINT fk_cita_paciente FOREIGN KEY (id_paciente) REFERENCES PACIENTES(id_paciente)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_cita_doctor   FOREIGN KEY (id_doctor)   REFERENCES DOCTORES(id_doctor)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_cita_estado   FOREIGN KEY (id_estado)   REFERENCES ESTADOS(id_estado)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uq_doctor_fecha_hora   UNIQUE (id_doctor,   fecha, hora),
    CONSTRAINT uq_paciente_fecha_hora UNIQUE (id_paciente, fecha, hora)
) ENGINE=InnoDB;

INSERT INTO CITAS (id_paciente, id_doctor, fecha, hora, motivo, id_estado) VALUES
    (1, 1, '2026-07-25', '09:00', 'Chequeo general de cardiología',    1),
    (2, 2, '2026-07-25', '10:30', 'Consulta pediátrica de control',    4),
    (3, 3, '2026-07-26', '11:15', 'Consulta dermatológica por acné',   1),
    (4, 4, '2026-07-27', '14:00', 'Control rutinario ginecológico',    3);

-- ------------------------------------------------------------
-- TABLA: USUARIOS
-- Autenticación y control de acceso al sistema.
-- Fase 2 – Bloque 1: se agregan id_paciente e id_doctor para vincular
--   el usuario de sistema con su entidad clínica correspondiente.
--   Esto permite filtrar "mis citas" sin exponer datos de otros.
-- ------------------------------------------------------------
CREATE TABLE USUARIOS (
    id_usuario     INT          NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(150) NOT NULL,
    usuario        VARCHAR(80)  NOT NULL UNIQUE,
    clave          VARCHAR(255) NOT NULL,    -- Fase 2-Seguridad: reemplazar por hash BCrypt
    rol            VARCHAR(30)  NOT NULL,    -- Administrador | Médico | Cliente
    estado         VARCHAR(20)  NOT NULL DEFAULT 'Activo',  -- Activo | Inactivo
    fecha_creacion TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_paciente    INT          NULL,        -- vinculado si rol = Cliente
    id_doctor      INT          NULL,        -- vinculado si rol = Médico
    PRIMARY KEY (id_usuario),
    CONSTRAINT chk_rol_usuario    CHECK (rol    IN ('Administrador','Médico','Cliente')),
    CONSTRAINT chk_estado_usuario CHECK (estado IN ('Activo','Inactivo')),
    CONSTRAINT fk_usuario_paciente FOREIGN KEY (id_paciente) REFERENCES PACIENTES(id_paciente) ON DELETE SET NULL,
    CONSTRAINT fk_usuario_doctor   FOREIGN KEY (id_doctor)   REFERENCES DOCTORES(id_doctor)   ON DELETE SET NULL
) ENGINE=InnoDB;

-- Usuarios de prueba (clave en texto plano – solo para desarrollo)
INSERT INTO USUARIOS (nombre, usuario, clave, rol, estado, id_paciente, id_doctor) VALUES
    ('Administrador Central', 'admin',  'admin123',  'Administrador', 'Activo', NULL, NULL),
    ('Dr. Fernando Ríos',     'medico', 'medico123', 'Médico',        'Activo', NULL, 1),
    ('Juan Pérez',            'juan',   'juan123',   'Cliente',       'Activo', 1,    NULL);

-- ============================================================
-- VISTAS ÚTILES (para consultas frecuentes y reportes)
-- ============================================================

-- Vista completa de citas con nombres legibles y notas médicas
CREATE OR REPLACE VIEW VW_CITAS_COMPLETAS AS
SELECT
    c.id_cita,
    c.id_paciente,
    c.id_doctor,
    CONCAT(p.nombre, ' ', p.apellido) AS paciente,
    p.cedula,
    p.telefono   AS tel_paciente,
    CONCAT(d.nombre, ' ', d.apellido) AS doctor,
    d.especialidad,
    c.fecha,
    c.hora,
    c.motivo,
    c.notas_medicas,
    e.nombre_estado        AS estado,
    c.fecha_registro,
    c.fecha_modificacion
FROM CITAS c
JOIN PACIENTES p ON c.id_paciente = p.id_paciente
JOIN DOCTORES  d ON c.id_doctor   = d.id_doctor
JOIN ESTADOS   e ON c.id_estado   = e.id_estado
ORDER BY c.fecha, c.hora;
