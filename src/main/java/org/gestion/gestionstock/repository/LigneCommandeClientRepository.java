package org.gestion.gestionstock.repository;

import org.gestion.gestionstock.dto.LigneCommandeClientDto;
import org.gestion.gestionstock.model.LigneCommandeClient;
import org.gestion.gestionstock.model.LigneVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LigneCommandeClientRepository extends JpaRepository<LigneCommandeClient, Integer> {

    List<LigneCommandeClient> findAllByCommandeClientId(Integer id);

    List<LigneCommandeClient> findAllByArticleId(Integer id);
}
