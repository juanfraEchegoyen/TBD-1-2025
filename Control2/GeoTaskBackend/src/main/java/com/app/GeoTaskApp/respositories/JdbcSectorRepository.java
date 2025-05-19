package com.app.GeoTaskApp.respositories;

import com.app.GeoTaskApp.models.Sector;
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

    private RowMapper<Sector> sectorRowMapper = new RowMapper<Sector>() {
        @Override
        public Sector mapRow(ResultSet rs, int rowNum) throws SQLException {
            Sector sector = new Sector();
            sector.setIdSector(rs.getLong("id_sector"));
            sector.setNombre(rs.getString("nombre"));
            sector.setUbicacion(rs.getString("ubicacion"));
            return sector;
        }
    };

    public List<Sector> findAll() {
        String sql = "SELECT * FROM sector";
        return jdbcTemplate.query(sql, sectorRowMapper);
    }

    public Sector findById(Long id) {
        String sql = "SELECT * FROM sector WHERE id_sector = ?";
        return jdbcTemplate.queryForObject(sql, sectorRowMapper, id);
    }

    public int save(Sector sector) {
        String sql = "INSERT INTO sector (nombre, ubicacion) VALUES (?, ST_GeomFromText(?, 4326))";
        return jdbcTemplate.update(sql, sector.getNombre(), sector.getUbicacion());
    }

    public int update(Sector sector) {
        String sql = "UPDATE sector SET nombre = ?, ubicacion = ST_GeomFromText(?, 4326) WHERE id_sector = ?";
        return jdbcTemplate.update(sql, sector.getNombre(), sector.getUbicacion(), sector.getIdSector());
    }

    public int delete(Long id) {
        String sql = "DELETE FROM sector WHERE id_sector = ?";
        return jdbcTemplate.update(sql, id);
    }
}
