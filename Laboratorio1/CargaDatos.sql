-- Clientes
INSERT INTO Cliente (rut_cliente, nombre_cliente, telefono, direccion, comuna) VALUES 
('11111111-1', 'Ana López', '912345678', 'Calle 1', 'Santiago'),
('22222222-2', 'Carlos Pérez', '912345679', 'Calle 2', 'Providencia'),
('33333333-3', 'Daniela Rojas', '912345680', 'Calle 3', 'Maipú'),
('44444444-4', 'Elena Torres', '912345681', 'Calle 4', 'Ñuñoa'),
('55555555-5', 'Felipe Gutiérrez', '912345682', 'Calle 5', 'Las Condes'),
('66666666-6', 'Gabriela Silva', '912345683', 'Calle 6', 'La Florida'),
('77777777-7', 'Hugo Martínez', '912345684', 'Calle 7', 'Peñalolén'),
('88888888-8', 'Ignacia Castro', '912345685', 'Calle 8', 'San Miguel'),
('99999999-9', 'Joaquín Vega', '912345686', 'Calle 9', 'Lo Barnechea'),
('10101010-0', 'Karen Herrera', '912345687', 'Calle 10', 'Puente Alto');

-- Empresas asociadas
INSERT INTO EmpresaAsociada (rut_empresa, nombre_empresa) VALUES 
('80000000-1', 'Comidas Express'),
('80000000-2', 'Farmacia Popular'),
('80000000-3', 'Librería Central'),
('80000000-4', 'ElectroWorld'),
('80000000-5', 'Panadería San Juan');

-- Productos (20)
INSERT INTO Producto (nombre_producto, precio, categoria, tipo_producto, stock) VALUES 
('Hamburguesa', 5000, 'Comida', 'Alimento', 100),
('Pizza', 8000, 'Comida', 'Alimento', 50),
('Sándwich', 4000, 'Comida', 'Alimento', 60),
('Antigripal', 3000, 'Medicamento', 'Farmacia', 200),
('Paracetamol', 1000, 'Medicamento', 'Farmacia', 300),
('Ibuprofeno', 1500, 'Medicamento', 'Farmacia', 250),
('Libro A', 7000, 'Educación', 'Libro', 30),
('Libro B', 7500, 'Educación', 'Libro', 20),
('Cuaderno', 2000, 'Papelería', 'Útil escolar', 80),
('Lápiz', 500, 'Papelería', 'Útil escolar', 100),
('Audífonos', 12000, 'Tecnología', 'Accesorio', 25),
('Mouse', 8000, 'Tecnología', 'Accesorio', 30),
('Teclado', 10000, 'Tecnología', 'Accesorio', 20),
('Pan de molde', 1500, 'Comida', 'Panadería', 90),
('Marraqueta', 1000, 'Comida', 'Panadería', 100),
('Croissant', 2000, 'Comida', 'Panadería', 80),
('Bebida 1.5L', 1800, 'Bebida', 'Bebestible', 60),
('Jugo natural', 2500, 'Bebida', 'Bebestible', 40),
('Agua mineral', 1000, 'Bebida', 'Bebestible', 70),
('Café molido', 4500, 'Bebida', 'Bebestible', 30);

-- Repartidores
INSERT INTO Repartidor (rut_repartidor, nombre_repartidor, telefono, puntuacion_promedio, cantidad_entregas) VALUES 
('12121212-1', 'Luis Soto', '913000001', 4, 50),
('13131313-2', 'María Díaz', '913000002', 5, 60),
('14141414-3', 'Pedro Ramírez', '913000003', 3, 45),
('15151515-4', 'Claudia Fuentes', '913000004', 5, 70),
('16161616-5', 'José Navarro', '913000005', 4, 55);

-- Puntuaciones
INSERT INTO Puntuacion (puntaje, comentario, rut_repartidor) VALUES 
(4, 'Muy puntual', '12121212-1'),
(5, 'Excelente atención', '13131313-2'),
(3, 'Llegó tarde', '14141414-3'),
(5, 'Amable y rápido', '15151515-4'),
(4, 'Buen servicio', '16161616-5');

-- Medios de pago
INSERT INTO MedioDePago (nombre_mediodepago, rut_cliente) VALUES 
('Tarjeta de crédito', '11111111-1'),
('Débito', '22222222-2'),
('Transferencia', '33333333-3'),
('Efectivo', '44444444-4'),
('Tarjeta de débito', '55555555-5'),
('Paypal', '66666666-6'),
('Webpay', '77777777-7'),
('Crédito', '88888888-8'),
('Débito', '99999999-9'),
('Transferencia', '10101010-0');

-- Pedidos (20, enlazados a clientes, empresas y repartidores)
INSERT INTO Pedido (estado_entrega, prioridad_pedido, problema_critico, rut_cliente, rut_empresa, rut_repartidor) VALUES 
('Entregado', 'Alta', false, '11111111-1', '80000000-1', '12121212-1'),
('En camino', 'Media', false, '22222222-2', '80000000-2', '13131313-2'),
('Entregado', 'Baja', false, '33333333-3', '80000000-3', '14141414-3'),
('Pendiente', 'Alta', true, '44444444-4', '80000000-4', '15151515-4'),
('Entregado', 'Media', false, '55555555-5', '80000000-5', '16161616-5'),
('En camino', 'Alta', false, '66666666-6', '80000000-1', '12121212-1'),
('Entregado', 'Baja', false, '77777777-7', '80000000-2', '13131313-2'),
('Entregado', 'Media', false, '88888888-8', '80000000-3', '14141414-3'),
('Pendiente', 'Alta', true, '99999999-9', '80000000-4', '15151515-4'),
('Entregado', 'Baja', false, '10101010-0', '80000000-5', '16161616-5'),
('Entregado', 'Media', false, '11111111-1', '80000000-1', '12121212-1'),
('Pendiente', 'Alta', true, '22222222-2', '80000000-2', '13131313-2'),
('En camino', 'Media', false, '33333333-3', '80000000-3', '14141414-3'),
('Entregado', 'Baja', false, '44444444-4', '80000000-4', '15151515-4'),
('En camino', 'Alta', false, '55555555-5', '80000000-5', '16161616-5'),
('Entregado', 'Media', false, '66666666-6', '80000000-1', '12121212-1'),
('Pendiente', 'Baja', true, '77777777-7', '80000000-2', '13131313-2'),
('Entregado', 'Alta', false, '88888888-8', '80000000-3', '14141414-3'),
('En camino', 'Baja', false, '99999999-9', '80000000-4', '15151515-4'),
('Entregado', 'Media', false, '10101010-0', '80000000-5', '16161616-5');

-- DetallePedido (20 detalles con producto y pedido)
INSERT INTO DetallePedido (precio_total, tiempo_entrega, fecha_entrega, cantidad, id_pedido, id_producto) VALUES
(10000, 30, '2024-04-01', 2, 1, 1),
(8000, 45, '2024-04-02', 1, 2, 2),
(4000, 20, '2024-04-03', 1, 3, 3),
(3000, 25, '2024-04-04', 1, 4, 4),
(1000, 15, '2024-04-05', 1, 5, 5),
(1500, 20, '2024-04-06', 1, 6, 6),
(7000, 50, '2024-04-07', 1, 7, 7),
(7500, 55, '2024-04-08', 1, 8, 8),
(2000, 10, '2024-04-09', 1, 9, 9),
(500, 5, '2024-04-10', 2, 10, 10),
(12000, 60, '2024-04-11', 1, 11, 11),
(8000, 40, '2024-04-12', 1, 12, 12),
(10000, 50, '2024-04-13', 1, 13, 13),
(1500, 15, '2024-04-14', 3, 14, 14),
(1000, 12, '2024-04-15', 5, 15, 15),
(2000, 20, '2024-04-16', 1, 16, 16),
(1800, 30, '2024-04-17', 2, 17, 17),
(2500, 25, '2024-04-18', 1, 18, 18),
(1000, 10, '2024-04-19', 1, 19, 19),
(4500, 35, '2024-04-20', 1, 20, 20);
