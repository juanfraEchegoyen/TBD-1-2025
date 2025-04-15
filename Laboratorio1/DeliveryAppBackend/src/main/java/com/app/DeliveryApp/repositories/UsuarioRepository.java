package com.app.DeliveryApp.repositories;


import com.app.DeliveryApp.models.Usuario;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findByEmail(String email); // Buscar por email
}
