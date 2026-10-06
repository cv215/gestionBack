package org.gestion.gestionstock.repository;


import org.gestion.gestionstock.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur,Integer> {

    void deleteById(Integer id);
}
