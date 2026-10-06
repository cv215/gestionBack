package org.gestion.gestionstock.controller.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gestion.gestionstock.dto.EntrepriseDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.gestion.gestionstock.utils.Constants.APP_ROOT;
import static org.gestion.gestionstock.utils.Constants.ENTREPRISE_ENDPOINT;

@Tag(name = ENTREPRISE_ENDPOINT)
public interface EntrepriseApi {

    @PostMapping(value = APP_ROOT + "/entreprises/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer une entreprise", description = "cette methode permet d'enregistrer ou modifier une entreprise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object entreprise cree / modifier",
                    content = @Content(schema = @Schema(implementation = EntrepriseDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object entreprise n'est pas valide")
    })
    EntrepriseDto save(@RequestBody EntrepriseDto dto);

    @GetMapping(value = APP_ROOT + "/entreprises/{idEntreprise}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher une entreprise par ID", description = "cette methode permet de chercher une entreprise par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la entreprise a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = EntrepriseDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucune entreprise n'existe dans la BDD avec l'ID fourni")
    })
    EntrepriseDto findById(@PathVariable("idEntreprise") Integer id);

    @GetMapping(value = APP_ROOT + "/entreprises/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoit la liste des entreprise", description = "cette methode permet de chercher et renvoyer la liste des entreprises qui existent dans la BDD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la liste des entreprises / une liste vide",
                    content = @Content(schema = @Schema(implementation = EntrepriseDto.class )))
    })
    List<EntrepriseDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/entreprises/delete/{idEntreprise}")
    @Operation(summary = "Supprimer une entreprise", description = "cette methode permet de supprimer une entreprise par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'entreprise a ete supprimer dans la BDD",
                    content = @Content(schema = @Schema(implementation = EntrepriseDto.class )))
    })
    void delete(@PathVariable("idEntreprise") Integer id);
}
