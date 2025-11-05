package com.phong.identify_service.configuration;

import com.phong.identify_service.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS =
            {
                    "/users",
                    "/auth/login", "/auth/introspect",
                    "/index.html",
                    "/*.html", // Thêm dòng này để cho phép truy cập các file HTML khác
                    "/*.css",  // Thêm dòng này để cho phép truy cập các file CSS
                    "/*.js"    // Thêm dòng này để cho phép truy cập các file JS
            };

    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // 1. KÍCH HOẠT CẤU HÌNH CORS
        httpSecurity.cors(Customizer.withDefaults());

        httpSecurity.authorizeHttpRequests(request ->
            request.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                    .requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.GET, "/users/search").hasRole(Role.ADMIN.name())
                    .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS).permitAll()
                    .requestMatchers("/ws-connect/**").permitAll()
                    .anyRequest().authenticated());
        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer ->
                        jwtConfigurer.decoder(jwtDecoder()).jwtAuthenticationConverter(jwtAuthenticationConverter())));


        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ⬅️ ĐỊA CHỈ FRONTEND CỦA BẠN (NuxtJS/React/Vue)
        // Nếu chạy ở localhost:3000, hãy dùng "http://localhost:3000"
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));

        // Các phương thức được phép
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Headers được phép (cần thiết cho Authorization header)
        configuration.setAllowedHeaders(Collections.singletonList("*"));

        // Cho phép gửi thông tin xác thực (cookies/Authorization header)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Áp dụng cấu hình này cho tất cả các đường dẫn
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    JwtDecoder jwtDecoder(){
        SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}
