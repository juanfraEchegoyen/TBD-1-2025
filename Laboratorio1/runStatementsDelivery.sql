-- 1. ¿Qué cliente ha gastado más dinero en pedidos entregados? 
SELECT rut_cliente, nombre_cliente FROM Cliente;
SELECT id_pedido, estado_entrega, rut_cliente FROM Pedido;
SELECT id_detalle, precio_total, id_pedido FROM DetallePedido;

SELECT c.nombre_cliente, SUM(dp.precio_total) AS dinero_gastado 
FROM Cliente AS c
JOIN Pedido AS p ON c.rut_cliente = p.rut_cliente
JOIN DetallePedido AS dp ON p.id_pedido = dp.id_pedido
WHERE p.estado_entrega LIKE 'Entregado'
GROUP BY c.nombre_cliente
ORDER BY dinero_gastado DESC
LIMIT 1;

-- 2. ¿Cuáles son los productos o servicios más pedidos en el último mes por categoría? 
SELECT pr.categoria, pr.nombre_producto, COUNT(*) AS total_pedidos
FROM Producto pr
JOIN DetallePedido dp ON pr.id_producto = dp.id_producto
WHERE DATE_TRUNC('month', dp.fecha_entrega) = 
	(
    SELECT DATE_TRUNC('month', MAX(fecha_entrega)) FROM DetallePedido
	)
GROUP BY pr.categoria, pr.nombre_producto
ORDER BY total_pedidos DESC;


-- 3. Listar las empresas asociadas con más entregas fallidas.
SELECT e.nombre_empresa, COUNT(*) AS total_entregas_fallidas
FROM Pedido AS p
JOIN EmpresaAsociada AS e ON p.rut_empresa = e.rut_empresa
WHERE p.estado_entrega = 'Entrega fallida'
GROUP BY e.nombre_empresa
ORDER BY total_entregas_fallidas DESC;

-- 4. Calcular el tiempo promedio entre pedido y entrega por repartidor.
SELECT rut_repartidor FROM Repartidor;
SELECT rut_repartidor, id_pedido, estado_entrega FROM Pedido;
SELECT tiempo_entrega, id_pedido FROM DetallePedido;

SELECT r.rut_repartidor, AVG(dp.tiempo_entrega) AS tiempo_promedio
FROM DetallePedido AS dp
JOIN Pedido AS p ON p.id_pedido = dp.id_pedido
JOIN Repartidor AS r ON r.rut_repartidor = p.rut_repartidor
WHERE p.estado_entrega = 'Entregado'
GROUP BY r.rut_repartidor;

-- 5. Obtener los 3 repartidores con mejor rendimiento (basado en entregas y puntuación).
SELECT nombre_repartidor, 
	MAX(puntuacion_promedio) AS puntuacion, 
	MAX(cantidad_entregas) AS entregas
FROM Repartidor
GROUP BY nombre_repartidor
ORDER BY puntuacion DESC
LIMIT 3;


-- 6. ¿Qué medio de pago se utiliza más en pedidos urgentes?
SELECT mp.nombre_mediodepago, COUNT(*) AS cantidad_usos
FROM MedioDePago AS mp
JOIN Pedido AS p ON p.id_pedido = mp.id_pedido
WHERE p.prioridad_pedido = 'Alta'
GROUP BY mp.nombre_mediodepago
ORDER BY cantidad_usos DESC
LIMIT 1;

