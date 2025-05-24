package com.app.GeoTaskApp.respositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import com.app.GeoTaskApp.Dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcQuerysRepository implements QueryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<TareaPorSectorDTO> getTareasPorSector(Long id_usuario) {
        String sql = """
            SELECT u.nombre, t.id_sector, COUNT(*) AS cantidad_tareas
            FROM Tarea t
            JOIN Usuario u ON t.id_usuario = u.id_usuario
            WHERE u.id_usuario = ?
            GROUP BY t.id_sector, u.nombre
        """;

        try {
            return jdbcTemplate.query(sql,
                    (rs, rowNum) -> new TareaPorSectorDTO(
                            rs.getString("nombre"),
                            rs.getLong("id_sector"),
                            rs.getInt("cantidad_tareas")
                    ), id_usuario);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public TareaCercanaDTO getTareaPendienteMasCercanaV2(Long idUsuario) {
        return getTareaPendienteMasCercana(idUsuario);
    }

    @Override
    public TareaCercanaDTO getTareaPendienteMasCercana(Long id_usuario) {
        String sql = """
            SELECT u.nombre, t.id_tarea, t.titulo, 
                   ST_Distance(s.ubicacion, sector_usuario.ubicacion) AS distancia
            FROM Tarea t
            JOIN Sector s ON t.id_sector = s.id_sector
            JOIN Usuario u ON t.id_usuario = u.id_usuario
            JOIN Sector sector_usuario ON u.id_sector = sector_usuario.id_sector
            WHERE t.estado = 'pendiente' AND u.id_usuario = ?
            ORDER BY distancia ASC
            LIMIT 1
        """;
        try {

        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new TareaCercanaDTO(
                        rs.getString("nombre"),
                        rs.getLong("id_tarea"),
                        rs.getString("titulo"),
                        rs.getDouble("distancia")
                ), id_usuario);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public TareaPorSectorDTO getSectorConMasTareasCompletadasEn2Km(Long idUsuario) {
        return getSectorConMasTareasCompletadasEnRadio(idUsuario, 2000);
    }
    @Override
    public TareaPorSectorDTO getSectorConMasTareasCompletadasEn5Km(Long idUsuario) {
        return getSectorConMasTareasCompletadasEnRadio(idUsuario, 5000);
    }

    public TareaPorSectorDTO getSectorConMasTareasCompletadasEnRadio(Long id_usuario, int radioMetros) {
        String sql = """
            SELECT u.nombre, s.id_sector, COUNT(*) AS tareas_completadas
            FROM Tarea t
            JOIN Sector s ON t.id_sector = s.id_sector
            JOIN Usuario u ON t.id_usuario = u.id_usuario
            JOIN Sector sector_usuario ON u.id_sector = sector_usuario.id_sector
            WHERE t.estado = 'completado'
              AND ST_DWithin(s.ubicacion, sector_usuario.ubicacion, ?) 
              AND u.id_usuario = ?
            GROUP BY s.id_sector, u.nombre
            ORDER BY tareas_completadas DESC
            LIMIT 1
        """;
        try {

        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new TareaPorSectorDTO(
                        rs.getString("nombre"),
                        rs.getLong("id_sector"),
                        rs.getInt("tareas_completadas")
                ), radioMetros, id_usuario);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public DistanciaPromedioDTO getPromedioDistanciaTareasCompletadas(Long id_usuario) {
        String sql = """
            SELECT u.nombre, AVG(ST_Distance(s.ubicacion, sector_usuario.ubicacion)) AS promedio_distancia
            FROM Tarea t
            JOIN Sector s ON t.id_sector = s.id_sector
            JOIN Usuario u ON t.id_usuario = u.id_usuario
            JOIN Sector sector_usuario ON u.id_sector = sector_usuario.id_sector
            WHERE t.estado = 'completado' AND u.id_usuario = ?
            GROUP BY u.nombre
        """;

        try {

        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new DistanciaPromedioDTO(
                        rs.getString("nombre"),
                        rs.getDouble("promedio_distancia")
                ), id_usuario);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    public List<SectorDTO> getSectoresConMasTareasPendientes(Long id_usuario) {
        String sql = """
        SELECT u.nombre, s.id_sector, s.asignacion, s.comuna, s.calle,
               ST_X(ST_Centroid(s.ubicacion::geometry)) AS longitud,
               ST_Y(ST_Centroid(s.ubicacion::geometry)) AS latitud,
               COUNT(*) AS cantidad_tareas_pendientes
        FROM Tarea t
        JOIN Sector s ON t.id_sector = s.id_sector
        JOIN Usuario u ON t.id_usuario = u.id_usuario
        WHERE t.estado = 'pendiente' AND u.id_usuario = ?
        GROUP BY u.nombre, s.id_sector, s.asignacion, s.comuna, s.calle, s.ubicacion
        ORDER BY cantidad_tareas_pendientes DESC
    """;

        try {

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new SectorDTO(
                        rs.getString("nombre"),
                        rs.getLong("id_sector"),
                        rs.getString("asignacion"),
                        rs.getString("comuna"),
                        rs.getString("calle"),
                        rs.getDouble("longitud"),
                        rs.getDouble("latitud"),
                        rs.getInt("cantidad_tareas_pendientes")
                ), id_usuario);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }



    public List<SectorDTO> getSectoresPendientes(Long id_usuario) {
        return getSectoresConMasTareasPendientes(id_usuario);
    }


    public List<UsuarioSectorDTO> getCantidadTareasPorUsuarioPorSector() {
        String sql = """
        SELECT u.nombre, t.id_usuario, s.id_sector, COUNT(*) AS cantidad_tareas
        FROM Tarea t
        JOIN Sector s ON t.id_sector = s.id_sector
        JOIN Usuario u ON t.id_usuario = u.id_usuario
        WHERE t.estado = 'completado'
        GROUP BY t.id_usuario, s.id_sector, u.nombre
        ORDER BY t.id_usuario, cantidad_tareas DESC
    """;
        try {

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new UsuarioSectorDTO(
                        rs.getString("nombre"),
                        rs.getLong("id_usuario"),
                        rs.getLong("id_sector"),
                        rs.getInt("cantidad_tareas")
                ));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }




    @Override
    public List<SectorDTO> getSectoresConMasTareasPendientes() {
        return List.of();
    }
}