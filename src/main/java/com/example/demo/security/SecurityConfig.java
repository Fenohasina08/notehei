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

        /*
         * API JWT :
         * - pas de CSRF
         * - pas de session HTTP
         * - authentification par JWT
         */
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
                auth

                    /*
                     * Authentification publique
                     */
                    .requestMatchers("/auth/login", "/auth/logout", "/hello")
                    .permitAll()

                    /*
                     * Endpoints publics de santé
                     */
                    .requestMatchers("/ping", "/health/**")
                    .permitAll()

                    /*
                     * Administration
                     */
                    .requestMatchers("/admins/**")
                    .hasRole("ADMIN")

                    /*
                     * Création des étudiants et enseignants
                     */
                    .requestMatchers(HttpMethod.POST, "/students", "/teachers")
                    .hasRole("ADMIN")

                    /*
                     * Consultation des étudiants
                     */
                    .requestMatchers(HttpMethod.GET, "/students/**")
                    .hasAnyRole("ADMIN", "TEACHER", "STUDENT")

                    /*
                     * Modification d'un étudiant
                     */
                    .requestMatchers(HttpMethod.PATCH, "/students/**")
                    .hasAnyRole("ADMIN", "STUDENT")

                    /*
                     * Consultation des enseignants
                     */
                    .requestMatchers(HttpMethod.GET, "/teachers/**")
                    .hasAnyRole("ADMIN", "TEACHER")

                    /*
                     * Modification d'un enseignant
                     */
                    .requestMatchers(HttpMethod.PATCH, "/teachers/**")
                    .hasAnyRole("ADMIN", "TEACHER")

                    /*
                     * Relevés de notes
                     */
                    .requestMatchers("/transcripts/**")
                    .hasAnyRole("ADMIN", "TEACHER", "STUDENT")

                    /*
                     * Diplômes
                     */
                    .requestMatchers("/diplomas/**")
                    .hasAnyRole("ADMIN", "TEACHER", "STUDENT")

                    /*
                     * Tout le reste de l'API
                     * nécessite une authentification.
                     */
                    .anyRequest()
                    .authenticated())

        /*
         * JWT avant le filtre Username/Password
         */
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {

    http
        /*
         * Les pages Thymeleaf utilisent également le JWT.
         */
        .csrf(AbstractHttpConfigurer::disable)

        /*
         * Pas de session HTTP.
         * L'utilisateur est identifié grâce au JWT
         * présent dans le cookie NOTEHEI_TOKEN.
         */
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth

                    /*
                     * Pages accessibles sans authentification.
                     */
                    .requestMatchers(
                        "/login", "/register", "/register/**", "/css/**", "/js/**", "/webjars/**")
                    .permitAll()

                    /*
                     * Toutes les autres pages
                     * nécessitent une authentification.
                     */
                    .anyRequest()
                    .authenticated())

        /*
         * Le même filtre JWT lit :
         *
         * Authorization: Bearer <token>
         *
         * ou :
         *
         * NOTEHEI_TOKEN=<token>
         */
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

        /*
         * Si un utilisateur authentifié essaie
         * d'accéder à une ressource interdite.
         */
        .exceptionHandling(
            exceptions ->
                exceptions.accessDeniedHandler(
                    (request, response, ex) -> response.sendRedirect("/access-denied")));

    return http.build();
  }
}
