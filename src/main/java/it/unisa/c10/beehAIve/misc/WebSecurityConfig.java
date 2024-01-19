package it.unisa.c10.beehAIve.misc;

import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    /* @Bean
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
                  .requestMatchers("/login-form", "login", "login-page","/registration-form",
                    "/user-page", "/logout","/home").permitAll()
                  .requestMatchers("/user-page", "/error404", "/error500").permitAll()
                  .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login-page").permitAll()
                )
                .logout(LogoutConfigurer::permitAll);

//        http.csrf().disable(); Abilità le POST request

        return http.build();
    } */

  @Bean
  public SecurityFilterChain filterSecurity(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
        .requestMatchers("/", "/css/**", "/js/**","/assets/**", "/Boostrap/**").permitAll()
        .requestMatchers("/login-form").permitAll())
      .authorizeHttpRequests((authorize) ->
        authorize.anyRequest().authenticated()
      ).formLogin(
        form -> form
          .loginPage("/login")
          .defaultSuccessUrl("/")
          .permitAll()
      ).logout(
        logout -> logout
          .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
          .permitAll()
      );
    return http.build();
  }




}
