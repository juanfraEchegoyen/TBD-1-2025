package com.app.GeoTaskApp.respositories;

import com.app.GeoTaskApp.Models.Ubicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcUbicacionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Ubicacion> sectorRowMapper = new RowMapper<Ubicacion>() {
        @Override
        public Ubicacion mapRow(ResultSet rs, int rowNum) throws SQLException {
            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setIdUbicacion(rs.getLong("id_ubicacion"));
            ubicacion.setRegion(rs.getString("region"));
            ubicacion.setProvincia(rs.getString("provincia"));
            ubicacion.setComuna(rs.getString("comuna"));
            ubicacion.setCoordenadas(rs.getString("ubicacion"));
            return ubicacion;
        }
    };

    public List<Ubicacion> findAll() {
        String sql = "SELECT * FROM ubicacion";
        return jdbcTemplate.query(sql, sectorRowMapper);
    }

    public Ubicacion findById(Long id) {
        String sql = "SELECT * FROM ubicacion WHERE id_ubicacion = ?";
        return jdbcTemplate.queryForObject(sql, sectorRowMapper, id);
    }

    public int save(Ubicacion ubicacion) {
        String sql = "INSERT INTO ubicacion (region, provincia, comuna, coordenadas) VALUES (?, ?, ?, ST_GeomFromText(?, 4326))";
        return jdbcTemplate.update(sql, ubicacion.getRegion(), ubicacion.getProvincia(), ubicacion.getComuna(), ubicacion.getCoordenadas());
    }

    public int update(Ubicacion ubicacion) {
        String sql = "UPDATE ubicacion SET region = ?, provincia = ?, comuna = ?, coordenadas = ST_GeomFromText(?, 4326) WHERE id_ubicacion = ?";
        return jdbcTemplate.update(sql, ubicacion.getRegion(), ubicacion.getProvincia(), ubicacion.getComuna(), ubicacion.getCoordenadas(), ubicacion.getIdUbicacion());
    }

    public int delete(Long id) {
        String sql = "DELETE FROM ubicacion WHERE id_ubicacion = ?";
        return jdbcTemplate.update(sql, id);
    }
}
