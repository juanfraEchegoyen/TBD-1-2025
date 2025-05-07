package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.sentenciasSQL.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class JdbcSentenciasSQLRepository implements SentenciasSQLRepository{

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
            WHERE p.prioridad_pedido = 'Alta'
            GROUP BY mp.nombre_mediodepago
            ORDER BY cantidad_usos DESC
            LIMIT 1
            """;

    @Override
    public ClienteGasto getClienteConMayorGastos() {
        try {
            return jdbcTemplate.queryForObject(SELECT_CLIENTE_CON_MAYOR_GASTOS_SQL,
                    (rs, rowNum) -> new ClienteGasto(
                            rs.getString("nombre_cliente"),
                            rs.getInt("dinero_gastado")
                    )
            );
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener el cliente con mayor gasto", ex);
        }
    }

    @Override
    public List<ProductoMasVendido> getProductosMasVendidosUltimoMes() {
        try {
            return jdbcTemplate.query(SELECT_PRODUCTOS_MAS_VENDIDOS_ULTIMO_MES_SQL,
                    (rs, rowNum) -> new ProductoMasVendido(
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
    public List<EmpresaEntregasFallidas> getEmpresasEntregasFallidas() {
        try {
            return jdbcTemplate.query(SELECT_EMPRESAS_ENTREGAS_FALLIDAS_SQL,
                    (rs, rowNum) -> new EmpresaEntregasFallidas(
                            rs.getString("nombre_empresa"),
                            rs.getInt("total_entregas_fallidas")
                    )
            );
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener empresas con entregas fallidas", ex);
        }
    }

    @Override
    public List<RepartidorTiempoPromedio> getTiempoPromedioRepartidor() {
        try {
            return jdbcTemplate.query(SELECT_TIEMPO_PROMEDIO_REPARTIDOR_SQL,
                    (rs, rowNum) -> new RepartidorTiempoPromedio(
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
    public List<RepartidorMejorRendimiento> getRepartidoresMejorRendimiento() {
        try {
            return jdbcTemplate.query(SELECT_REPARTIDORES_MEJOR_RENDIMIENTO_SQL,
                    (rs, rowNum) -> new RepartidorMejorRendimiento(
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
    public MetodoPagoFrecuente getMetodoPagoFrecuente(){
        try {
            return jdbcTemplate.queryForObject(SELECT_METODO_PAGO_FRECUENTE_SQL,
                    (rs, rowNum) -> new MetodoPagoFrecuente(
                            rs.getString("nombre_mediodepago"),
                            rs.getInt("cantidad_usos")
                    )
            );
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error al obtener el método de pago más frecuente", ex);
        }
    }

}
