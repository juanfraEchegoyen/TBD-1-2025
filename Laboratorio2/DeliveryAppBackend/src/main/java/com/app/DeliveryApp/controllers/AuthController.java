package com.app.DeliveryApp.controllers;

import com.app.DeliveryApp.dto.LoginRequestDTO;
import com.app.DeliveryApp.dto.RefreshTokenRequestDTO;
import com.app.DeliveryApp.models.Cliente;
import com.app.DeliveryApp.models.Repartidor;
import com.app.DeliveryApp.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDTO loginRequest) {
        Map<String, String> respuesta = servicioAutenticacion.login(loginRequest);
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/registro/cliente")
    public ResponseEntity<String> registroCliente(@RequestBody Cliente cliente) {
        servicioAutenticacion.registroCliente(cliente);
        return ResponseEntity.ok("Cliente registrado exitosamente");
    }

    @PostMapping("/registro/repartidor")
    public ResponseEntity<String> registroRepartidor(@RequestBody Repartidor repartidor) {
        servicioAutenticacion.registroRepartidor(repartidor);
        return ResponseEntity.ok("Repartidor registrado exitosamente");
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