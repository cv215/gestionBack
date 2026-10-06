package org.gestion.gestionstock.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gestion.gestionstock.dto.VentesDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.gestion.gestionstock.utils.Constants.APP_ROOT;

@Tag(name = APP_ROOT + "/ventes")
public interface VentesApi {

    @PostMapping(value = "/ventes/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer une vente", description = "cette methode permet d'enregistrer ou modifier une vente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object vente cree / modifier",
                    content = @Content(schema = @Schema(implementation = VentesDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object vente n'est pas valide")
    })
    VentesDto save(@RequestBody VentesDto dto);

    @GetMapping(value = APP_ROOT + "/ventes/{idVentes}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher une vente par ID", description = "cette methode permet de chercher une vente par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la vente a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = VentesDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucune vente n'existe dans la BDD avec l'ID fourni")
    })
    VentesDto findById(@PathVariable("idVentes") Integer id);

    @GetMapping(value = APP_ROOT + "/ventes/{codeVentes}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher une vente par code", description = "cette methode permet de chercher une vente par son code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la vente a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = VentesDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucune vente n'existe dans la BDD avec le code fourni ")
    })
    VentesDto findByCode(@PathVariable("codeVentes") String code);

    @GetMapping(value = APP_ROOT + "/ventes/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoit la liste des ventes", description = "cette methode permet de chercher et renvoyer la liste des ventes qui existent dans la BDD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la liste des ventes / une liste vide",
                    content = @Content(schema = @Schema(implementation = VentesDto.class )))
    })
    List<VentesDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/ventes/delete/{idVentes}")
    @Operation(summary = "Supprimer une vente", description = "cette methode permet de supprimer une vente par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la vente a ete supprimer dans la BDD",
                    content = @Content(schema = @Schema(implementation = VentesDto.class )))
    })
    void delete(@PathVariable("idVentes") Integer id);
}
