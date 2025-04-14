package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.DetallePedido; // Importa tu modelo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcDetallePedidoRepository implements DetallePedidoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_DETALLE_SQL =
            "INSERT INTO DetallePedido (precio_total, tiempo_entrega, fecha_entrega, cantidad, id_pedido, id_producto) VALUES (?, ?, ?, ?, ?, ?)";
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


    private final RowMapper<DetallePedido> detallePedidoRowMapper = (rs, rowNum) -> {
        DetallePedido detalle = new DetallePedido();
        detalle.setIdDetalle(rs.getLong("id_detalle"));


        int precioInt = rs.getInt("precio_total");
        if (!rs.wasNull()) detalle.setPrecioTotal((double) precioInt);
        else detalle.setPrecioTotal(null);

        int tiempoInt = rs.getInt("tiempo_entrega");
        if (!rs.wasNull()) detalle.setTiempoEntrega(tiempoInt); else detalle.setTiempoEntrega(null);

        java.sql.Date fechaSql = rs.getDate("fecha_entrega");
        if (fechaSql != null) {
            detalle.setFechaEntrega(new java.util.Date(fechaSql.getTime()));
        } else {
            detalle.setFechaEntrega(null);
        }

        detalle.setCantidad(rs.getInt("cantidad")); // OK

        int idPedidoInt = rs.getInt("id_pedido");
        if (!rs.wasNull()) detalle.setIdPedido((long) idPedidoInt);
        else detalle.setIdPedido(null);

        int idProductoInt = rs.getInt("id_producto");
        if (!rs.wasNull()) detalle.setIdProducto((long) idProductoInt);
        else detalle.setIdProducto(null);

        return detalle;
    };

    @Override
    public DetallePedido save(DetallePedido detallePedido) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_DETALLE_SQL, Statement.RETURN_GENERATED_KEYS);


            if (detallePedido.getPrecioTotal() != null) {
                ps.setInt(1, detallePedido.getPrecioTotal().intValue());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setObject(2, detallePedido.getTiempoEntrega(), Types.INTEGER);

            if (detallePedido.getFechaEntrega() != null) {
                ps.setDate(3, new java.sql.Date(detallePedido.getFechaEntrega().getTime()));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setInt(4, detallePedido.getCantidad()); // OK

            if (detallePedido.getIdPedido() != null) {
                ps.setInt(5, detallePedido.getIdPedido().intValue());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (detallePedido.getIdProducto() != null) {
                ps.setInt(6, detallePedido.getIdProducto().intValue());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            detallePedido.setIdDetalle(keyHolder.getKey().longValue());
        } else {
            System.err.println("No se obtuvo el ID para el detalle del pedido");
        }
        return detallePedido;
    }

    @Override
    public Optional<DetallePedido> findById(Long id) {
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
                detallePedido.getPrecioTotal() != null ? detallePedido.getPrecioTotal().intValue() : null,
                detallePedido.getTiempoEntrega(),
                detallePedido.getFechaEntrega() != null ? new java.sql.Date(detallePedido.getFechaEntrega().getTime()) : null,
                detallePedido.getCantidad(),
                detallePedido.getIdPedido() != null ? detallePedido.getIdPedido().intValue() : null,
                detallePedido.getIdProducto() != null ? detallePedido.getIdProducto().intValue() : null,
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
    public List<DetallePedido> findByPedidoId(Long idPedidoLong) {
        if (idPedidoLong == null) {
            throw new IllegalArgumentException("El ID del pedido no puede ser nulo para buscar detalles.");
        }
        // Modelo FK: Long -> DDL FK: INT
        Integer idPedido = idPedidoLong.intValue(); // Convertir Long a Integer para la consulta WHERE
        return jdbcTemplate.query(SELECT_DETALLES_BY_PEDIDO_ID_SQL, new Object[]{idPedido}, detallePedidoRowMapper);
    }
}