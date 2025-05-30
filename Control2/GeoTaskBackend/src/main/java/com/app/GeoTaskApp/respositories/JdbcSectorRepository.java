package com.app.GeoTaskApp.respositories;

import com.app.GeoTaskApp.Models.Sector;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcSectorRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Sector> sectorRowMapper = (rs, rowNum) -> {
        Sector sector = new Sector();
        sector.setIdSector(rs.getLong("id_sector"));
        sector.setAsignacion(rs.getString("asignacion"));
        sector.setCalle(rs.getString("calle"));
        sector.setComuna(rs.getString("comuna"));

        try {
            String wkt = rs.getString("ubicacion");
            if (wkt != null && !wkt.isEmpty()) {
                sector.setUbicacionWkt(wkt);
            }
        } catch (Exception e) {
            // Manejo de error o logging
            System.err.println("Error al convertir ubicación: " + e.getMessage());
        }

        return sector;
    };

    public List<Sector> findAll() {
        String sql = "SELECT * FROM sector";
        return jdbcTemplate.query(sql, sectorRowMapper);
    }

    public Sector findById(Long id) {
        try {
            String sql = "SELECT id_sector, asignacion, calle, comuna, ST_AsText(ubicacion) AS ubicacion FROM sector WHERE id_sector = ?";
            return jdbcTemplate.queryForObject(sql, sectorRowMapper, id);
        } catch (Exception e) {
            return null;
        }
    }

    public Long save(Sector sector) {
        if (sector.getUbicacion() == null) {
            throw new IllegalArgumentException("La ubicación no puede ser nula");
        }

        String wkt = new WKTWriter().write(sector.getUbicacion());

        // Verificar si el punto está dentro de algún polígono
        String checkSql = "SELECT COUNT(*) FROM ubicacion WHERE ST_Contains(coordenadas, ST_SetSRID(ST_GeomFromText(?, 4326), 4326))";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, wkt);

        if (count == 0) {
            System.out.println("El punto no está dentro de ningún polígono, no se guarda el sector");
            return 0L;
        }

        // Guardar el sector
        String sql = "INSERT INTO sector (asignacion, comuna, calle, ubicacion) VALUES (?, ?, ?, ST_SetSRID(ST_GeomFromText(?, 4326), 4326)) RETURNING id_sector";
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                sector.getAsignacion(),
                sector.getComuna(),
                sector.getCalle(),
                wkt);
        sector.setIdSector(id);
        return id;
    }

    public int update(Sector sector) {
        // Verificar si el punto está dentro de algún polígono de la tabla ubicacion
        String checkSql = "SELECT COUNT(*) FROM ubicacion WHERE ST_Contains(coordenadas, ST_GeomFromText(?, 4326))";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, sector.getUbicacion());

        if (count == 0) {
            // El punto no está dentro de ningún polígono, no se actualiza
            return 0;
        }

        String sql = "UPDATE sector SET asignacion = ?, comuna = ?, calle = ?, ubicacion = ST_GeomFromText(?, 4326) WHERE id_sector = ?";
        return jdbcTemplate.update(sql, sector.getAsignacion(), sector.getComuna(), sector.getCalle(), sector.getUbicacion(), sector.getIdSector());
    }

    public int delete(Long id) {
        String sql = "DELETE FROM sector WHERE id_sector = ?";
        return jdbcTemplate.update(sql, id);
    }
}
