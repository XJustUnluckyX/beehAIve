package it.unisa.c10.beehAIve.misc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
  @Bean
  public SecurityFilterChain filterSecurity(HttpSecurity http) throws Exception {
    http
      .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
        .requestMatchers("/css/**", "/js/**","/assets/**", "/Boostrap/**").permitAll()
        .requestMatchers("/", "/about_us", "/sensors", "/subscription_plans","/contact_us", "/registration").permitAll()
        .requestMatchers("/login-form","/registration-form").permitAll())
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
