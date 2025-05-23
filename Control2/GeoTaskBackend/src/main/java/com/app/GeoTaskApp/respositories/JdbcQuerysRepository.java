package com.app.GeoTaskApp.respositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import com.app.GeoTaskApp.Dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcQuerysRepository implements QueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<TareaPorSectorDTO> getTareasPorSector(Long idUsuario) {
        String sql = """
            SELECT idSector, COUNT(*) AS cantidad_tareas
            FROM Tarea
            WHERE idUsuario = ?
            GROUP BY idSector
        """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new TareaPorSectorDTO(
                        rs.getLong("idSector"),
                        rs.getInt("cantidad_tareas")
                ), idUsuario);
    }
    @Override
    public List<SectorDTO> getSectoresConMasTareasPendientes() {
        String sql = """
            SELECT s.idSector, s.asignacion, s.comuna, s.calle,
                   ST_X(ST_Centroid(s.ubicacion::geometry)) AS longitud,
                   ST_Y(ST_Centroid(s.ubicacion::geometry)) AS latitud
            FROM Tarea t
            JOIN Sector s ON t.idSector = s.idSector
            WHERE t.estado = 'pendiente'
            GROUP BY s.idSector, s.asignacion, s.comuna, s.calle, s.ubicacion
            ORDER BY COUNT(*) DESC
        """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new SectorDTO(
                        rs.getLong("idSector"),
                        rs.getString("asignacion"),
                        rs.getString("comuna"),
                        rs.getString("calle"),
                        rs.getDouble("longitud"),
                        rs.getDouble("latitud")
                ));
    }

    @Override
    public TareaCercanaDTO getTareaPendienteMasCercanaV2(Long idUsuario) {
        return getTareaPendienteMasCercana(idUsuario);
    }

    @Override
    public TareaCercanaDTO getTareaPendienteMasCercana(Long idUsuario) {
        String sql = """
            SELECT t.idTarea, t.titulo,
                   ST_Distance(s.ubicacion, sector_usuario.ubicacion) AS distancia
            FROM Tarea t
            JOIN Sector s ON t.idSector = s.idSector
            JOIN Usuario u ON t.idUsuario = u.idUsuario
            JOIN Sector sector_usuario ON u.idSector = sector_usuario.idSector
            WHERE t.estado = 'pendiente'
            ORDER BY distancia ASC
            LIMIT 1
        """;

        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new TareaCercanaDTO(
                        rs.getLong("idTarea"),
                        rs.getString("titulo"),
                        rs.getDouble("distancia")
                ));
    }

    @Override
    public SectorDTO getSectorConMasTareasCompletadasEn2Km(Long idUsuario) {
        return getSectorConMasTareasCompletadasEnRadio(idUsuario, 2000);
    }

    public List<UsuarioSectorDTO> getCantidadTareasPorUsuarioPorSector() {
        String sql = """
            SELECT t.idUsuario, s.idSector, COUNT(*) AS cantidad_tareas
            FROM Tarea t
            JOIN Sector s ON t.idSector = s.idSector
            WHERE t.estado = 'completado'
            GROUP BY t.idUsuario, s.idSector
            ORDER BY t.idUsuario, cantidad_tareas DESC
        """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new UsuarioSectorDTO(
                        rs.getLong("idUsuario"),
                        rs.getLong("idSector"),
                        rs.getInt("cantidad_tareas")
                ));
    }

    @Override
    public SectorDTO getSectorConMasTareasCompletadasEn5Km(Long idUsuario) {
        return getSectorConMasTareasCompletadasEnRadio(idUsuario, 5000);
    }

    public SectorDTO getSectorConMasTareasCompletadasEnRadio(Long idUsuario, int radioMetros) {
        String sql = """
            SELECT s.idSector, s.asignacion, s.comuna, s.calle,
                   ST_X(ST_Centroid(s.ubicacion::geometry)) AS longitud,
                   ST_Y(ST_Centroid(s.ubicacion::geometry)) AS latitud,
                   COUNT(*) AS tareas_completadas
            FROM Tarea t
            JOIN Sector s ON t.idSector = s.idSector
            JOIN Usuario u ON t.idUsuario = u.idUsuario
            JOIN Sector sector_usuario ON u.idSector = sector_usuario.idSector
            WHERE t.estado = 'completado'
              AND ST_DWithin(s.ubicacion, sector_usuario.ubicacion, ?)
            GROUP BY s.idSector, s.asignacion, s.comuna, s.calle, s.ubicacion
            ORDER BY tareas_completadas DESC
            LIMIT 1
        """;

        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new SectorDTO(
                        rs.getLong("idSector"),
                        rs.getString("asignacion"),
                        rs.getString("comuna"),
                        rs.getString("calle"),
                        rs.getDouble("longitud"),
                        rs.getDouble("latitud")
                ), radioMetros);
    }

    public DistanciaPromedioDTO getPromedioDistanciaTareasCompletadas(Long idUsuario) {
        String sql = """
            SELECT AVG(ST_Distance(s.ubicacion, sector_usuario.ubicacion)) AS promedio_distancia
            FROM Tarea t
            JOIN Sector s ON t.idSector = s.idSector
            JOIN Usuario u ON t.idUsuario = u.idUsuario
            JOIN Sector sector_usuario ON u.idSector = sector_usuario.idSector
            WHERE t.estado = 'completado'
        """;

        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new DistanciaPromedioDTO(
                        rs.getDouble("promedio_distancia")
                ));
    }
}