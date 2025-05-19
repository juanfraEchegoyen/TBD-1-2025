package com.app.GeoTaskApp.respositories;

import com.app.GeoTaskApp.models.Tarea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class JdbcTareaRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Tarea> tareaRowMapper = new RowMapper<Tarea>() {
        @Override
        public Tarea mapRow(ResultSet rs, int rowNum) throws SQLException {
            Tarea tarea = new Tarea();
            tarea.setIdTarea(rs.getLong("id_tarea"));
            tarea.setTitulo(rs.getString("titulo"));
            tarea.setDescripcion(rs.getString("descripcion"));
            tarea.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
            tarea.setEstado(rs.getString("estado"));
            tarea.setIdUsuario(rs.getLong("id_usuario"));
            tarea.setIdSector(rs.getLong("id_sector"));
            return tarea;
        }
    };

    public List<Tarea> findAll() {
        String sql = "SELECT * FROM tarea";
        return jdbcTemplate.query(sql, tareaRowMapper);
    }

    public Tarea findById(Long id) {
        String sql = "SELECT * FROM tarea WHERE id_tarea = ?";
        return jdbcTemplate.queryForObject(sql, tareaRowMapper, id);
    }

    public int save(Tarea tarea) {
        String sql = "INSERT INTO tarea (titulo, descripcion, fecha_vencimiento, estado, id_usuario, id_sector) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, tarea.getTitulo(), tarea.getDescripcion(), tarea.getFechaVencimiento(), tarea.getEstado(), tarea.getIdUsuario(), tarea.getIdSector());
    }

    public int update(Tarea tarea) {
        String sql = "UPDATE tarea SET titulo = ?, descripcion = ?, fecha_vencimiento = ?, estado = ?, id_usuario = ?, id_sector = ? WHERE id_tarea = ?";
        return jdbcTemplate.update(sql, tarea.getTitulo(), tarea.getDescripcion(), tarea.getFechaVencimiento(), tarea.getEstado(), tarea.getIdUsuario(), tarea.getIdSector(), tarea.getIdTarea());
    }

    public int delete(Long id) {
        String sql = "DELETE FROM tarea WHERE id_tarea = ?";
        return jdbcTemplate.update(sql, id);
    }
}
