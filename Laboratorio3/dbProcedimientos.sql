CREATE PROCEDURE registrar_pedido(
    IN estado_entrega VARCHAR(50),
    IN prioridad_pedido VARCHAR(50),
    IN problema_critico BOOLEAN,
    IN rut_cliente_pedido VARCHAR(15),
    IN rut_empresa_pedido VARCHAR(15),
    IN rut_repartidor_pedido VARCHAR(15),
    IN precio_total INT,
    IN tiempo_entrega INT,
    IN fecha_entrega DATE,
    IN cantidad INT,
    IN id_producto_pedido INT,
    IN nombre_mediodepago VARCHAR(50),
    IN rutas_estimadas_pedido geometry(LineString, 4326)
)
LANGUAGE plpgsql
AS $$
DECLARE 
    nuevo_id_pedido INT;
	stock_actual INT;
BEGIN
	-- Validar existencia de cliente, empresa, repartidor y producto
    IF NOT EXISTS (SELECT 1 FROM Cliente WHERE rut_cliente = rut_cliente_pedido) THEN
        RAISE EXCEPTION 'El cliente no existe';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM EmpresaAsociada WHERE rut_empresa = rut_empresa_pedido) THEN
        RAISE EXCEPTION 'La empresa no existe';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM Repartidor WHERE rut_repartidor = rut_repartidor_pedido) THEN
        RAISE EXCEPTION 'El repartidor no existe';
    END IF;

	IF NOT EXISTS (SELECT 1 FROM Producto WHERE id_producto = id_producto_pedido) THEN
		RAISE EXCEPTION 'El producto no existe';
	END IF;	

	-- Verificar stock disponible
	SELECT stock INTO stock_actual FROM Producto WHERE id_producto = id_producto_pedido;

	IF stock_actual < cantidad THEN
        RAISE EXCEPTION 'Stock insuficiente para el pedido';
    END IF;
	
    -- Insertar datos en las tablas
    INSERT INTO Pedido (estado_entrega, prioridad_pedido, problema_critico, rut_cliente, rut_empresa, rut_repartidor, rutas_estimadas)
    VALUES (estado_entrega, prioridad_pedido, problema_critico, rut_cliente_pedido, rut_empresa_pedido, rut_repartidor_pedido, rutas_estimadas_pedido)
    RETURNING id_pedido INTO nuevo_id_pedido;

    INSERT INTO DetallePedido (precio_total, tiempo_entrega, fecha_entrega, cantidad, id_pedido, id_producto)
    VALUES (precio_total, tiempo_entrega, fecha_entrega, cantidad, nuevo_id_pedido, id_producto_pedido);

    INSERT INTO MedioDePago (nombre_mediodepago, rut_cliente, id_pedido)
    VALUES (nombre_mediodepago, rut_cliente_pedido, nuevo_id_pedido);

    COMMIT;
END;
$$;

-----------------------------

CREATE PROCEDURE actualizar_estado_pedido(
    IN id_pedido_actualizar INT,
    IN nuevo_estado VARCHAR(50)
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- validamos que el pedido exista
    IF NOT EXISTS (SELECT 1 FROM Pedido WHERE id_pedido = id_pedido_actualizar) THEN
        RAISE EXCEPTION 'El pedido no existe';
    END IF;

    -- actualizamos el estado del pedido
    UPDATE Pedido 
    SET estado_entrega = nuevo_estado
    WHERE id_pedido = id_pedido_actualizar;

    COMMIT;
END;
$$;

------------------------------------------

CREATE PROCEDURE descontar_stock_al_confirmar(
    IN id_pedido_confirmado INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    estado_actual VARCHAR(50);
BEGIN

    IF NOT EXISTS (SELECT 1 FROM Pedido WHERE id_pedido = id_pedido_confirmado) THEN
        RAISE EXCEPTION 'El pedido no existe';
    END IF;

    -- Verifica si el pedido ya fue confirmado
    SELECT estado_entrega INTO estado_actual FROM Pedido WHERE id_pedido = id_pedido_confirmado;

    IF estado_actual = 'Entregado' OR estado_actual = 'Entrega fallida' THEN
        RAISE EXCEPTION 'El pedido ya fue entregado o se perdió en el camino. No se puede volver a descontar el stock.';
    END IF;

    -- descontamos
    UPDATE Producto 
    SET stock = stock - dp.cantidad
    FROM DetallePedido dp
    WHERE Producto.id_producto = dp.id_producto
    AND dp.id_pedido = id_pedido_confirmado;

    -- si el stock quedo negativo hacemos ROLLBACK (dije rollback en mi cabeza como lo hace el marin XD)
    IF EXISTS (SELECT 1 FROM Producto WHERE stock < 0) THEN
        ROLLBACK;
        RAISE EXCEPTION 'No hay suficiente stock para procesar el pedido';
    END IF;

    COMMIT;
END;
$$;

--------------------------------------

CREATE PROCEDURE Aumentar_stock_al_fallar(
    IN id_pedido_fallado INT
)
LANGUAGE plpgsql
AS $$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM Pedido WHERE id_pedido = id_pedido_fallado) THEN
        RAISE EXCEPTION 'El pedido no existe';
    END IF;

    -- aumenntamos stcok
    UPDATE Producto 
    SET stock = stock + dp.cantidad
    FROM DetallePedido dp
    WHERE Producto.id_producto = dp.id_producto
    AND dp.id_pedido = id_pedido_fallado;


    COMMIT;
END;
$$;

/*
--- CALLS DE PRUEBA CORRER UNA VEZ SE HAYA HECHO LA POBLACION
--- 

-- Registrar_Pedido(estado_entrega, prioridad,problema, rut cliente, rut empresa, 
--					rut repartidor, precio , tiempo, fecha, cantidad, id producto)
CALL registrar_pedido(
    'En proceso', 'Alta', FALSE, '11111111-1', '80000000-1', '12121212-1', 
    10000, 30, '2025-05-02', 2, 1
);

--actualizar_estado_pedido(id_pedido,estado)
CALL actualizar_estado_pedido(1,'Entregado');

--descontar_stock_al_confirmar(id_pedido_confirmado)
CALL descontar_stock_al_confirmar(1);

--Aumentar_stock_al_fallar(id_pedido)
CALL aumentar_stock_al_fallar(1);

*/




