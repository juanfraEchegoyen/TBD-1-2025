-- 1. Lista de profesores con su sueldo, indicando si son o no profesores jefe y los alumnos de su jefatura, si corresponde. 
-- 2. Lista de alumnos por curso con más inasistencias por mes en el año 2019. 

-- 3. Lista de empleados identificando su rol, sueldo y comuna de residencia. Debe estar ordenada por comuna y sueldo. 
SELECT 
    e.nombre AS nombre_empleado,
    e.rol, e.sueldo, co.nombre AS nombre_comuna
FROM Empleado e
JOIN Comuna co ON e.id_comuna = co.id_comuna
ORDER BY
    co.nombre ASC,
    e.sueldo ASC;

-- 4. Curso con menos alumnos por año. 

-- alumnos por curso: id_curso/anio/numero_alumnos
WITH ConteoAlumnosPorCurso AS (
    SELECT 
        c.id_curso,
        c.anio,
        COUNT(ac.rut_alumno) AS numero_alumnos
    FROM 
        Curso c
    LEFT JOIN -- por si hay un curso sin alumnos se cuenta como 0
        AluCurso ac ON c.id_curso = ac.id_curso
    GROUP BY 
        c.id_curso, c.anio 
),
-- ranking por año: toma id_curso/anio/numero_alumno y crea un ranking con 1 como el "mejor" del ranking
RankingCursosPorAño AS (
    SELECT
        id_curso,
        anio,
        numero_alumnos,
        RANK() OVER (PARTITION BY anio ORDER BY numero_alumnos ASC) as ranking_num_alumnos
    FROM
        ConteoAlumnosPorCurso
)

-- del ranking por curso se une la tabla grado la tabla curso y colegio 
SELECT 
    rank.anio,
    rank.id_curso,
    g.nombre_grado,
    col.nombre AS nombre_colegio,
    rank.numero_alumnos
FROM 
    RankingCursosPorAño rank
JOIN 
    Curso c ON rank.id_curso = c.id_curso
JOIN 
    Grado g ON c.id_grado = g.id_grado
JOIN
    Colegio col ON c.id_colegio = col.id_colegio
WHERE
    rank.ranking_num_alumnos = 1
ORDER BY 
    rank.anio;

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

--9. Colegio con mayor promedio de asistencia el año 2019, identificando la comuna. 

select  col.nombre as Colegio, com.nombre as Comuna, AVG(CASE WHEN a.flg_presente THEN 1 ELSE -1 END) AS Promedio_Asistencia
from asistencia AS a Join alucurso as ac on a.id_alumnocurso = ac.id_alumnocurso
join alumno as al on ac.rut_alumno = al.rut_alumno
join colegio as col on al.id_colegio = col.id_colegio
join comuna as com on col.id_comuna = com.id_comuna
Where EXTRACT (YEAR FROM fecha) = 2019 
group by colegio,Comuna
order by Promedio_Asistencia DESC
LIMIT 1;

-- 10. Lista de colegios con mayor número de alumnos por año.

SELECT col.nombre as Colegio, count(*) as Alumnos, c.anio as año
FROM CURSO as C join alucurso as ac on ac.id_curso = c.id_curso
join alumno as al on ac.rut_alumno = al.rut_alumno
join colegio as col on al.id_colegio = col.id_colegio
group by año,colegio
ORDER BY Alumnos DESC;
