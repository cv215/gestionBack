package org.gestion.gestionstock.controller.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gestion.gestionstock.dto.FournisseurDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.gestion.gestionstock.utils.Constants.APP_ROOT;
import static org.gestion.gestionstock.utils.Constants.FOURNISSEUR_ENDPOINT;

@Tag(name = FOURNISSEUR_ENDPOINT)
public interface FournisseurApi {

    @PostMapping(value = "/fournisseurs/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer un fournisseur", description = "cette methode permet d'enregistrer ou modifier le fournisseur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object fournisseur cree / modifier",
                    content = @Content(schema = @Schema(implementation = FournisseurDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object fournisseur n'est pas valide")
    })
    FournisseurDto save(@RequestBody FournisseurDto fournisseurDto);

    @GetMapping(value = APP_ROOT + "/fournisseurs/{idFournisseur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un fournisseur par ID", description = "cette methode permet de chercher un fournisseur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "le fournisseur a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = FournisseurDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucun fournisseur n'existe dans la BDD avec l'ID fourni")
    })
    FournisseurDto findById(@PathVariable("idFournisseur") Integer id);

    @GetMapping(value = APP_ROOT + "/fournisseurs/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoit la liste des fournisseurs", description = "cette methode permet de chercher et renvoyer la liste des fournisseurs qui existent dans la BDD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la liste des fournisseurs / une liste vide",
                    content = @Content(schema = @Schema(implementation = FournisseurDto.class )))
    })
    List<FournisseurDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/fournisseurs/delete/{idFournisseur}")
    @Operation(summary = "Supprimer un fournisseur", description = "cette methode permet de supprimer un fournisseur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "le fournisseur a ete supprimer dans la BDD",
                    content = @Content(schema = @Schema(implementation = FournisseurDto.class )))
    })
    void delete(@PathVariable("idFournisseur") Integer id);
}
