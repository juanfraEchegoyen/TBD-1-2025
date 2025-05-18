package com.app.GeoTaskApp.Controller;

import com.app.GeoTaskApp.Dto.LoginRequestDTO;
import com.app.GeoTaskApp.Dto.RefreshTokenRequestDTO;
import com.app.GeoTaskApp.Models.Usuario;
import com.app.GeoTaskApp.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService servicioAutenticacion;

    /**
     * Constructor que recibe el servicio de autenticación
     * El sistema lo inyecta automáticamente gracias a Spring
     */
    public AuthController(AuthService servicioAutenticacion) {
        this.servicioAutenticacion = servicioAutenticacion;
    }

    /**
     * Endpoint para registrar un nuevo usuario en el sistema
     *
     * Recibe los datos del usuario en formato JSON y los guarda en la base de datos
     * La contraseña se guarda encriptada para mayor seguridad
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            // Intenta registrar el usuario usando el servicio de autenticación
            servicioAutenticacion.registro(usuario);

            // Si todo sale bien, devuelve un mensaje de éxito
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Usuario registrado exitosamente");
            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Si el usuario ya existe, devuelve un error de conflicto
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        } catch (Exception e) {
            // Para cualquier otro error, devuelve un error interno del servidor
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al registrar usuario: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para iniciar sesión en el sistema
     *
     * Recibe credenciales y devuelve tokens JWT para autenticación
     */
    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginRequestDTO solicitudLogin) {
        try {
            // Intenta iniciar sesión y obtener los tokens
            Map<String, String> tokens = servicioAutenticacion.login(solicitudLogin.getNombre(), solicitudLogin.getPassword());

            // Devuelve los tokens de acceso y refresco
            return new ResponseEntity<>(tokens, HttpStatus.OK);
        } catch (Exception e) {
            // Si las credenciales son incorrectas, devuelve un error de no autorizado
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciales inválidas");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Endpoint para renovar un token de acceso expirado
     *
     * Usa el token de refresco para generar un nuevo token de acceso
     * sin necesidad de volver a introducir credenciales
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> renovarToken(@RequestBody RefreshTokenRequestDTO solicitudRenovacion) {
        try {
            // Intenta renovar los tokens usando el token de refresco
            Map<String, String> tokens = servicioAutenticacion.refreshToken(solicitudRenovacion.getRefreshToken());

            // Devuelve los nuevos tokens
            return new ResponseEntity<>(tokens, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Si el token de refresco no es válido o está expirado
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // Para cualquier otro error
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al refrescar el token: " + e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}