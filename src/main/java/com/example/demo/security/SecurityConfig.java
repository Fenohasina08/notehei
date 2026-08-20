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
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

/**
 * Two independent filter chains, matching the REST vs Thymeleaf split described in task.md:
 *
 * <ul>
 *   <li>{@link #apiSecurityFilterChain} — every existing REST endpoint (stateless JWT, no session,
 *       CSRF disabled, 401/403 responses are JSON via {@link RestAuthenticationEntryPoint}/{@link
 *       CustomAccessDeniedHandler}).
 *   <li>{@link #webSecurityFilterChain} — Thymeleaf pages (session-based form login on {@code
 *       /login}, CSRF enabled — token carried by each {@code <form>} via
 *       thymeleaf-extras-springsecurity6, unauthenticated access redirects to {@code /login}).
 * </ul>
 *
 * Adaptation note: the existing REST controllers are not namespaced under {@code /api/**} (they
 * live at {@code /students}, {@code /teachers}, {@code /exams}, ...), so the API chain matches
 * those explicit paths instead of a generic {@code /api/**} prefix, to avoid renaming every
 * existing controller.
 */
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
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/login", "/css/**", "/js/**", "/webjars/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .formLogin(
            form ->
                form.loginPage("/login")
                    .loginProcessingUrl("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error")
                    .permitAll())
        .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout"))
        .exceptionHandling(
            exceptions ->
                exceptions.accessDeniedHandler(
                    (request, response, ex) -> response.sendRedirect("/access-denied")));
    return http.build();
  }
}
