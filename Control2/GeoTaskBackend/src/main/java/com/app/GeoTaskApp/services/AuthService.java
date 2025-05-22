package com.app.GeoTaskApp.services;

import com.app.GeoTaskApp.Dto.RegistroRequestDTO;
import com.app.GeoTaskApp.Models.Sector;
import com.app.GeoTaskApp.jwt.Token;
import com.app.GeoTaskApp.Models.Usuario;
import com.app.GeoTaskApp.respositories.JdbcSectorRepository;
import com.app.GeoTaskApp.respositories.UsuarioRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {
    private final UsuarioRepository repositorioUsuarios;
    private final PasswordEncoder codificadorContraseñas;
    private final AuthenticationManager gestorAutenticacion;
    private final Token utilidadToken;
    private final JdbcSectorRepository sectorRepository;

    /**
     * Constructor que inicializa todos los componentes necesarios para la autenticación
     * La anotación @Lazy es necesaria para evitar dependencias circulares
     */
    public AuthService(UsuarioRepository repositorioUsuarios,
                       JdbcSectorRepository sectorRepository,
                       PasswordEncoder codificadorContraseñas,
                       @Lazy AuthenticationManager gestorAutenticacion,
                       Token utilidadToken) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.sectorRepository = sectorRepository;
        this.codificadorContraseñas = codificadorContraseñas;
        this.gestorAutenticacion = gestorAutenticacion;
        this.utilidadToken = utilidadToken;
    }

    /**
     * Registra un nuevo usuario en el sistema
     *
     * 1. Verifica que el nombre de usuario no exista ya
     * 2. Encripta la contraseña para almacenarla de forma segura
     * 3. Guarda el usuario en la base de datos
     */
    public void registro(RegistroRequestDTO solicitudRegistro) {
        Usuario usuario = solicitudRegistro.toUsuario();
        Sector sector = solicitudRegistro.toSector();

        Optional<Usuario> usuarioExistente = repositorioUsuarios.findByNombre(usuario.getNombre());
        if (usuarioExistente.isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        if (sectorRepository.save(sector) != 0) {
            usuario.setIdSector(sector.getIdSector());
            // Encriptar la contraseña antes de guardarla
            String contraseñaEncriptada = codificadorContraseñas.encode(usuario.getPassword());
            usuario.setPassword(contraseñaEncriptada);
            // Guardar el usuario en la base de datos
            repositorioUsuarios.save(usuario);
        } else {
            throw new IllegalArgumentException("El sector no tiene una ubicación válida");
        }
    }

    /**
     * Autentica al usuario y genera los tokens necesarios para acceso
     *
     * 1. Verifica las credenciales del usuario (nombre y contraseña)
     * 2. Si son correctas, crea un token de acceso y uno de refresco
     * 3. Devuelve ambos tokens en un mapa para que el cliente pueda usarlos
     */
    public Map<String, String> login(String nombre, String contraseña) {
        System.out.println("Iniciando autenticación para el usuario: " + nombre);

        try {
            // Crear un token de autenticación con las credenciales recibidas
            UsernamePasswordAuthenticationToken tokenAutenticacion =
                    new UsernamePasswordAuthenticationToken(nombre, contraseña);

            // Verificar las credenciales usando el gestor de autenticación
            Authentication resultadoAutenticacion = gestorAutenticacion.authenticate(tokenAutenticacion);

            // Guardar la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(resultadoAutenticacion);

            // Generar los tokens JWT
            String tokenAcceso = utilidadToken.generarToken(resultadoAutenticacion);
            String tokenRefresco = utilidadToken.generarTokenDeRefresco(nombre);

            // Crear un mapa con ambos tokens para devolver al cliente
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", tokenAcceso);
            tokens.put("refreshToken", tokenRefresco);
            tokens.put("userId", String.valueOf(repositorioUsuarios.findByNombre(nombre).get().getIdUsuario()));

            System.out.println("Autenticación exitosa para el usuario: " + nombre);
            return tokens;
        } catch (Exception e) {
            System.err.println("Error durante la autenticación: " + e.getMessage());
            throw new IllegalArgumentException("Credenciales inválidas");
        }
    }

    /**
     * Refresca los tokens cuando el token de acceso ha expirado
     *
     * 1. Verifica que el token de refresco sea válido
     * 2. Extrae el nombre de usuario del token
     * 3. Genera nuevos tokens de acceso y refresco
     */
    public Map<String, String> refreshToken(String tokenRefresco) {
        // Verificar que el token de refresco sea válido
        if (!utilidadToken.validarTokenDeRefresco(tokenRefresco)) {
            throw new IllegalArgumentException("Refresh token inválido o expirado");
        }

        // Extraer el nombre de usuario del token
        String nombreUsuario = utilidadToken.extraerNombreDeUsuario(tokenRefresco);

        // Cargar los detalles del usuario
        UserDetails detallesUsuario = loadUserByUsername(nombreUsuario);

        // Crear una nueva autenticación
        Authentication autenticacion = new UsernamePasswordAuthenticationToken(
                detallesUsuario, null, detallesUsuario.getAuthorities());

        // Generar nuevos tokens
        String nuevoTokenAcceso = utilidadToken.generarToken(autenticacion);
        String nuevoTokenRefresco = utilidadToken.generarTokenDeRefresco(nombreUsuario);

        // Crear un mapa con los nuevos tokens
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", nuevoTokenAcceso);
        tokens.put("refreshToken", nuevoTokenRefresco);

        return tokens;
    }

    /**
     * Carga los detalles del usuario desde la base de datos
     *
     * Este método es requerido por la interfaz UserDetailsService
     * Spring Security lo usa para verificar los datos de usuario durante la autenticación
     */
    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) {
        System.out.println("Cargando detalles del usuario: " + nombreUsuario);
        // Buscar el usuario en la base de datos
        Usuario usuario = repositorioUsuarios.findByNombre(nombreUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + nombreUsuario));

        // Crear un objeto UserDetails con la información del usuario
        System.out.println("Detalles del usuario cargados: " + usuario);
        return new org.springframework.security.core.userdetails.User(
                usuario.getNombre(),
                usuario.getPassword(),
                Collections.emptyList()  // Lista vacía de roles/autoridades
        );
    }
}