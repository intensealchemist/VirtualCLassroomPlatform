package com.example.demo;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
 
@Configuration
@EnableWebSecurity
public class WebSecurityConfig{
	
	
    @Bean
    UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }
 
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
     
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
         
        return authProvider;
    }
 
    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
         
        http.authenticationProvider(authenticationProvider());

        http.authorizeHttpRequests(auth ->
                auth.requestMatchers("/users").authenticated()
                        .requestMatchers("/home").authenticated()
                        .requestMatchers("/upload").authenticated()
                        .anyRequest().permitAll()

        )
        .formLogin(login ->
                login.usernameParameter("username")
                        .defaultSuccessUrl("/users")
                        .permitAll()
                        
        )
        .formLogin(login -> login
                .loginPage("/login") 
                .permitAll()
                .loginProcessingUrl("/authenticate"))
        
        .logout(logout -> logout.logoutSuccessUrl("/").permitAll()
        );
      
        
         
        return http.build();
    }
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
 
}