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
            SELECT u.nombre, t.id_sector
            FROM Tarea t
            JOIN Usuario u ON t.id_usuario = u.id_usuario
            WHERE u.id_usuario = ? AND t.estado = 'completado'
            GROUP BY t.id_sector, u.nombre
        """;

        try {
            return jdbcTemplate.query(sql,
                    (rs, rowNum) -> new TareaPorSectorDTO(
                            rs.getString("nombre"),
                            rs.getLong("id_sector")

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


    public TareaPorSectorDTO getSectorConMasTareasCompletadasEnRadio(Long id_usuario, int radioMetros) {
        String sql = """
            SELECT u.nombre, s.id_sector
            FROM Tarea t
            JOIN Sector s ON t.id_sector = s.id_sector
            JOIN Usuario u ON t.id_usuario = u.id_usuario
            JOIN Sector sector_usuario ON u.id_sector = sector_usuario.id_sector
            WHERE t.estado = 'completado'
              AND ST_DWithin(ST_Transform(s.ubicacion,32719), ST_Transform(sector_usuario.ubicacion,32719), ?) 
              AND u.id_usuario = ?
            ORDER BY ST_Distance(s.ubicacion, sector_usuario.ubicacion) ASC
            LIMIT 1
        """;
        try {

        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new TareaPorSectorDTO(
                        rs.getString("nombre"),
                        rs.getLong("id_sector")
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


    @Override
    public List<SectorDTO> getSectoresConMasTareasPendientes() {
        String sql = """
        SELECT s.comuna, COUNT(*) AS cantidad_tareas_pendientes
        FROM Tarea t
        JOIN Sector s ON t.id_sector = s.id_sector
        WHERE t.estado = 'pendiente' 
        GROUP BY s.comuna
        ORDER BY cantidad_tareas_pendientes DESC
    """;

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> new SectorDTO(
                    rs.getString("comuna"),
                    rs.getInt("cantidad_tareas_pendientes")
            ));
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
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






    public TareaPorSectorDTO getSectorConMasTareasCompletadasEnRadio5000(Long id_usuario) {
        String sql = """
            WITH ubicacion_usuario AS (
                        SELECT s.ubicacion
                        FROM Usuario u
                        JOIN Sector s ON u.id_sector = s.id_sector
                        WHERE u.id_usuario = ?
                    )
                    SELECT s.comuna ,s.id_sector, COUNT(*) AS tareas_completadas
                    FROM Tarea t
                    JOIN Sector s ON t.id_sector = s.id_sector
                    JOIN ubicacion_usuario uu ON TRUE
                    WHERE t.estado = 'completado'
                      AND ST_DWithin(ST_Transform(s.ubicacion, 32719), ST_Transform(uu.ubicacion, 32719), 5000)
                    GROUP BY s.id_sector
                    ORDER BY tareas_completadas DESC
                    LIMIT 1;
        """;
        try {

            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new TareaPorSectorDTO(
                            rs.getString("comuna"),
                            rs.getLong("id_sector"),
                            rs.getInt("tareas_completadas")
                    ), id_usuario);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    @Override
    public TareaPorSectorDTO getSectorConMasTareasCompletadasEn5Km(Long idUsuario) {
        return getSectorConMasTareasCompletadasEnRadio5000(idUsuario);
    }
}