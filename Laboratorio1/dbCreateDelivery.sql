-- TABLAS

-- Repartidor
CREATE TABLE IF NOT EXISTS Repartidor (
	rut_repartidor VARCHAR(15) PRIMARY KEY,
	nombre_repartidor VARCHAR(100) NOT NULL,
	telefono VARCHAR(12),
	puntuacion_promedio INT,
	cantidad_entregas INT
);

-- Cliente
CREATE TABLE IF NOT EXISTS Cliente (
	rut_cliente VARCHAR(15) PRIMARY KEY,
	nombre_cliente VARCHAR(100) NOT NULL,
	telefono VARCHAR(12),
	direccion VARCHAR(255),
	comuna VARCHAR(100) NOT NULL
);

-- Empresa asociada
CREATE TABLE IF NOT EXISTS EmpresaAsociada (
	rut_empresa VARCHAR(15) PRIMARY KEY,
	nombre_empresa VARCHAR(100) NOT NULL
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
	rut_cliente VARCHAR(15) NOT NULL,
	rut_empresa VARCHAR(15) NOT NULL,
	rut_repartidor VARCHAR(15) NOT NULL,
	FOREIGN KEY (rut_cliente) REFERENCES Cliente(rut_cliente),
	FOREIGN KEY (rut_empresa) REFERENCES EmpresaAsociada(rut_empresa),
	FOREIGN KEY (rut_repartidor) REFERENCES Repartidor(rut_repartidor)
);

-- Producto
CREATE TABLE IF NOT EXISTS Producto (
	id_producto SERIAL PRIMARY KEY,
	nombre_producto VARCHAR(100) NOT NULL,
	precio INT,
	categoria VARCHAR(100),
	tipo_producto VARCHAR(50),
	stock INT
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

-- Usuario
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);