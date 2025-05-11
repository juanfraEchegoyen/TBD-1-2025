package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.DetallePedido;
import com.app.DeliveryApp.models.MedioPago;
import com.app.DeliveryApp.models.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPedidoRepository implements PedidoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final String INSERT_PEDIDO_SQL_RETURNING_ID =
            "INSERT INTO Pedido (estado_entrega, prioridad_pedido, problema_critico, rut_cliente, rut_empresa, rut_repartidor) VALUES (?, ?, ?, ?, ?, ?) RETURNING id_pedido";
    private static final String SELECT_PEDIDO_BY_ID_SQL =
            "SELECT id_pedido, estado_entrega, prioridad_pedido, problema_critico, rut_cliente, rut_empresa, rut_repartidor FROM Pedido WHERE id_pedido = ?";
    private static final String SELECT_ALL_PEDIDOS_SQL =
            "SELECT id_pedido, estado_entrega, prioridad_pedido, problema_critico, rut_cliente, rut_empresa, rut_repartidor FROM Pedido";
    private static final String UPDATE_PEDIDO_SQL =
            "UPDATE Pedido SET estado_entrega = ?, prioridad_pedido = ?, problema_critico = ?, rut_cliente = ?, rut_empresa = ?, rut_repartidor = ? WHERE id_pedido = ?";
    private static final String DELETE_PEDIDO_BY_ID_SQL =
            "DELETE FROM Pedido WHERE id_pedido = ?";

    private final RowMapper<Pedido> pedidoRowMapper = (rs, rowNum) -> {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(rs.getLong("id_pedido"));
        pedido.setEstadoEntrega(rs.getString("estado_entrega"));
        pedido.setPrioridadPedido(rs.getString("prioridad_pedido"));
        pedido.setProblemaCritico(rs.getBoolean("problema_critico"));
        pedido.setRutCliente(rs.getString("rut_cliente"));
        pedido.setRutEmpresa(rs.getString("rut_empresa"));
        pedido.setRutRepartidor(rs.getString("rut_repartidor"));
        return pedido;
    };

    @Override
    public Pedido save(Pedido pedido) {
        try {
            Long generatedId = jdbcTemplate.queryForObject(
                    INSERT_PEDIDO_SQL_RETURNING_ID,
                    new Object[]{
                            pedido.getEstadoEntrega(),
                            pedido.getPrioridadPedido(),
                            pedido.isProblemaCritico(),
                            pedido.getRutCliente(),
                            pedido.getRutEmpresa(),
                            pedido.getRutRepartidor()
                    },
                    Long.class
            );

            if (generatedId != null) {
                pedido.setIdPedido(generatedId);
            } else {
                System.err.println("error queryForObject con returning devolvio null");
                throw new RuntimeException("No se pudo obtener el id generado para pedido");
            }
        } catch (Exception e) {
            System.err.println("Error al guardar pedido y obtener el id con returning: " + e.getMessage());
            throw new RuntimeException("Error en la BD al guardar el pedido", e);
        }

        return pedido;
    }


    public void RegistrarPedido(Pedido pedido, DetallePedido detalle, MedioPago medioPago) {
        String sql = "CALL registrar_pedido(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Convertir Double a Integer para precio_total
        Integer precioTotal = detalle.getPrecioTotal().intValue();

        // Convertir java.util.Date a java.sql.Date
        java.sql.Date sqlFechaEntrega = new java.sql.Date(detalle.getFechaEntrega().getTime());

        // Convertir Long a Integer para idProducto
        Integer idProducto = detalle.getIdProducto().intValue();

        jdbcTemplate.update(sql,
                pedido.getEstadoEntrega(),
                pedido.getPrioridadPedido(),
                pedido.isProblemaCritico(),
                pedido.getRutCliente(),
                pedido.getRutEmpresa(),
                pedido.getRutRepartidor(),
                precioTotal,           // Double → Integer
                detalle.getTiempoEntrega(),
                sqlFechaEntrega,       // java.util.Date → java.sql.Date
                detalle.getCantidad(),
                idProducto,             // Long → Integer
                medioPago.getNombreMedioPago()
        );
    }

    public void actualizarEstadoPedido(Integer idPedido, String nuevoEstado) {
        String sql = "CALL actualizar_estado_pedido(?, ?)";
        jdbcTemplate.update(sql, idPedido, nuevoEstado);
    }

    public void Aumentar_stock_al_fallar(Integer idPedido) {
        String sql = "CALL Aumentar_stock_al_fallar(?)";
        jdbcTemplate.update(sql, idPedido);
    }

    public void descontar_stock_al_confirmar(Integer idPedido) {
        String sql = "CALL descontar_stock_al_confirmar(?)";
        jdbcTemplate.update(sql, idPedido);
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        if (id == null) return Optional.empty();
        try {
            Pedido pedido = jdbcTemplate.queryForObject(SELECT_PEDIDO_BY_ID_SQL, new Object[]{id}, pedidoRowMapper);
            return Optional.of(pedido);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Pedido> findAll() {
        return jdbcTemplate.query(SELECT_ALL_PEDIDOS_SQL, pedidoRowMapper);
    }

    @Override
    public int update(Pedido pedido) {
        if (pedido == null || pedido.getIdPedido() == null) {
            throw new IllegalArgumentException("Pedido o Id pedido no pueden ser nulos para el update");
        }
        return jdbcTemplate.update(UPDATE_PEDIDO_SQL,
                pedido.getEstadoEntrega(),
                pedido.getPrioridadPedido(),
                pedido.isProblemaCritico(),
                pedido.getRutCliente(),
                pedido.getRutEmpresa(),
                pedido.getRutRepartidor(),
                pedido.getIdPedido());
    }

    @Override
    public int deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID pedido no puede ser nulo para eliminar");
        }
        return jdbcTemplate.update(DELETE_PEDIDO_BY_ID_SQL, id);
    }

    @Override
    public int countByRutCliente(String rutCliente) {
        String sql = "SELECT COUNT(*) FROM Pedido WHERE rut_cliente = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, rutCliente);
        return count != null ? count : 0;
    }

    @Override
    public int countByRutClienteAndEstado(String rutCliente, String estado) {
        String sql = "SELECT COUNT(*) FROM Pedido WHERE rut_cliente = ? AND estado_entrega = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, rutCliente, estado);
        return count != null ? count : 0;
    }
}