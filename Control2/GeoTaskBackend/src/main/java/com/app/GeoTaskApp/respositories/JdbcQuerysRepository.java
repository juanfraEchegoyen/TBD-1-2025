package com.app.GeoTaskApp.respositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class JdbcQuerysRepository {

    private final Connection connection;

    public JdbcQuerysRepository(Connection connection) {
        this.connection = connection;
    }

    // ¿Cuántas tareas ha hecho el usuario por sector?
    public void getTareasPorSector(int idUsuario) throws SQLException {
        String sql = "SELECT idSector, COUNT(*) AS cantidad_tareas FROM Tarea WHERE idUsuario = ? GROUP BY idSector";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("Sector: " + rs.getLong("idSector") + " - Tareas: " + rs.getInt("cantidad_tareas"));
                }
            }
        }
    }

    // ¿En qué sectores geográficos se concentran la mayoría de las tareas pendientes?
    public void getSectoresConMasTareasPendientes() throws SQLException {
        String sql = "SELECT s.comuna, COUNT(*) AS cantidad_tareas " +
                "FROM Tarea t " +
                "JOIN Sector s ON t.idSector = s.idSector " +
                "WHERE t.estado = 'pendiente' " +
                "GROUP BY s.comuna " +
                "ORDER BY cantidad_tareas DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Comuna: " + rs.getString("comuna") + " - Tareas pendientes: " + rs.getInt("cantidad_tareas"));
            }
        }
    }

    //  ¿Cuál es la tarea pendiente más cercana a la ubicación del usuario?
    public void getTareaPendienteMasCercana(int idUsuario) throws SQLException {
        String sql = "SELECT t.idTarea, t.titulo, ST_Distance(s.ubicacion, sector_usuario.ubicacion) AS distancia " +
                "FROM Tarea t " +
                "JOIN Sector s ON t.idSector = s.idSector " +
                "JOIN Usuario u ON t.idUsuario = u.idUsuario " +
                "JOIN Sector sector_usuario ON u.idSector = sector_usuario.idSector " +
                "WHERE t.estado = 'pendiente' " +
                "ORDER BY distancia ASC " +
                "LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println("Tarea más cercana: " + rs.getString("titulo") + " - Distancia: " + rs.getDouble("distancia") + " metros");
            }
        }
    }

    //  ¿Cuántas tareas ha realizado cada usuario por sector?
    public void getCantidadTareasPorUsuarioPorSector() throws SQLException {
        String sql = "SELECT t.idUsuario, s.idSector, COUNT(*) AS cantidad_tareas " +
                "FROM Tarea t " +
                "JOIN Sector s ON t.idSector = s.idSector " +
                "WHERE t.estado = 'completado' " +
                "GROUP BY t.idUsuario, s.idSector " +
                "ORDER BY idUsuario, cantidad_tareas DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Usuario: " + rs.getInt("idUsuario") + " - Sector: " + rs.getLong("idSector") + " - Tareas completadas: " + rs.getInt("cantidad_tareas"));
            }
        }
    }

    //  ¿Cuál es el sector con más tareas completadas dentro de un radio de 5 km desde la ubicación del usuario?
    public void getSectorConMasTareasCompletadas(int idUsuario) throws SQLException {
        String sql = "SELECT s.idSector, COUNT(*) AS tareas_completadas " +
                "FROM Tarea t " +
                "JOIN Sector s ON t.idSector = s.idSector " +
                "JOIN Usuario u ON t.idUsuario = u.idUsuario " +
                "JOIN Sector sector_usuario ON u.idSector = sector_usuario.idSector " +
                "WHERE t.estado = 'completado' " +
                "AND ST_DWithin(s.ubicacion, sector_usuario.ubicacion, 5000) " +
                "GROUP BY s.idSector " +
                "ORDER BY tareas_completadas DESC " +
                "LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println("Sector con más tareas completadas en 5km: " + rs.getLong("idSector"));
            }
        }
    }

    //  ¿Cuál es el promedio de distancia entre las tareas completadas y el punto registrado del usuario?
    public void getPromedioDistanciaTareasCompletadas(int idUsuario) throws SQLException {
        String sql = "SELECT AVG(ST_Distance(s.ubicacion, sector_usuario.ubicacion)) AS promedio_distancia " +
                "FROM Tarea t " +
                "JOIN Sector s ON t.idSector = s.idSector " +
                "JOIN Usuario u ON t.idUsuario = u.idUsuario " +
                "JOIN Sector sector_usuario ON u.idSector = sector_usuario.idSector " +
                "WHERE t.estado = 'completado'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println("Promedio de distancia: " + rs.getDouble("promedio_distancia") + " metros");
            }
        }
    }

    //  ¿Cuál es el sector con más tareas completadas dentro de un radio de 2 km desde la ubicación del usuario?
    public void getSectorConMasTareasCompletadas2km(int idUsuario) throws SQLException {
        String sql = "SELECT s.idSector, COUNT(*) AS tareas_completadas " +
                "FROM Tarea t " +
                "JOIN Sector s ON t.idSector = s.idSector " +
                "JOIN Usuario u ON t.idUsuario = u.idUsuario " +
                "JOIN Sector sector_usuario ON u.idSector = sector_usuario.idSector " +
                "WHERE t.estado = 'completado' " +
                "AND ST_DWithin(s.ubicacion, sector_usuario.ubicacion, 2000) " +
                "GROUP BY s.idSector " +
                "ORDER BY tareas_completadas DESC " +
                "LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println("Sector con más tareas completadas en 2km: " + rs.getLong("idSector"));
            }
        }
    }
}
