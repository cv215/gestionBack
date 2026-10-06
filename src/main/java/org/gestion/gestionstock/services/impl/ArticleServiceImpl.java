package org.gestion.gestionstock.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.dto.ArticleDto;
import org.gestion.gestionstock.dto.LigneCommandeClientDto;
import org.gestion.gestionstock.dto.LigneCommandeFournisseurDto;
import org.gestion.gestionstock.dto.LigneVenteDto;
import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.gestion.gestionstock.model.Article;
import org.gestion.gestionstock.model.LigneCommandeClient;
import org.gestion.gestionstock.model.LigneCommandeFournisseur;
import org.gestion.gestionstock.model.LigneVente;
import org.gestion.gestionstock.repository.ArticleRepository;
import org.gestion.gestionstock.repository.LigneCommandeClientRepository;
import org.gestion.gestionstock.repository.LigneCommandeFournisseurRepository;
import org.gestion.gestionstock.repository.LigneVenteRepository;
import org.gestion.gestionstock.services.ArticleService;
import org.gestion.gestionstock.validator.ArticleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    private ArticleRepository articleRepository;
    private  LigneVenteRepository venteRepository;
    private LigneCommandeClientRepository commandeClientRepository;
    private LigneCommandeFournisseurRepository commandeFournisseurRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository, LigneCommandeClientRepository commandeClientRepository,
                              LigneCommandeFournisseurRepository commandeFournisseurRepository, LigneVenteRepository venteRepository) {
        this.articleRepository = articleRepository;
        this.commandeClientRepository = commandeClientRepository;
        this.commandeFournisseurRepository = commandeFournisseurRepository;
        this.venteRepository = venteRepository;
    }

    @Override
    public ArticleDto save(ArticleDto dto) {
        List<String> errors = ArticleValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("Article is not valid {}", dto);
            throw new InvalidEntityException("l'article n'est pas valide", ErrorCodes.ARTICLE_NOT_FOUND, errors);
        }
        return ArticleDto.fromEntity(
                articleRepository.save(
                        ArticleDto.toEntity(dto)
                )
        );
    }

    @Override
    public ArticleDto findById(Integer id) {
        if (id== null){
            log.error("article ID is null");
            return null;
        }

        Optional<Article> article = articleRepository.findById(id);

        return Optional.of(ArticleDto.
                fromEntity(article.get())).orElseThrow(
                () -> new EntityNotFoundException(
                        "Aucun article avec l'ID = " + id + "n'a été trouvé dans la BBD",
                        ErrorCodes.ARTICLE_NOT_FOUND)
        );
    }

    @Override
    public ArticleDto findByCodeArticle(String codeArticle) {
        if (!StringUtils.hasLength(codeArticle)){
            log.error("article CODE is null");
            return null;
        }
        Optional<Article> article = articleRepository.findArticleByCodeArticle(codeArticle);

        return Optional.of(ArticleDto.fromEntity(article.get())).orElseThrow(
                () -> new EntityNotFoundException(
                        "Aucun article avec le CODE = " + codeArticle + "n'a été trouvé dans la BBD",
                        ErrorCodes.ARTICLE_NOT_FOUND)
        );
    }

    @Override
    public List<ArticleDto> findAll() {
        return articleRepository.findAll().stream()
                .map(ArticleDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<LigneVenteDto> findHistoriqueVentes(Integer idArticle) {
        return venteRepository.findAllByArticleId(idArticle).stream()
                .map(LigneVenteDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<LigneCommandeClientDto> findHistoriqueCommandeClient(Integer idArticle) {
        return commandeClientRepository.findAllByArticleId(idArticle).stream()
                .map(LigneCommandeClientDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<LigneCommandeFournisseurDto> findHistoriqueCommandeFournisseur(Integer idArticle) {
        return commandeFournisseurRepository.findAllByArticleId(idArticle).stream()
                .map(LigneCommandeFournisseurDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleDto> findAllArticleByIdCathegory(Integer idCathegory) {
        return articleRepository.findAllByCathegoryId(idCathegory).stream()
                .map(ArticleDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        if (id== null){
            log.error("article ID is null");
            return ;
        }
        List<LigneCommandeClient> ligneCommandeClients = commandeClientRepository.findAllByArticleId(id);
        if (!ligneCommandeClients.isEmpty()){
            throw new InvalidOperationException("Impossible de supprimer un article déjà utilisé dans les commandes clients",ErrorCodes.ARTICLE_ALREADY_IN_USE);
        }
        List<LigneCommandeFournisseur> ligneCommandeFournisseurs = commandeFournisseurRepository.findAllByArticleId(id);
        if (!ligneCommandeFournisseurs.isEmpty()){
            throw new InvalidOperationException("Impossible de supprimer un article déjà utilisé dans les commandes clients",ErrorCodes.ARTICLE_ALREADY_IN_USE);
        }
        List<LigneVente> ligneVentes = venteRepository.findAllByArticleId(id);
        if (!ligneVentes.isEmpty()){
            throw new InvalidOperationException("Impossible de supprimer un article déjà utilisé dans les commandes clients",ErrorCodes.ARTICLE_ALREADY_IN_USE);
        }
        articleRepository.deleteById(id);
    }
}
