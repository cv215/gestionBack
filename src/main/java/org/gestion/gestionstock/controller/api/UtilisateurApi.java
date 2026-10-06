package org.gestion.gestionstock.controller.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gestion.gestionstock.dto.UtilisateurDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.gestion.gestionstock.utils.Constants.APP_ROOT;
import static org.gestion.gestionstock.utils.Constants.UTILISATEUR_ENDPOINT;

@Tag(name = UTILISATEUR_ENDPOINT)
public interface UtilisateurApi {

        @PostMapping(value = APP_ROOT + "/utilisateurs/inscription", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer un utilisateur", description = "cette methode permet d'enregistrer ou modifier un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object utilisateur cree / modifier",
            content = @Content(schema = @Schema(implementation = UtilisateurDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object utilisateur n'est pas valide")
    })
    UtilisateurDto save(@RequestBody UtilisateurDto utilisateurDto);

    @GetMapping(value = APP_ROOT + "/utilisateurs/{idUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un utilisateur par ID", description = "cette methode permet de chercher un utilisateur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'utilisateur a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = UtilisateurDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur n'existe dans la BDD avec l'ID fourni")
    })
    UtilisateurDto findById(@PathVariable("idUtilisateur") Integer id);

        @GetMapping(value = APP_ROOT + "/utilisateurs/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un utilisateur par email", description = "cette methode permet de chercher un utilisateur par son email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'utilisateur a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = UtilisateurDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur n'existe dans la BDD avec l'Email fourni")
    })
    UtilisateurDto findByEmail(@PathVariable("email") String email);

    @GetMapping(value = APP_ROOT + "/utilisateurs/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoit la liste des utilisateurs", description = "cette methode permet de chercher et renvoyer la liste des utilisateurs qui existent dans la BDD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la liste des utilisateurs / une liste vide",
                    content = @Content(schema = @Schema(implementation = UtilisateurDto.class )))
    })
    List<UtilisateurDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/utilisateurs/delete/{idUtilisateur}")
    @Operation(summary = "Supprimer un utilisateur", description = "cette methode permet de supprimer un utilisateur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'utilisateur a ete supprimer dans la BDD",
                    content = @Content(schema = @Schema(implementation = UtilisateurDto.class )))
    })
    void delete(@PathVariable("idUtilisateur") Integer id);
}
