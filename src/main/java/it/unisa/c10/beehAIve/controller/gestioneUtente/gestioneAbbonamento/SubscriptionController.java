package it.unisa.c10.beehAIve.controller.gestioneUtente.gestioneAbbonamento;


import it.unisa.c10.beehAIve.persistence.entities.Bee;
import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import it.unisa.c10.beehAIve.service.gestioneArnie.DashboardService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import it.unisa.c10.beehAIve.service.gestioneUtente.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import java.util.List;
import java.util.Locale;

@Controller
public class SubscriptionController {

  private SubscriptionService subscriptionService;
  private DashboardService dashboardService;

  @Autowired
  public SubscriptionController (SubscriptionService subscriptionService, DashboardService dashboardService) {
    this.subscriptionService = subscriptionService;
    this.dashboardService = dashboardService;
  }

  // Per PayPal
  private String payerEmail;
  private String subscriptionType;
  public static final String PAYPAL_SUCCESS_URL = "pay/success";
  public static final String PAYPAL_CANCEL_URL = "pay/cancel";

  public boolean canModifySubscription(String beekeeperEmail, String subscriptionType) {
    double beekeeperPaymentDue = subscriptionService.calculatePayment(subscriptionType);
    /* Si restituisce 'false' nei seguenti casi:
     * - L'apicoltore vuole passare dall'abbonamento "Medium" a "Small" e possiede più di 15 arnie
     * - L'apicoltore vuole passare dall'abbonamento "Large" a "Small" e possiede più di 15 arnie
     * - L'apicoltore vuole passare dall'abbonamento "Large" a "Medium" e possiede più di 100 arnie
     */
    if (((beekeeperPaymentDue == 350 && subscriptionType.equals("small"))
        && dashboardService.getBeekeeperHivesCount(beekeeperEmail) > 15)
     || ((beekeeperPaymentDue == 1050 && subscriptionType.equals("small"))
        && dashboardService.getBeekeeperHivesCount(beekeeperEmail) > 15)
     || ((beekeeperPaymentDue == 1050 && subscriptionType.equals("medium"))
        && dashboardService.getBeekeeperHivesCount(beekeeperEmail) > 100)){
      return false;
    }

    // Si restituisce 'true' se non si presenta nessuno dei casi precedenti perché il limite massimo
    // di arnie è rispettato oppure perché beekeeperPaymentDue == null (l'apicoltore non ha alcun
    // abbonamento)
    return true;
  }

  public void cancelBeekeeperExpiredSubscription(String beekeeperEmail) {
    Beekeeper beekeeper = subscriptionService.getBeekeeper(beekeeperEmail);

    // Controllo sull'esistenza di un abbonamento attivo ed eventuale scadenza
    if (beekeeper.isSubscribed() && subscriptionService.isSubscriptionExpired(beekeeperEmail)) {
      subscriptionService.cancelSubscription(beekeeperEmail); // Cancellazione dell'abbonamento
    }
  }

  @Scheduled(cron = "0 0 0 * * *")
  public void cancelAllExpiredSubscription() {
    subscriptionService.cancelAllExpiredSubscriptions();
  }

  //--------------------------------------Metodi di PayPal------------------------------------------

  @GetMapping("/pay")
  public String payment(@RequestParam String subscriptionType, HttpSession session, Model model) {
    if (!subscriptionType.equals("small") &&
        !subscriptionType.equals("medium") &&
        !subscriptionType.equals("large")) {
      throw new RuntimeException();
    }

    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Salvataggio dei valori da utilizzare successivamente nel metodo successPay()
    this.subscriptionType = subscriptionType;
    // Calcolo dell'importo in base alla tipologia di abbonamento
    double price = subscriptionService.calculatePayment(subscriptionType);

    // Controllo sull'abbonamento corrente, così da impedire che l'apicoltore passi a un
    // abbonamento di taglia inferiore nel caso in cui il numero attuale delle sue arnie ne supera
    // il limite massimo
    if (!canModifySubscription(beekeeper.getEmail(), subscriptionType)) {
      // TODO: Mostrare questo error con un popup
      model.addAttribute("error",
          "Your current hive count exceeds the maximum limit for this subscription.");
      return "user-page";
    }

    // Imposta il separatore decimale come punto invece che virgola (necessario per PayPal)
    Locale.setDefault(Locale.US);

    try {
      /* Creazione di un nuovo pagamento, composto da:
       * - VALUTA: Euro
       * - METODO DI PAGAMENTO: PayPal
       * - FINALITÀ: Pagamento immediato
       * - URL di cancellazione pagamento
       * - URL di pagamento effettuato
       */
      Payment payment = subscriptionService.createPayment(
          price, "EUR", "paypal", "sale", "",
          "http://localhost:8080/beehAIve_war/" + PAYPAL_CANCEL_URL,
          "http://localhost:8080/beehAIve_war/" + PAYPAL_SUCCESS_URL);

      for (Links link : payment.getLinks()) {
        // Reindirizzamento all'URL di approvazione del pagamento di PayPal
        if (link.getRel().equals("approval_url")) {
          return "redirect:" + link.getHref();
        }
      }
    } catch (PayPalRESTException e) {
      e.printStackTrace();
    }
    return "index";
  }

  @GetMapping(PAYPAL_SUCCESS_URL)
  public String successPay(@RequestParam("paymentId") String paymentId,
                           @RequestParam("PayerID") String payerId, HttpSession session) {
    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    try {
      Payment payment = subscriptionService.executePayment(paymentId, payerId);
      if (payment.getState().equals("approved")) {
        // Modifica delle informazioni relative all'abbonamento dell'acquirente nel database
        subscriptionService.modifySubscription(beekeeper.getEmail(), subscriptionType);

        // Modifica del beekeeper nella sessione
        beekeeper.setPaymentDue(subscriptionService.calculatePayment(subscriptionType));
        session.setAttribute("beekeeper", beekeeper);

        return "payments/payment-successful";
      }
    } catch (PayPalRESTException e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
    return "index";
  }

  @GetMapping(PAYPAL_CANCEL_URL)
  public String cancelPay() {
    return "payments/payment-cancelled";
  }
}