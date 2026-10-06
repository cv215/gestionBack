package org.gestion.gestionstock.repository;


import org.gestion.gestionstock.model.CommandeFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CommandeFournisseurRepository extends JpaRepository< CommandeFournisseur, Integer> {

     List<CommandeFournisseur> findAllByFournisseurId(Integer id);

    Optional<CommandeFournisseur> findCommandeFournisseurByCode(String code);
}
