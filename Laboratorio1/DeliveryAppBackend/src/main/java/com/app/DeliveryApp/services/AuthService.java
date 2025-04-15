package com.app.DeliveryApp.services;

import com.app.DeliveryApp.models.Usuario;
import com.app.DeliveryApp.repositories.UsuarioRepository;

import java.util.Optional;

public class AuthService {
    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<String> login(String email, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent() && usuario.get().getPassword().equals(password)) {
            return Optional.of(JwtUtil.generateToken(email)); // Genera el JWT
        }
        return Optional.empty();
    }
}
