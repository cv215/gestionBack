package org.gestion.gestionstock.controller;

import org.gestion.gestionstock.controller.api.CommandeClientApi;
import org.gestion.gestionstock.dto.CommandeClientDto;
import org.gestion.gestionstock.dto.LigneCommandeClientDto;
import org.gestion.gestionstock.model.EtatCommande;
import org.gestion.gestionstock.services.CommandeClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class CommandeClientController implements CommandeClientApi {

    private final CommandeClientService commandeClientService;

    @Autowired
    public CommandeClientController(CommandeClientService commandeClientService) {
        this.commandeClientService = commandeClientService;
    }

    @Override
    public void delete(Integer id) {
        commandeClientService.delete(id);
    }

    @Override
    public ResponseEntity<CommandeClientDto> deleteArticle(Integer idCommande, Integer idLigneCommande) {
        return ResponseEntity.ok(commandeClientService.deleteArticle(idCommande, idLigneCommande));
    }

    @Override
    public ResponseEntity<List<LigneCommandeClientDto>> findAllLigneCommandeClientByCommandeClientId(Integer idCommande) {
        return ResponseEntity.ok(commandeClientService.findAllLigneCommandeClientByCommandeClientId(idCommande));
    }

    @Override
    public CommandeClientDto save(CommandeClientDto dto) {
        return commandeClientService.save(dto);
    }

    @Override
    public ResponseEntity<CommandeClientDto> updateEtatCommande(Integer idCommande, EtatCommande etatCommande) {
        return ResponseEntity.ok(commandeClientService.updateEtatCommande(idCommande, etatCommande));
    }

    @Override
    public ResponseEntity<CommandeClientDto> updateArticle(Integer idCommande, Integer idLigneCommande, Integer idArticle) {
        return ResponseEntity.ok(commandeClientService.updateArticle(idCommande, idLigneCommande, idArticle ));
    }

    @Override
    public ResponseEntity<CommandeClientDto> updateQuantiteCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite) {
        return ResponseEntity.ok(commandeClientService.updateQuantiteCommande(idCommande, idLigneCommande, quantite));
    }

    @Override
    public ResponseEntity<CommandeClientDto> updateClient(Integer idCommande, Integer idClient) {
        return ResponseEntity.ok(commandeClientService.updateClient(idCommande, idClient));
    }

    @Override
    public CommandeClientDto findById(Integer id) {
        return commandeClientService.findById(id);
    }

    @Override
    public CommandeClientDto findByCode(String code) {
        return commandeClientService.findByCode(code);
    }

    @Override
    public List<CommandeClientDto> findAll() {
        return commandeClientService.findAll();
    }
}
