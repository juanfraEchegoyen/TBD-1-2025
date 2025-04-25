package com.app.DeliveryApp.repositories;


import com.app.DeliveryApp.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUsuarioRepository implements UsuarioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_USUARIO_SQL =
            "INSERT INTO Usuario (id_usuario, nombre, email, password) VALUES (?, ?, ?, ?)";
    private static final String SELECT_USUARIO_BY_ID_SQL =
            "SELECT id_usuario, nombre, email, password FROM Usuario WHERE id_usuario = ?";
    private static final String SELECT_ALL_USUARIOS_SQL =
            "SELECT id_usuario, nombre, email, password FROM Usuario";
    private static final String UPDATE_USUARIO_SQL =
            "UPDATE Usuario SET nombre = ?, email = ?, password = ? WHERE id_usuario = ?";
    private static final String DELETE_USUARIO_BY_ID_SQL =
            "DELETE FROM Usuario WHERE id_usuario = ?";


    @Override
    public Usuario save(Usuario usuario) {
        jdbcTemplate.update(INSERT_USUARIO_SQL,
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getPassword());
        return usuario;
    }

    private final RowMapper<Usuario> usuarioRowMapper = new RowMapper<Usuario>() {
        @Override
        public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(rs.getInt("id_usuario"));
            usuario.setNombre(rs.getString("nombre"));
            usuario.setEmail(rs.getString("email"));
            usuario.setPassword(rs.getString("password"));
            return usuario;
        }
    };

    @Override
    public Optional<Usuario> findById(Integer id) {
        try {
            Usuario usuario = jdbcTemplate.queryForObject(SELECT_USUARIO_BY_ID_SQL, new Object[]{id}, usuarioRowMapper);
            return Optional.of(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    @Override
    public int update(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            throw new IllegalArgumentException("Usuario o idUsuario no pueden ser nulos para update");
        }
        return jdbcTemplate.update(UPDATE_USUARIO_SQL,
                usuario.getNombre(),
                usuario.getEmail(),
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
    public Optional<Usuario> findByNombre(String nombre){
        try {
            Usuario usuario = jdbcTemplate.queryForObject("SELECT * FROM Usuario WHERE nombre = ?", new Object[]{nombre}, usuarioRowMapper);
            return Optional.of(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}