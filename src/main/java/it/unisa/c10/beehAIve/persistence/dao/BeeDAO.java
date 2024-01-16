package it.unisa.c10.beehAIve.persistence.dao;

import it.unisa.c10.beehAIve.persistence.entities.Bee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeeDAO extends JpaRepository<Bee, String> {
  // save()

  // findAll()

  // findById()

  // deleteById
}
