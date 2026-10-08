package org.gestion.gestionstock.repository;

import org.gestion.gestionstock.model.Cathegory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CathegoryRepository extends JpaRepository< Cathegory, Integer> {

    Optional<Cathegory> findByCode(String code);
}
