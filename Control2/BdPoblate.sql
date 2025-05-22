INSERT INTO Usuario (nombre, password, idSector) VALUES 
('Ariel', 'hashedpassword', 1),
('Juan', 'hashedpassword', 2),
('Maria', 'hashedpassword', 1),
('Carlos', 'hashedpassword', 3),
('Sofía', 'hashedpassword', 4),
('Luis', 'hashedpassword', 2),
('Ana', 'hashedpassword', 5),
('Pedro', 'hashedpassword', 3),
('Camila', 'hashedpassword', 4),
('Fernando', 'hashedpassword', 5),

INSERT INTO Ubicacion (region, provincia, comuna, coordenadas) VALUES 
('Metropolitana', 'Santiago', 'San Bernardo', ST_GeomFromText('POINT(-70.7024 -33.5664)', 4326)),
('Metropolitana', 'Santiago', 'Providencia', ST_GeomFromText('POINT(-70.6099 -33.4249)', 4326)),
('Valparaíso', 'Valparaíso', 'Viña del Mar', ST_GeomFromText('POINT(-71.5522 -33.0245)', 4326)),
('Biobío', 'Concepción', 'Talcahuano', ST_GeomFromText('POINT(-73.121 -36.713)', 4326)),
('Metropolitana', 'Santiago', 'La Florida', ST_GeomFromText('POINT(-70.5832 -33.5141)', 4326)),

INSERT INTO Sector (asignacion, comuna, calle, ubicacion) VALUES 
('Sector A', 'San Bernardo', 'Calle 1', ST_GeomFromText('POINT(-70.7050 -33.5670)', 4326)),
('Sector B', 'Providencia', 'Calle 2', ST_GeomFromText('POINT(-70.6105 -33.4255)', 4326)),
('Sector C', 'Viña del Mar', 'Calle 3', ST_GeomFromText('POINT(-71.5500 -33.0231)', 4326)),
('Sector D', 'Talcahuano', 'Calle 4', ST_GeomFromText('POINT(-73.120 -36.714)', 4326)),
('Sector E', 'La Florida', 'Calle 5', ST_GeomFromText('POINT(-70.585 -33.513)', 4326)),

INSERT INTO Tarea (titulo, descripcion, fechaVencimiento, estado, idUsuario, idSector) VALUES 
('Revisión de equipamiento', 'Verificar herramientas', '2025-06-01', 'pendiente', 1, 1),
('Reporte de seguridad', 'Generar informe', '2025-06-05', 'completado', 2, 2),
('Inspección de área', 'Registrar observaciones', '2025-06-03', 'pendiente', 1, 1),
('Mantenimiento', 'Realizar ajustes técnicos', '2025-06-02', 'completado', 3, 2),
('Auditoría interna', 'Revisar procedimientos', '2025-06-07', 'pendiente', 4, 3),
('Revisión eléctrica', 'Inspeccionar cables y conexiones', '2025-06-10', 'completado', 5, 4),
('Evaluación de riesgos', 'Determinar peligros', '2025-06-12', 'pendiente', 6, 5),
('Reparación de infraestructura', 'Arreglar desperfectos', '2025-06-15', 'completado', 7, 3),
('Control de calidad', 'Verificar estándares', '2025-06-18', 'pendiente', 8, 4),
('Capacitación técnica', 'Impartir formación', '2025-06-20', 'completado', 9, 5);
