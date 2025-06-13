    package com.app.DeliveryApp.config;

    import com.app.DeliveryApp.jwt.JwtAuthenticationFilter;
    import com.app.DeliveryApp.jwt.JwtEntryPoint;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.CorsConfigurationSource;
    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

    import java.util.List;

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

        private final JwtEntryPoint jwtEntryPoint;

        public SecurityConfig(JwtEntryPoint jwtEntryPoint) {
            this.jwtEntryPoint = jwtEntryPoint;
        }
        // Descripción: Este método configura el codificador de contraseñas utilizando BCrypt.
        @Bean
        public PasswordEncoder passwordEncoder(){
            return new BCryptPasswordEncoder();
        }

        // Descripción: Este método configura el AuthenticationManager para la autenticación de usuarios.
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
            return authConfig.getAuthenticationManager();
        }

        // Descripción: Este método configura la cadena de filtros de seguridad HTTP.
        // Se encarga de establecer la configuración de seguridad para las solicitudes HTTP entrantes.
        // Permite el acceso a ciertas rutas sin autenticación y aplica un filtro JWT para la autenticación.
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
            http
                    .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configura CORS
                    .csrf(AbstractHttpConfigurer::disable) // Desactiva la protección CSRF
                    .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtEntryPoint)) // Configura el punto de entrada de excepciones
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configura la gestión de sesiones para que sea sin estado
                    .authorizeHttpRequests(auth -> auth // Configura las solicitudes HTTP autorizadas
                            .requestMatchers(
                                "/auth/registro",
                                "/auth/login/**",
                                "/auth/refresh"
                            ).permitAll()
                            .anyRequest().authenticated() // Requiere autenticación para cualquier otra solicitud
                    )
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

        // Descripción: Este método configura CORS (Cross-Origin Resource Sharing) para permitir solicitudes desde dominios específicos.
        @Bean
        CorsConfigurationSource corsConfigurationSource(){
            CorsConfiguration configuration = new CorsConfiguration(); // Crea una nueva configuración de CORS

            configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Permite solicitudes desde el origen especificado
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Permite los métodos HTTP especificados
            configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Permite los encabezados HTTP especificados
            configuration.setAllowCredentials(true); // Permite el uso de credenciales (cookies, autenticación HTTP básica, etc.)

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // Crea una nueva fuente de configuración de CORS
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter();
        }

    }