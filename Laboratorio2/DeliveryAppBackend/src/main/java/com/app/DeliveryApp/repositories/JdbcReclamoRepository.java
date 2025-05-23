package com.app.DeliveryApp.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcReclamoRepository implements ReclamoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int countByRutCliente(String rutCliente) {
        String sql = "SELECT COUNT(*) FROM Reclamo WHERE rut_cliente = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, rutCliente);
        return count != null ? count : 0;
    }
}
