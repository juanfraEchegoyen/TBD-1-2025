CREATE EXTENSION postgis;

CREATE TABLE Usuario (
    idUsuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    password VARCHAR(255),
    idSector BIGINT
);

CREATE TABLE Ubicacion (
    idUbicacion SERIAL PRIMARY KEY,
    region VARCHAR(100),
    provincia VARCHAR(100),
    comuna VARCHAR(100),
    coordenadas GEOMETRY(Point, 4326)
);

CREATE TABLE Sector (
    idSector SERIAL PRIMARY KEY,
    asignacion VARCHAR(100),
    comuna VARCHAR(100),
    calle VARCHAR(255),
    ubicacion GEOMETRY(Point, 4326)
);

CREATE TABLE Tarea (
    idTarea SERIAL PRIMARY KEY,
    titulo VARCHAR(255),
    descripcion TEXT,
    fechaVencimiento DATE,
    estado VARCHAR(50),
    idUsuario BIGINT REFERENCES Usuario(idUsuario),
    idSector BIGINT REFERENCES Sector(idSector)
);

CREATE INDEX idx_sector_location ON Sector USING GIST(ubicacion);
CREATE INDEX idx_ubicacion_coordinates ON Ubicacion USING GIST(coordenadas);
CREATE INDEX idx_tarea_sector ON Tarea(idSector);
CREATE INDEX idx_tarea_usuario ON Tarea(idUsuario);
