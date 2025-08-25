-- Tabla de usuarios (simulada para validación)
DROP TABLE IF EXISTS usuarios;
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(20),
    correo_electronico VARCHAR(150) NOT NULL UNIQUE,
    salario_base DECIMAL(12,2) NOT NULL,
    documento_identidad VARCHAR(20) NOT NULL UNIQUE
);

-- Tabla de solicitudes de préstamo
DROP TABLE IF EXISTS solicitudes_prestamo;
CREATE TABLE solicitudes_prestamo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    documento_identidad VARCHAR(20) NOT NULL,
    monto DECIMAL(15,2) NOT NULL,
    plazo INTEGER NOT NULL,
    tipo_prestamo VARCHAR(20) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL
);

-- Insertar algunos usuarios de prueba
INSERT INTO usuarios (nombres, apellidos, fecha_nacimiento, direccion, telefono, correo_electronico, salario_base, documento_identidad) 
VALUES 
('Juan Carlos', 'Pérez García', '1990-05-15', 'Calle 123 #45-67', '+57 300 123 4567', 'juan.perez@email.com', 2500000.00, '12345678'),
('María Fernanda', 'López Rodríguez', '1988-03-22', 'Carrera 45 #123-89', '+57 310 987 6543', 'maria.lopez@email.com', 3200000.00, '87654321'),
('Carlos Andrés', 'Martínez Silva', '1992-11-08', 'Avenida 68 #25-14', '+57 320 456 7890', 'carlos.martinez@email.com', 2800000.00, '11223344');
