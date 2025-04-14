package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Pedido; // Asegúrate que el import sea correcto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPedidoRepository implements PedidoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_PEDIDO_SQL =
            "INSERT INTO Pedido (estado_entrega, prioridad_pedido, problema_critico, rut_cliente, rut_empresa, rut_repartidor) VALUES (?, ?, ?, ?, ?, ?)";
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
        if (pedido == null || pedido.getRutRepartidor() == null) {
            throw new IllegalArgumentException("El RUT del repartidor es obligatorio para crear un pedido.");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_PEDIDO_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, pedido.getEstadoEntrega());
            ps.setString(2, pedido.getPrioridadPedido());

            ps.setBoolean(3, pedido.isProblemaCritico());

            ps.setString(4, pedido.getRutCliente());
            ps.setString(5, pedido.getRutEmpresa());
            ps.setString(6, pedido.getRutRepartidor());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            pedido.setIdPedido(keyHolder.getKey().longValue());
        } else {
            System.err.println("Advertencia: No se pudo obtener el ID generado para el pedido.");
        }
        return pedido;
    }

    @Override
    public Optional<Pedido> findById(Long id) {
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
        // Validaciones
        if (pedido == null || pedido.getIdPedido() == null) {
            throw new IllegalArgumentException("Pedido o Id pedido no pueden ser nulos para el update");
        }
        if (pedido.getRutRepartidor() == null) { // Según DDL
            throw new IllegalArgumentException("RUT repartidor obligatorio para actualizar un pedido");
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

        // TO DO: estrategia ON DELETE CASCADE en BD para borrar detalles desde servicio
        return jdbcTemplate.update(DELETE_PEDIDO_BY_ID_SQL, id);
    }
}