package com.app.DeliveryApp.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private Token token;

    @Autowired
    private UserDetailsService servicioDatosUsuario;

    /**
     * Este método es como un "inspector de seguridad" que revisa cada petición que llega al servidor.
     * Se ejecuta una sola vez por cada petición HTTP y funciona así:
     *
     * 1. Extrae el token JWT del encabezado "Authorization"
     * 2. Verifica si el token existe y es válido
     * 3. Si es válido, busca al usuario en la base de datos
     * 4. Establece al usuario como "autenticado" para que pueda acceder a los recursos
     */
    @Override
    protected void doFilterInternal(HttpServletRequest peticion, HttpServletResponse respuesta, FilterChain cadenaFiltros)
            throws ServletException, IOException {
        try {
            // Paso 1: Extraer el token del encabezado
            String jwt = extraerTokenJwt(peticion);

            if (jwt != null) {
                // Paso 2: Obtener el nombre de usuario del token
                String nombreUsuario = token.extraerNombreDeUsuario(jwt);

                // Paso 3: Verificar si hay un usuario y no está ya autenticado
                if (nombreUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Paso 4: Cargar los datos del usuario desde la base de datos
                    UserDetails datosUsuario = servicioDatosUsuario.loadUserByUsername(nombreUsuario);

                    // Paso 5: Validar el token con los datos del usuario
                    if (token.validarToken(jwt, datosUsuario)) {
                        // Paso 6: Crear el objeto de autenticación para el usuario
                        UsernamePasswordAuthenticationToken autenticacion =
                                new UsernamePasswordAuthenticationToken(
                                        datosUsuario,
                                        null,
                                        datosUsuario.getAuthorities());

                        // Añadir detalles de la petición a la autenticación
                        autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(peticion));

                        // Paso 7: Establecer la autenticación en el contexto de seguridad
                        SecurityContextHolder.getContext().setAuthentication(autenticacion);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("No se pudo establecer la autenticación del usuario", e);
        }

        // Continuar con la cadena de filtros
        cadenaFiltros.doFilter(peticion, respuesta);
    }

    /**
     * Este método extrae el token JWT del encabezado "Authorization"
     *
     * El formato esperado es: "Bearer eyJhbGciOiJIUzI1NiIsInR5..."
     * Donde después de "Bearer " viene el token real
     */
    private String extraerTokenJwt(HttpServletRequest peticion) {
        String encabezadoAuth = peticion.getHeader("Authorization");

        if (StringUtils.hasText(encabezadoAuth) && encabezadoAuth.startsWith("Bearer ")) {
            // Quitar la parte "Bearer " y quedarse solo con el token
            return encabezadoAuth.substring(7);
        }

        return null;
    }
}