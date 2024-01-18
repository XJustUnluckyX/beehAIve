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

  public double calculatePayment(String subscriptionType) {
    if (subscriptionType.equals("small")) { // Prezzo dell'abbonamento "Small"
      return 50;
    } else if (subscriptionType.equals("medium")) { // Prezzo dell'abbonamento "Medium"
      return 350;
    } else { // Prezzo dell'abbonamento "Large"
      return 1050;
    }
  }

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

  public void cancelSubscription(String beekeeperEmail) {
    Beekeeper beekeeper = getBeekeeper(beekeeperEmail);

    // Cancellazione dell'abbonamento dal database
    beekeeper.setSubscribed(false);
    beekeeper.setPaymentDue(0);
    beekeeper.setSubscrExpirationDate(null);
    // Salvataggio delle modifiche nel database
    beekeeperDAO.save(beekeeper);
  }

  public boolean isSubscriptionExpired(String beekeeperEmail) {
    Beekeeper beekeeper = getBeekeeper(beekeeperEmail);

    // Restituisce 'true' se la data odierna supera la data di scadenza, 'false' altrimenti
    return beekeeper.getSubscrExpirationDate().isBefore(LocalDate.now());
  }

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

  //--------------------------------------Metodi di PayPal------------------------------------------

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

  public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
    Payment payment = new Payment();
    payment.setId(paymentId);
    PaymentExecution paymentExecute = new PaymentExecution();
    paymentExecute.setPayerId(payerId);
    return payment.execute(apiContext, paymentExecute);
  }
}