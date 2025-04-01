-- 1. Lista de profesores con su sueldo, indicando si son o no profesores jefe y los alumnos de su jefatura, si corresponde. 
-- 2. Lista de alumnos por curso con más inasistencias por mes en el año 2019. 
-- 3. Lista de empleados identificando su rol, sueldo y comuna de residencia. Debe estar ordenada por comuna y sueldo. 
-- 4. Curso con menos alumnos por año. 
-- 5. Identificar por curso a los alumnos que no han faltado nunca. 
-- 6. Profesor con más horas de clases y mostrar su sueldo. 

-- 7. Profesor con menos horas de clases y mostrar su sueldo. 
SELECT nombre, sueldo
FROM Empleado
WHERE rut_empleado = (
    SELECT rut_empleado
    FROM Profesor
    ORDER BY horas_clase ASC
    LIMIT 1
);

-- 8. Listado de alumnos por curso, donde el apoderado no es su padre o madre.
SELECT id_curso, rut_alumno FROM AluCurso;
SELECT id_curso, id_grado FROM Curso;
SELECT id_grado, nombre_grado FROM Grado;
SELECT rut_alumno, rut_apoderado FROM Alumno;
SELECT rut_apoderado, es_padre FROM Apoderado;

SELECT g.nombre_grado, al.nombre FROM AluCurso AS ac
JOIN Curso AS c ON ac.id_curso = c.id_curso
JOIN Grado AS g ON c.id_grado = g.id_grado
JOIN Alumno AS al ON ac.rut_alumno = al.rut_alumno
JOIN Apoderado AS ap ON al.rut_apoderado = ap.rut_apoderado
WHERE ap.es_padre = TRUE;

-- 9. Colegio con mayor promedio de asistencia el año 2019, identificando la comuna.
-- 10. Lista de colegios con mayor número de alumnos por año.