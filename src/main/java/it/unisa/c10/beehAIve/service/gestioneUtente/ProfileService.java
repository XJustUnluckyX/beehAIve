package it.unisa.c10.beehAIve.service.gestioneUtente;

import it.unisa.c10.beehAIve.persistence.dao.BeekeeperDAO;
import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProfileService {
  private final BeekeeperDAO beekeerperDAO;

  @Autowired
  public ProfileService(BeekeeperDAO beekeerperdao) {
    this.beekeerperDAO = beekeerperdao;
  }

  /**
   * Registra un nuovo apicoltore alla piattaforma attraverso le sue informazioni personali.
   * @param email l'email dell'apicoltore che ha intenzione di registrarsi.
   * @param password la password dell'apicoltore che ha intenzione di registrarsi.
   * @param firstName il nome dell'apicoltore che ha intenzione di registrarsi.
   * @param lastName il cognome dell'apicoltore che ha intenzione di registrarsi.
   * @param companyName il nome dell'azienda dell'apicoltore.
   * @param companyPiva la PIVA dell'azienda dell'apicoltore.
   * @return un oggetto {@code Beekeeper} contenente i dati inseriti dall'apicoltore registrato.
   */
  public Beekeeper registration(String email, String password, String firstName, String lastName,
      String companyName, String companyPiva) {
    Beekeeper beekeeper = new Beekeeper();
    beekeeper.setEmail(email);
    beekeeper.setPasswordhash(password);
    beekeeper.setFirstName(firstName);
    beekeeper.setLastName(lastName);
    beekeeper.setCompanyName(companyName);
    beekeeper.setCompanyPiva(companyPiva);
    return beekeerperDAO.save(beekeeper);
  }

  /**
   * Modifica le informazioni personali di un apicoltore registrato alla piattaforma.
   * @param email L'email dell'apicoltore che ha intenzione di modificare le proprie informazioni.
   * @param firstName Il nuovo nome dell'apicoltore.
   * @param lastName Il nuovo cognome dell'apicoltore.
   * @param companyName Il nuovo nome dell'azienda dell'apicoltore.
   * @return Un oggetto {@code Beekeeper} contenente i dati inseriti dall'apicoltore registrato.
   */
  public Beekeeper changeInfo(String email, String firstName, String lastName, String companyName) {
    Optional<Beekeeper> beekeeperOptional = beekeerperDAO.findById(email);
    Beekeeper beekeeper;
    if (beekeeperOptional.isPresent()) {
      beekeeper = beekeeperOptional.get();
      beekeeper.setFirstName(firstName);
      beekeeper.setLastName(lastName);
      beekeeper.setCompanyName(companyName);
      beekeerperDAO.save(beekeeper);
      return beekeeper;
    }
    return null;
  }

  /**
   * Modifica la password di un apicoltore registrato alla piattaforma.
   * @param email L'email dell'apicoltore che ha intenzione di modificare la propria password.
   * @param password La nuova password dell'apicoltore.
   */
  public void changePassword(String email, String password) {
    Optional<Beekeeper> beekeeperOptional = beekeerperDAO.findById(email);
    Beekeeper beekeeper;
    if (beekeeperOptional.isPresent()) {
      beekeeper = beekeeperOptional.get();
      beekeeper.setPasswordhash(password);
      beekeerperDAO.save(beekeeper);
    }
  }

  /**
   * Verifica se un utente &eacute; gi&aacute; esistente nel sistema attraverso un indirizzo email.
   * @param email L'indirizzo email dell'utente di cui verificare l'esistenza nel sistema.
   * @param password La password corrispondente alla email da verificare.
   * @return {@code true} se l'utente esiste nel sistema, {@code false} altrimenti.
   */
  public boolean userExists(String email, String password) {
    Beekeeper beekeeper = new Beekeeper();
    beekeeper.setPasswordhash(password);
    return !(beekeerperDAO.findByEmailAndPasswordhash(email,beekeeper.getPasswordhash()).isEmpty());
  }

  /**
   * Verifica se un indirizzo email &egrave; registrato nel sistema.
   * @param email L'indirizzo email di cui vogliamo verificare l'esistenza nel sistema.
   * @return {@code true} se l'indirizzo email &egrave; presente nel sistema, {@code false} altrimenti.
   */
  public boolean emailExists(String email) {
    return (beekeerperDAO.findById(email)).isPresent();
  }

  /**
   * Verifica se la partita IVA &egrave; registrata nel sistema.
   * @param piva La partita IVA di cui vogliamo verificare l'esistenza nel sistema.
   * @return {@code true} se la partita IVA &egrave; presente nel sistema, {@code false} altrimenti.
   */
  public boolean pivaExists(String piva) {
    return !(beekeerperDAO.findByCompanyPiva(piva).isEmpty());
  }

  /**
   * Ottiene un apicoltore dal database attraverso la sua email.
   * @param email L'email dell'apicoltore che stiamo cercando.
   * @return L'oggetto {@code Optional} che contiene l'oggetto {@code Beekeeper} se esiste.
   */
  public Optional<Beekeeper> findBeekeeper(String email) {
    return beekeerperDAO.findById(email);
  }
}

