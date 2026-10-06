package org.gestion.gestionstock.controller.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gestion.gestionstock.dto.CommandeClientDto;
import org.gestion.gestionstock.dto.LigneCommandeClientDto;
import org.gestion.gestionstock.model.EtatCommande;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static org.gestion.gestionstock.utils.Constants.APP_ROOT;

@Tag(name = APP_ROOT + "/commandesclients")
public interface CommandeClientApi {

    @PostMapping(value = "/commandesclients/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer une commandeClient", description = "cette methode permet d'enregistrer ou modifier une commandeClient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object commandeClient cree / modifier",
                    content = @Content(schema = @Schema(implementation = CommandeClientDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object commandeClient n'est pas valide")
    })
    CommandeClientDto save(@RequestBody CommandeClientDto dto);

    @PatchMapping(value = "/commandesclients/update/etat/{idCommande}/{etatCommande}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer une commandeClient", description = "cette methode permet d'enregistrer ou modifier une commandeClient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object commandeClient cree / modifier",
                    content = @Content(schema = @Schema(implementation = CommandeClientDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object commandeClient n'est pas valide")
    })
    ResponseEntity<CommandeClientDto> updateEtatCommande(@PathVariable("idCommande") Integer idCommande, @PathVariable("etatCommande") EtatCommande etatCommande);

    @PatchMapping(value = "/commandesclients/update/article/{idCommande}/{idLigneCommande}/{idArticle}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Modifier un client dans la commande", description = "cette methode permet modifier un client de la commande")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object client de la commandeClient cree / modifier",
                    content = @Content(schema = @Schema(implementation = CommandeClientDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object client n'est pas valide")
    })
    ResponseEntity<CommandeClientDto> updateArticle(@PathVariable("idCommande") Integer idCommande, @PathVariable("idLigneCommande") Integer idLigneCommande,
                                                             @PathVariable("idArticle") Integer idArticle);


    @PatchMapping(value = "/commandesclients/update/quantite/{idCommande}/{idLigneCommande}/{quantite}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer une quantité de la commande client", description = "cette methode permet d'enregistrer ou modifier une quantité de la commande client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object quantité de la commandeClient cree / modifier",
                    content = @Content(schema = @Schema(implementation = CommandeClientDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object quantité n'est pas valide")
    })
    ResponseEntity<CommandeClientDto> updateQuantiteCommande(@PathVariable("idCommande") Integer idCommande, @PathVariable("idLigneCommande") Integer idLigneCommande,
                                                             @PathVariable("quantite")BigDecimal quantite);


    @PatchMapping(value = "/commandesclients/update/client/{idCommande}/{idClient}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "modifier un client", description = "cette methode permet de modifier un client sur une commande")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object client cree / modifier",
                    content = @Content(schema = @Schema(implementation = CommandeClientDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object client n'est pas valide")
    })
    ResponseEntity<CommandeClientDto> updateClient(@PathVariable("idCommande") Integer idCommande, @PathVariable("idClient") Integer idClient);



    @GetMapping(value = APP_ROOT + "/commandesclients/{idCommandeClient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Modifier l'état de la commande", description = "cette methode permet de modifier l'état d'une commandeClient par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'état de la commande a ete trouve modifié",
                    content = @Content(schema = @Schema(implementation = CommandeClientDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucune état n'existe dans la BDD avec l'ID fourni")
    })
    CommandeClientDto findById(@PathVariable("idCommandeClient") Integer id);

    @GetMapping(value = APP_ROOT + "/commandesclients/{codeCommandeClient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher une commandeClient par code", description = "cette methode permet de chercher une commandeClient par son code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la commande client a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = CommandeClientDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucune commande client n'existe dans la BDD avec le code fourni ")
    })
    CommandeClientDto findByCode(@PathVariable("codeCommandeClient") String code);

    @GetMapping(value = APP_ROOT + "/commandesclients/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoit la liste des commandeClients", description = "cette methode permet de chercher et renvoyer la liste des commandeClients qui existent dans la BDD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la liste des commandeClients / une liste vide",
                    content = @Content(schema = @Schema(implementation = CommandeClientDto.class )))
    })
    List<CommandeClientDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/commandesclients/delete/{idCommandeClient}")
    @Operation(summary = "Supprimer une commandeClient", description = "cette methode permet de supprimer une commandeClient par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la commandeClient a ete supprimer dans la BDD",
                    content = @Content(schema = @Schema(implementation = CommandeClientDto.class )))
    })
     void delete(@PathVariable("idCommandeClient") Integer id);

    @DeleteMapping(value = APP_ROOT + "/commandesclients/delete/article/{idCommande}/{idLigneCommande}")
    @Operation(summary = "Supprimer un article", description = "cette methode permet de supprimer l'article dans la BDD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'article a été supprimer dans la BDD",
                    content = @Content(schema = @Schema(implementation = CommandeClientDto.class )))
    })
    ResponseEntity<CommandeClientDto> deleteArticle(@PathVariable("idCommande") Integer idCommande, @PathVariable("idLigneCommande") Integer idLigneCommande);

    @GetMapping(value = APP_ROOT + "/commandesclients/lignesCommande/{idCommande}")
    @Operation(summary = "Chercher une ligne commandeClient", description = "cette methode permet de chercher une ligne de commandeClient par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la ligne de commande client a ete trouvé dans la BDD",
                    content = @Content(schema = @Schema(implementation = CommandeClientDto.class )))
    })
    ResponseEntity<List<LigneCommandeClientDto>> findAllLigneCommandeClientByCommandeClientId(@PathVariable("idCommande") Integer idCommande);
}
