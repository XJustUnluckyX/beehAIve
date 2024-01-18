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
                  .requestMatchers("/", "/css/**", "/js/**","/assets/**", "/Boostrap/**", "/create_report").permitAll()
                  .requestMatchers("/registration-page", "/subscription-page", "/creation-hive", "/graph-test", "/generate_report_test").permitAll()
                  .requestMatchers("/driver_fia","/predict_with_cnn", "/predict_without_cnn", "/mimmo", "/produce_graph").permitAll()
                  .requestMatchers("/creation-hive", "/dashboard", "/state-hive").permitAll()
                  .requestMatchers("/subscription-test","/pay","/pay/success","/pay/cancel").permitAll()
                  .requestMatchers("/dashboard", "/parameters-hive", "/operations-hive", "/contact-us", "/about-us", "/sensor-spec").permitAll()
                  .requestMatchers("/user-page","/get_hive_operation_history").permitAll()
                  .anyRequest().authenticated()

                )
                .formLogin((form) -> form
                        .loginPage("/login-page").permitAll()
                )
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }




}
