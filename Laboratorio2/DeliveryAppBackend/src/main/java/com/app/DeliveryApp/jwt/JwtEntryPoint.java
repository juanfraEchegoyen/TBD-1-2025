package com.app.DeliveryApp.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        // Configura la respuesta como JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Examina si la causa raíz es un error de base de datos
        Throwable causa = authException.getCause();
        boolean esErrorDB = false;

        while (causa != null) {
            if (causa instanceof DataAccessException) {
                esErrorDB = true;
                break;
            }
            causa = causa.getCause();
        }

        Map<String, Object> mensajeError = new HashMap<>();

        if (esErrorDB) {
            // Es un error de base de datos, devolver 500
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mensajeError.put("estado", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mensajeError.put("error", "Error de Base de Datos");
            mensajeError.put("mensaje", "Error al acceder a los datos: " +
                    (causa != null ? causa.getMessage() : "vista SQL no encontrada"));
        } else {
            // Es un error de autenticación genuino, devolver 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            mensajeError.put("estado", HttpServletResponse.SC_UNAUTHORIZED);
            mensajeError.put("error", "No autorizado");
            mensajeError.put("mensaje", authException.getMessage());
        }

        mensajeError.put("ruta", request.getServletPath());

        // Registrar el error para diagnóstico
        logger.error("Error en el punto de entrada JWT: {}", mensajeError.get("mensaje"));

        // Convierte el mapa a formato JSON y lo escribe en la respuesta
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), mensajeError);
    }
}