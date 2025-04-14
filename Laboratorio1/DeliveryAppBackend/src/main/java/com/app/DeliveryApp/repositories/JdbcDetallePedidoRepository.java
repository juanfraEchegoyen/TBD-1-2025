package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.DetallePedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
// Quitar KeyHolder si ya no se usa
// import org.springframework.jdbc.support.GeneratedKeyHolder;
// import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*; // Mantenido por Types.* aunque no se use en save ahora
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcDetallePedidoRepository implements DetallePedidoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private static final String INSERT_DETALLE_SQL_RETURNING_ID =
            "INSERT INTO DetallePedido (precio_total, tiempo_entrega, fecha_entrega, cantidad, id_pedido, id_producto) VALUES (?, ?, ?, ?, ?, ?) RETURNING id_detalle";
    private static final String SELECT_DETALLE_BY_ID_SQL =
            "SELECT id_detalle, precio_total, tiempo_entrega, fecha_entrega, cantidad, id_pedido, id_producto FROM DetallePedido WHERE id_detalle = ?";
    private static final String SELECT_ALL_DETALLES_SQL =
            "SELECT id_detalle, precio_total, tiempo_entrega, fecha_entrega, cantidad, id_pedido, id_producto FROM DetallePedido";
    private static final String UPDATE_DETALLE_SQL =
            "UPDATE DetallePedido SET precio_total = ?, tiempo_entrega = ?, fecha_entrega = ?, cantidad = ?, id_pedido = ?, id_producto = ? WHERE id_detalle = ?";
    private static final String DELETE_DETALLE_BY_ID_SQL =
            "DELETE FROM DetallePedido WHERE id_detalle = ?";
    private static final String SELECT_DETALLES_BY_PEDIDO_ID_SQL =
            "SELECT id_detalle, precio_total, tiempo_entrega, fecha_entrega, cantidad, id_pedido, id_producto FROM DetallePedido WHERE id_pedido = ?";
    private static final String DELETE_DETALLES_BY_PEDIDO_ID_SQL =
            "DELETE FROM DetallePedido WHERE id_pedido = ?";


    private final RowMapper<DetallePedido> detallePedidoRowMapper = (rs, rowNum) -> {
        DetallePedido detalle = new DetallePedido();
        detalle.setIdDetalle(rs.getLong("id_detalle"));

        int precioInt = rs.getInt("precio_total");
        if (!rs.wasNull()) detalle.setPrecioTotal((double) precioInt);
        else detalle.setPrecioTotal(null);

        int tiempoInt = rs.getInt("tiempo_entrega");
        if (!rs.wasNull()) detalle.setTiempoEntrega(tiempoInt);
        else detalle.setTiempoEntrega(null);

        java.sql.Date fechaSql = rs.getDate("fecha_entrega");
        if (fechaSql != null) {
            detalle.setFechaEntrega(new java.util.Date(fechaSql.getTime()));
        } else {
            detalle.setFechaEntrega(null);
        }

        detalle.setCantidad(rs.getInt("cantidad"));

        long idPedidoLong = rs.getLong("id_pedido");
        if (!rs.wasNull()) detalle.setIdPedido(idPedidoLong);
        else detalle.setIdPedido(null);

        long idProductoLong = rs.getLong("id_producto");
        if (!rs.wasNull()) detalle.setIdProducto(idProductoLong);
        else detalle.setIdProducto(null);

        return detalle;
    };


    @Override
    public DetallePedido save(DetallePedido detallePedido) {
        try {
            Long generatedId = jdbcTemplate.queryForObject(
                    INSERT_DETALLE_SQL_RETURNING_ID,
                    new Object[]{
                            detallePedido.getPrecioTotal(),
                            detallePedido.getTiempoEntrega(),
                            detallePedido.getFechaEntrega() != null ? new java.sql.Date(detallePedido.getFechaEntrega().getTime()) : null,
                            detallePedido.getCantidad(),
                            detallePedido.getIdPedido(),
                            detallePedido.getIdProducto()
                    },
                    Long.class
            );

            if (generatedId != null) {
                detallePedido.setIdDetalle(generatedId);
            } else {
                System.err.println("ERROR de queryForObject con RETURNING devolvió null para DetallePedido");
                throw new RuntimeException("No se pudo obtener el id para detalle del pedido");
            }
        } catch (Exception e) {
            System.err.println("Error al guardar detalle de pedido y obtener el id con RETURNING " + e.getMessage());
            throw new RuntimeException("Error en la BD al guardar el detalle del pedido", e);
        }
        return detallePedido;
    }

    @Override
    public Optional<DetallePedido> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            DetallePedido detalle = jdbcTemplate.queryForObject(SELECT_DETALLE_BY_ID_SQL, new Object[]{id}, detallePedidoRowMapper);
            return Optional.of(detalle);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<DetallePedido> findAll() {
        return jdbcTemplate.query(SELECT_ALL_DETALLES_SQL, detallePedidoRowMapper);
    }

    @Override
    public int update(DetallePedido detallePedido) {
        if (detallePedido == null || detallePedido.getIdDetalle() == null) {
            throw new IllegalArgumentException("Detalle o Id detalle no pueden ser nulos para el update");
        }
        return jdbcTemplate.update(UPDATE_DETALLE_SQL,
                detallePedido.getPrecioTotal(),
                detallePedido.getTiempoEntrega(),
                detallePedido.getFechaEntrega() != null ? new java.sql.Date(detallePedido.getFechaEntrega().getTime()) : null,
                detallePedido.getCantidad(),
                detallePedido.getIdPedido(),
                detallePedido.getIdProducto(),
                detallePedido.getIdDetalle());
    }

    @Override
    public int deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del detalle no puede ser nulo para eliminar.");
        }
        return jdbcTemplate.update(DELETE_DETALLE_BY_ID_SQL, id);
    }

    @Override
    public List<DetallePedido> findByPedidoId(Long idPedido) {
        if (idPedido == null) {
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo para buscar detalles.");
        }
        return jdbcTemplate.query(SELECT_DETALLES_BY_PEDIDO_ID_SQL, new Object[]{idPedido}, detallePedidoRowMapper);
    }

    @Override
    public int deleteByPedidoId(Long pedidoId) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo para eliminar detalles.");
        }
        return jdbcTemplate.update(DELETE_DETALLES_BY_PEDIDO_ID_SQL, pedidoId);
    }
}