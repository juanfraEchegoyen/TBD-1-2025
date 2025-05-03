package com.app.DeliveryApp.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {

    /**
     * Este método se activa cuando un usuario intenta acceder a un recurso protegido
     * pero no está autenticado o su token es inválido/expirado.
     *
     * Es el metodo que devuelve un mensaje de error 401 cuando alguien
     * intenta entrar sin permiso o con credenciales incorrectas.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // Configura la respuesta como JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Establece el código 401 (No autorizado)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Crea un mensaje de error personalizado con formato JSON
        Map<String, Object> mensajeError = new HashMap<>();
        mensajeError.put("estado", HttpServletResponse.SC_UNAUTHORIZED);
        mensajeError.put("error", "No autorizado");
        mensajeError.put("mensaje", authException.getMessage());
        mensajeError.put("ruta", request.getServletPath());

        // Convierte el mapa a formato JSON y lo escribe en la respuesta
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), mensajeError);
    }
}