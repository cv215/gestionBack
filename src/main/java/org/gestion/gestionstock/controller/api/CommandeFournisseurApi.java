package org.gestion.gestionstock.controller.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gestion.gestionstock.dto.CommandeClientDto;
import org.gestion.gestionstock.dto.CommandeFournisseurDto;
import org.gestion.gestionstock.dto.LigneCommandeClientDto;
import org.gestion.gestionstock.dto.LigneCommandeFournisseurDto;
import org.gestion.gestionstock.model.EtatCommande;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static org.gestion.gestionstock.utils.Constants.APP_ROOT;

@Tag(name = APP_ROOT + "/commandesfournisseurs")
public interface CommandeFournisseurApi {

    @PostMapping(value = "/commandesfournisseurs/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer une commande fournisseur", description = "cette methode permet d'enregistrer ou modifier une commande fournisseur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object commande fournisseur cree / modifier",
                    content = @Content(schema = @Schema(implementation = CommandeFournisseurDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object commande fournisseur n'est pas valide")
    })
    CommandeFournisseurDto save(@RequestBody CommandeFournisseurDto dto);

    @PatchMapping(value = "/commandesfournisseurs/update/etat/{idCommande}/{etatCommande}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer une commandeFournisseur", description = "cette methode permet d'enregistrer ou modifier une commandeFournisseur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object commandeFournisseur cree / modifier",
                    content = @Content(schema = @Schema(implementation = CommandeFournisseurDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object commandeFournisseur n'est pas valide")
    })
    ResponseEntity<CommandeFournisseurDto> updateEtatCommande(@PathVariable("idCommande") Integer idCommande, @PathVariable("etatCommande") EtatCommande etatCommande);

    @PatchMapping(value = "/commandesfournisseurs/update/article/{idCommande}/{idLigneCommande}/{idArticle}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Modifier un client dans la commande", description = "cette methode permet modifier un fournisseur de la commande")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object fournisseur de la commandeFournisseur cree / modifier",
                    content = @Content(schema = @Schema(implementation = CommandeFournisseurDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object fournisseur n'est pas valide")
    })
    ResponseEntity<CommandeFournisseurDto> updateArticle(@PathVariable("idCommande") Integer idCommande, @PathVariable("idLigneCommande") Integer idLigneCommande,
                                                    @PathVariable("idArticle") Integer idArticle);


    @PatchMapping(value = "/commandesfournisseurs/update/quantite/{idCommande}/{idLigneCommande}/{quantite}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer une quantité de la commande client", description = "cette methode permet d'enregistrer ou modifier une quantité de la commande fournisseur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object quantité de la commandeFournisseur cree / modifier",
                    content = @Content(schema = @Schema(implementation = CommandeFournisseurDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object quantité n'est pas valide")
    })
    ResponseEntity<CommandeFournisseurDto> updateQuantiteCommande(@PathVariable("idCommande") Integer idCommande, @PathVariable("idLigneCommande") Integer idLigneCommande,
                                                             @PathVariable("quantite") BigDecimal quantite);


    @PatchMapping(value = "/commandesfournisseurs/update/fournisseur/{idCommande}/{idFournisseur}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "modifier un fournisseur", description = "cette methode permet de modifier un fournisseur sur une commande")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object fournisseur cree / modifier",
                    content = @Content(schema = @Schema(implementation = CommandeFournisseurDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object fournisseur n'est pas valide")
    })
    ResponseEntity<CommandeFournisseurDto> updateFournisseur(@PathVariable("idCommande") Integer idCommande, @PathVariable("idFournisseur") Integer idFournisseur);



    @GetMapping(value = APP_ROOT + "/commandesfournisseurs/{idCommandeFournisseur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher une commande fournisseur par ID", description = "cette methode permet de chercher une commande fournisseur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la commande fournisseur a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = CommandeFournisseurDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucune commande fournisseur n'existe dans la BDD avec l'ID fourni")
    })
    CommandeFournisseurDto findById(@PathVariable("idCommandeFournisseur") Integer id);

    @GetMapping(value = APP_ROOT + "/commandesfournisseurs/{codeCommandeFournisseur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher une commande fournisseur par code", description = "cette methode permet de chercher une comande fournisseur par son code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la commande fournisseur a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = CommandeFournisseurDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucune commande fournisseur n'existe dans la BDD avec le code fourni ")
    })
    CommandeFournisseurDto findByCode(@PathVariable("codeCommandeFournisseur") String code);

    @GetMapping(value = APP_ROOT + "/commandesfournisseurs/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoit la liste des commande fournisseur", description = "cette methode permet de chercher et renvoyer la liste des commande fournisseur qui existent dans la BDD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la liste des commande fournisseur / une liste vide",
                    content = @Content(schema = @Schema(implementation = CommandeFournisseurDto.class )))
    })
    List<CommandeFournisseurDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/commandesfournisseurs/delete/{idCommandeFournisseur}")
    @Operation(summary = "Supprimer une commande fournisseur", description = "cette methode permet de supprimer une commande fournisseur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la commande fournisseur a ete supprimer dans la BDD",
                    content = @Content(schema = @Schema(implementation = CommandeFournisseurDto.class )))
    })
    void delete(@PathVariable("idCommandeFournisseur")  Integer id);

    @GetMapping(value = APP_ROOT + "/commandesfournisseurs/lignesCommande/{idCommande}")
    @Operation(summary = "Chercher une ligne commandeFournisseur", description = "cette methode permet de chercher une ligne de commandeFournisseur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la ligne de commande fournisseur a ete trouvé dans la BDD",
                    content = @Content(schema = @Schema(implementation = CommandeFournisseurDto.class )))
    })
    ResponseEntity<List<LigneCommandeFournisseurDto>> findAllLigneCommandeFournisseurByCommandeFournisseurId(@PathVariable("idCommande") Integer idCommande);
}
