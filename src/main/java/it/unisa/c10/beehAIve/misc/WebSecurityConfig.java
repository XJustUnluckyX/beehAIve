package it.unisa.c10.beehAIve.misc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                  .requestMatchers("/", "/css/**", "/js/**","/assets/**", "/Boostrap/**").permitAll()
                  .requestMatchers("/registration-page", "/subscription-page", "/creation-hive", "/dashboard", "/state-hive").permitAll()
                  .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login").permitAll()
                )
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }




}
