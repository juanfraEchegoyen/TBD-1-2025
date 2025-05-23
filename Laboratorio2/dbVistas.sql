
-- creamos la vista de pedidos por cliente
CREATE VIEW vista_pedidos_por_cliente AS
--obtenemos el rut, nnombre canntidad de pedidos y el monto total 
SELECT c.rut_cliente, c.nombre_cliente, COUNT(p.id_pedido) AS cantidad_pedidos, 
       SUM(dp.precio_total) AS monto_total
FROM Cliente c
JOIN Pedido p ON c.rut_cliente = p.rut_cliente
JOIN DetallePedido dp ON p.id_pedido = dp.id_pedido
GROUP BY c.rut_cliente, c.nombre_cliente;

--------

--creamos la vista de desempeño
CREATE VIEW vista_desempeno_repartidor AS

--selecionamos el rut, nombre, cuantas entregas realizo y su puntaje promedio
SELECT r.rut_repartidor, r.nombre_repartidor, COUNT(p.id_pedido) AS entregas_realizadas, 
       COALESCE(AVG(pt.puntaje), 0) AS puntuacion_promedio
FROM Repartidor r
LEFT JOIN Pedido p ON r.rut_repartidor = p.rut_repartidor
LEFT JOIN Puntuacion pt ON r.rut_repartidor = pt.rut_repartidor
GROUP BY r.rut_repartidor, r.nombre_repartidor;

--------

--creamos la vista de empresas con mas pedidos
CREATE VIEW vista_empresas_mayor_pedidos AS
--obtennemos el rut, nombre y la cantidad de pedidos
SELECT ea.rut_empresa, ea.nombre_empresa, COUNT(p.id_pedido) AS cantidad_pedidos
FROM EmpresaAsociada ea
JOIN Pedido p ON ea.rut_empresa = p.rut_empresa
GROUP BY ea.rut_empresa, ea.nombre_empresa
ORDER BY cantidad_pedidos DESC;

/* probamos las vistas
SELECT * FROM vista_pedidos_por_cliente;
SELECT * FROM vista_desempeno_repartidor;
SELECT * FROM vista_empresas_mayor_pedidos;
*/
