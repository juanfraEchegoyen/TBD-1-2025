
-- extension de postgis
CREATE EXTENSION IF NOT EXISTS postgis;

-- Repartidor
CREATE TABLE IF NOT EXISTS Repartidor (
    rut_repartidor VARCHAR(15) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    nombre_repartidor VARCHAR(100) NOT NULL,
    telefono VARCHAR(12),
    puntuacion_promedio INT,
    cantidad_entregas INT,
    distancia_recorrida DOUBLE PRECISION DEFAULT 0.0,
    ubicacion geometry(Point, 4326)
);

-- Cliente
CREATE TABLE IF NOT EXISTS Cliente (
    rut_cliente VARCHAR(15) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    nombre_cliente VARCHAR(100) NOT NULL,
    telefono VARCHAR(12),
    direccion VARCHAR(255),
    comuna VARCHAR(100) NOT NULL,
    ubicacion geometry(Point, 4326)
);

-- Empresa asociada
CREATE TABLE IF NOT EXISTS EmpresaAsociada (
	rut_empresa VARCHAR(15) PRIMARY KEY,
	nombre_empresa VARCHAR(100) NOT NULL,
	ubicacion geometry(Point, 4326)
);

-- Puntuacion
CREATE TABLE IF NOT EXISTS Puntuacion (
	id_puntuacion SERIAL PRIMARY KEY,
	puntaje INT,
	comentario VARCHAR(200),
	rut_repartidor VARCHAR(15) NOT NULL,
	FOREIGN KEY (rut_repartidor) REFERENCES Repartidor(rut_repartidor)
);

-- Pedido
CREATE TABLE IF NOT EXISTS Pedido (
	id_pedido SERIAL PRIMARY KEY,
	estado_entrega VARCHAR(50),
	prioridad_pedido VARCHAR(50),
	problema_critico BOOLEAN,
	rutas_estimadas geometry(LineString, 4326),
	rut_cliente VARCHAR(15) NOT NULL,
	rut_empresa VARCHAR(15) NOT NULL,
	rut_repartidor VARCHAR(15) NOT NULL,
	FOREIGN KEY (rut_cliente) REFERENCES Cliente(rut_cliente),
	FOREIGN KEY (rut_empresa) REFERENCES EmpresaAsociada(rut_empresa),
	FOREIGN KEY (rut_repartidor) REFERENCES Repartidor(rut_repartidor)
);

-- Producto (modificada para incluir rut_empresa)
CREATE TABLE IF NOT EXISTS Producto (
	id_producto SERIAL PRIMARY KEY,
	nombre_producto VARCHAR(100) NOT NULL,
	precio INT,
	categoria VARCHAR(100),
	tipo_producto VARCHAR(50),
	stock INT,
	rut_empresa VARCHAR(15),
	FOREIGN KEY (rut_empresa) REFERENCES EmpresaAsociada(rut_empresa)
);

-- Detalle de pedido
CREATE TABLE IF NOT EXISTS DetallePedido (
	id_detalle SERIAL PRIMARY KEY,
	precio_total INT,
	tiempo_entrega INT,
	fecha_entrega DATE,
	cantidad INT,
	id_pedido INT,
	id_producto INT,
	FOREIGN KEY (id_pedido) REFERENCES Pedido(id_pedido),
	FOREIGN KEY (id_producto) REFERENCES Producto(id_producto)
);

-- Medio de pago
CREATE TABLE IF NOT EXISTS MedioDePago (
	id_mediodepago SERIAL PRIMARY KEY,
	nombre_mediodepago VARCHAR(100) NOT NULL,
	rut_cliente VARCHAR(15) NOT NULL,
	id_pedido INT,
	FOREIGN KEY (rut_cliente) REFERENCES Cliente(rut_cliente),
	FOREIGN KEY (id_pedido) REFERENCES Pedido(id_pedido)
);

-- Notificacion
CREATE TABLE IF NOT EXISTS Notificacion (
    id_notificacion SERIAL PRIMARY KEY,
    mensaje TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_pedido INT,
    FOREIGN KEY (id_pedido) REFERENCES Pedido(id_pedido)
);

-- punto de interes
CREATE TABLE IF NOT EXISTS PuntoInteres (
    id_punto_interes SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    descripcion TEXT,
    ubicacion geometry(Point, 4326)
);

-- zona de cobertura
CREATE TABLE IF NOT EXISTS ZonaCobertura (
    id_zona_cobertura SERIAL PRIMARY KEY,
    comuna VARCHAR(100) NOT NULL,
    zona VARCHAR(100),
    descripcion TEXT,
    rut_empresa VARCHAR(15),
    area_cobertura geometry(Multipolygon, 4326),
    FOREIGN KEY (rut_empresa) REFERENCES EmpresaAsociada(rut_empresa)
);

-- indices para consultas geoespaciales
CREATE INDEX IF NOT EXISTS idx_repartidor_ubicacion ON Repartidor USING GIST (ubicacion);
CREATE INDEX IF NOT EXISTS idx_cliente_ubicacion ON Cliente USING GIST (ubicacion);
CREATE INDEX IF NOT EXISTS idx_empresa_ubicacion ON EmpresaAsociada USING GIST (ubicacion);
CREATE INDEX IF NOT EXISTS idx_punto_interes_ubicacion ON PuntoInteres USING GIST (ubicacion);
CREATE INDEX IF NOT EXISTS idx_zona_cobertura_area ON ZonaCobertura USING GIST (area_cobertura);
CREATE INDEX IF NOT EXISTS idx_pedido_rutas ON Pedido USING GIST (rutas_estimadas);
