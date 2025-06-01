-- Clientes con coord
INSERT INTO Cliente (rut_cliente, password, nombre_cliente, telefono, direccion, comuna, ubicacion) VALUES 
('11111111-1', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'Ana López', '912345678', 'Calle 1', 'Santiago', ST_GeomFromText('POINT(-70.6506 -33.4372)', 4326)),
('22222222-2', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'Carlos Pérez', '912345679', 'Calle 2', 'Providencia', ST_GeomFromText('POINT(-70.6118 -33.4260)', 4326)),
('33333333-3', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'Daniela Rojas', '912345680', 'Calle 3', 'Maipú', ST_GeomFromText('POINT(-70.7569 -33.5110)', 4326)),
('44444444-4', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'Elena Torres', '912345681', 'Calle 4', 'Ñuñoa', ST_GeomFromText('POINT(-70.5952 -33.4569)', 4326)),
('55555555-5', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'Felipe Gutiérrez', '912345682', 'Calle 5', 'Las Condes', ST_GeomFromText('POINT(-70.5200 -33.4000)', 4326)),
('66666666-6', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'Gabriela Silva', '912345683', 'Calle 6', 'La Florida', ST_GeomFromText('POINT(-70.5892 -33.5203)', 4326)),
('77777777-7', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'Hugo Martínez', '912345684', 'Calle 7', 'Peñalolén', ST_GeomFromText('POINT(-70.5400 -33.4800)', 4326)),
('88888888-8', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'Ignacia Castro', '912345685', 'Calle 8', 'San Miguel', ST_GeomFromText('POINT(-70.6500 -33.4950)', 4326)),
('99999999-9', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'Joaquín Vega', '912345686', 'Calle 9', 'Lo Barnechea', ST_GeomFromText('POINT(-70.4850 -33.3500)', 4326)),
('10101010-0', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'Karen Herrera', '912345687', 'Calle 10', 'Puente Alto', ST_GeomFromText('POINT(-70.5750 -33.6100)', 4326));

-- Repartidores con coordenadas geoespaciales y distancia recorrida
INSERT INTO Repartidor (rut_repartidor, password, nombre_repartidor, telefono, puntuacion_promedio, cantidad_entregas, distancia_recorrida, ubicacion) VALUES 
('12121212-1', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'Luis Soto', '913000001', 4, 4, 125.5, ST_GeomFromText('POINT(-70.6300 -33.4450)', 4326)),
('13131313-2', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'María Díaz', '913000002', 3, 3, 98.2, ST_GeomFromText('POINT(-70.6100 -33.4200)', 4326)),
('14141414-3', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'Pedro Ramírez', '913000003', 3, 4, 156.8, ST_GeomFromText('POINT(-70.5800 -33.4600)', 4326)),
('15151515-4', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'Claudia Fuentes', '913000004', 5, 3, 87.3, ST_GeomFromText('POINT(-70.5500 -33.4100)', 4326)),
('16161616-5', '$2a$10$UHwxgZleWAsTlCvT5NaQ5.5MpnLoNo09tzLXBVu/NPEki0fOOgefa', 'José Navarro', '913000005', 3, 7, 234.7, ST_GeomFromText('POINT(-70.6800 -33.4800)', 4326));

-- Empresas asociadas
INSERT INTO EmpresaAsociada (rut_empresa, nombre_empresa) VALUES 
('80000000-1', 'Rápido y Sabroso'),
('80000000-2', 'Sabor Urbano'),
('80000000-3', 'Pan al Paso'),
('80000000-4', 'Tech & Snacks'),
('80000000-5', 'Delibox Express');

-- Productos
INSERT INTO Producto (nombre_producto, precio, categoria, tipo_producto, stock, rut_empresa) VALUES 
('Burger Clásica', 5000, 'Comida Rápida', 'Hamburguesa', 100, '80000000-1'),
('Pizza Napolitana', 8000, 'Comida Rápida', 'Pizza', 50, '80000000-1'),
('Sándwich Veggie', 4000, 'Comida Rápida', 'Sándwich', 60, '80000000-1'),
('Wrap de Pollo', 7500, 'Comida Rápida', 'Wrap', 65, '80000000-1'),
('Empanada de Pino', 3000, 'Comida Rápida', 'Empanada', 80, '80000000-2'),
('Pan Italiano', 1000, 'Panadería', 'Pan', 90, '80000000-2'),
('Marraqueta Doble', 750, 'Panadería', 'Pan', 100, '80000000-2'),
('Pan amasado', 500, 'Panadería', 'Pan', 55, '80000000-3'),
('Croissant de Mantequilla', 800, 'Panadería', 'Pan Dulce', 80, '80000000-3'),
('Taco Mexicano', 1500, 'Internacional', 'Taco', 70, '80000000-3'),
('Sushi Roll', 7000, 'Internacional', 'Sushi', 50, '80000000-3'),
('Galletas Artesanales', 2000, 'Dulces', 'Postre', 75, '80000000-4'),
('Torta de Chocolate', 6000, 'Dulces', 'Pastel', 30, '80000000-4'),
('Helado de Vainilla', 1100, 'Dulces', 'Postre', 40, '80000000-4'),
('Bebida Cola 1.5L', 3000, 'Bebidas', 'Gaseosa', 60, '80000000-5'),
('Jugo Natural de Naranja 1.5L', 2500, 'Bebidas', 'Jugo', 40, '80000000-5'),
('Agua Mineral con Gas', 1200, 'Bebidas', 'Agua', 70, '80000000-5'),
('Café Molido Premium', 4500, 'Bebidas', 'Café', 30, '80000000-5'),
('Té Verde Importado', 3500, 'Bebidas', 'Té', 50, '80000000-5'),
('Batido Proteico', 4000, 'Bebidas', 'Batido', 25, '80000000-5');


-- Puntuaciones
INSERT INTO Puntuacion (puntaje, comentario, rut_repartidor) VALUES 
(4, 'Muy puntual', '12121212-1'),
(3, 'Amable y rápido', '14141414-3'),
(1, 'Llegó tarde', '16161616-5'),
(4, 'Buen servicio', '16161616-5'),
(5, 'Excelente atención', '13131313-2'),
(3, 'Amable y rápido', '14141414-3'),
(1, 'Llegó tarde', '15151515-4'),
(4, 'Buen servicio', '16161616-5'),
(4, 'Muy puntual', '12121212-1'),
(1, 'No llega', '13131313-2'),
(5, 'Llegó tarde', '15151515-4'),
(4, 'Muy puntual', '12121212-1'),
(5, 'Excelente atención', '16161616-5'),
(3, 'Amable y rápido', '14141414-3'),
(4, 'Buen servicio', '16161616-5'),
(3, 'Buen servicio', '16161616-5'),
(1, 'Penca', '16161616-5');

-- Pedidos
INSERT INTO Pedido (estado_entrega, prioridad_pedido, problema_critico, rut_cliente, rut_empresa, rut_repartidor) VALUES 
('Entregado', 'Urgente', false, '11111111-1', '80000000-1', '12121212-1'),   -- Producto 1: comida rápida
('Pendiente', 'Media', false, '22222222-2', '80000000-1', '13131313-2'), -- Producto 1
('Entregado', 'Baja', false, '33333333-3', '80000000-1', '14141414-3'),  -- Producto 1
('Entrega fallida', 'Alta', true, '44444444-4', '80000000-1', '16161616-5'), -- Producto 2
('Entregado', 'Media', false, '55555555-5', '80000000-1', '16161616-5'), -- Producto 2
('Pendiente', 'Alta', false, '66666666-6', '80000000-1', '12121212-1'),  -- Producto 2
('Entregado', 'Baja', false, '77777777-7', '80000000-1', '13131313-2'),  -- Producto 3
('Entregado', 'Media', false, '88888888-8', '80000000-1', '14141414-3'), -- Producto 4
('Entrega fallida', 'Alta', true, '99999999-9', '80000000-2', '15151515-4'), -- Producto 5
('Entregado', 'Baja', false, '10101010-0', '80000000-2', '16161616-5'),  -- Producto 6
('Entregado', 'Media', false, '11111111-1', '80000000-2', '12121212-1'), -- Producto 7
('Entrega fallida', 'Alta', true, '22222222-2', '80000000-2', '13131313-2'), -- Producto 7
('Pendiente', 'Media', false, '33333333-3', '80000000-2', '14141414-3'), -- Producto 7
('Entregado', 'Baja', false, '44444444-4', '80000000-3', '15151515-4'),  -- Producto 8
('Pendiente', 'Alta', false, '55555555-5', '80000000-3', '16161616-5'),  -- Producto 9
('Entregado', 'Urgente', false, '66666666-6', '80000000-3', '12121212-1'), -- Producto 10
('Entrega fallida', 'Baja', true, '77777777-7', '80000000-3', '16161616-5'), -- Producto 11
('Entregado', 'Urgente', false, '88888888-8', '80000000-4', '14141414-3'),  -- Producto 12
('Pendiente', 'Baja', false, '99999999-9', '80000000-4', '15151515-4'),  -- Producto 13
('Entregado', 'Urgente', false, '10101010-0', '80000000-4', '16161616-5'), -- Producto 14
('Cancelada', 'Baja', false, '10101010-0', '80000000-4', '16161616-5'), -- Producto 14
('Devolución', 'Baja', true, '11111111-1', '80000000-4', '16161616-5'); -- Producto 14


-- DetallePedido (modificado para reflejar popularidad de productos)
INSERT INTO DetallePedido (precio_total, tiempo_entrega, fecha_entrega, cantidad, id_pedido, id_producto) VALUES
(10000, 30, '2025-04-01', 2, 1, 1),   -- Burger Clásica
(5000, 25, '2025-04-02', 1, 2, 1),    -- Burger Clásica
(15000, 45, '2025-04-03', 3, 3, 1),   -- Burger Clásica
(8000, 40, '2025-04-04', 1, 4, 2),    -- Pizza Napolitana
(16000, 50, '2025-04-05', 2, 5, 2),   -- Pizza Napolitana
(24000, 60, '2025-04-06', 3, 6, 2),   -- Pizza Napolitana
(4000, 20, '2025-04-07', 1, 7, 3),    -- Sándwich Veggie
(3000, 25, '2025-04-08', 1, 8, 4),    -- Wrap de Pollo
(1000, 15, '2025-05-01', 1, 9, 5),    -- Empanada de Pino
(2000, 20, '2025-05-02', 2, 10, 6),   -- Pan Italiano
(750, 10, '2025-05-03', 1, 11, 7),    -- Marraqueta Doble
(1000, 12, '2025-05-04', 2, 12, 7),   -- Marraqueta Doble
(1500, 15, '2025-05-05', 3, 13, 7),   -- Marraqueta Doble
(800, 15, '2025-05-06', 1, 14, 8),    -- Pan amasado
(800, 10, '2025-05-07', 1, 15, 9),    -- Croissant
(14000, 55, '2025-05-08', 2, 16, 10), -- Taco Mexicano
(7000, 50, '2025-05-09', 1, 17, 11),  -- Sushi Roll
(4000, 10, '2025-05-10', 2, 18, 12),  -- Galletas
(6000, 20, '2025-05-11', 1, 19, 13),  -- Torta de Chocolate
(1100, 20, '2025-05-12', 1, 20, 14),  -- Helado de Vainilla
(2200, 20, '2025-05-13', 2, 21, 14),  -- Helado de Vainilla
(1100, 40, '2025-05-14', 1, 22, 14);  -- Helado de Vainilla

-- Medios de pago
INSERT INTO MedioDePago (nombre_mediodepago, rut_cliente, id_pedido) VALUES 
('Crédito', '11111111-1', 1),
('Débito', '22222222-2', 2),
('Transferencia', '33333333-3', 3),
('Débito', '44444444-4', 4),
('Débito', '55555555-5', 5),
('Efectivo', '66666666-6', 6),
('Efectivo', '77777777-7', 7),
('Crédito', '88888888-8',8 ),
('Débito', '99999999-9', 9),
('Transferencia', '10101010-0', 10),
('Crédito', '11111111-1', 1),
('Débito', '22222222-2', 2),
('Transferencia', '33333333-3', 3),
('Efectivo', '44444444-4', 4),
('Débito', '55555555-5', 5),
('Efectivo', '66666666-6', 6),
('Efectivo', '77777777-7', 7),
('Crédito', '88888888-8',8 ),
('Débito', '99999999-9', 9),
('Transferencia', '10101010-0', 10),
('Transferencia', '11111111-1', 10),
('Débito', '11111111-1', 10);
