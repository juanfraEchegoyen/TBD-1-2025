package com.app.DeliveryApp.repositories;


import com.app.DeliveryApp.models.Usuario;
import java.sql.*;
import java.util.Optional;

public class JdbcUsuarioRepository implements UsuarioRepository {
    private final Connection connection;

    public JdbcUsuarioRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Usuario save(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getPassword());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                usuario.setIdUsuario(rs.getInt(1)); // Obtener ID generado
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEmail(rs.getString("email"));
                usuario.setPassword(rs.getString("password"));
                return Optional.of(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}