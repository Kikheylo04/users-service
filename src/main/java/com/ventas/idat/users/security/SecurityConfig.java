package com.ventas.idat.users.security;

import com.ventas.idat.users.config.JwtAuthFilter;
import com.ventas.idat.users.exception.CustomAccessDeniedHandler;
import com.ventas.idat.users.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            JwtAuthFilter jwtAuthFilter,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            CustomAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    @SuppressWarnings("java:S4502")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var pp = PathPatternRequestMatcher.withDefaults();

        CookieCsrfTokenRepository csrfCookieRepo = new CookieCsrfTokenRepository();

        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfCookieRepo)
                .ignoringRequestMatchers(
                    pp.matcher("/v3/api-docs/**"),
                    pp.matcher("/swagger-ui/**"),
                    pp.matcher("/swagger-ui.html"),
                    pp.matcher(HttpMethod.POST, "/api/users/login"),
                    pp.matcher(HttpMethod.POST, "/api/users/register")
                )
            )
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    pp.matcher("/v3/api-docs/**"),
                    pp.matcher("/swagger-ui/**"),
                    pp.matcher("/swagger-ui.html")
                ).permitAll()
                .requestMatchers(pp.matcher(HttpMethod.POST, "/api/users/login")).permitAll()
                .requestMatchers(pp.matcher(HttpMethod.POST, "/api/users/register")).permitAll()
                .requestMatchers(pp.matcher(HttpMethod.GET, "/api/users/")).hasRole("ADMIN")
                .requestMatchers(pp.matcher(HttpMethod.GET, "/api/users/profile")).authenticated()
                .requestMatchers(pp.matcher(HttpMethod.GET, "/api/users/profile/**")).hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
