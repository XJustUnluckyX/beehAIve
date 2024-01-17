package it.unisa.c10.beehAIve.service.gestioneUtente;

import it.unisa.c10.beehAIve.persistence.dao.BeekeeperDAO;
import it.unisa.c10.beehAIve.persistence.entities.Bee;
import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/*
La seguente classe deve supportare le seguenti operazioni:
1. Registrare l'apicoltore.
2. Loggare dentro l'apicoltore.
3. Loggare fuori l'apicoltore.
4. Modificare i dati personali dell'apicoltore.
5. Modificare la password dell'apicoltore.
6. Modificare il metodo di pagamento dell'apicoltore (? - controllare l'API di PayPal).
 */

@Service
public class ProfileService {
  private final BeekeeperDAO beekeerperdao;

  @Autowired
  public ProfileService(BeekeeperDAO beekeerperdao) {
    this.beekeerperdao = beekeerperdao;
  }

  public Beekeeper registration(String email, String password, String firstName, String lastName){
    Beekeeper beekeeper = new Beekeeper();
    beekeeper.setEmail(email);
    beekeeper.setPasswordhash(password);
    beekeeper.setFirstName(firstName);
    beekeeper.setLastName(lastName);
    return beekeerperdao.save(beekeeper);
  }

  public Beekeeper registration(String email, String password, String firstName, String lastName,
      String companyName, String companyPiva) {
    Beekeeper beekeeper = new Beekeeper();
    beekeeper.setEmail(email);
    beekeeper.setPasswordhash(password);
    beekeeper.setFirstName(firstName);
    beekeeper.setLastName(lastName);
    beekeeper.setCompanyName(companyName);
    beekeeper.setCompanyPiva(companyPiva);
    return beekeerperdao.save(beekeeper);
  }

  public boolean emailExists(String email) {
    return (beekeerperdao.findById(email)).isPresent();
  }

  public boolean pivaExists(String piva) {
    return beekeerperdao.findByCompanyPiva(piva) != null;
  }

  public UserDetails login(String email, String password) {
    if(!(this.emailExists(email)))
      return null;
    Optional<Beekeeper> beekeeper = beekeerperdao.findById(email);
    return new org.springframework.security.core.userdetails.User(
        beekeeper.get().getEmail(),
        beekeeper.get().getPasswordhash(),
        Collections.emptyList()
    );
  }

  public boolean userExists(String email, String password) {
    return beekeerperdao.findByEmailAndPasswordhash(email, password) != null;
  }

  /*public void logout() {
    // TODO cancellare sessione
  }*/

  /*public void changeInfo(String email, String firstName, String lastName, String password){
    // TODO sostituire il metodo dopo aver fatto il DAO
    if (beekeerperdao.doRetriveByEmail(email) == null) {
      // TODO gestione errore
    }
    // TODO sostituire il metodo dopo aver fatto il DAO
    beekeerperdao.changeInfo(email, firstName, lastName, password);
  }*/
}
