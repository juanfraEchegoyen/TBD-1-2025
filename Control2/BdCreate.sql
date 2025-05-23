-- Habilitar PostGIS (si no esta habilitado)
CREATE EXTENSION IF NOT EXISTS postgis;

-- Tabla de ubicaciones - mapa base de la región metropolitana
CREATE TABLE ubicacion (
    id_ubicacion SERIAL PRIMARY KEY,
    region VARCHAR(100) NOT NULL,
    provincia VARCHAR(100) NOT NULL,
    comuna VARCHAR(100) NOT NULL,
    coordenadas geometry(MultiPolygon,4326) NOT NULL
);

-- Tabla de sectores - donde se encuentran las tareas
CREATE TABLE sector (
    id_sector SERIAL PRIMARY KEY,
    asignacion VARCHAR(100) NOT NULL, -- tarea/usuario
    comuna VARCHAR(100) NOT NULL,
    calle VARCHAR(100) NOT NULL,
    ubicacion geometry(Point,4326) NOT NULL
);

-- Tabla de usuarios
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    id_sector INTEGER,
    FOREIGN KEY (id_sector) REFERENCES sector(id_sector)
);

-- Tabla de tareas
CREATE TABLE tarea (
    id_tarea SERIAL PRIMARY KEY,
    titulo VARCHAR(30) NOT NULL,
    descripcion VARCHAR(255),
    fecha_vencimiento DATE NOT NULL, -- YYYY-MM-DD
    estado VARCHAR(30) NOT NULL,
    id_usuario INTEGER NOT NULL,
    id_sector INTEGER,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_sector) REFERENCES sector(id_sector)
);