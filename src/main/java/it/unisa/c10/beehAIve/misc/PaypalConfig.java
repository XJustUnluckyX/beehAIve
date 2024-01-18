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
SANDBOX ACCOUNTS (Payer & Seller)

Payer
-----
Email: sb-8duro29328269@personal.example.com
Password: Hi*bW4MT

Seller
------
Email: sb-bbrm729165879@business.example.com
Password: 6c7t@Bd+
*/

@Configuration
public class PaypalConfig {
  @Value("${paypal.client.id}")
  private String clientId;
  @Value("${paypal.client.secret}")
  private String clientSecret;
  @Value("${paypal.mode}")
  private String mode;

  @Bean
  public Map<String, String> paypalSdkConfig() {
    Map<String, String> configMap = new HashMap<>();
    configMap.put("mode", mode);
    return configMap;
  }

  @Bean
  public OAuthTokenCredential oAuthTokenCredential() {
    return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
  }

  @Bean
  public APIContext apiContext() throws PayPalRESTException {
    APIContext context = new APIContext(oAuthTokenCredential().getAccessToken());
    context.setConfigurationMap(paypalSdkConfig());
    return context;
  }

}
