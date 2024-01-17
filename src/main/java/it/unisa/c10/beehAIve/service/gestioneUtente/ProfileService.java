package it.unisa.c10.beehAIve.service.gestioneUtente;

import it.unisa.c10.beehAIve.persistence.dao.BeekeeperDAO;
import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {
  private final BeekeeperDAO beekeerperdao;

  @Autowired
  public ProfileService(BeekeeperDAO beekeerperdao) {
    this.beekeerperdao = beekeerperdao;
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
    return !(beekeerperdao.findByCompanyPiva(piva).isEmpty());
  }

  public boolean userExists(String email, String password) {
    Beekeeper beekeeper = new Beekeeper();
    beekeeper.setPasswordhash(password);
    return !(beekeerperdao.findByEmailAndPasswordhash(email,beekeeper.getPasswordhash()).isEmpty());
  }

  public void changeInfo(String email, String firstName, String lastName) {
    Optional<Beekeeper> beekeeperOptional = beekeerperdao.findById(email);
    Beekeeper beekeeper;
    if (beekeeperOptional.isPresent()) {
      beekeeper = beekeeperOptional.get();
      beekeeper.setFirstName(firstName);
      beekeeper.setLastName(lastName);
      beekeerperdao.save(beekeeper);
    }
  }

  public void changePassword(String email, String password) {
    Optional<Beekeeper> beekeeperOptional = beekeerperdao.findById(email);
    Beekeeper beekeeper;
    if (beekeeperOptional.isPresent()) {
      beekeeper = beekeeperOptional.get();
      beekeeper.setPasswordhash(password);
      beekeerperdao.save(beekeeper);
    }
  }
}
