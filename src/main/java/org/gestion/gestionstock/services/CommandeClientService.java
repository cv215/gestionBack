package org.gestion.gestionstock.services;


import org.gestion.gestionstock.dto.CommandeClientDto;
import org.gestion.gestionstock.dto.LigneCommandeClientDto;
import org.gestion.gestionstock.model.EtatCommande;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface CommandeClientService {

    CommandeClientDto save(CommandeClientDto dto);

    CommandeClientDto updateEtatCommande(Integer idCommande, EtatCommande etatCommande);

    CommandeClientDto updateQuantiteCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite);

    CommandeClientDto updateClient(Integer idCommande, Integer idClient);

    CommandeClientDto updateArticle(Integer idCommande, Integer idLigneCommande,Integer newIdArticle);

    CommandeClientDto deleteArticle(Integer idCommande, Integer idLigneCommande);

    CommandeClientDto findById(Integer id);

    CommandeClientDto findByCode(String code);

    List<CommandeClientDto> findAll();

    void delete(Integer id);

    List<LigneCommandeClientDto> findAllLigneCommandeClientByCommandeClientId(Integer idCommande);
}
