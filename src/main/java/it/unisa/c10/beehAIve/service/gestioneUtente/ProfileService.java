package it.unisa.c10.beehAIve.service.gestioneUtente;

import it.unisa.c10.beehAIve.persistence.dao.BeekeeperDAO;
import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


/*
La seguente classe deve supportare le seguenti operazioni:
1. Registrare l'apicoltore.
2. Loggare dentro l'apicoltore.
3. Loggare fuori l'apicoltore.
4. Modificare i dati personali dell'apicoltore.
5. Modificare la password dell'apicoltore.
 */

@Service
public class ProfileService {
  private final BeekeeperDAO beekeerperDAO;

  @Autowired
  public ProfileService(BeekeeperDAO beekeerperdao) {
    this.beekeerperDAO = beekeerperdao;
  }

  public boolean regexEmail (String email) {
    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{5,}$";
    return email.matches(emailRegex);
  }

  public boolean regexPassword (String password) {
    String passwordRegex =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@.$!%*?&])[A-Za-z\\d@.$!%*?&]{8,}$";
    return password.matches(passwordRegex);
  }

  public boolean regexFirstName (String firstName) {
    String firstNameRegex = "^[A-Z][a-z'-]+(?: [A-Z][a-z'-]+)*$";
    return firstName.matches(firstNameRegex);
  }

  public boolean regexLastName (String lastName) {
    String lastNameRegex = "^[A-Z][a-z'-]+(?: [A-Z][a-z'-]+)*$";
    return lastName.matches(lastNameRegex);
  }

  public boolean regexCompanyName (String companyName) {
    String companyRegex = "^[a-zA-Z0-9\\s.'\",&()-]+$";
    return companyName.matches(companyRegex);
  }

  public boolean regexCompanyPiva (String companyPiva) {
    String companyPivaRegex = "^[\\d-]{9,}$";
    return companyPiva.matches(companyPivaRegex);
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
    return beekeerperDAO.save(beekeeper);
  }

  public boolean emailExists(String email) {
    return (beekeerperDAO.findById(email)).isPresent();
  }

  public boolean pivaExists(String piva) {
    return !(beekeerperDAO.findByCompanyPiva(piva).isEmpty());
  }

  public boolean userExists(String email, String password) {
    Beekeeper beekeeper = new Beekeeper();
    beekeeper.setPasswordhash(password);
    return !(beekeerperDAO.findByEmailAndPasswordhash(email,beekeeper.getPasswordhash()).isEmpty());
  }

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

  public void changePassword(String email, String password) {
    Optional<Beekeeper> beekeeperOptional = beekeerperDAO.findById(email);
    Beekeeper beekeeper;
    if (beekeeperOptional.isPresent()) {
      beekeeper = beekeeperOptional.get();
      beekeeper.setPasswordhash(password);
      beekeerperDAO.save(beekeeper);
    }
  }

  public Optional<Beekeeper> findBeekeeper(String email) {
    return beekeerperDAO.findById(email);
  }

}

