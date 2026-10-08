package org.gestion.gestionstock.controller.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gestion.gestionstock.dto.CathegoryDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.gestion.gestionstock.utils.Constants.APP_ROOT;

@Tag(name = APP_ROOT + "/cathegory")
public interface CathegoryApi {

    @PostMapping(value = "/cathegory/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer une cathegory", description = "cette methode permet d'enregistrer ou modifier une cathegory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object cathegory cree / modifier",
                    content = @Content(schema = @Schema(implementation = CathegoryDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object cathegory n'est pas valide")
    })
    CathegoryDto save(@RequestBody CathegoryDto dto);

    @GetMapping(value = APP_ROOT + "/cathegory/{idCathegory}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher une cathegory par ID", description = "cette methode permet de chercher une cathegory par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la cathegory a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = CathegoryDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucune cathegory n'existe dans la BDD avec l'ID fourni")
    })
    CathegoryDto findById(@PathVariable("idCathegory") Integer idCathegory);

    @GetMapping(value = APP_ROOT + "/cathegory/{codeCathegory}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher une cathegory par code", description = "cette methode permet de chercher une cathegory par son code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la cathegory a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = CathegoryDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucune cathegory n'existe dans la BDD avec le code fourni ")
    })
    CathegoryDto findByCode(@PathVariable("codeCathegory") String code);

    @GetMapping(value = APP_ROOT + "/cathegory/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoit la liste des cathegory", description = "cette methode permet de chercher et renvoyer la liste des cathegory qui existent dans la BDD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la liste des cathegory / une liste vide",
                    content = @Content(schema = @Schema(implementation = CathegoryDto.class )))
    })
    List<CathegoryDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/cathegory/delete/{idCathegory}")
    @Operation(summary = "Supprimer une cathegory", description = "cette methode permet de supprimer une cathegory par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la cathegory a ete supprimer dans la BDD",
                    content = @Content(schema = @Schema(implementation = CathegoryDto.class )))
    })
    void delete(@PathVariable("idCathegory") Integer id);
}
