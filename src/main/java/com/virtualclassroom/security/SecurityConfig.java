package com.virtualclassroom.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Optional: present only if OAuth2 clients are configured
    @Autowired(required = false)
    private ClientRegistrationRepository clientRegistrationRepository;
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return new CustomOAuth2UserService();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Keep CSRF enabled by default (recommended for production)
            .csrf(csrf -> {})
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/", "/index", "/index.html", "/home", "/about", "/contact").permitAll()
                .requestMatchers("/guest").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/static/**", "/public/**", "/assets/**").permitAll()
                .requestMatchers("/favicon.ico", "/error").permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // Authentication endpoints
                .requestMatchers("/auth/**", "/api/auth/**").permitAll()
                .requestMatchers("/register", "/signup", "/process_register").permitAll()
                .requestMatchers("/forgot-password", "/reset-password").permitAll()

                // API Documentation
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()

                // Public course browsing
                .requestMatchers("/courses/browse", "/courses/search", "/courses/*/preview").permitAll()

                // WebSocket endpoints
                .requestMatchers("/ws/**", "/topic/**", "/app/**").permitAll()

                // Admin endpoints
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")

                // Instructor endpoints
                .requestMatchers("/instructor/**", "/api/instructor/**").hasAnyRole("INSTRUCTOR", "ADMIN")

                // Student endpoints
                .requestMatchers("/student/**", "/api/student/**").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")

                // Course management
                .requestMatchers("/api/courses/create", "/api/courses/*/edit").hasAnyRole("INSTRUCTOR", "ADMIN")
                .requestMatchers("/api/courses/*/enroll").hasAnyRole("STUDENT", "INSTRUCTOR")

                // Assignment management
                .requestMatchers("/api/assignments/create", "/api/assignments/*/grade").hasAnyRole("INSTRUCTOR", "ADMIN")
                .requestMatchers("/api/assignments/*/submit").hasRole("STUDENT")

                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")                // GET /login returns the form
                .loginProcessingUrl("/login")       // Spring Security handles POST /login
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .permitAll()
            );
        // Configure OAuth2 login only if client registrations are available
        if (clientRegistrationRepository != null) {
            http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService()))
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=oauth2")
            );
        }
        // Allow H2 console frames in dev
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    private static class CustomOAuth2UserService extends DefaultOAuth2UserService {
        @Override
        public OAuth2User loadUser(OAuth2UserRequest userRequest) {
            OAuth2User oauth2User = super.loadUser(userRequest);
            // Custom logic to process OAuth2 user and create/update local user
            return oauth2User;
        }
    }
}
