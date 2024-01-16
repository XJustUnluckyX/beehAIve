package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Beekeeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BeekeeperDAO extends JpaRepository<Beekeeper, String> {
  // save()

  // findAll()

  // findById()

  // deleteById()

  List<Beekeeper> findByEmailAndPasswordhash(String email, String passwordhash);

  List<Beekeeper> findByFirstName(String firstName);

  List<Beekeeper> findByLastName(String lastName);

  List<Beekeeper> findByFirstNameAndLastName(String firstName, String lastName);

  List<Beekeeper> findByCompanyName(String companyName);

  List<Beekeeper> findByCompanyPiva(String companyPiva);

  List<Beekeeper> findByCompanyNameAndCompanyPiva(String companyName, String companyPiva);

  List<Beekeeper> findByCompanyNameIsNull();

  List<Beekeeper> findBySubscribedTrue();

  List<Beekeeper> findBySubscribedFalse();

  List<Beekeeper> findBySubscrExpirationDateBefore(LocalDate currentDate);
}
