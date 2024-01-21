package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BeekeeperDAO extends JpaRepository<Beekeeper, String> {


  List<Beekeeper> findByEmailAndPasswordhash(String email, String passwordhash);

  List<Beekeeper> findByCompanyPiva(String companyPiva);

}
