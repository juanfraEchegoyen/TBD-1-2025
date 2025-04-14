package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Empresa; // Importa tu modelo Empresa
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcEmpresaRepository implements EmpresaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_EMPRESA_SQL =
            "INSERT INTO EmpresaAsociada (rut_empresa, nombre_empresa) VALUES (?, ?)";
    private static final String SELECT_EMPRESA_BY_RUT_SQL =
            "SELECT rut_empresa, nombre_empresa FROM EmpresaAsociada WHERE rut_empresa = ?";
    private static final String SELECT_ALL_EMPRESAS_SQL =
            "SELECT rut_empresa, nombre_empresa FROM EmpresaAsociada";
    private static final String UPDATE_EMPRESA_SQL =
            "UPDATE EmpresaAsociada SET nombre_empresa = ? WHERE rut_empresa = ?";
    private static final String DELETE_EMPRESA_BY_RUT_SQL =
            "DELETE FROM EmpresaAsociada WHERE rut_empresa = ?";

    private final RowMapper<Empresa> empresaRowMapper = (rs, rowNum) -> {
        Empresa empresa = new Empresa();
        empresa.setRut(rs.getString("rut_empresa"));
        empresa.setNombre(rs.getString("nombre_empresa"));
        return empresa;
    };

    @Override
    public Empresa save(Empresa empresa) {
        jdbcTemplate.update(INSERT_EMPRESA_SQL,
                empresa.getRut(),
                empresa.getNombre());
        return empresa;
    }

    @Override
    public Optional<Empresa> findByRut(String rut) {
        try {
            Empresa empresa = jdbcTemplate.queryForObject(SELECT_EMPRESA_BY_RUT_SQL, new Object[]{rut}, empresaRowMapper);
            return Optional.of(empresa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Empresa> findAll() {
        return jdbcTemplate.query(SELECT_ALL_EMPRESAS_SQL, empresaRowMapper);
    }

    @Override
    public int update(Empresa empresa) {
        if (empresa == null || empresa.getRut() == null) { // Usa getRut
            throw new IllegalArgumentException("Empresa o RUT empresa no pueden ser nulos para el update");
        }
        return jdbcTemplate.update(UPDATE_EMPRESA_SQL,
                empresa.getNombre(),
                empresa.getRut());

    }

    @Override
    public int deleteByRut(String rut) {
        if (rut == null) {
            throw new IllegalArgumentException("RUT empresa no puede ser nulo para eliminar");
        }
        return jdbcTemplate.update(DELETE_EMPRESA_BY_RUT_SQL, rut);
    }
}