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
  /**
   * Configura la catena di filtri di sicurezza per gestire l'autenticazione e l'autorizzazione delle richieste HTTP.
   * @param http La configurazione di sicurezza per l'applicazione.
   * @return Una catena di filtri di sicurezza configurata.
   * @throws Exception Nel caso in cui si verifichino errori durante la configurazione della sicurezza.
   */
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
