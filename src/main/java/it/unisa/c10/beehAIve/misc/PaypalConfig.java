package it.unisa.c10.beehAIve.misc;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

/*
SANDBOX ACCOUNT
Email: sb-8duro29328269@personal.example.com
Password: Hi*bW4MT
*/
@Configuration
public class PaypalConfig {
  @Value("${paypal.client.id}")
  private String clientId;
  @Value("${paypal.client.secret}")
  private String clientSecret;
  @Value("${paypal.mode}")
  private String mode;

  /**
   * Configura le credenziali PayPal SDK.
   * @return Una mappa contenente le configurazioni necessarie per le credenziali PayPal SDK.
   */
  @Bean
  public Map<String, String> paypalSdkConfig() {
    Map<String, String> configMap = new HashMap<>();
    configMap.put("mode", mode);
    return configMap;
  }

  /**
   * Fornisce le credenziali OAuthToken per l'autenticazione con PayPal.
   * @return Un oggetto OAuthTokenCredential contenente le credenziali OAuthToken.
   */
  @Bean
  public OAuthTokenCredential oAuthTokenCredential() {
    return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
  }

  /**
   * Fornisce un contesto API per le operazioni di PayPal.
   * @return Un oggetto APIContext configurato per l'autenticazione e l'utilizzo delle API di PayPal.
   * @throws PayPalRESTException Nel caso in cui si verifichino errori durante la creazione del contesto API.
   */
  @Bean
  public APIContext apiContext() throws PayPalRESTException {
    APIContext context = new APIContext(oAuthTokenCredential().getAccessToken());
    context.setConfigurationMap(paypalSdkConfig());
    return context;
  }

}
