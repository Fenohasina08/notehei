package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

  private static final String[] API_PATHS = {
    "/auth/**",
    "/students/**",
    "/teachers/**",
    "/admins/**",
    "/academic-years/**",
    "/cohorts/**",
    "/semesters/**",
    "/programs/**",
    "/groups/**",
    "/group-memberships/**",
    "/course-units/**",
    "/courses/**",
    "/teaching-assignments/**",
    "/exams/**",
    "/grades/**",
    "/grade-history/**",
    "/transcripts/**",
    "/diplomas/**",
    "/ping",
    "/health/**",
    "/hello"
  };

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  @Bean
  @Order(1)
  public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {

    http.securityMatcher(API_PATHS)

        // API JWT = pas de CSRF et pas de session
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(restAuthenticationEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/auth/login", "/hello")
                    .permitAll()
                    .requestMatchers("/ping", "/health/**")
                    .permitAll()
                    .requestMatchers("/admins/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/students", "/teachers")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/students/**")
                    .hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                    .requestMatchers(HttpMethod.PATCH, "/students/**")
                    .hasAnyRole("ADMIN", "STUDENT")
                    .requestMatchers(HttpMethod.GET, "/teachers/**")
                    .hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.PATCH, "/teachers/**")
                    .hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers("/transcripts/**")
                    .hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                    .requestMatchers("/diplomas/**")
                    .hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {

    http.csrf(AbstractHttpConfigurer::disable)

        // IMPORTANT :
        // aucune HttpSession pour les pages Thymeleaf
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/login", "/register", "/register/**", "/css/**", "/js/**", "/webjars/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())

        // Le même JWT filter est utilisé pour les pages HTML.
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(
            exceptions ->
                exceptions.accessDeniedHandler(
                    (request, response, ex) -> response.sendRedirect("/access-denied")));

    return http.build();
  }
}
