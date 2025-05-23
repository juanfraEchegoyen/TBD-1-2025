package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Repartidor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcRepartidorRepository implements RepartidorRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_REPARTIDOR_SQL =
            "INSERT INTO Repartidor (rut_repartidor, nombre_repartidor, telefono, puntuacion_promedio, cantidad_entregas) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_REPARTIDOR_BY_RUT_SQL =
            "SELECT rut_repartidor, nombre_repartidor, telefono, puntuacion_promedio, cantidad_entregas FROM Repartidor WHERE rut_repartidor = ?";
    private static final String SELECT_ALL_REPARTIDORES_SQL =
            "SELECT rut_repartidor, nombre_repartidor, telefono, puntuacion_promedio, cantidad_entregas FROM Repartidor";
    private static final String UPDATE_REPARTIDOR_SQL =
            "UPDATE Repartidor SET nombre_repartidor = ?, telefono = ?, puntuacion_promedio = ?, cantidad_entregas = ? WHERE rut_repartidor = ?";
    private static final String DELETE_REPARTIDOR_BY_RUT_SQL =
            "DELETE FROM Repartidor WHERE rut_repartidor = ?";

    private final RowMapper<Repartidor> repartidorRowMapper = (rs, rowNum) -> {
        Repartidor repartidor = new Repartidor();
        repartidor.setRut(rs.getString("rut_repartidor"));
        repartidor.setNombreRepartidor(rs.getString("nombre_repartidor"));
        repartidor.setTelefono(rs.getString("telefono"));

        int puntuacion = rs.getInt("puntuacion_promedio");
        if (!rs.wasNull()) {
            repartidor.setPuntuacionPromedio((double) puntuacion);
        } else {
            repartidor.setPuntuacionPromedio(null);
        }

        int entregas = rs.getInt("cantidad_entregas");
        if (!rs.wasNull()) {
            repartidor.setCantidadEntregas(entregas);
        } else {
            repartidor.setCantidadEntregas(null);
        }
        return repartidor;
    };

    @Override
    public Repartidor save(Repartidor repartidor) {
        jdbcTemplate.update(INSERT_REPARTIDOR_SQL,
                repartidor.getRut(),
                repartidor.getNombreRepartidor(),
                repartidor.getTelefono(),
                repartidor.getPuntuacionPromedio(),
                repartidor.getCantidadEntregas());
        return repartidor;
    }

    @Override
    public Optional<Repartidor> findByRut(String rut) {
        try {
            Repartidor repartidor = jdbcTemplate.queryForObject(SELECT_REPARTIDOR_BY_RUT_SQL, new Object[]{rut}, repartidorRowMapper);
            return Optional.of(repartidor);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Repartidor> findAll() {
        return jdbcTemplate.query(SELECT_ALL_REPARTIDORES_SQL, repartidorRowMapper);
    }

    @Override
    public int update(Repartidor repartidor) {
        if (repartidor == null || repartidor.getRut() == null) {
            throw new IllegalArgumentException("Repartido o rut  no pueden ser nulos para update");
        }
        return jdbcTemplate.update(UPDATE_REPARTIDOR_SQL,
                repartidor.getNombreRepartidor(),
                repartidor.getTelefono(),
                repartidor.getPuntuacionPromedio(),
                repartidor.getCantidadEntregas(),
                repartidor.getRut());
    }

    @Override
    public int deleteByRut(String rut) {
        if (rut == null) {
            throw new IllegalArgumentException("RUT repartidor debe ser distinto de null");
        }
        return jdbcTemplate.update(DELETE_REPARTIDOR_BY_RUT_SQL, rut);
    }
}