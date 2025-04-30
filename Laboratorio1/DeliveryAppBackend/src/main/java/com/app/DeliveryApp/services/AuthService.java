package com.app.DeliveryApp.services;

import com.app.DeliveryApp.jwt.Token;
import com.app.DeliveryApp.models.Usuario;
import com.app.DeliveryApp.repositories.UsuarioRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final Token jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       @Lazy AuthenticationManager authenticationManager,
                       Token jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public void registro(Usuario usuario) {
        Optional<Usuario> existingUser = usuarioRepository.findByNombre(usuario.getNombre());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        // Encriptar la contraseña antes de guardarla
        String encodedPassword = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(encodedPassword);
        // Guardar el usuario en la base de datos
        usuarioRepository.save(usuario);
    }

    public String login(String nombre, String password) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(nombre, password);
        Authentication authResult = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authResult);
        return jwtUtil.generateToken(authResult);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) {
        Usuario usuario = usuarioRepository.findByNombre(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + userName));

        return new org.springframework.security.core.userdetails.User(
                usuario.getNombre(),
                usuario.getPassword(),
                Collections.emptyList()
        );
    }
}