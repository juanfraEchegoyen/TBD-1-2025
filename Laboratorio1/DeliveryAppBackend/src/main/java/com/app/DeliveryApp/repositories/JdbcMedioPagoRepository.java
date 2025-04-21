package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.MedioPago;
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
public class JdbcMedioPagoRepository implements MedioPagoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final String INSERT_MEDIOPAGO_SQL =
            "INSERT INTO MedioDePago (nombre_mediodepago, rut_cliente) VALUES (?, ?)";
    private static final String SELECT_MEDIOPAGO_BY_ID_SQL =
            "SELECT id_mediodepago, nombre_mediodepago, rut_cliente FROM MedioDePago WHERE id_mediodepago = ?";
    private static final String SELECT_ALL_MEDIOSPAGO_SQL =
            "SELECT id_mediodepago, nombre_mediodepago, rut_cliente FROM MedioDePago";
    private static final String UPDATE_MEDIOPAGO_SQL =
            "UPDATE MedioDePago SET nombre_mediodepago = ?, rut_cliente = ? WHERE id_mediodepago = ?";
    private static final String DELETE_MEDIOPAGO_BY_ID_SQL =
            "DELETE FROM MedioDePago WHERE id_mediodepago = ?";
    private static final String SELECT_MEDIOSPAGO_BY_CLIENTE_RUT_SQL =
            "SELECT id_mediodepago, nombre_mediodepago, rut_cliente FROM MedioDePago WHERE rut_cliente = ?";

    private final RowMapper<MedioPago> medioPagoRowMapper = (rs, rowNum) -> {
        MedioPago medioPago = new MedioPago();

        medioPago.setIdMedioPago(rs.getLong("id_mediodepago"));
        medioPago.setNombreMedioPago(rs.getString("nombre_mediodepago"));
        medioPago.setRutCliente(rs.getString("rut_cliente"));
        return medioPago;
    };

    @Override
    public MedioPago save(MedioPago medioPago) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_MEDIOPAGO_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, medioPago.getNombreMedioPago());
            ps.setString(2, medioPago.getRutCliente());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            medioPago.setIdMedioPago(keyHolder.getKey().longValue());
        } else {
            System.err.println("Advertencia: No se pudo obtener el ID generado para el medio de pago.");
        }
        return medioPago;
    }

    @Override
    public Optional<MedioPago> findById(Long id) {
        try {
            MedioPago medioPago = jdbcTemplate.queryForObject(SELECT_MEDIOPAGO_BY_ID_SQL, new Object[]{id}, medioPagoRowMapper);
            return Optional.of(medioPago);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<MedioPago> findAll() {
        return jdbcTemplate.query(SELECT_ALL_MEDIOSPAGO_SQL, medioPagoRowMapper);
    }

    @Override
    public int update(MedioPago medioPago) {
        if (medioPago == null || medioPago.getIdMedioPago() == null) {
            throw new IllegalArgumentException("Medio pago o id no pueden ser nulos para el update.");
        }
        return jdbcTemplate.update(UPDATE_MEDIOPAGO_SQL,
                medioPago.getNombreMedioPago(),
                medioPago.getRutCliente(),
                medioPago.getIdMedioPago());
    }

    @Override
    public int deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del medio de pago no puede ser nulo para eliminar");
        }

        return jdbcTemplate.update(DELETE_MEDIOPAGO_BY_ID_SQL, id);
    }

    @Override
    public List<MedioPago> findByRutCliente(String rutCliente) {
        if (rutCliente == null) {
            throw new IllegalArgumentException("Rut cliente no puede ser nulo para buscar medios de pago");
        }
        return jdbcTemplate.query(SELECT_MEDIOSPAGO_BY_CLIENTE_RUT_SQL, new Object[]{rutCliente}, medioPagoRowMapper);
    }
}