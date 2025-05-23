package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcClienteRepository implements ClienteRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_CLIENTE_SQL =
            "INSERT INTO Cliente (rut_cliente, nombre_cliente, telefono, direccion, comuna) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_CLIENTE_BY_RUT_SQL =
            "SELECT rut_cliente, nombre_cliente, telefono, direccion, comuna FROM Cliente WHERE rut_cliente = ?";
    private static final String SELECT_ALL_CLIENTES_SQL =
            "SELECT rut_cliente, nombre_cliente, telefono, direccion, comuna FROM Cliente";
    private static final String UPDATE_CLIENTE_SQL =
            "UPDATE Cliente SET nombre_cliente = ?, telefono = ?, direccion = ?, comuna = ? WHERE rut_cliente = ?";
    private static final String DELETE_CLIENTE_BY_RUT_SQL =
            "DELETE FROM Cliente WHERE rut_cliente = ?"; // Tabla: Cliente

    private final RowMapper<Cliente> clienteRowMapper = (rs, rowNum) -> {
        Cliente cliente = new Cliente();
        cliente.setRut(rs.getString("rut_cliente"));
        cliente.setNombre(rs.getString("nombre_cliente"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setDireccion(rs.getString("direccion"));
        cliente.setComuna(rs.getString("comuna"));
        return cliente;
    };

    @Override
    public Cliente save(Cliente cliente) {
        jdbcTemplate.update(INSERT_CLIENTE_SQL,
                cliente.getRut(),
                cliente.getNombre(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getComuna());
        return cliente;
    }

    @Override
    public Optional<Cliente> findByRut(String rut) {
        try {
            Cliente cliente = jdbcTemplate.queryForObject(SELECT_CLIENTE_BY_RUT_SQL, new Object[]{rut}, clienteRowMapper);
            return Optional.of(cliente);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Cliente> findAll() {
        return jdbcTemplate.query(SELECT_ALL_CLIENTES_SQL, clienteRowMapper);
    }

    @Override
    public int update(Cliente cliente) {
        if (cliente == null || cliente.getRut() == null) {
            throw new IllegalArgumentException("RUT o cliente no pueden ser nulos para el update");
        }
        return jdbcTemplate.update(UPDATE_CLIENTE_SQL,
                cliente.getNombre(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getComuna(),
                cliente.getRut());
    }

    @Override
    public int deleteByRut(String rut) {
        if (rut == null) {
            throw new IllegalArgumentException("RUT cliente no puede ser nulo para eliminar");
        }
        return jdbcTemplate.update(DELETE_CLIENTE_BY_RUT_SQL, rut);
    }
}