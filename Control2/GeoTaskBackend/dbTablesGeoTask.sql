-- Habilitar PostGIS (si no esta habilitado)
CREATE EXTENSION IF NOT EXISTS postgis;

-- Tabla de usuarios
CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    ubicacion geometry(Point,4326) NOT NULL
);

-- Tabla de sectores
CREATE TABLE sector (
    id_sector SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ubicacion geometry(Point,4326) NOT NULL
);

-- Tabla de tareas
CREATE TABLE tarea (
    id_tarea SERIAL PRIMARY KEY,
    titulo VARCHAR(30) NOT NULL,
    descripcion VARCHAR(255),
    fecha_vencimiento DATE NOT NULL,
    estado VARCHAR(30) NOT NULL,
    id_usuario INTEGER NOT NULL,
    id_sector INTEGER NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_sector) REFERENCES sector(id_sector)
);
