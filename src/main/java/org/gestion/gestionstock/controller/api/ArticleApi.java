package org.gestion.gestionstock.controller.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gestion.gestionstock.dto.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.gestion.gestionstock.utils.Constants.APP_ROOT;

@Tag(name = APP_ROOT + "/articles")
public interface ArticleApi {

    @PostMapping(value = "/articles/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer un article", description = "cette methode permet d'enregistrer ou modifier un article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'object article cree / modifier",
                    content = @Content(schema = @Schema(implementation = ArticleDto.class ))),
            @ApiResponse(responseCode = "400", description = "l'object article n'est pas valide")
    })
    ArticleDto save(@RequestBody ArticleDto dto);

    @GetMapping(value = APP_ROOT + "/articles/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un article par ID", description = "cette methode permet de chercher un article par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'article a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = ArticleDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucun article n'existe dans la BDD avec l'ID fourni",
                    content = @Content(schema = @Schema(implementation = ArticleDto.class )))
    })
    ArticleDto findById(@PathVariable("idArticle") Integer id);

    @GetMapping(value = APP_ROOT + "/articles/{codeArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un article par code", description = "cette methode permet de chercher un article par son code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'article a ete trouve dans la BDD",
                    content = @Content(schema = @Schema(implementation = ArticleDto.class ))),
            @ApiResponse(responseCode = "404", description = "Aucun article n'existe dans la BDD avec le code fourni")
    })
    ArticleDto findByCodeArticle(@PathVariable("codeArticle") String codeArticle);

    @GetMapping(value = APP_ROOT + "/articles/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoit la liste des articles", description = "cette methode permet de chercher et renvoyer la liste des articles qui existent dans la BDD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "la liste des articles / une liste vide",
                    content = @Content(schema = @Schema(implementation = ArticleDto.class )))
    })
    List<ArticleDto> findAll();

    @DeleteMapping(value = APP_ROOT + "/articles/delete/{idArticle}")
    @Operation(summary = "Supprimer un article", description = "cette methode permet de supprimer un article par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "l'article a ete supprimer dans la BDD",
                    content = @Content(schema = @Schema(implementation = ArticleDto.class )))
    })
    void delete(@PathVariable("idArticle") Integer id);

    @GetMapping(value = APP_ROOT + "/articles/historique/ventes/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<LigneVenteDto> findHistoriqueVentes(@PathVariable("idArticle") Integer idArticle);

    @GetMapping(value = APP_ROOT + "/articles/historique/commandeclient/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<LigneCommandeClientDto> findHistoriqueCommandeClient(@PathVariable("idArticle") Integer idArticle);

    @GetMapping(value = APP_ROOT + "/articles/historique/commandefournisseur/{idArticle}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<LigneCommandeFournisseurDto> findHistoriqueCommandeFournisseur(@PathVariable("idArticle") Integer idArticle);

    @GetMapping(value = APP_ROOT + "/articles/filter/cathegory/{idCathegory}", produces = MediaType.APPLICATION_JSON_VALUE)
    List<ArticleDto> findAllArticleByIdCathegory(@PathVariable("idCathegory") Integer idCathegory);
}
