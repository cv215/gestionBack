package org.gestion.gestionstock.controller;

import org.gestion.gestionstock.controller.api.CommandeFournisseurApi;
import org.gestion.gestionstock.dto.CommandeFournisseurDto;
import org.gestion.gestionstock.dto.LigneCommandeFournisseurDto;
import org.gestion.gestionstock.model.EtatCommande;
import org.gestion.gestionstock.services.CommandeFournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class CommandeFournisseurController implements CommandeFournisseurApi {
    private final CommandeFournisseurService commandeFournisseurService;

    @Autowired
    public CommandeFournisseurController(CommandeFournisseurService commandeFournisseurService) {
        this.commandeFournisseurService = commandeFournisseurService;
    }

    @Override
    public void delete(Integer id) {
        commandeFournisseurService.delete(id);
    }

    @Override
    public ResponseEntity<List<LigneCommandeFournisseurDto>> findAllLigneCommandeFournisseurByCommandeFournisseurId(Integer idCommande) {
        return ResponseEntity.ok(commandeFournisseurService.findAllLigneCommandeFournisseurByCommandeFournisseurId(idCommande));
    }

    @Override
    public CommandeFournisseurDto save(CommandeFournisseurDto dto) {
        return commandeFournisseurService.save(dto);
    }

    @Override
    public ResponseEntity<CommandeFournisseurDto> updateEtatCommande(Integer idCommande, EtatCommande etatCommande) {
        return ResponseEntity.ok(commandeFournisseurService.updateEtatCommande(idCommande, etatCommande));
    }

    @Override
    public ResponseEntity<CommandeFournisseurDto> updateArticle(Integer idCommande, Integer idLigneCommande, Integer idArticle) {
        return ResponseEntity.ok(commandeFournisseurService.updateArticle(idCommande, idLigneCommande, idArticle));
    }

    @Override
    public ResponseEntity<CommandeFournisseurDto> updateQuantiteCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite) {
        return ResponseEntity.ok(commandeFournisseurService.updateQuantiteCommande(idCommande, idLigneCommande, quantite));
    }

    @Override
    public ResponseEntity<CommandeFournisseurDto> updateFournisseur(Integer idCommande, Integer idFournisseur) {
        return ResponseEntity.ok(commandeFournisseurService.updateFournisseur(idCommande, idFournisseur));
    }

    @Override
    public CommandeFournisseurDto findById(Integer id) {
        return commandeFournisseurService.findById(id);
    }

    @Override
    public CommandeFournisseurDto findByCode(String code) {
        return commandeFournisseurService.findByCode(code);
    }

    @Override
    public List<CommandeFournisseurDto> findAll() {
        return commandeFournisseurService.findAll();
    }
}
