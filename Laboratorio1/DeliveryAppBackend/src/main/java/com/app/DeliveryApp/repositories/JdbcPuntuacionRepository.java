package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Puntuacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPuntuacionRepository implements PuntuacionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_PUNTUACION_SQL =
            "INSERT INTO Puntuacion (puntaje, comentario, rut_repartidor) VALUES (?, ?, ?)";
    private static final String SELECT_PUNTUACION_BY_ID_SQL =
            "SELECT id_puntuacion, puntaje, comentario, rut_repartidor FROM Puntuacion WHERE id_puntuacion = ?";
    private static final String SELECT_ALL_PUNTUACIONES_SQL =
            "SELECT id_puntuacion, puntaje, comentario, rut_repartidor FROM Puntuacion";
    private static final String UPDATE_PUNTUACION_SQL =
            "UPDATE Puntuacion SET puntaje = ?, comentario = ?, rut_repartidor = ? WHERE id_puntuacion = ?";
    private static final String DELETE_PUNTUACION_BY_ID_SQL =
            "DELETE FROM Puntuacion WHERE id_puntuacion = ?"; // Tabla: Puntuacion
    private static final String SELECT_PUNTUACIONES_BY_REP_RUT_SQL =
            "SELECT id_puntuacion, puntaje, comentario, rut_repartidor FROM Puntuacion WHERE rut_repartidor = ?";

    private final RowMapper<Puntuacion> puntuacionRowMapper = (rs, rowNum) -> {
        Puntuacion puntuacion = new Puntuacion();
        puntuacion.setIdPuntuacion(rs.getLong("id_puntuacion"));

        int puntajeInt = rs.getInt("puntaje");
        if (!rs.wasNull()) {
            puntuacion.setPuntaje((double) puntajeInt);
        } else {
            puntuacion.setPuntaje(null);
        }

        puntuacion.setComentario(rs.getString("comentario"));
        puntuacion.setRutRepartidor(rs.getString("rut_repartidor"));
        return puntuacion;
    };

    @Override
    public Puntuacion save(Puntuacion puntuacion) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_PUNTUACION_SQL, Statement.RETURN_GENERATED_KEYS);
            if (puntuacion.getPuntaje() != null) {
                ps.setInt(1, puntuacion.getPuntaje().intValue());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, puntuacion.getComentario());
            ps.setString(3, puntuacion.getRutRepartidor());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            puntuacion.setIdPuntuacion(keyHolder.getKey().longValue());
        } else {
            System.err.println("no obtuve el id generado para la puntuacion");
        }
        return puntuacion;
    }

    @Override
    public Optional<Puntuacion> findById(Long id) {
        try {
            Puntuacion puntuacion = jdbcTemplate.queryForObject(SELECT_PUNTUACION_BY_ID_SQL, new Object[]{id}, puntuacionRowMapper);
            return Optional.of(puntuacion);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Puntuacion> findAll() {
        return jdbcTemplate.query(SELECT_ALL_PUNTUACIONES_SQL, puntuacionRowMapper);
    }

    @Override
    public int update(Puntuacion puntuacion) {
        if (puntuacion == null || puntuacion.getIdPuntuacion() == null) {
            throw new IllegalArgumentException("Puntuación o ID puntuacion no pueden ser nulos para update");
        }
        return jdbcTemplate.update(UPDATE_PUNTUACION_SQL,
                puntuacion.getPuntaje(),
                puntuacion.getComentario(),
                puntuacion.getRutRepartidor(),
                puntuacion.getIdPuntuacion());
    }

    @Override
    public int deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID de puntuacion no debe ser nulo para eliminar");
        }
        return jdbcTemplate.update(DELETE_PUNTUACION_BY_ID_SQL, id);
    }

    @Override
    public List<Puntuacion> findByRutRepartidor(String rutRepartidor) {
        if (rutRepartidor == null) {
            throw new IllegalArgumentException("RUT repartidor no puede ser nulo para buscar puntuaciones");
        }
        return jdbcTemplate.query(SELECT_PUNTUACIONES_BY_REP_RUT_SQL, new Object[]{rutRepartidor}, puntuacionRowMapper);
    }
}