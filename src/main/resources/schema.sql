-- Tabla de usuarios (ya existe en la BD crediya_db)
-- Esta tabla debe estar creada por el microservicio HU1

-- Verificar si no existe la tabla usuarios, crearla
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(20),
    correo_electronico VARCHAR(150) NOT NULL UNIQUE,
    salario_base DECIMAL(12,2) NOT NULL,
    documento_identidad VARCHAR(20) NOT NULL UNIQUE
);

-- Tabla de estados (según diagrama Pragma)
CREATE TABLE IF NOT EXISTS estados (
    id_estado BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200) NOT NULL
);

-- Tabla de tipos de préstamo (según diagrama Pragma)
CREATE TABLE IF NOT EXISTS tipo_prestamo (
    id_tipo_prestamo BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    monto_minimo DECIMAL(15,2) NOT NULL,
    monto_maximo DECIMAL(15,2) NOT NULL,
    tasa_interes DECIMAL(5,4) NOT NULL,
    validacion_automatica BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla de solicitudes (según diagrama Pragma)
CREATE TABLE IF NOT EXISTS solicitud (
    id_solicitud BIGSERIAL PRIMARY KEY,
    monto DECIMAL(15,2) NOT NULL,
    plazo INTEGER NOT NULL,
    email VARCHAR(150) NOT NULL,
    documento_identidad VARCHAR(20) NOT NULL,
    id_estado BIGINT NOT NULL,
    id_tipo_prestamo BIGINT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL,
    FOREIGN KEY (id_estado) REFERENCES estados(id_estado),
    FOREIGN KEY (id_tipo_prestamo) REFERENCES tipo_prestamo(id_tipo_prestamo)
);

-- Insertar estados de solicitud (datos maestros) solo si no existen
INSERT INTO estados (nombre, descripcion) 
SELECT 'PENDIENTE_REVISION', 'Solicitud pendiente de revisión inicial'
WHERE NOT EXISTS (SELECT 1 FROM estados WHERE nombre = 'PENDIENTE_REVISION');

INSERT INTO estados (nombre, descripcion) 
SELECT 'EN_EVALUACION', 'Solicitud en proceso de evaluación'
WHERE NOT EXISTS (SELECT 1 FROM estados WHERE nombre = 'EN_EVALUACION');

INSERT INTO estados (nombre, descripcion) 
SELECT 'DOCUMENTOS_PENDIENTES', 'Esperando documentos adicionales del cliente'
WHERE NOT EXISTS (SELECT 1 FROM estados WHERE nombre = 'DOCUMENTOS_PENDIENTES');

INSERT INTO estados (nombre, descripcion) 
SELECT 'PRE_APROBADA', 'Solicitud pre-aprobada, esperando aprobación final'
WHERE NOT EXISTS (SELECT 1 FROM estados WHERE nombre = 'PRE_APROBADA');

INSERT INTO estados (nombre, descripcion) 
SELECT 'APROBADA', 'Solicitud aprobada para desembolso'
WHERE NOT EXISTS (SELECT 1 FROM estados WHERE nombre = 'APROBADA');

INSERT INTO estados (nombre, descripcion) 
SELECT 'RECHAZADA', 'Solicitud rechazada'
WHERE NOT EXISTS (SELECT 1 FROM estados WHERE nombre = 'RECHAZADA');

INSERT INTO estados (nombre, descripcion) 
SELECT 'DESEMBOLSADA', 'Préstamo desembolsado al cliente'
WHERE NOT EXISTS (SELECT 1 FROM estados WHERE nombre = 'DESEMBOLSADA');

INSERT INTO estados (nombre, descripcion) 
SELECT 'CANCELADA', 'Solicitud cancelada por el cliente'
WHERE NOT EXISTS (SELECT 1 FROM estados WHERE nombre = 'CANCELADA');

-- Insertar tipos de préstamo (datos maestros según industria colombiana) solo si no existen
INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica)
SELECT 'PERSONAL', 500000.00, 50000000.00, 0.0249, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipo_prestamo WHERE nombre = 'PERSONAL');

INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica)
SELECT 'VEHICULAR', 5000000.00, 200000000.00, 0.0199, FALSE
WHERE NOT EXISTS (SELECT 1 FROM tipo_prestamo WHERE nombre = 'VEHICULAR');

INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica)
SELECT 'HIPOTECARIO', 20000000.00, 800000000.00, 0.0089, FALSE
WHERE NOT EXISTS (SELECT 1 FROM tipo_prestamo WHERE nombre = 'HIPOTECARIO');

INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica)
SELECT 'EDUCATIVO', 1000000.00, 30000000.00, 0.0149, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tipo_prestamo WHERE nombre = 'EDUCATIVO');

INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica)
SELECT 'EMPRESARIAL', 10000000.00, 500000000.00, 0.0299, FALSE
WHERE NOT EXISTS (SELECT 1 FROM tipo_prestamo WHERE nombre = 'EMPRESARIAL');

-- Insertar algunos usuarios de prueba solo si no existen (compatibilidad con HU1)
INSERT INTO usuarios (nombres, apellidos, fecha_nacimiento, direccion, telefono, correo_electronico, salario_base, documento_identidad) 
SELECT 'Juan Carlos', 'Pérez García', '1990-05-15', 'Calle 123 #45-67', '+57 300 123 4567', 'juan.perez@email.com', 2500000.00, '12345678'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE documento_identidad = '12345678');

INSERT INTO usuarios (nombres, apellidos, fecha_nacimiento, direccion, telefono, correo_electronico, salario_base, documento_identidad) 
SELECT 'María Fernanda', 'López Rodríguez', '1988-03-22', 'Carrera 45 #123-89', '+57 310 987 6543', 'maria.lopez@email.com', 3200000.00, '87654321'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE documento_identidad = '87654321');

INSERT INTO usuarios (nombres, apellidos, fecha_nacimiento, direccion, telefono, correo_electronico, salario_base, documento_identidad) 
SELECT 'Carlos Andrés', 'Martínez Silva', '1992-11-08', 'Avenida 68 #25-14', '+57 320 456 7890', 'carlos.martinez@email.com', 2800000.00, '11223344'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE documento_identidad = '11223344');
