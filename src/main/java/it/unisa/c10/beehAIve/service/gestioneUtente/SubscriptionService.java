package it.unisa.c10.beehAIve.service.gestioneUtente;

import it.unisa.c10.beehAIve.persistence.dao.BeekeeperDAO;
import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@Service
public class SubscriptionService {
  @Autowired
  private APIContext apiContext; // per PayPal
  private final BeekeeperDAO beekeeperDAO;

  @Autowired
  public SubscriptionService(BeekeeperDAO beekeeperDAO) {
    this.beekeeperDAO = beekeeperDAO;
  }

  /**
   * Restituisce un oggetto Payment che rappresenta un nuovo pagamento che l'utente dovr&agrave; effettuare attraverso PayPal.
   * L'oggetto creato contiene tutte le informazioni relative al pagamento.
   * @param total l'importo totale che l'utente dovr&agrave; versare
   * @param currency la valuta utilizzata dall'utente
   * @param method il metodo di pagamento (e.g. "PayPal")
   * @param intent l'intento del pagamento, che pu&ograve; assumere solo i seguenti tre valori:
   *               <ul>
   *                 <li>"sale": pagamento immediato</li>
   *                 <li>"order": pagamento futuro</li>
   *                 <li>"authorize": autorizzazione di un pagamento</li>
   *               </ul>
   * @param description la descrizione del pagamento
   * @param cancelUrl l'URL a cui renderizzare l'utente in caso di annullamento del pagamento
   * @param successUrl l'URL a cui renderizzare l'utente dopo aver effettuato il pagamento
   * @return un oggetto {@code Payment} che rappresenta la transazione di pagamento
   * @throws PayPalRESTException Nel caso in cui si verifichino errori durante la creazione del pagamento.
   */
  public Payment createPayment(Double total, String currency, String method, String intent,
                               String description, String cancelUrl, String successUrl)
      throws PayPalRESTException {
    Amount amount = new Amount();
    amount.setCurrency(currency);
    total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
    amount.setTotal(String.format("%.2f", total));

    Transaction transaction = new Transaction();
    transaction.setDescription(description);
    transaction.setAmount(amount);

    List<Transaction> transactions = new ArrayList<>();
    transactions.add(transaction);

    Payer payer = new Payer();
    payer.setPaymentMethod(method.toString());

    Payment payment = new Payment();
    payment.setIntent(intent.toString());
    payment.setPayer(payer);
    payment.setTransactions(transactions);
    RedirectUrls redirectUrls = new RedirectUrls();
    redirectUrls.setCancelUrl(cancelUrl);
    redirectUrls.setReturnUrl(successUrl);
    payment.setRedirectUrls(redirectUrls);

    return payment.create(apiContext);
  }

  /**
   * Esegue un pagamento attraverso PayPal, utilizando l'ID del pagamento e dell'acquirente.
   * @param paymentId l'ID del pagamento
   * @param payerId l'ID dell'acquirente
   * @return un oggetto {@code Payment} che rappresenta la transazione di pagamento
   * @throws PayPalRESTException Nel caso in cui si verifichino errori durante l'esecuzione del pagamento.
   */
  public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
    Payment payment = new Payment();
    payment.setId(paymentId);
    PaymentExecution paymentExecute = new PaymentExecution();
    paymentExecute.setPayerId(payerId);
    return payment.execute(apiContext, paymentExecute);
  }

  /**
   * Verifica se l'abbonamento di un apicoltore sia scaduto o meno.
   * @param beekeeperEmail l'email dell'apicoltore
   * @return {@code true} se la data odierna supera la data di scadenza dell'abbonamento, altrimenti {@code false}
   */
  public boolean isSubscriptionExpired(String beekeeperEmail) {
    Beekeeper beekeeper = getBeekeeper(beekeeperEmail);

    // Restituisce 'true' se la data odierna supera la data di scadenza, 'false' altrimenti
    return beekeeper.getSubscrExpirationDate().isBefore(LocalDate.now());
  }

  /**
   * Permette un nuovo acquisto di un qualsiasi piano d'abbonamento per un apicoltore, salvandone i dati.
   * @param beekeeperEmail l'email dell'apicoltore intenzionato ad acquistare un nuovo abbonamento
   * @param subscriptionType il tipo di abbonamento che l'utente intende acquistare
   */
  public void modifySubscription(String beekeeperEmail, String subscriptionType) {
    Beekeeper beekeeper = getBeekeeper(beekeeperEmail);

    // Sottoscrizione a un nuovo abbonamento
    beekeeper.setSubscribed(true);
    // Calcolo dell'importo pagato
    beekeeper.setPaymentDue(calculatePayment(subscriptionType));
    // Un mese alla scadenza a partire dalla data odierna
    beekeeper.setSubscrExpirationDate(LocalDate.now().plusMonths(1));

    // Salvataggio delle modifiche nel database
    beekeeperDAO.save(beekeeper);
  }

  /**
   * Cancella gli abbonamenti scaduti di ogni singolo utente.
   */
  public void cancelAllExpiredSubscriptions() {
    List<Beekeeper> beekeepers = beekeeperDAO.findAll();

    for (Beekeeper b : beekeepers) {
      // Se l'abbonamento risulta scaduto...
      if (b.getSubscrExpirationDate() != null && isSubscriptionExpired(b.getEmail())) {
        // Cancellazione dell'abbonamento dal database
        b.setSubscribed(false);
        b.setPaymentDue(0);
        b.setSubscrExpirationDate(null);
        beekeeperDAO.save(b);
      }
    }
  }

  /**
   * Restituisce un oggetto {@code Beekeeper}, contenente i dati di un apicoltore.
   * @param beekeeperEmail l'email dell'apicoltore di cui vogliamo ottenere i dati
   * @return un oggetto {@code Beekeeper} contenente i dati di un apicoltore
   */
  public Beekeeper getBeekeeper(String beekeeperEmail) {
    // Ricerca del beekeeper nel database
    Optional<Beekeeper> optionalBeekeeper = beekeeperDAO.findById(beekeeperEmail);

    // Controllo sull'esistenza del beekeeper nel database
    if (optionalBeekeeper.isPresent()) {
      return optionalBeekeeper.get();
    } else {
      throw new NullPointerException("Beekeeper not found for email: " + beekeeperEmail);
    }
  }

  /**
   * Restituisce l'importo da pagare o pagato dall'apicoltore in base alla tipologia di abbonamento.
   * @param subscriptionType il tipo di abbonamento da pagare o gi&agrave; pagato
   * @return l'importo da pagare o gi&agrave; pagato per il tipo di abbonamento specificato
   */
  public double calculatePayment(String subscriptionType) {
    if (subscriptionType.equals("small")) { // Prezzo dell'abbonamento "Small"
      return 49.99;
    } else if (subscriptionType.equals("medium")) { // Prezzo dell'abbonamento "Medium"
      return 319.99;
    } else { // Prezzo dell'abbonamento "Large"
      return 969.99;
    }
  }

}