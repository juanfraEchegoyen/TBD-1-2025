package com.app.GeoTaskApp.respositories;

import com.app.GeoTaskApp.Models.Usuario;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class JdbcUsuarioRepository implements UsuarioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_USUARIO_SQL =
            "INSERT INTO usuario (nombre, ubicacion, password) VALUES (?, ST_GeomFromText(?, 4326), ?) RETURNING id_usuario";
    private static final String SELECT_USUARIO_BY_ID_SQL =
            "SELECT id_usuario, nombre, ST_AsText(ubicacion) as ubicacion, password FROM usuario WHERE id_usuario = ?";
    private static final String SELECT_ALL_USUARIOS_SQL =
            "SELECT id_usuario, nombre, ST_AsText(ubicacion) as ubicacion, password FROM usuario";
    private static final String UPDATE_USUARIO_SQL =
            "UPDATE usuario SET nombre = ?, ubicacion = ST_GeomFromText(?, 4326), password = ? WHERE id_usuario = ?";
    private static final String DELETE_USUARIO_BY_ID_SQL =
            "DELETE FROM usuario WHERE id_usuario = ?";

    @Override
    public Usuario save(Usuario usuario) {
        Integer id = jdbcTemplate.queryForObject(
                INSERT_USUARIO_SQL,
                Integer.class,
                usuario.getNombre(),
                usuario.getUbicacion() != null ? new WKTWriter().write(usuario.getUbicacion()) : null,
                usuario.getPassword()
        );

        usuario.setIdUsuario(id);
        return usuario;
    }

    private final RowMapper<Usuario> usuarioRowMapper = new RowMapper<Usuario>() {
        @Override
        public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(rs.getInt("id_usuario"));
            usuario.setNombre(rs.getString("nombre"));
            usuario.setPassword(rs.getString("password"));

            // Manejo correcto de la columna de geometría (WKT)
            String wktGeometry = rs.getString("ubicacion");
            if (wktGeometry != null) {
                try {
                    WKTReader reader = new WKTReader();
                    Geometry geometry = reader.read(wktGeometry);
                    usuario.setUbicacion(geometry);
                } catch (ParseException e) {
                    throw new SQLException("Error al parsear la geometría", e);
                }
            }

            return usuario;
        }
    };


    @Override
    public int update(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            throw new IllegalArgumentException("Usuario o idUsuario no pueden ser nulos para update");
        }
        return jdbcTemplate.update(UPDATE_USUARIO_SQL,
                usuario.getNombre(),
                usuario.getUbicacion() != null ? new WKTWriter().write(usuario.getUbicacion()) : null,
                usuario.getPassword(),
                usuario.getIdUsuario());
    }

    @Override
    public int deleteById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El idUsuario no puede ser null");
        }
        return jdbcTemplate.update(DELETE_USUARIO_BY_ID_SQL, id);
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        try {
            Usuario usuario = jdbcTemplate.queryForObject(
                    SELECT_USUARIO_BY_ID_SQL,
                    usuarioRowMapper,
                    id
            );
            return Optional.of(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Usuario> findByNombre(String nombre){
        try {
            Usuario usuario = jdbcTemplate.queryForObject(
                    "SELECT id_usuario, nombre, ST_AsText(ubicacion) as ubicacion, password FROM usuario WHERE nombre = ?",
                    usuarioRowMapper,
                    nombre
            );
            return Optional.of(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}