package org.gestion.gestionstock.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

import javax.crypto.spec.SecretKeySpec;

import static org.springframework.http.HttpMethod.POST;
import java.util.HexFormat;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
    @Value("${app.jwt.secret-key}")
    private String secret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/login", "/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST,
                                        "/entreprises/create").permitAll()
                                .requestMatchers(HttpMethod.POST,
                                        "/inscription").permitAll()
                                .requestMatchers(HttpMethod.POST,
                                        "/activation").permitAll()
                                .requestMatchers(HttpMethod.POST,
                                        "/utilisateurs/inscription").permitAll()
                                .requestMatchers(HttpMethod.POST,
                                        "/auth/connexion").permitAll()
                                .requestMatchers(
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**").permitAll()
                                .anyRequest().authenticated()
                        ).oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            );

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider( UserDetailsService userDetailsService){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
}

    @Bean
    JwtEncoder jwtEncoder() {

        byte[] keyBytes = HexFormat.of().parseHex(secret);

        SecretKey key = new SecretKeySpec(
                keyBytes,
                "HmacSHA256"
        );

        return new NimbusJwtEncoder(
                new ImmutableSecret<>(key)
        );
    }

    @Bean
    JwtDecoder jwtDecoder() {

        byte[] keyBytes = HexFormat.of().parseHex(secret);

        SecretKey key = new SecretKeySpec(
                keyBytes,
                "HmacSHA256"
        );

        return NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}