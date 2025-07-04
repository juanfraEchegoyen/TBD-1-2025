package com.app.DeliveryApp.repositories;

import com.app.DeliveryApp.models.PuntoInteres;
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
public class JdbcPuntoInteresRepository implements PuntoInteresRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final WKTReader wktReader = new WKTReader();
    private final WKTWriter wktWriter = new WKTWriter();

    private static final String INSERT_PUNTO_SQL_RETURNING_ID =
            "INSERT INTO PuntoInteres (nombre, tipo, descripcion, ubicacion) VALUES (?, ?, ?, ST_GeomFromText(?, 4326)) RETURNING id_punto_interes";
    private static final String SELECT_PUNTO_BY_ID_SQL =
            "SELECT id_punto_interes, nombre, tipo, descripcion, ST_AsText(ubicacion) as ubicacion_wkt FROM PuntoInteres WHERE id_punto_interes = ?";
    private static final String SELECT_ALL_PUNTOS_SQL =
            "SELECT id_punto_interes, nombre, tipo, descripcion, ST_AsText(ubicacion) as ubicacion_wkt FROM PuntoInteres";
    private static final String UPDATE_PUNTO_SQL =
            "UPDATE PuntoInteres SET nombre = ?, tipo = ?, descripcion = ?, ubicacion = ST_GeomFromText(?, 4326) WHERE id_punto_interes = ?";
    private static final String DELETE_PUNTO_BY_ID_SQL =
            "DELETE FROM PuntoInteres WHERE id_punto_interes = ?";
    private static final String SELECT_PUNTOS_BY_TIPO_SQL =
            "SELECT id_punto_interes, nombre, tipo, descripcion, ST_AsText(ubicacion) as ubicacion_wkt FROM PuntoInteres WHERE tipo = ?";

    private final RowMapper<PuntoInteres> puntoInteresRowMapper = (rs, rowNum) -> {
        PuntoInteres punto = new PuntoInteres();
        punto.setIdPunto(rs.getLong("id_punto_interes"));
        punto.setNombre(rs.getString("nombre"));
        punto.setTipo(rs.getString("tipo"));
        punto.setDescripcion(rs.getString("descripcion"));
        
        // Conversión de WKT a Point
        String ubicacionWkt = rs.getString("ubicacion_wkt");
        if (ubicacionWkt != null) {
            try {
                punto.setUbicacion((Point) wktReader.read(ubicacionWkt));
            } catch (Exception e) {
                System.err.println("Error al convertir WKT a Point: " + e.getMessage());
                punto.setUbicacion(null);
            }
        } else {
            punto.setUbicacion(null);
        }
        
        return punto;
    };

    @Override
    public PuntoInteres save(PuntoInteres puntoInteres) {
        try {
            String ubicacionWkt = null;
            if (puntoInteres.getUbicacion() != null) {
                ubicacionWkt = wktWriter.write(puntoInteres.getUbicacion());
            }
              Long generatedId = jdbcTemplate.queryForObject(
                    INSERT_PUNTO_SQL_RETURNING_ID,
                    Long.class,
                    puntoInteres.getNombre(),
                    puntoInteres.getTipo(),
                    puntoInteres.getDescripcion(),
                    ubicacionWkt
            );

            if (generatedId != null) {
                puntoInteres.setIdPunto(generatedId);
            } else {
                throw new RuntimeException("No se pudo obtener el id generado para punto de interés");
            }
        } catch (Exception e) {
            System.err.println("Error al guardar punto de interés: " + e.getMessage());
            throw new RuntimeException("Error en la BD al guardar el punto de interés", e);
        }

        return puntoInteres;
    }

    @Override
    public Optional<PuntoInteres> findById(Long id) {
        if (id == null) return Optional.empty();
        try {
            PuntoInteres punto = jdbcTemplate.queryForObject(SELECT_PUNTO_BY_ID_SQL, puntoInteresRowMapper, id);
            return Optional.of(punto);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<PuntoInteres> findAll() {
        return jdbcTemplate.query(SELECT_ALL_PUNTOS_SQL, puntoInteresRowMapper);
    }

    @Override
    public int update(PuntoInteres puntoInteres) {
        if (puntoInteres == null || puntoInteres.getIdPunto() == null) {
            throw new IllegalArgumentException("PuntoInteres o ID no pueden ser nulos para el update");
        }
        
        String ubicacionWkt = null;
        if (puntoInteres.getUbicacion() != null) {
            ubicacionWkt = wktWriter.write(puntoInteres.getUbicacion());
        }
          return jdbcTemplate.update(UPDATE_PUNTO_SQL,
                puntoInteres.getNombre(),
                puntoInteres.getTipo(),
                puntoInteres.getDescripcion(),
                ubicacionWkt,
                puntoInteres.getIdPunto());
    }

    @Override
    public int deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID punto no puede ser nulo para eliminar");
        }
        return jdbcTemplate.update(DELETE_PUNTO_BY_ID_SQL, id);
    }

    @Override
    public List<PuntoInteres> findByTipo(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo no puede ser nulo");
        }
        return jdbcTemplate.query(SELECT_PUNTOS_BY_TIPO_SQL, puntoInteresRowMapper, tipo);
    }

    @Override
    public List<PuntoInteres> findPuntosInteresEnRadio(Point centro, double radioMetros) {
        if (centro == null) {
            throw new IllegalArgumentException("Centro no puede ser nulo");
        }
        
        String centroWkt = wktWriter.write(centro);
        String sql = """
            SELECT id_punto_interes, nombre, tipo, descripcion, ST_AsText(ubicacion) as ubicacion_wkt
            FROM PuntoInteres
            WHERE ST_DWithin(
                ST_Transform(ubicacion, 32719),
                ST_Transform(ST_GeomFromText(?, 4326), 32719),
                ?
            )
            ORDER BY ST_Distance(ubicacion, ST_GeomFromText(?, 4326))
        """;
        
        return jdbcTemplate.query(sql, puntoInteresRowMapper, centroWkt, radioMetros, centroWkt);
    }    @Override
    public PuntoInteres findPuntoInteresmasCercano(Point ubicacion, String tipo) {
        if (ubicacion == null) {
            throw new IllegalArgumentException("Ubicación no puede ser nula");
        }
        
        String ubicacionWkt = wktWriter.write(ubicacion);
        String sql = """
            SELECT id_punto_interes, nombre, tipo, descripcion, ST_AsText(ubicacion) as ubicacion_wkt
            FROM PuntoInteres
            WHERE tipo = ?
            ORDER BY ST_Distance(ubicacion, ST_GeomFromText(?, 4326))
            LIMIT 1
        """;
        
        try {
            return jdbcTemplate.queryForObject(sql, puntoInteresRowMapper, tipo, ubicacionWkt);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<PuntoInteres> findByDistanceWithin(Point punto, double distanciaMaxima) {
        if (punto == null) {
            throw new IllegalArgumentException("Punto no puede ser nulo");
        }
        
        String puntoWkt = wktWriter.write(punto);
        String sql = """
            SELECT id_punto_interes, nombre, tipo, descripcion, ST_AsText(ubicacion) as ubicacion_wkt
            FROM PuntoInteres
            WHERE ST_DWithin(
                ST_Transform(ubicacion, 32719),
                ST_Transform(ST_GeomFromText(?, 4326), 32719),
                ?
            )
            ORDER BY ST_Distance(ubicacion, ST_GeomFromText(?, 4326))
        """;
        
        return jdbcTemplate.query(sql, puntoInteresRowMapper, puntoWkt, distanciaMaxima, puntoWkt);
    }

    @Override
    public Optional<PuntoInteres> findNearestByType(Point punto, String tipo) {
        if (punto == null) {
            throw new IllegalArgumentException("Punto no puede ser nulo");
        }
        
        String puntoWkt = wktWriter.write(punto);
        String sql = """
            SELECT id_punto_interes, nombre, tipo, descripcion, ST_AsText(ubicacion) as ubicacion_wkt
            FROM PuntoInteres
            WHERE tipo = ?
            ORDER BY ST_Distance(ubicacion, ST_GeomFromText(?, 4326))
            LIMIT 1
        """;
        
        try {
            PuntoInteres punto_cercano = jdbcTemplate.queryForObject(sql, puntoInteresRowMapper, tipo, puntoWkt);
            return Optional.of(punto_cercano);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
