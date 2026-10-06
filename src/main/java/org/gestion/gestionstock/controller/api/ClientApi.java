package org.gestion.gestionstock.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gestion.gestionstock.dto.ClientDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.gestion.gestionstock.utils.Constants.APP_ROOT;

@Tag(name = APP_ROOT + "/client")
public interface ClientApi {

    @PostMapping(value = "/clients/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer un client", description = "cette methode permet d'enregistrer ou modifier un client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object client cree / modifier",
                    content = @Content(schema = @Schema(implementation = ClientDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object client n'est pas valide")
    })
    ClientDto save(@RequestBody ClientDto clientDto);

    @GetMapping(value = APP_ROOT + "/clients/{idClient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un client par ID", description = "cette methode permet de chercher un client par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "le client a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = ClientDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucun client n'existe dans la BDD avec l'ID fourni")
    })
    ClientDto findById(@PathVariable("idClient") Integer id);

    @GetMapping(value = APP_ROOT + "/clients/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoit la liste des clients", description = "cette methode permet de chercher et renvoyer la liste des clients qui existent dans la BDD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la liste des clients / une liste vide",
                    content = @Content(schema = @Schema(implementation = ClientDto.class )))
    })
    List<ClientDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/clients/delete/{idClient}")
    @Operation(summary = "Supprimer un client", description = "cette methode permet de supprimer un client par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "le client a ete supprimer dans la BDD",
                    content = @Content(schema = @Schema(implementation = ClientDto.class )))
    })
    void delete(@PathVariable("idClient") Integer id);
}
