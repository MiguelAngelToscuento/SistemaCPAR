use sistema_agua_texcalac;

DROP TABLE IF EXISTS detalle_pago;
DROP TABLE IF EXISTS pagos;
DROP TABLE IF EXISTS cuentas_servicio;
DROP TABLE IF EXISTS usuarios;

-- NUEVA TABLA UNIFICADA (Tú dictas el folio)
CREATE TABLE cuentas_servicio (
    folio_tarjeta INT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(50) NOT NULL,
    apellido_materno VARCHAR(50),
    fecha_registro DATE NOT NULL,
    
    id_calle INT NOT NULL,
    numero_casa VARCHAR(20) NOT NULL,
    codigo_postal VARCHAR(10) NOT NULL,
    id_servicio INT NOT NULL,
    descuento_inapam BOOLEAN DEFAULT FALSE,
    estatus_cuenta INT DEFAULT 1,
    
    FOREIGN KEY (id_calle) REFERENCES cat_calles(id_calle),
    FOREIGN KEY (id_servicio) REFERENCES cat_tipo_servicios(id_servicio)
);

-- Recreamos las tablas de pagos
CREATE TABLE pagos (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    folio_tarjeta INT NOT NULL,
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