-- 1. CREACIÓN DE BASE DE DATOS
-- DROP DATABASE IF EXISTS sistema_agua_texcalac; -- Descomentar si se necesita borrarla desde cero
CREATE DATABASE IF NOT EXISTS sistema_agua_texcalac;
USE sistema_agua_texcalac;

-- 2. TABLAS DE CATÁLOGOS Y ADMINISTRACIÓN
CREATE TABLE administradores (
                                 id_admin INT AUTO_INCREMENT PRIMARY KEY,
                                 correo VARCHAR(100) UNIQUE NOT NULL,
                                 password_hash VARCHAR(255) NOT NULL,
                                 activo TINYINT(1) DEFAULT 1
);

CREATE TABLE cat_calles (
                            id_calle INT AUTO_INCREMENT PRIMARY KEY,
                            nombre_calle VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE cat_tipo_servicios (
                                    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
                                    nombre_servicio VARCHAR(50) NOT NULL,
                                    tarifa DECIMAL(10,2) NOT NULL
);

-- 3. TABLA PRINCIPAL: CUENTAS DE SERVICIO
CREATE TABLE cuentas_servicio (
                                  folio_tarjeta VARCHAR(50) PRIMARY KEY,
                                  nombres VARCHAR(100) NOT NULL,
                                  apellido_paterno VARCHAR(50) NOT NULL,
                                  apellido_materno VARCHAR(50),
                                  fecha_registro DATE NOT NULL,
                                  id_calle INT NOT NULL,
                                  numero_exterior VARCHAR(20) NOT NULL,
                                  numero_interior VARCHAR(20) DEFAULT NULL,
                                  codigo_postal VARCHAR(10) NOT NULL,
                                  id_servicio INT NOT NULL,
                                  descuento_inapam TINYINT(1) DEFAULT 0,
                                  estatus_cuenta INT DEFAULT 1,
                                  FOREIGN KEY (id_calle) REFERENCES cat_calles(id_calle),
                                  FOREIGN KEY (id_servicio) REFERENCES cat_tipo_servicios(id_servicio)
);

-- 4. TABLAS DE OPERACIÓN (Pagos y Seguridad)
CREATE TABLE pagos (
                       id_pago INT AUTO_INCREMENT PRIMARY KEY,
                       folio_tarjeta VARCHAR(50) NOT NULL,
                       id_admin INT NOT NULL,
                       fecha_pago DATETIME DEFAULT CURRENT_TIMESTAMP,
                       monto_total DECIMAL(10,2) NOT NULL,
                       FOREIGN KEY (folio_tarjeta) REFERENCES cuentas_servicio(folio_tarjeta),
                       FOREIGN KEY (id_admin) REFERENCES administradores(id_admin)
);

CREATE TABLE detalle_pago (
                              id_detalle INT AUTO_INCREMENT PRIMARY KEY,
                              id_pago INT NOT NULL,
                              periodo_cubierto DATE NOT NULL,
                              monto_aplicado DECIMAL(10,2) NOT NULL,
                              FOREIGN KEY (id_pago) REFERENCES pagos(id_pago) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS password_reset_tokens (
                                                     id_token INT AUTO_INCREMENT PRIMARY KEY,
                                                     token VARCHAR(255) NOT NULL UNIQUE,
    id_admin INT NOT NULL,
    fecha_expiracion DATETIME NOT NULL,
    FOREIGN KEY (id_admin) REFERENCES administradores(id_admin) ON DELETE CASCADE
    );

-- 5. INSERCIÓN DE DATOS INICIALES
INSERT INTO cat_calles (nombre_calle) VALUES
                                          ('Avenida Independencia'),
                                          ('Calle 16 de Septiembre'),
                                          ('Calle Juárez');

INSERT INTO cat_tipo_servicios (nombre_servicio, tarifa) VALUES
                                                             ('Hogar', 60.00),
                                                             ('Fraccionamiento', 80.00),
                                                             ('Tubo general', 120.00);