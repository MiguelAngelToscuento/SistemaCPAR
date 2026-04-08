-- =========================================================
-- 1. CREACIÓN Y USO DE LA BASE DE DATOS
-- =========================================================
CREATE DATABASE IF NOT EXISTS sistema_agua_texcalac;
USE sistema_agua_texcalac;

-- =========================================================
-- 2. TABLAS DE CATÁLOGOS (Sin dependencias)
-- =========================================================

-- Tabla para los usuarios del sistema (los que cobran)
CREATE TABLE administradores (
    id_admin INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL, 
    correo VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

-- Catálogo de calles para evitar errores de escritura al registrar
CREATE TABLE cat_calles (
    id_calle INT AUTO_INCREMENT PRIMARY KEY,
    nombre_calle VARCHAR(100) UNIQUE NOT NULL
);

-- Catálogo de los tipos de servicio y sus costos
CREATE TABLE cat_tipo_servicios (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    nombre_servicio VARCHAR(50) NOT NULL,
    tarifa DECIMAL(10,2) NOT NULL
);

-- Insertamos los datos base para el catálogo de servicios de una vez
INSERT INTO cat_tipo_servicios (nombre_servicio, tarifa) VALUES
('Hogar', 100.00),        
('Empresarial', 300.00),
('Institucional', 500.00);

-- =========================================================
-- 3. TABLAS PRINCIPALES (Usuarios y sus Tomas de Agua)
-- =========================================================

-- Aquí guardamos los datos personales de la persona física
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(50) NOT NULL,
    apellido_materno VARCHAR(50),
    fecha_registro DATE NOT NULL
);

-- Aquí vinculamos a la persona con el servicio y su folio físico
CREATE TABLE cuentas_servicio (
    folio_tarjeta INT PRIMARY KEY, -- No es auto_increment, tú asignas el número del cartón físico
    id_usuario INT NOT NULL,
    id_calle INT NOT NULL,
    numero_casa VARCHAR(20) NOT NULL,
    codigo_postal VARCHAR(10) NOT NULL,
    id_servicio INT NOT NULL,
    descuento_inapam BOOLEAN DEFAULT FALSE,
    estatus_cuenta ENUM('Activo', 'Suspendido') DEFAULT 'Activo',
    
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_calle) REFERENCES cat_calles(id_calle),
    FOREIGN KEY (id_servicio) REFERENCES cat_tipo_servicios(id_servicio)
);

-- =========================================================
-- 4. TABLAS DE OPERACIÓN (Transacciones y Recibos)
-- =========================================================

-- El encabezado del ticket o comprobante de pago general
CREATE TABLE pagos (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    folio_tarjeta INT NOT NULL,
    id_admin INT NOT NULL, 
    fecha_pago DATETIME DEFAULT CURRENT_TIMESTAMP,
    monto_total DECIMAL(10,2) NOT NULL,
    
    FOREIGN KEY (folio_tarjeta) REFERENCES cuentas_servicio(folio_tarjeta),
    FOREIGN KEY (id_admin) REFERENCES administradores(id_admin)
);

-- El desglose exacto de qué meses se pagaron en ese ticket
CREATE TABLE detalle_pago (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_pago INT NOT NULL,
    periodo_cubierto DATE NOT NULL, -- Se guarda el primer día del mes pagado, Ej: '2026-01-01' para Enero 2026
    monto_aplicado DECIMAL(10,2) NOT NULL,
    
    FOREIGN KEY (id_pago) REFERENCES pagos(id_pago) ON DELETE CASCADE
);