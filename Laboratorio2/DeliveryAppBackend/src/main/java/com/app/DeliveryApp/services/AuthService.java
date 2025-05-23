package com.app.DeliveryApp.services;

import com.app.DeliveryApp.dto.LoginRequestDTO;
import com.app.DeliveryApp.jwt.Token;
import com.app.DeliveryApp.models.Cliente;
import com.app.DeliveryApp.models.Repartidor;
import com.app.DeliveryApp.repositories.ClienteRepository;
import com.app.DeliveryApp.repositories.RepartidorRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {
    private final ClienteRepository repositorioClientes;
    private final RepartidorRepository repositorioRepartidores;
    private final PasswordEncoder codificadorContraseñas;
    private final AuthenticationManager gestorAutenticacion;
    private final Token utilidadToken;

    /**
     * Constructor que inicializa todos los componentes necesarios para la autenticación
     * La anotación @Lazy es necesaria para evitar dependencias circulares
     */
    public AuthService(ClienteRepository repositorioClientes,
                       RepartidorRepository repositorioRepartidores,
                       PasswordEncoder codificadorContraseñas,
                       @Lazy AuthenticationManager gestorAutenticacion,
                       Token utilidadToken) {
        this.repositorioClientes = repositorioClientes;
        this.repositorioRepartidores = repositorioRepartidores;
        this.codificadorContraseñas = codificadorContraseñas;
        this.gestorAutenticacion = gestorAutenticacion;
        this.utilidadToken = utilidadToken;
    }

    /**
     * Registra un nuevo cliente en el sistema
     */
    public void registroCliente(Cliente cliente) {
        Optional<Cliente> clienteExistente = repositorioClientes.findByRut(cliente.getRut());
        if (clienteExistente.isPresent()) {
            throw new IllegalArgumentException("El cliente ya existe con este RUT");
        }
        // Encriptar la contraseña antes de guardarla
        String contraseñaEncriptada = codificadorContraseñas.encode(cliente.getPassword());
        cliente.setPassword(contraseñaEncriptada);
        // Guardar el cliente en la base de datos
        repositorioClientes.save(cliente);
    }

    /**
     * Registra un nuevo repartidor en el sistema
     */
    public void registroRepartidor(Repartidor repartidor) {
        Optional<Repartidor> repartidorExistente = repositorioRepartidores.findByRut(repartidor.getRut());
        if (repartidorExistente.isPresent()) {
            throw new IllegalArgumentException("El repartidor ya existe con este RUT");
        }
        // Encriptar la contraseña antes de guardarla
        String contraseñaEncriptada = codificadorContraseñas.encode(repartidor.getPassword());
        repartidor.setPassword(contraseñaEncriptada);
        // Guardar el repartidor en la base de datos
        repositorioRepartidores.save(repartidor);
    }

    /**
     * Autentica al usuario (cliente o repartidor) usando LoginRequestDTO
     *
     * 1. Verifica las credenciales del usuario según su tipo (CLIENTE o REPARTIDOR)
     * 2. Busca en el repositorio correspondiente
     * 3. Valida la contraseña
     * 4. Genera los tokens de acceso y refresco
     */
    public Map<String, String> login(LoginRequestDTO loginRequest) {
        // Validar que el LoginRequest no sea nulo
        if (loginRequest == null) {
            throw new IllegalArgumentException("Los datos de login no pueden ser nulos");
        }

        // Validar que los campos requeridos estén presentes
        if (loginRequest.getRut() == null || loginRequest.getRut().trim().isEmpty()) {
            throw new IllegalArgumentException("El RUT es requerido");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }
        if (loginRequest.getTipoUsuario() == null) {
            throw new IllegalArgumentException("El tipo de usuario es requerido");
        }

        // Buscar el usuario según el tipo especificado
        String nombreUsuario = null;
        String contraseñaAlmacenada = null;
        String rutUsuario = loginRequest.getRut();

        switch (loginRequest.getTipoUsuario()) {
            case CLIENTE:
                Optional<Cliente> cliente = repositorioClientes.findByRut(rutUsuario);
                if (cliente.isPresent()) {
                    nombreUsuario = cliente.get().getNombre();
                    contraseñaAlmacenada = cliente.get().getPassword();
                } else {
                    throw new UsernameNotFoundException("Cliente no encontrado con RUT: " + rutUsuario);
                }
                break;

            case REPARTIDOR:
                Optional<Repartidor> repartidor = repositorioRepartidores.findByRut(rutUsuario);
                if (repartidor.isPresent()) {
                    nombreUsuario = repartidor.get().getNombre();
                    contraseñaAlmacenada = repartidor.get().getPassword();
                } else {
                    throw new UsernameNotFoundException("Repartidor no encontrado con RUT: " + rutUsuario);
                }
                break;

            default:
                throw new IllegalArgumentException("Tipo de usuario no válido");
        }

        // Verificar la contraseña
        if (!codificadorContraseñas.matches(loginRequest.getPassword(), contraseñaAlmacenada)) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        // El identificador completo para la autenticación y el token
        String identificadorCompleto = rutUsuario + ":" + loginRequest.getTipoUsuario().toString();

        // Autenticar al usuario basándose en el RUT y contraseña
        Authentication autenticacion = new UsernamePasswordAuthenticationToken(
                identificadorCompleto,
                null,
                Collections.emptyList()
        );

        // Guardar la autenticación en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(autenticacion);

        // Generar los tokens JWT
        String tokenAcceso = utilidadToken.generarToken(autenticacion);
        String tokenRefresco = utilidadToken.generarTokenDeRefresco(identificadorCompleto);

        // Crear un mapa con los tokens y información adicional
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("accessToken", tokenAcceso);
        respuesta.put("refreshToken", tokenRefresco);
        respuesta.put("tipoUsuario", loginRequest.getTipoUsuario().toString());
        respuesta.put("rut", rutUsuario);
        respuesta.put("nombre", nombreUsuario);

        return respuesta;
    }

    /**
     * Refresca los tokens cuando el token de acceso ha expirado
     */
    public Map<String, String> refreshToken(String tokenRefresco) {
        // Verificar que el token de refresco sea válido
        if (!utilidadToken.validarTokenDeRefresco(tokenRefresco)) {
            throw new IllegalArgumentException("Refresh token inválido o expirado");
        }

        // Extraer el identificador del usuario del token (formato: "rut:tipoUsuario")
        String identificadorUsuario = utilidadToken.extraerNombreDeUsuario(tokenRefresco);

        // Cargar los detalles del usuario
        UserDetails detallesUsuario = loadUserByUsername(identificadorUsuario);

        // Crear una nueva autenticación
        Authentication autenticacion = new UsernamePasswordAuthenticationToken(
                detallesUsuario, null, detallesUsuario.getAuthorities());

        // Generar nuevos tokens
        String nuevoTokenAcceso = utilidadToken.generarToken(autenticacion);
        String nuevoTokenRefresco = utilidadToken.generarTokenDeRefresco(identificadorUsuario);

        // Crear un mapa con los nuevos tokens
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", nuevoTokenAcceso);
        tokens.put("refreshToken", nuevoTokenRefresco);

        return tokens;
    }

    /**
     * Carga los detalles del usuario desde la base de datos
     *
     * El identificador viene en formato "rut:tipoUsuario"
     * Este método es requerido por la interfaz UserDetailsService
     */
    @Override
    public UserDetails loadUserByUsername(String identificadorUsuario) {
        // Parsear el identificador (formato: "rut:tipoUsuario")
        String[] partes = identificadorUsuario.split(":");
        if (partes.length != 2) {
            throw new UsernameNotFoundException("Formato de identificador inválido: " + identificadorUsuario);
        }

        String rut = partes[0];
        String tipoUsuario = partes[1];

        String nombreUsuario = null;
        String contraseña = null;

        // Buscar según el tipo de usuario
        if ("CLIENTE".equals(tipoUsuario)) {
            Optional<Cliente> cliente = repositorioClientes.findByRut(rut);
            if (cliente.isPresent()) {
                nombreUsuario = cliente.get().getNombre();
                contraseña = cliente.get().getPassword();
            } else {
                throw new UsernameNotFoundException("Cliente no encontrado: " + rut);
            }
        } else if ("REPARTIDOR".equals(tipoUsuario)) {
            Optional<Repartidor> repartidor = repositorioRepartidores.findByRut(rut);
            if (repartidor.isPresent()) {
                nombreUsuario = repartidor.get().getNombre();
                contraseña = repartidor.get().getPassword();
            } else {
                throw new UsernameNotFoundException("Repartidor no encontrado: " + rut);
            }
        } else {
            throw new UsernameNotFoundException("Tipo de usuario no válido: " + tipoUsuario);
        }

        // Crear un objeto UserDetails con la información del usuario
        return new org.springframework.security.core.userdetails.User(
                identificadorUsuario, // Usamos el identificador completo como username
                contraseña,
                Collections.emptyList()  // Lista vacía de roles/autoridades
        );
    }

    /**
     * Obtiene información del usuario autenticado actualmente
     */
    public Map<String, String> obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No hay usuario autenticado");
        }

        String identificadorUsuario = auth.getName();
        String[] partes = identificadorUsuario.split(":");

        if (partes.length != 2) {
            throw new IllegalStateException("Formato de identificador inválido");
        }

        Map<String, String> info = new HashMap<>();
        info.put("rut", partes[0]);
        info.put("tipoUsuario", partes[1]);

        return info;
    }
}