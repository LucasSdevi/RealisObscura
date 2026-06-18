package com.projetoAula.aulaProjeto.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/img/**", "/js/**").permitAll()
                        .requestMatchers("/login", "/registro", "/auth/**").permitAll()
                        .requestMatchers("/itens", "/teorias").permitAll()
                        .requestMatchers("/posts/{id:\\d+}").permitAll()
                        .requestMatchers("/posts/{id:\\d+}/comentar").authenticated()
                        .requestMatchers("/posts/novo").authenticated()
                        .requestMatchers("/posts/editar/**").authenticated()
                        .requestMatchers("/posts/deletar/**").authenticated()
                        .requestMatchers("/admin/**").hasAuthority("admin")
                        .requestMatchers("/form", "/listar/**").hasAuthority("admin")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            // Limpa o cookie JWT
                            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", "");
                            cookie.setHttpOnly(true);
                            cookie.setPath("/");
                            cookie.setMaxAge(0);
                            response.addCookie(cookie);
                        })
                        .permitAll()
                )
                .build();
    }
}
