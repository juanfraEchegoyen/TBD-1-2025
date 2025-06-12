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
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;

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
        
        // Validar ubicación si está presente
        if (cliente.getUbicacion() != null) {
            validarUbicacionChile(cliente.getUbicacion());
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
        
        // Validar ubicación si está presente
        if (repartidor.getUbicacion() != null) {
            validarUbicacionChile(repartidor.getUbicacion());
        }
        
        // Inicializar valores por defecto para repartidor si no están seteados
        if (repartidor.getPuntuacionPromedio() == null) {
            repartidor.setPuntuacionPromedio(0.0);
        }
        if (repartidor.getCantidadEntregas() == null) {
            repartidor.setCantidadEntregas(0);
        }
        if (repartidor.getDistanciaRecorrida() == null) {
            repartidor.setDistanciaRecorrida(0.0);
        }
        
        // Encriptar la contraseña antes de guardarla
        String contraseñaEncriptada = codificadorContraseñas.encode(repartidor.getPassword());
        repartidor.setPassword(contraseñaEncriptada);
        
        // Guardar el repartidor en la base de datos
        repositorioRepartidores.save(repartidor);
    }
    
    private void validarUbicacionChile(Point ubicacion) {
        double lat = ubicacion.getY();
        double lng = ubicacion.getX();
        
        // Validar que las coordenadas estén en un rango válido para Chile
        if (lat < -56 || lat > -17 || lng < -110 || lng > -66) {
            throw new IllegalArgumentException("Las coordenadas no están dentro del territorio chileno");
        }
    }
    
    /**
     * Autentica al usuario (cliente o repartidor) usando LoginRequestDTO
     * Busca automáticamente si el usuario es cliente o repartidor
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

        String rutUsuario = loginRequest.getRut();
        String nombreUsuario = null;
        String contraseñaAlmacenada = null;
        String tipoUsuario = null;

        // Buscar primero en clientes
        Optional<Cliente> cliente = repositorioClientes.findByRut(rutUsuario);
        if (cliente.isPresent()) {
            nombreUsuario = cliente.get().getNombre();
            contraseñaAlmacenada = cliente.get().getPassword();
            tipoUsuario = "CLIENTE";
        } else {
            // Si no se encuentra como cliente, buscar en repartidores
            Optional<Repartidor> repartidor = repositorioRepartidores.findByRut(rutUsuario);
            if (repartidor.isPresent()) {
                nombreUsuario = repartidor.get().getNombre();
                contraseñaAlmacenada = repartidor.get().getPassword();
                tipoUsuario = "REPARTIDOR";
            } else {
                throw new UsernameNotFoundException("Usuario no encontrado con RUT: " + rutUsuario);
            }
        }

        // Verificar la contraseña
        if (!codificadorContraseñas.matches(loginRequest.getPassword(), contraseñaAlmacenada)) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        // El identificador completo para la autenticación y el token
        String identificadorCompleto = rutUsuario + ":" + tipoUsuario;

        // Autenticar al usuario
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
        respuesta.put("tipoUsuario", tipoUsuario);
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