package org.gestion.gestionstock.repository;

import org.gestion.gestionstock.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ClientRepository extends JpaRepository< Client, Integer> {
    java.util.Optional<Client> findFirstByEmailIgnoreCaseAndIdEntreprise(String email, Integer idEntreprise);
}
