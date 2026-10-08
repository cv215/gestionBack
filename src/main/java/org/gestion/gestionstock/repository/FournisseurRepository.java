package org.gestion.gestionstock.repository;


import org.gestion.gestionstock.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur,Integer> {

    void deleteById(Integer id);
}
