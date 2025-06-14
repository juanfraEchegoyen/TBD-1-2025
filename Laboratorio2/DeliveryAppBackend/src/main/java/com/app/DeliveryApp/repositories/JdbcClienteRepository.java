package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Cliente;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
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
    
    private final WKTReader wktReader = new WKTReader();
    private final WKTWriter wktWriter = new WKTWriter();private static final String INSERT_CLIENTE_SQL =
            "INSERT INTO Cliente (rut_cliente, password, nombre_cliente, telefono, direccion, comuna, ubicacion) VALUES (?, ?, ?, ?, ?, ?, ST_GeomFromText(?, 4326))";
    private static final String SELECT_CLIENTE_BY_RUT_SQL =
            "SELECT rut_cliente, password, nombre_cliente, telefono, direccion, comuna, ST_AsText(ubicacion) as ubicacion_wkt FROM Cliente WHERE rut_cliente = ?";
    private static final String SELECT_ALL_CLIENTES_SQL =
            "SELECT rut_cliente, password, nombre_cliente, telefono, direccion, comuna, ST_AsText(ubicacion) as ubicacion_wkt FROM Cliente";
    private static final String UPDATE_CLIENTE_SQL =
            "UPDATE Cliente SET password = ?, nombre_cliente = ?, telefono = ?, direccion = ?, comuna = ?, ubicacion = ST_GeomFromText(?, 4326) WHERE rut_cliente = ?";
    private static final String DELETE_CLIENTE_BY_RUT_SQL =
            "DELETE FROM Cliente WHERE rut_cliente = ?";    private final RowMapper<Cliente> clienteRowMapper = (rs, rowNum) -> {
        Cliente cliente = new Cliente();
        cliente.setRut(rs.getString("rut_cliente"));
        cliente.setPassword(rs.getString("password"));
        cliente.setNombre(rs.getString("nombre_cliente"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setComuna(rs.getString("comuna"));
        
        // Conversión de WKT a Point
        String ubicacionWkt = rs.getString("ubicacion_wkt");
        if (ubicacionWkt != null) {
            try {
                cliente.setUbicacion((Point) wktReader.read(ubicacionWkt));
            } catch (Exception e) {
                System.err.println("Error al convertir WKT a Point: " + e.getMessage());
                cliente.setUbicacion(null);
            }
        } else {
            cliente.setUbicacion(null);
        }
        
        return cliente;
    };

    @Override
    public Cliente save(Cliente cliente) {
        String ubicacionWkt = null;
        if (cliente.getUbicacion() != null) {
            ubicacionWkt = wktWriter.write(cliente.getUbicacion());
        }

        // Verificar si el punto está dentro de alguna área de cobertura
        String checkSql = "SELECT COUNT(*) FROM ZonaCobertura WHERE ST_Contains(area_cobertura, ST_GeomFromText(?, 4326))";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, ubicacionWkt);

        if (count == null || count == 0) {
            throw new IllegalArgumentException("La ubicación del cliente no está dentro de ninguna zona de cobertura.");
        }

        jdbcTemplate.update(INSERT_CLIENTE_SQL,
                cliente.getRut(),
                cliente.getPassword(),
                cliente.getNombre(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getComuna(),
                ubicacionWkt);
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

        String ubicacionWkt = null;
        if (cliente.getUbicacion() != null) {
            ubicacionWkt = wktWriter.write(cliente.getUbicacion());
        }

        // Verificar si el punto está dentro de la zona de cobertura de la Región Metropolitana
        String checkSql = "SELECT COUNT(*) FROM ZonaCobertura WHERE ST_Contains(area_cobertura, ST_GeomFromText(?, 4326)) AND nombre_comuna = 'Región Metropolitana'";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, ubicacionWkt);

        if (count == null || count == 0) {
            throw new IllegalArgumentException("La ubicación del cliente no está dentro de la zona de cobertura de la Región Metropolitana.");
        }

        return jdbcTemplate.update(UPDATE_CLIENTE_SQL,
                cliente.getPassword(),
                cliente.getNombre(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getComuna(),
                ubicacionWkt,
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