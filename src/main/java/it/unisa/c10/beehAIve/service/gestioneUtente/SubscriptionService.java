package it.unisa.c10.beehAIve.service.gestioneUtente;

import it.unisa.c10.beehAIve.persistence.dao.BeekeeperDAO;
import it.unisa.c10.beehAIve.persistence.dao.HiveDAO;
import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import it.unisa.c10.beehAIve.persistence.entities.Hive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/*
 * La seguente classe supporta le seguenti operazioni:
 * 1. Sottoscrivere un beekeeper a un abbonamento.
 * 2. Modificare il piano di abbonamento di un beekeeper (mensile, trimestrale, annuale).
 * 3. Controllare che l'abbonamento di un beekeeper sia scaduto o meno.
 * 4. Annullare l'abbonamento di un beekeeper.
 * 5. Aggiornare il prezzo dell'abbonamento di un beekeeper.
 */

@Service
public class SubscriptionService {
  private final BeekeeperDAO beekeeperDAO;
  private final HiveDAO hiveDAO;
  private final double monthlyPricePerHive;
  private final double quarterlyPricePerHive;
  private final double annualPricePerHive;

  @Autowired
  public SubscriptionService(BeekeeperDAO beekeeperDAO, HiveDAO hiveDAO) {
    this.beekeeperDAO = beekeeperDAO;
    this.hiveDAO = hiveDAO;
    this.monthlyPricePerHive = 5;
    this.quarterlyPricePerHive = 4.5;
    this.annualPricePerHive = 4;
  }

  // Metodo privato per ricercare il beekeeper nel database, così da evitare ripetizioni nel codice
  private Beekeeper getBeekeeper(String beekeeperEmail) {
    // Ricerca del beekeeper nel database
    Optional<Beekeeper> optionalBeekeeper = beekeeperDAO.findById(beekeeperEmail);

    // Controllo sull'esistenza del beekeeper nel database
    if (optionalBeekeeper.isPresent()) {
      return optionalBeekeeper.get();
    } else {
      throw new NullPointerException("Beekeeper not found for email: " + beekeeperEmail);
    }
  }

  public void modifySubscription(String beekeeperEmail, int subscriptionPlan) {
    Beekeeper beekeeper = getBeekeeper(beekeeperEmail);
    // Ricerca e conteggio di tutte le arnie appartenenti al beekeeper nel database
    List<Hive> beekeeperHives = hiveDAO.findByBeekeeperEmail(beekeeperEmail);
    int hivesCount = beekeeperHives.size();

    // Sottoscrizione a un nuovo abbonamento
    // ...roba di PayPal/Stripe
    beekeeper.setSubscribed(true);
    if (subscriptionPlan == 1) { // Sottoscrizione ad abbonamento mensile
      beekeeper.setPaymentDue(hivesCount * monthlyPricePerHive); // Prezzo
      beekeeper.setSubscrExpirationDate(LocalDate.now().plusMonths(1)); // Scadenza
    } else if (subscriptionPlan == 2) { // Sottoscrizione ad abbonamento trimestrale
      beekeeper.setPaymentDue(hivesCount * quarterlyPricePerHive); // Prezzo
      beekeeper.setSubscrExpirationDate(LocalDate.now().plusMonths(3)); // Scadenza
    } else { // Sottoscrizione ad abbonamento annuale
      beekeeper.setPaymentDue(hivesCount * annualPricePerHive); // Prezzo
      beekeeper.setSubscrExpirationDate(LocalDate.now().plusMonths(12)); // Scadenza
    }

    // Salvataggio delle modifiche nel database
    beekeeperDAO.save(beekeeper);
  }

  public boolean isSubscriptionExpired(String beekeeperEmail) {
    Beekeeper beekeeper = getBeekeeper(beekeeperEmail);

    // Si restituisce 'true' se la data di scadenza precede la data odierna, 'false' altrimenti
    return beekeeper.getSubscrExpirationDate().isBefore(LocalDate.now());
  }

  public void cancelSubscription(String beekeeperEmail) {
    Beekeeper beekeeper = getBeekeeper(beekeeperEmail);

    // Controllo sull'esistenza di un abbonamento attivo
    if (!beekeeper.isSubscribed()) {
      throw new IllegalStateException
          ("Cannot cancel beekeeper's subscription because it is not subscribed to any plan.");
    }

    // Cancellazione dell'abbonamento
    // ...roba di PayPal/Stripe (?)
    beekeeper.setSubscribed(false);
    beekeeper.setPaymentDue(0);
    beekeeper.setSubscrExpirationDate(null);

    // Salvataggio delle modifiche nel database
    beekeeperDAO.save(beekeeper);
  }

  public void updatePrice(String beekeeperEmail, int subscriptionPlan) {
    Beekeeper beekeeper = getBeekeeper(beekeeperEmail);
    // Ricerca e conteggio di tutte le arnie appartenenti al beekeeper nel database
    List<Hive> beekeeperHives = hiveDAO.findByBeekeeperEmail(beekeeperEmail);
    int hivesCount = beekeeperHives.size();

    // Controllo sull'esistenza di un abbonamento attivo
    if (!beekeeper.isSubscribed()) {
      throw new IllegalStateException
          ("Cannot update beekeeper's subscription price because it is not subscribed to any plan.");
    }

    // Aggiornamento del prezzo
    if (subscriptionPlan == 1) { // Se il beekeeper ha un abbonamento mensile...
      beekeeper.setPaymentDue(hivesCount * monthlyPricePerHive);
    } else if (subscriptionPlan == 2) { // Se il beekeeper ha un abbonamento trimestrale...
      beekeeper.setPaymentDue(hivesCount * quarterlyPricePerHive);
    } else { // Se il beekeeper ha un abbonamento annuale...
      beekeeper.setPaymentDue(hivesCount * annualPricePerHive);
    }

    // Salvataggio delle modifiche nel database
    beekeeperDAO.save(beekeeper);
  }
}