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
            "INSERT INTO usuario (nombre, password, id_sector) VALUES (?, ?, ?) RETURNING id_usuario";
    private static final String SELECT_USUARIO_BY_ID_SQL =
            "SELECT id_usuario, nombre, password, id_sector FROM usuario WHERE id_usuario = ?";
    private static final String SELECT_ALL_USUARIOS_SQL =
            "SELECT id_usuario, nombre, password, id_sector FROM usuario";
    private static final String UPDATE_USUARIO_SQL =
            "UPDATE usuario SET nombre = ?, password = ?, id_sector = ? WHERE id_usuario = ?";
    private static final String DELETE_USUARIO_BY_ID_SQL =
            "DELETE FROM usuario WHERE id_usuario = ?";

    @Override
    public Usuario save(Usuario usuario) {
        Integer id = jdbcTemplate.queryForObject(
                INSERT_USUARIO_SQL,
                Integer.class,
                usuario.getNombre(),
                usuario.getPassword(),
                usuario.getIdSector()
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
            usuario.setIdSector(rs.getLong("id_sector"));
            return usuario;
        }
    };


    @Override
    public int update(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            throw new IllegalArgumentException("Usuario o idUsuario no pueden ser nulos para update");
        }

        // Validar que se ingresó un sector
        if (usuario.getIdSector() != null) {
            String checkSql = "SELECT COUNT(*) FROM sector s JOIN ubicacion u ON " +
                    "ST_Contains(u.coordenadas, s.ubicacion) " +
                    "WHERE s.id_sector = ?";

            int count = jdbcTemplate.queryForObject(checkSql, Integer.class, usuario.getIdSector());

            if (count == 0) {
                throw new IllegalArgumentException("El sector asignado al usuario no está dentro de un área permitida");
            }
        }

        return jdbcTemplate.update(UPDATE_USUARIO_SQL,
                usuario.getNombre(),
                usuario.getPassword(),
                usuario.getIdSector(),
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
            Usuario usuario = jdbcTemplate.queryForObject("SELECT * FROM usuario WHERE nombre = ?", new Object[]{nombre}, usuarioRowMapper);
            return Optional.of(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}