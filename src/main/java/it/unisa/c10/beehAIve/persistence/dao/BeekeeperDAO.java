package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BeekeeperDAO extends JpaRepository<Beekeeper, String> {

  /**
   * Restituisce l'apicoltore partendo da email e password.
   * @param email L'email dell'apicoltore che vogliamo ottenere.
   * @param passwordhash La password dell'apicoltore che vogliamo ottenere.
   * @return Un oggetto lista che contiene il {@code Beekeeper} cercato se esiste
   */
  List<Beekeeper> findByEmailAndPasswordhash(String email, String passwordhash);

  /**
   * Restituisce l'apicoltore con una determinata partita IVA.
   * @param companyPiva La partita IVA dell'apicoltore che vogliamo ottenere.
   * @return Un oggetto lista che contiene il {@code Beekeeper} cercato se esiste
   */
  List<Beekeeper> findByCompanyPiva(String companyPiva);

}
