
--Creamos la funcion que realizara el trigger 
CREATE OR REPLACE FUNCTION actualizar_fecha_entrega()
RETURNS TRIGGER AS $$
BEGIN
	--si el estado es entrgado entonces se updatea la fecha de entrega
    IF NEW.estado_entrega = 'Entregado' THEN
        UPDATE DetallePedido
        SET fecha_entrega = CURRENT_DATE
        WHERE id_pedido = NEW.id_pedido;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- se crea el trigger para cada vez que se actualiza un pedido
CREATE TRIGGER trigger_actualizar_fecha_entrega
AFTER UPDATE ON Pedido
FOR EACH ROW
EXECUTE FUNCTION actualizar_fecha_entrega();


-----------
--Creamos la funcion que realizara el trigger
CREATE OR REPLACE FUNCTION registrar_notificacion()
RETURNS TRIGGER AS $$
BEGIN
		--si hay unn problema critico, crea noti que hubo un problema critico xd
    IF NEW.problema_critico = TRUE THEN
        INSERT INTO Notificacion (mensaje, id_pedido)
        VALUES ('Problema crítico', NEW.id_pedido);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--- creamos el trigger para que funncione cada vez que se inserta o actualiza un pedido
CREATE TRIGGER trigger_registrar_notificacion
AFTER INSERT OR UPDATE ON Pedido
FOR EACH ROW
EXECUTE FUNCTION registrar_notificacion();

----------------------------
--creamos la funncion que realizara el trigger

CREATE OR REPLACE FUNCTION calificacion_automatica_nopedido()
RETURNS TRIGGER AS $$
Declare
	fecha_entrega_pedido DATE;
BEGIN
	--Obtennemos la fecha de entrega desde detalle pedido
    SELECT fecha_entrega INTO fecha_entrega_pedido 
    FROM DetallePedido 
    WHERE id_pedido = NEW.id_pedido;
	
	--- si el pedido fue entregado y la fecha actual - la de entrega >= 2 entonces se incerta 
    IF NEW.estado_entrega <> 'Entregado' AND CURRENT_DATE - fecha_entrega_pedido >= 2 THEN
        INSERT INTO Puntuacion (puntaje, comentario, rut_repartidor)
        VALUES (1, 'Pedido no entregado a tiempo', NEW.rut_repartidor);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- se crea el trigger para cada vez que se actualiza un pedido
CREATE TRIGGER trigger_calificacion_automatica_nopedido
AFTER UPDATE ON Pedido
FOR EACH ROW
EXECUTE FUNCTION calificacion_automatica_nopedido();


CREATE OR REPLACE FUNCTION generar_ruta_estimada()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.rutas_estimadas IS NULL THEN
        NEW.rutas_estimadas := ST_MakeLine(
            ARRAY[
                (SELECT ubicacion FROM EmpresaAsociada WHERE rut_empresa = NEW.rut_empresa),
                (SELECT ubicacion FROM Cliente WHERE rut_cliente = NEW.rut_cliente)
            ]
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER generar_ruta_trigger
BEFORE INSERT ON Pedido
FOR EACH ROW
EXECUTE FUNCTION generar_ruta_estimada();



/*
--Probamos el de noti
CALL registrar_pedido(
    'En proceso', 'Alta', TRUE, '11111111-1', '80000000-1', '12121212-1', 
    10000, 30, '2025-05-00', 2, 1
);

--probamos el de actualizar fecha
CALL actualizar_estado_pedido(1,'Entregado');
*/

