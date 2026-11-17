package org.gestion.gestionstock.repository;


import org.gestion.gestionstock.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur,Integer> {

    void deleteById(@NonNull Integer id);

    java.util.Optional<Fournisseur> findFirstByEmailIgnoreCaseAndIdEntreprise(String email, Integer idEntreprise);

}
