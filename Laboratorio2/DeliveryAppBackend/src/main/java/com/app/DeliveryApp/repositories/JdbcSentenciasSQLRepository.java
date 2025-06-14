package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.dto.*;
import com.app.DeliveryApp.models.ZonaCobertura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.locationtech.jts.geom.MultiPolygon;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

@Repository
public class JdbcSentenciasSQLRepository implements SentenciasSQLRepository{

    @Autowired
    private JdbcTemplate jdbcTemplate;


    //Bonus Generar un ranking de productos o servicios según devoluciones o cancelaciones.
    private static String SELECT_RANKING_DEVOLUCIONES_O_CANCELACIONES = """
            SELECT pr.nombre_producto, pr.categoria,
                COUNT(CASE WHEN p.estado_entrega = 'Devolución' THEN 1 END) AS devoluciones,
                COUNT(CASE WHEN p.estado_entrega = 'Cancelada' THEN 1 END) AS cancelaciones,
                COUNT(*) AS total_problemas
            FROM Producto pr
            JOIN DetallePedido dp ON pr.id_producto = dp.id_producto
            JOIN Pedido p ON dp.id_pedido = p.id_pedido
            WHERE p.estado_entrega IN ('Cancelada', 'Devolución')
            GROUP BY pr.nombre_producto, pr.categoria
            ORDER BY total_problemas DESC
            """;

    private static final String SELECT_CLIENTE_CON_MAYOR_GASTOS_SQL = """
            SELECT c.nombre_cliente, SUM(dp.precio_total) AS dinero_gastado
            FROM Cliente AS c
            JOIN Pedido AS p ON c.rut_cliente = p.rut_cliente
            JOIN DetallePedido AS dp ON p.id_pedido = dp.id_pedido
            WHERE p.estado_entrega LIKE 'Entregado'
            GROUP BY c.nombre_cliente
            ORDER BY dinero_gastado DESC
            LIMIT 1
            """;

    private static final String SELECT_PRODUCTOS_MAS_VENDIDOS_ULTIMO_MES_SQL = """
            SELECT pr.categoria, pr.nombre_producto, COUNT(*) AS total_pedidos
            FROM Producto pr
            JOIN DetallePedido dp ON pr.id_producto = dp.id_producto
            WHERE DATE_TRUNC('month', dp.fecha_entrega) =\s
            	(
                SELECT DATE_TRUNC('month', MAX(fecha_entrega)) FROM DetallePedido
            	)
            GROUP BY pr.categoria, pr.nombre_producto
            ORDER BY total_pedidos DESC
            """;

    public static final String SELECT_EMPRESAS_ENTREGAS_FALLIDAS_SQL = """
            SELECT e.nombre_empresa, COUNT(*) AS total_entregas_fallidas
            FROM Pedido AS p
            JOIN EmpresaAsociada AS e ON p.rut_empresa = e.rut_empresa
            WHERE p.estado_entrega = 'Entrega fallida'
            GROUP BY e.nombre_empresa
            ORDER BY total_entregas_fallidas DESC
            """;

    public static final String SELECT_TIEMPO_PROMEDIO_REPARTIDOR_SQL = """
            SELECT r.rut_repartidor, r.nombre_repartidor, AVG(dp.tiempo_entrega) AS tiempo_promedio
            FROM DetallePedido AS dp
            JOIN Pedido AS p ON p.id_pedido = dp.id_pedido
            JOIN Repartidor AS r ON r.rut_repartidor = p.rut_repartidor
            WHERE p.estado_entrega = 'Entregado'
            GROUP BY r.rut_repartidor
            """;

    public static final String SELECT_REPARTIDORES_MEJOR_RENDIMIENTO_SQL = """
            SELECT nombre_repartidor,\s
            	MAX(puntuacion_promedio) AS puntuacion,\s
            	MAX(cantidad_entregas) AS entregas
            FROM Repartidor
            GROUP BY nombre_repartidor
            ORDER BY puntuacion DESC
            LIMIT 3
            """;

    public static final String SELECT_METODO_PAGO_FRECUENTE_SQL = """
            SELECT mp.nombre_mediodepago, COUNT(*) AS cantidad_usos
            FROM MedioDePago AS mp
            JOIN Pedido AS p ON p.id_pedido = mp.id_pedido
            WHERE p.prioridad_pedido = 'Urgente'
            GROUP BY mp.nombre_mediodepago
            ORDER BY cantidad_usos DESC
            LIMIT 1
            """;

    public static final String SELECT_ZONA_PERTENECE_CLIENTE_SQL = """
            SELECT z.zona, z.area_cobertura
            FROM ZonaCobertura AS z
            JOIN Cliente AS c ON ST_Within(c.ubicacion, z.area_cobertura)
            WHERE c.rut_cliente = ?
            """;


    @Override
    public ClienteGastoDTO getClienteConMayorGastos() {
        try {
            return jdbcTemplate.queryForObject(SELECT_CLIENTE_CON_MAYOR_GASTOS_SQL,
                    (rs, rowNum) -> new ClienteGastoDTO(
                            rs.getString("nombre_cliente"),
                            rs.getInt("dinero_gastado")
                    )
            );
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener el cliente con mayor gasto", ex);
        }
    }

    @Override
    public List<ProductoMasVendidoDTO> getProductosMasVendidosUltimoMes() {
        try {
            return jdbcTemplate.query(SELECT_PRODUCTOS_MAS_VENDIDOS_ULTIMO_MES_SQL,
                    (rs, rowNum) -> new ProductoMasVendidoDTO(
                            rs.getString("categoria"),
                            rs.getString("nombre_producto"),
                            rs.getInt("total_pedidos")
                    )
            );
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener los productos más vendidos", ex);
        }
    }

    @Override
    public List<EmpresaEntregasFallidasDTO> getEmpresasEntregasFallidas() {
        try {
            return jdbcTemplate.query(SELECT_EMPRESAS_ENTREGAS_FALLIDAS_SQL,
                    (rs, rowNum) -> new EmpresaEntregasFallidasDTO(
                            rs.getString("nombre_empresa"),
                            rs.getInt("total_entregas_fallidas")
                    )
            );
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener empresas con entregas fallidas", ex);
        }
    }

    @Override
    public List<RepartidorTiempoPromedioDTO> getTiempoPromedioRepartidor() {
        try {
            return jdbcTemplate.query(SELECT_TIEMPO_PROMEDIO_REPARTIDOR_SQL,
                    (rs, rowNum) -> new RepartidorTiempoPromedioDTO(
                            rs.getString("rut_repartidor"),
                            rs.getString("nombre_repartidor"),
                            rs.getDouble("tiempo_promedio")
                    )
            );
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener el tiempo promedio de los repartidores", ex);
        }
    }

    @Override
    public List<RepartidorMejorRendimientoDTO> getRepartidoresMejorRendimiento() {
        try {
            return jdbcTemplate.query(SELECT_REPARTIDORES_MEJOR_RENDIMIENTO_SQL,
                    (rs, rowNum) -> new RepartidorMejorRendimientoDTO(
                            rs.getString("nombre_repartidor"),
                            rs.getDouble("puntuacion"),
                            rs.getInt("entregas")
                    )
            );
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener los repartidores con mejor rendimiento", ex);
        }
    }

    @Override
    public MetodoPagoFrecuenteDTO getMetodoPagoFrecuente(){
        try {
            return jdbcTemplate.queryForObject(SELECT_METODO_PAGO_FRECUENTE_SQL,
                    (rs, rowNum) -> new MetodoPagoFrecuenteDTO(
                            rs.getString("nombre_mediodepago"),
                            rs.getInt("cantidad_usos")
                    )
            );
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener el método de pago más frecuente", ex);
        }
    }

    @Override
    public List<RankingBonusDTO> getRankingDevolucionesOCancelaciones() {
        try {
            return jdbcTemplate.query(SELECT_RANKING_DEVOLUCIONES_O_CANCELACIONES,
                    (rs, rowNum) -> new RankingBonusDTO(
                            rs.getString("nombre_producto"),
                            rs.getString("categoria"),
                            rs.getInt("devoluciones"),
                            rs.getInt("cancelaciones"),
                            rs.getInt("total_problemas")
                    )
            );  // Añade este paréntesis de cierre
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener el ranking de devoluciones o cancelaciones", ex);
        }
    }


    //LAB 2

    //1. Obtener los 5 puntos de entrega más cercanos a una empresa
    private static final String SELECT_ENTREGAS_CERCANAS_SQL = """
    WITH empresa_seleccionada AS (
        SELECT ubicacion,rut_empresa FROM EmpresaAsociada WHERE rut_empresa = ?
    ),
    entregas_pendientes AS (
        SELECT c.rut_cliente, c.ubicacion, p.id_pedido,p.rut_empresa
        FROM Cliente c
        JOIN Pedido p ON c.rut_cliente = p.rut_cliente
        WHERE p.estado_entrega = 'Pendiente'
    )
    SELECT ep.rut_cliente, ep.id_pedido, ST_AsText(ep.ubicacion) AS ubicacion
    FROM entregas_pendientes ep, empresa_seleccionada es
    WHERE ep.rut_empresa = es.rut_empresa
    ORDER BY ST_Distance(ep.ubicacion, es.ubicacion) ASC
    LIMIT 5;
    """;


    @Override
    public List<EntregaDTO> obtenerEntregasCercanas(String rutEmpresa) {
        try {
            return jdbcTemplate.query(SELECT_ENTREGAS_CERCANAS_SQL,
                    ps -> ps.setString(1, rutEmpresa),
                    (rs, rowNum) -> new EntregaDTO(
                            rs.getString("rut_cliente"),
                            rs.getInt("id_pedido"),
                            rs.getString("ubicacion") // Ahora recibe la ubicación en WKT
                    )
            );
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener entregas cercanas", ex);
        }
    }




    // 2. Obtener zonas de cobertura por cliente

    private static final String SELECT_ZONAS_Y_UBICACION_CLIENTE = """
    SELECT ST_AsText(z.area_cobertura) AS areaCoberturaWkt, ST_AsText(c.ubicacion) AS ubicacionClienteWkt
    FROM ZonaCobertura z
    JOIN Cliente c ON c.rut_cliente = ?
    WHERE ST_Within(c.ubicacion, z.area_cobertura)
""";


    //3. Calcular la distancia total recorrida por un repartidor
    private static final String SELECT_DISTANCIA_RECORRIDA_SQL = """
    SELECT p.rut_repartidor, COUNT(*) AS pedidos_entregados, 
           SUM(ST_Distance(c.ubicacion, e.ubicacion)) AS distancia_total_km
    FROM Pedido p
    JOIN Cliente c ON p.rut_cliente = c.rut_cliente
    JOIN EmpresaAsociada e ON p.rut_empresa = e.rut_empresa
    WHERE p.fecha_pedido >= NOW() - INTERVAL '1 month'
          AND p.estado_entrega = 'Entregado'
          AND p.rut_repartidor = ?
    GROUP BY p.rut_repartidor
    ORDER BY distancia_total_km DESC;
""";


    @Override
    public DistanciaDTO calcularDistanciaRepartidor(String rutRepartidor) {
        try {
            return jdbcTemplate.queryForObject(SELECT_DISTANCIA_RECORRIDA_SQL,
                    new Object[]{rutRepartidor},
                    (rs, rowNum) -> new DistanciaDTO(
                            rs.getString("rut_repartidor"),
                            rs.getInt("pedidos_entregados"),
                            rs.getDouble("distancia_total_km")
                    )
            );

        } catch (EmptyResultDataAccessException ex) {
            return null; // Si no hay resultados, retorna null
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al calcular distancia recorrida", ex);
        }
    }




    //5. Listar pedidos que cruzan más de 2 zonas de reparto

    private static final String SELECT_PEDIDOS_CRUZAN_ZONAS_SQL = """
    SELECT p.id_pedido, p.rut_repartidor, COUNT(DISTINCT z.id_zona_cobertura) AS zonas_cruzadas
    FROM Pedido p
    JOIN ZonaCobertura z ON ST_Intersects(p.rutas_estimadas, z.area_cobertura)
    GROUP BY p.id_pedido, p.rut_repartidor
    HAVING COUNT(DISTINCT z.id_zona_cobertura) > 2
    ORDER BY zonas_cruzadas DESC;
""";

    @Override
    public List<PedidoZonasDTO> obtenerPedidosQueCruzaronZonas() {
        try {
            return jdbcTemplate.query(SELECT_PEDIDOS_CRUZAN_ZONAS_SQL,
                    (rs, rowNum) -> new PedidoZonasDTO(
                            rs.getInt("id_pedido"),
                            rs.getString("rut_repartidor"),
                            rs.getInt("zonas_cruzadas")
                    )
            );
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener pedidos que cruzan zonas", ex);
        }
    }



    // 6. Determinar los clientes que están a más de 5km de cualquier empresa
    private static final String SELECT_CLIENTES_MAS_5KM_EMPRESA = """
    SELECT c.rut_cliente, c.nombre_cliente, ST_AsText(c.ubicacion) AS ubicacion_cliente_wkt
    FROM Cliente c
    WHERE EXISTS (
        SELECT 1
        FROM EmpresaAsociada e
        WHERE ST_Distance(c.ubicacion, e.ubicacion) > 5000
    )
    """;

    //2. Query para obtener zonas de cobertura y ubicación del cliente
    public List<ZonaCoberturaClienteDTO> getZonasCoberturaYUbicacionPorCliente(String rutCliente) {
        try {
            return jdbcTemplate.query(
                    SELECT_ZONAS_Y_UBICACION_CLIENTE,
                    new Object[]{rutCliente},
                    (rs, rowNum) -> new ZonaCoberturaClienteDTO(
                            rs.getString("areaCoberturaWkt"),
                            rs.getString("ubicacionClienteWkt")
                    )
            );
        } catch (DataAccessException ex) {
            return List.of();
        }
    }
    //6. Query para obtener clientes a más de 5km de cualquier empresa
    @Override
    public List<ClienteLejanoDTO> getClientesAMasDe5KmDeEmpresa() {
        try {
            return jdbcTemplate.query(SELECT_CLIENTES_MAS_5KM_EMPRESA,
                    (rs, rowNum) -> new ClienteLejanoDTO(
                            rs.getString("rut_cliente"),
                            rs.getString("nombre_cliente"),
                            rs.getString("ubicacion_cliente_wkt")
                    )
            );
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener clientes lejanos", ex);
        }
    }

    // Query que calcula automáticamente la zona a la que pertenece un cliente
    @Override
    public String getZonaPerteneceCliente(String rutCliente) {
        try {
            return jdbcTemplate.query(SELECT_ZONA_PERTENECE_CLIENTE_SQL,
                    ps -> ps.setString(1, rutCliente),
                    rs -> {
                        if (rs.next()) {
                            return rs.getString("zona");
                        } else {
                            return null; // Si no se encuentra la zona, retorna null
                        }
                    }
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new RuntimeException("Error al obtener la zona del cliente", e);
        }
    }

}
