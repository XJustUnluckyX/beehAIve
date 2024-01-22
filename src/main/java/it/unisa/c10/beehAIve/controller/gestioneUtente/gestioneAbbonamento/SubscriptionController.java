package it.unisa.c10.beehAIve.controller.gestioneUtente.gestioneAbbonamento;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.service.gestioneArnie.DashboardService;
import org.springframework.scheduling.annotation.Scheduled;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import it.unisa.c10.beehAIve.service.gestioneUtente.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.Locale;

@Controller
public class SubscriptionController {

  private final SubscriptionService subscriptionService;
  private final DashboardService dashboardService;
  private String subscriptionType;
  public static final String PAYPAL_SUCCESS_URL = "pay/success";
  public static final String PAYPAL_CANCEL_URL = "pay/cancel";
  @Autowired
  public SubscriptionController (SubscriptionService subscriptionService, DashboardService dashboardService) {
    this.subscriptionService = subscriptionService;
    this.dashboardService = dashboardService;
  }

  /**
   * Gestisce le richieste GET per avviare il processo di pagamento di un piano di abbonamento.
   * @param subscriptionType Il tipo di abbonamento che l'apicoltore intende acquistare.
   * @param session Un oggetto {@code HttpSession} per ottenere l'oggetto {@code Beekeeper} dalla
   *                sessione.
   * @param redirectAttributes Un oggetto {@code RedirectAttributes} per trasferire messaggi di
   *                           risposta.
   * @return Una stringa che rappresenta l'URL di reindirizzamento al sito di pagamento di PayPal.
   * @see SubscriptionService#createPayment(Double, String, String, String, String, String, String)
   */
  @GetMapping("/pay")
  public String payment(@RequestParam String subscriptionType, HttpSession session,
                        RedirectAttributes redirectAttributes) {
    // Controllo sulla validità del piano di abbonamento
    if (!subscriptionType.equals("small") &&
        !subscriptionType.equals("medium") &&
        !subscriptionType.equals("large")) {
      throw new RuntimeException();
    }

    Beekeeper beekeeper = (Beekeeper) session.getAttribute("beekeeper");

    // Prevenzione di un secondo pagamento per un piano di abbonamento già attivo
    if ((subscriptionType.equals("small") && beekeeper.getPaymentDue() == 49.99) ||
        (subscriptionType.equals("medium") && beekeeper.getPaymentDue() == 319.99) ||
        (subscriptionType.equals("large") && beekeeper.getPaymentDue() == 969.99) ) {
      throw new RuntimeException();
    }

    // Salvataggio dei valori da utilizzare successivamente nel metodo successPay()
    this.subscriptionType = subscriptionType;
    // Calcolo dell'importo in base alla tipologia di abbonamento
    double price = subscriptionService.calculatePayment(subscriptionType);

    // Controllo sull'abbonamento corrente, così da impedire che l'apicoltore passi a un
    // abbonamento di taglia inferiore nel caso in cui il numero attuale delle sue arnie ne supera
    // il limite massimo
    if (!canModifySubscription(beekeeper.getEmail(), subscriptionType)) {
      redirectAttributes.addFlashAttribute("error",
          "Your current hive count exceeds the maximum limit for this subscription.");
      return "redirect:/user";
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

  /**
   * Gestisce le richieste GET per registrare il pagamento effettuato con successo.
   * @param paymentId L'ID del pagamento.
   * @param payerId L'ID dell'acquirente.
   * @param session Un oggetto {@code HttpSession} per ottenere l'oggetto {@code Beekeeper} dalla
   *                sessione e registrarne le nuove informazioni.
   * @return Una stringa che rappresenta l'URL di reindirizzamento alla pagina di avvenuto
   *         pagamento.
   * @see SubscriptionService#modifySubscription(String, String)
   */
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
        beekeeper.setSubscribed(true);
        beekeeper.setPaymentDue(subscriptionService.calculatePayment(subscriptionType));
        beekeeper.setSubscrExpirationDate(LocalDate.now().plusMonths(1));
        session.setAttribute("beekeeper", beekeeper);

        return "payments/payment-successful";
      }
    } catch (PayPalRESTException e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
    return "index";
  }

  /**
   * Gestisce le richieste GET per reindirizzare l'apicoltore alla pagina di pagamento annullato.
   * @return Una stringa che rappresenta l'URL di reindirizzamento alla pagina di pagamento
   *         annullato.
   */
  @GetMapping(PAYPAL_CANCEL_URL)
  public String cancelPay() {
    return "payments/payment-cancelled";
  }

  /**
   * Effettua un controllo ogni giorno alle ore 00:00:00 sull'esistenza di eventuali abbonamenti
   * scaduti all'interno del sistema, eliminandoli.
   * @see SubscriptionService#cancelAllExpiredSubscriptions()
   */
  @Scheduled(cron = "0 0 0 * * *")
  public void cancelAllExpiredSubscription() {
    subscriptionService.cancelAllExpiredSubscriptions();
  }

  private boolean canModifySubscription(String beekeeperEmail, String subscriptionType) {
    double beekeeperPaymentDue = subscriptionService.getBeekeeper(beekeeperEmail).getPaymentDue();
    /* Si restituisce 'false' nei seguenti casi:
     * - L'apicoltore vuole passare dall'abbonamento "Medium" a "Small" e possiede più di 15 arnie
     * - L'apicoltore vuole passare dall'abbonamento "Large" a "Small" e possiede più di 15 arnie
     * - L'apicoltore vuole passare dall'abbonamento "Large" a "Medium" e possiede più di 100 arnie
     */
    if (((beekeeperPaymentDue == 49.99 && subscriptionType.equals("small"))
      && dashboardService.getBeekeeperHivesCount(beekeeperEmail) > 15)
      || ((beekeeperPaymentDue == 319.99 && subscriptionType.equals("small"))
      && dashboardService.getBeekeeperHivesCount(beekeeperEmail) > 15)
      || ((beekeeperPaymentDue == 969.99 && subscriptionType.equals("medium"))
      && dashboardService.getBeekeeperHivesCount(beekeeperEmail) > 100)){
      return false;
    }

    // Si restituisce 'true' se non si presenta nessuno dei casi precedenti perché il limite massimo
    // di arnie è rispettato oppure perché beekeeperPaymentDue == null (l'apicoltore non ha alcun
    // abbonamento)
    return true;
  }


}