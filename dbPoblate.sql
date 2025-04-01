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
-- Año 2019
('2019-01-15', TRUE, 1), ('2019-01-15', FALSE, 2), ('2019-01-15', TRUE, 3), ('2019-01-15', TRUE, 4), ('2019-01-15', FALSE, 5), ('2019-01-15', TRUE, 6),
('2019-02-10', TRUE, 1), ('2019-02-10', FALSE, 2), ('2019-02-10', TRUE, 3), ('2019-02-10', TRUE, 4), ('2019-02-10', FALSE, 5), ('2019-02-10', TRUE, 6),
('2019-03-05', TRUE, 1), ('2019-03-05', FALSE, 2), ('2019-03-05', TRUE, 3), ('2019-03-05', TRUE, 4), ('2019-03-05', FALSE, 5), ('2019-03-05', TRUE, 6),

-- Año 2020
('2020-01-10', TRUE, 1), ('2020-01-10', TRUE, 2), ('2020-01-10', FALSE, 3), ('2020-01-10', TRUE, 4), ('2020-01-10', FALSE, 5), ('2020-01-10', TRUE, 6),
('2020-02-15', TRUE, 1), ('2020-02-15', TRUE, 2), ('2020-02-15', FALSE, 3), ('2020-02-15', TRUE, 4), ('2020-02-15', FALSE, 5), ('2020-02-15', TRUE, 6),

-- Año 2021
('2021-01-05', FALSE, 1), ('2021-01-05', TRUE, 2), ('2021-01-05', TRUE, 3), ('2021-01-05', FALSE, 4), ('2021-01-05', TRUE, 5), ('2021-01-05', TRUE, 6),
('2021-02-18', TRUE, 1), ('2021-02-18', TRUE, 2), ('2021-02-18', TRUE, 3), ('2021-02-18', FALSE, 4), ('2021-02-18', TRUE, 5), ('2021-02-18', TRUE, 6),

-- Año 2022
('2022-01-07', TRUE, 1), ('2022-01-07', TRUE, 2), ('2022-01-07', FALSE, 3), ('2022-01-07', TRUE, 4), ('2022-01-07', FALSE, 5), ('2022-01-07', TRUE, 6),
('2022-02-20', TRUE, 1), ('2022-02-20', TRUE, 2), ('2022-02-20', FALSE, 3), ('2022-02-20', TRUE, 4), ('2022-02-20', FALSE, 5), ('2022-02-20', TRUE, 6),

-- Año 2023
('2023-01-12', FALSE, 1), ('2023-01-12', TRUE, 2), ('2023-01-12', TRUE, 3), ('2023-01-12', FALSE, 4), ('2023-01-12', TRUE, 5), ('2023-01-12', TRUE, 6),
('2023-02-07', TRUE, 1), ('2023-02-07', TRUE, 2), ('2023-02-07', TRUE, 3), ('2023-02-07', FALSE, 4), ('2023-02-07', TRUE, 5), ('2023-02-07', TRUE, 6),

-- Año 2024
('2024-01-03', TRUE, 1), ('2024-01-03', TRUE, 2), ('2024-01-03', FALSE, 3), ('2024-01-03', TRUE, 4), ('2024-01-03', TRUE, 5), ('2024-01-03', FALSE, 6),
('2024-02-27', TRUE, 1), ('2024-02-27', TRUE, 2), ('2024-02-27', FALSE, 3), ('2024-02-27', TRUE, 4), ('2024-02-27', TRUE, 5), ('2024-02-27', FALSE, 6),

-- Año 2025
('2025-01-19', TRUE, 1), ('2025-01-19', TRUE, 2), ('2025-01-19', FALSE, 3), ('2025-01-19', TRUE, 4), ('2025-01-19', FALSE, 5), ('2025-01-19', TRUE, 6),
('2025-02-05', TRUE, 1), ('2025-02-05', TRUE, 2), ('2025-02-05', FALSE, 3), ('2025-02-05', TRUE, 4), ('2025-02-05', FALSE, 5), ('2025-02-05', TRUE, 6),
('2025-03-31', TRUE, 1),
('2025-03-31', FALSE, 2),
('2025-03-31', TRUE, 3),
('2025-03-31', TRUE, 4),
('2025-03-31', FALSE, 5),
('2025-03-31', TRUE, 6);
