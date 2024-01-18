package it.unisa.c10.beehAIve.controller.gestioneUtente.gestioneAbbonamento;


import org.springframework.stereotype.Controller;
import it.unisa.c10.beehAIve.service.gestioneUtente.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import java.util.Locale;
// Gestisce tutte le operazioni che hanno a che fare con la sottoscrizione dell'abbonamento (Sottoscrizione effettiva,
// modifica del piano???, cancellazione, rinnovo, ecc)
// Questa cosa va ben vista, perchè dipende dall'API di PayPal
@Controller
public class SubscriptionController {
  @Autowired
  private SubscriptionService subscriptionService;

  // Per PayPal
  private String payerEmail;
  private String subscriptionType;
  public static final String PAYPAL_SUCCESS_URL = "pay/success";
  public static final String PAYPAL_CANCEL_URL = "pay/cancel";

  // ... (spazio per i metodi "normali")

  //--------------------------------------Metodi di PayPal------------------------------------------

  @GetMapping("/pay")
  public String payment(@RequestParam String beekeeperEmail, @RequestParam String subscriptionType) {
    try {
      // Salvataggio dei valori da utilizzare successivamente nel metodo successPay()
      this.payerEmail = subscriptionService.getBeekeeper(beekeeperEmail).getEmail();
      this.subscriptionType = subscriptionType;
      // Calcolo dell'importo in base alla tipologia di abbonamento
      double price = subscriptionService.calculatePayment(subscriptionType);

      // Imposta il separatore decimale come punto invece che virgola (necessario per PayPal)
      Locale.setDefault(Locale.US);

      Payment payment = subscriptionService.createPayment(
          price, "EUR", "paypal", "sale", "",
          "http://localhost:8080/beehAIve_war/" + PAYPAL_CANCEL_URL,
          "http://localhost:8080/beehAIve_war/" + PAYPAL_SUCCESS_URL);
      for (Links link : payment.getLinks()) {
        if (link.getRel().equals("approval_url")) {
          return "redirect:" + link.getHref();
        }
      }

    } catch (PayPalRESTException e) {
      e.printStackTrace();
    }
    return "redirect:/";
  }

  @GetMapping(value = PAYPAL_CANCEL_URL)
  public String cancelPay() {
    return "payments/payment-cancelled";
  }

  @GetMapping(value = PAYPAL_SUCCESS_URL)
  public String successPay(@RequestParam("paymentId") String paymentId,
                           @RequestParam("PayerID") String payerId) {

    try {
      Payment payment = subscriptionService.executePayment(paymentId, payerId);
      if (payment.getState().equals("approved")) {
        // Modifica delle informazioni relative all'abbonamento dell'acquirente nel database
        subscriptionService.modifySubscription(payerEmail, subscriptionType);
        return "payments/payment-successful";
      }
    } catch (PayPalRESTException e) {
      e.printStackTrace();
      return "redirect:/error500";
    }
    return "redirect:/";
  }
}