package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.Repartidor;
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
public class JdbcRepartidorRepository implements RepartidorRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final WKTReader wktReader = new WKTReader();
    private final WKTWriter wktWriter = new WKTWriter();

    private static final String INSERT_REPARTIDOR_SQL =
            "INSERT INTO Repartidor (rut_repartidor, password, nombre_repartidor, telefono, puntuacion_promedio, cantidad_entregas, ubicacion, distancia_recorrida) VALUES (?, ?, ?, ?, ?, ?, ST_GeomFromText(?, 4326), ?)";
    private static final String SELECT_REPARTIDOR_BY_RUT_SQL =
            "SELECT rut_repartidor, password, nombre_repartidor, telefono, puntuacion_promedio, cantidad_entregas, ST_AsText(ubicacion) as ubicacion_wkt, distancia_recorrida FROM Repartidor WHERE rut_repartidor = ?";
    private static final String SELECT_ALL_REPARTIDORES_SQL =
            "SELECT rut_repartidor, password, nombre_repartidor, telefono, puntuacion_promedio, cantidad_entregas, ST_AsText(ubicacion) as ubicacion_wkt, distancia_recorrida FROM Repartidor";
    private static final String UPDATE_REPARTIDOR_SQL =
            "UPDATE Repartidor SET password = ?, nombre_repartidor = ?, telefono = ?, puntuacion_promedio = ?, cantidad_entregas = ?, ubicacion = ST_GeomFromText(?, 4326), distancia_recorrida = ? WHERE rut_repartidor = ?";
    private static final String DELETE_REPARTIDOR_BY_RUT_SQL =
            "DELETE FROM Repartidor WHERE rut_repartidor = ?";    private final RowMapper<Repartidor> repartidorRowMapper = (rs, rowNum) -> {
        Repartidor repartidor = new Repartidor();
        repartidor.setRut(rs.getString("rut_repartidor"));
        repartidor.setPassword(rs.getString("password"));
        repartidor.setNombre(rs.getString("nombre_repartidor"));
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
        
        // Conversión de WKT a Point para ubicación
        String ubicacionWkt = rs.getString("ubicacion_wkt");
        if (ubicacionWkt != null) {
            try {
                repartidor.setUbicacion((Point) wktReader.read(ubicacionWkt));
            } catch (Exception e) {
                System.err.println("Error al convertir WKT a Point: " + e.getMessage());
                repartidor.setUbicacion(null);
            }
        } else {
            repartidor.setUbicacion(null);
        }
        
        // Obtener distancia recorrida
        double distancia = rs.getDouble("distancia_recorrida");
        if (!rs.wasNull()) {
            repartidor.setDistanciaRecorrida(distancia);
        } else {
            repartidor.setDistanciaRecorrida(null);
        }
        
        return repartidor;
    };    @Override
    public Repartidor save(Repartidor repartidor) {
        String ubicacionWkt = null;
        if (repartidor.getUbicacion() != null) {
            ubicacionWkt = wktWriter.write(repartidor.getUbicacion());
        }
        
        jdbcTemplate.update(INSERT_REPARTIDOR_SQL,
                repartidor.getRut(),
                repartidor.getPassword(),
                repartidor.getNombre(),
                repartidor.getTelefono(),
                repartidor.getPuntuacionPromedio(),
                repartidor.getCantidadEntregas(),
                ubicacionWkt,
                repartidor.getDistanciaRecorrida());
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
    }    @Override
    public int update(Repartidor repartidor) {
        if (repartidor == null || repartidor.getRut() == null) {
            throw new IllegalArgumentException("Repartidor o rut no pueden ser nulos para update");
        }
        
        String ubicacionWkt = null;
        if (repartidor.getUbicacion() != null) {
            ubicacionWkt = wktWriter.write(repartidor.getUbicacion());
        }
        
        return jdbcTemplate.update(UPDATE_REPARTIDOR_SQL,
                repartidor.getPassword(),
                repartidor.getNombre(),
                repartidor.getTelefono(),
                repartidor.getPuntuacionPromedio(),
                repartidor.getCantidadEntregas(),
                ubicacionWkt,
                repartidor.getDistanciaRecorrida(),
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