-- Poblar Comuna
INSERT INTO Comuna (nombre) VALUES 
('Ciudad Tokio'),
('Neo-Tokyo'),
('Konoha'),
('Ciudad Mob'),
('Shohoku');

-- Poblar Colegio
INSERT INTO Colegio (nombre, direccion, id_comuna) VALUES
('Academia U.A.', 'Distrito Musutafu', 1),
('Escuela Técnica Tokyo Jujutsu', 'Tokyo Metropolitano', 2),
('Escuela Kunugigaoka', 'Barrio Secreto', 3),
('Escuela Mob Psycho', 'Centro Urbano', 4),
('Instituto Shohoku', 'Kanagawa', 5);

-- Poblar Empleado
INSERT INTO Empleado (rut_empleado, nombre, rol, sueldo, dir_casa, id_comuna, id_colegio) VALUES
('12345678-9', 'Kakashi Hatake', 'Profesor', 800000, 'Konoha 123', 3, 3),
('23456789-0', 'Aizawa Shouta', 'Profesor', 850000, 'Tokyo 456', 1, 1),
('34567890-1', 'Gojo Satoru', 'Profesor', 900000, 'Tokyo 789', 2, 2),
('45678901-2', 'Reigen Arataka', 'Orientador', 700000, 'Ciudad Mob', 4, 4),
('56789012-3', 'Anzai Sensei', 'Entrenador', 750000, 'Shohoku', 5, 5);

-- Poblar Profesor
INSERT INTO Profesor (horas_clase, rut_empleado) VALUES
(20, '12345678-9'),
(25, '23456789-0'),
(30, '34567890-1'),
(35, '45678901-2'),
(40, '56789012-3');

-- Poblar Grado
INSERT INTO Grado (nombre_grado) VALUES 
('Primer Año'),
('Segundo Año'),
('Tercer Año');

-- Poblar Curso
INSERT INTO Curso (anio, id_grado, id_profesor_jefe, id_colegio) VALUES
(2025, 1, 1, 1),
(2025, 2, 2, 2),
(2025, 3, 3, 3),
(2025, 1, 4, 4),
(2025, 2, 5, 5);

-- Poblar ProfCurso
INSERT INTO ProfCurso (id_profesor, id_curso, flg_profesor_jefe) VALUES
(1, 1, TRUE),
(2, 2, TRUE),
(3, 3, TRUE),
(4, 4, TRUE),
(5, 5, TRUE);

-- Poblar Apoderado
INSERT INTO Apoderado (rut_apoderado, nombre, dir_casa, telefono, es_padre, id_comuna) VALUES
('67890123-4', 'Reigen Arataka', 'Ciudad Mob', '555-1234', FALSE, 4),
('78901234-5', 'Gendo Ikari', 'Neo-Tokyo', '555-5678', TRUE, 2),
('89012345-6', 'Hinata Hyuga', 'Konoha', '555-9876', TRUE, 3),
('90123456-7', 'Toshinori Yagi', 'Distrito Musutafu', '555-0000', FALSE, 4),
('01234567-8', 'Hiromi Shiota', 'Shohoku', '555-2468', TRUE, 5),
('41234567-8', 'Rukawa Kaede ', 'Shohoku', '525-2468', FALSE, 5);

-- Poblar Alumno
INSERT INTO Alumno (rut_alumno, nombre, dir_casa, rut_apoderado, id_comuna, id_colegio) VALUES
('11111111-1', 'Ritsu  Kageyama', 'Ciudad Mob', '67890123-4', 4, 4),
('22222222-2', 'Shigeo Kageyama', 'Ciudad Mob', '67890123-4', 4, 4),
('33333333-3', 'Nagisa Shiota', 'Barrio Secreto', '01234567-8', 3, 3),
('44444444-4', 'Itadori yuji', 'Tokyo', '78901234-5', 2, 2),
('55555555-5', 'Hanamichi Sakuragi', 'Shohoku', '01234567-8', 5, 5),
('66666666-6', 'Izuku Midoriya', 'Shohoku', '90123456-7', 1, 1);

-- Poblar AluCurso
INSERT INTO AluCurso (id_curso, rut_alumno) VALUES
(4, '11111111-1'),
(4, '22222222-2'),
(3, '33333333-3'),
(2, '44444444-4'),
(5, '55555555-5'),
(1, '66666666-6');


-- Poblar Asistencia
INSERT INTO Asistencia (fecha, flg_presente, id_alumnoCurso) VALUES
('2025-03-31', TRUE, 1),
('2025-03-31', FALSE, 2),
('2025-03-31', TRUE, 3),
('2025-03-31', TRUE, 4),
('2025-03-31', FALSE, 5),
('2025-03-31', TRUE, 6);