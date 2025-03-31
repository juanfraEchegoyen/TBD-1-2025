-- eliminar la base de datos si existe
DROP DATABASE IF EXISTS escuela_db;


CREATE DATABASE escuela_db
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'English_United States.1252'
    LC_CTYPE = 'English_United States.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

-- conectar a la base de datos creada
\c escuela_db;

-- TABLAS

-- COMUNA
CREATE TABLE IF NOT EXISTS Comuna (
    id_comuna SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);


-- COLEGIO
CREATE TABLE IF NOT EXISTS Colegio (
    id_colegio SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL, 
    direccion VARCHAR(100),
    id_comuna INT NOT NULL,
    FOREIGN KEY (id_comuna) REFERENCES Comuna(id_comuna)
);

-- EMPLEADO
CREATE TABLE IF NOT EXISTS Empleado (
    rut_empleado VARCHAR(15) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    rol VARCHAR(50),
    sueldo INT,
    dir_casa VARCHAR(255),
    id_comuna INT NOT NULL,
    id_colegio INT,
    FOREIGN KEY (id_comuna) REFERENCES Comuna(id_comuna),
    FOREIGN KEY (id_colegio) REFERENCES Colegio(id_colegio)
);

-- PROFESOR
CREATE TABLE IF NOT EXISTS Profesor (
    id_profesor SERIAL PRIMARY KEY,
    horas_clase SMALLINT,
    rut_empleado VARCHAR(15) NOT NULL UNIQUE,
    FOREIGN KEY (rut_empleado) REFERENCES Empleado(rut_empleado)
);

-- GRADO
CREATE TABLE IF NOT EXISTS Grado (
    id_grado SERIAL PRIMARY KEY,
    nombre_grado VARCHAR(50) NOT NULL UNIQUE
);

-- CURSO
CREATE TABLE IF NOT EXISTS Curso (
    id_curso SERIAL PRIMARY KEY,
    anio INT NOT NULL CHECK (anio > 1900 AND anio < 2100),
    id_grado INT NOT NULL,
    id_profesor_jefe INT,
    id_colegio INT NOT NULL,
    FOREIGN KEY (id_grado) REFERENCES Grado(id_grado),
    FOREIGN KEY (id_profesor_jefe) REFERENCES Profesor(id_profesor),
    FOREIGN KEY (id_colegio) REFERENCES Colegio(id_colegio)
);

-- PROFCURSO
CREATE TABLE IF NOT EXISTS ProfCurso (
    id_profesor INT NOT NULL,
    id_curso INT NOT NULL,
    flg_profesor_jefe BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id_profesor, id_curso),
    FOREIGN KEY (id_profesor) REFERENCES Profesor(id_profesor),
    FOREIGN KEY (id_curso) REFERENCES Curso(id_curso)
);

-- APODERADO
CREATE TABLE IF NOT EXISTS Apoderado (
    rut_apoderado VARCHAR(15) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    dir_casa VARCHAR(100),
    telefono VARCHAR(12),
    es_padre BOOLEAN,
    id_comuna INT NOT NULL,
    FOREIGN KEY (id_comuna) REFERENCES Comuna(id_comuna)
);

-- ALUMNO
CREATE TABLE IF NOT EXISTS Alumno (
    rut_alumno VARCHAR(15) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    dir_casa VARCHAR(100),
    rut_apoderado VARCHAR(15) NOT NULL,
    id_comuna INT NOT NULL,
    id_colegio INT NOT NULL,
    FOREIGN KEY (id_comuna) REFERENCES Comuna(id_comuna),
    FOREIGN KEY (rut_apoderado) REFERENCES Apoderado(rut_apoderado),
    FOREIGN KEY (id_colegio) REFERENCES Colegio(id_colegio)
);

-- ALUCURSO
CREATE TABLE IF NOT EXISTS AluCurso (
    id_alumnoCurso SERIAL PRIMARY KEY,
    id_curso INT NOT NULL,
    rut_alumno VARCHAR(15) NOT NULL,
    UNIQUE (id_curso, rut_alumno),
    FOREIGN KEY (id_curso) REFERENCES Curso(id_curso),
    FOREIGN KEY (rut_alumno) REFERENCES Alumno(rut_alumno)
);

-- ASISTENCIA
CREATE TABLE IF NOT EXISTS Asistencia (
    id_asistencia SERIAL PRIMARY KEY,
    fecha DATE NOT NULL DEFAULT CURRENT_DATE,
    flg_presente BOOLEAN NOT NULL,
    id_alumnoCurso INT NOT NULL,
    FOREIGN KEY (id_alumnoCurso) REFERENCES AluCurso(id_alumnoCurso)
);