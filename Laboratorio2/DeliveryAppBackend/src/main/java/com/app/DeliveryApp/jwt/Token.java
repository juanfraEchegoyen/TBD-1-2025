package com.app.DeliveryApp.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class Token {
    @Value("${jwt.secret}")
    private String secreto;

    @Value("${jwt.expiration}")
    private int tiempoExpiracion;

    @Value("${jwt.refresh-expiration}")
    private int tiempoExpiracionRefresco;

    /**
     * Genera un token de acceso JWT para un usuario autenticado
     * Este token permite acceder a recursos protegidos
     */
    public String generarToken(Authentication autenticacion){
        UserDetails usuarioPrincipal = (UserDetails) autenticacion.getPrincipal();
        SecretKey llave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder().setSubject(usuarioPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + tiempoExpiracion * 1000L))
                .signWith(llave, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Genera un token de refresco que permite obtener un nuevo token de acceso
     * sin necesidad de iniciar sesión nuevamente
     */
    public String generarTokenDeRefresco(String nombreUsuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("id", UUID.randomUUID().toString());

        SecretKey llave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(nombreUsuario)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + tiempoExpiracionRefresco * 1000L))
                .signWith(llave, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Comprueba si el token de acceso es válido para un usuario específico
     * Verifica que pertenezca al usuario correcto y que no haya expirado
     */
    public Boolean validarToken(String token, UserDetails detallesUsuario){
        final String nombreUsuario = extraerNombreDeUsuario(token);
        return (nombreUsuario.equals(detallesUsuario.getUsername()) && !estaTokenExpirado(token));
    }

    /**
     * Verifica si un token de refresco es válido
     * Comprueba que sea de tipo "refresh" y que no haya expirado
     */
    public Boolean validarTokenDeRefresco(String token) {
        try {
            Claims claims = extraerTodasLasClamaciones(token);
            return !estaTokenExpirado(token) && "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Comprueba si un token ha expirado comparando su fecha de expiración con la fecha actual
     */
    public Boolean estaTokenExpirado(String token){
        return extraerFechaDeExpiracion(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración de un token
     */
    public Date extraerFechaDeExpiracion(String token){
        return extraerTodasLasClamaciones(token).getExpiration();
    }

    /**
     * Extrae toda la información (claims) contenida en el token
     * Los claims son los datos almacenados dentro del token
     */
    public Claims extraerTodasLasClamaciones(String token){
        SecretKey llave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(llave)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extrae el nombre de usuario almacenado en el token
     */
    public String extraerNombreDeUsuario(String token){
        return extraerTodasLasClamaciones(token).getSubject();
    }
}