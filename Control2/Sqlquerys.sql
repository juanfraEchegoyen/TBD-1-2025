
--1. ¿Cuántas tareas ha hecho el usuario por sector?
SELECT u.nombre, t.id_sector, COUNT(*) AS cantidad_tareas
FROM Tarea t
JOIN Usuario u ON t.id_usuario = u.id_usuario
WHERE u.id_usuario = 1 -- Reemplaza con el ID del usuario que deseas consultar
GROUP BY t.id_sector, u.nombre;

----
--2. ¿Cuál es la tarea pendiente más cercana al usuario?
SELECT u.nombre,t.id_tarea, t.titulo, ST_Distance(s.ubicacion, sector_usuario.ubicacion) AS distancia
FROM Tarea t
JOIN Sector s ON t.id_sector = s.id_sector
JOIN Usuario u ON t.id_usuario = u.id_usuario
JOIN Sector sector_usuario ON u.id_sector = sector_usuario.id_sector
WHERE t.estado = 'pendiente' AND u.id_usuario = 4 --id del usuario a cambiar
ORDER BY distancia ASC
LIMIT 1;
---
--3. ¿Cuál es el sector con más tareas completadas en un radio de 2 km del usuario?
SELECT  u.nombre,s.id_sector, COUNT(*) AS tareas_completadas
FROM Tarea t
JOIN Sector s ON t.id_sector = s.id_sector
JOIN Usuario u ON t.id_usuario = u.id_usuario
JOIN Sector sector_usuario ON u.id_sector = sector_usuario.id_sector
WHERE t.estado = 'completado'
AND ST_DWithin(s.ubicacion, sector_usuario.ubicacion, 2000) -- 2 km de distancia
AND u.id_usuario = 2 --id del usuario a cambiar
GROUP BY s.id_sector, u.nombre
ORDER BY tareas_completadas DESC
LIMIT 1;

---
--4. ¿Cuál es el promedio de distancia de las tareas completadas respecto a la ubicación del usuario?
SELECT u.nombre, AVG(ST_Distance(s.ubicacion, sector_usuario.ubicacion)) AS promedio_distancia
FROM Tarea t
JOIN Sector s ON t.id_sector = s.id_sector
JOIN Usuario u ON t.id_usuario = u.id_usuario
JOIN Sector sector_usuario ON u.id_sector = sector_usuario.id_sector
WHERE t.estado = 'completado'
AND u.id_usuario = 2 --id del usuario a cambiar
GROUP BY u.nombre;

---
--5. ¿En qué sectores geográficos se concentran la mayoría de las tareas pendientes?
SELECT u.nombre, s.comuna, COUNT(*) AS cantidad_tareas
FROM Tarea t
JOIN Sector s ON t.id_sector = s.id_sector
JOIN Usuario u ON t.id_usuario = u.id_usuario
WHERE t.estado = 'pendiente'
AND u.id_usuario = 1 --id del usuario a cambiar
GROUP BY s.comuna,u.nombre
ORDER BY cantidad_tareas DESC;

----
--6. ¿Cuántas tareas ha realizado cada usuario por sector?
SELECT u.nombre,t.id_usuario, s.id_sector, COUNT(*) AS cantidad_tareas
FROM Tarea t
JOIN Sector s ON t.id_sector = s.id_sector
JOIN Usuario u ON t.id_usuario = u.id_usuario
WHERE t.estado = 'completado'
AND u.id_usuario = 2 --id del usuario a cambiar
GROUP BY t.id_usuario, s.id_sector,u.nombre
ORDER BY id_usuario, cantidad_tareas DESC;

---
---7. ¿Cuál es el sector con más tareas completadas dentro de un radio de 5 km desde la ubicación del usuario?
SELECT u.nombre, s.id_sector, COUNT(*) AS tareas_completadas
FROM Tarea t
JOIN Sector s ON t.id_sector = s.id_sector
JOIN Usuario u ON t.id_usuario = u.id_usuario
JOIN Sector sector_usuario ON u.id_sector = sector_usuario.id_sector
WHERE t.estado = 'completado'
AND ST_DWithin(s.ubicacion, sector_usuario.ubicacion, 5000) -- 5 km de distancia
AND u.id_usuario = 1 --id del usuario a cambiar
GROUP BY s.id_sector,u.nombre
ORDER BY tareas_completadas DESC
LIMIT 1;

----
SELECT u.nombre, AVG(ST_Distance(s.ubicacion, sector_usuario.ubicacion)) AS promedio_distancia
FROM Tarea t
JOIN Sector s ON t.id_sector = s.id_sector
JOIN Usuario u ON t.id_usuario = u.id_usuario
JOIN Sector sector_usuario ON u.id_sector = sector_usuario.id_sector
WHERE t.estado = 'completado'
AND u.id_usuario = 2 --id del usuario a cambiar
GROUP BY u.nombre;

-----
