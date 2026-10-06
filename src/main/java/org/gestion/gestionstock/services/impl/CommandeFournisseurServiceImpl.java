package org.gestion.gestionstock.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.dto.*;
import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.gestion.gestionstock.model.*;
import org.gestion.gestionstock.repository.*;
import org.gestion.gestionstock.services.CommandeFournisseurService;
import org.gestion.gestionstock.services.MvstkService;
import org.gestion.gestionstock.validator.ArticleValidator;
import org.gestion.gestionstock.validator.CommandeFournisseurValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@Slf4j
public class CommandeFournisseurServiceImpl implements CommandeFournisseurService {
    private final CommandeFournisseurRepository commandeFournisseurRepository;
    private final LigneCommandeFournisseurRepository ligneCommandeFournisseurRepository;
    private final FournisseurRepository fournisseurRepository;
    private final ArticleRepository articleRepository;
    private MvstkService mvstkService;

    @Autowired
    public CommandeFournisseurServiceImpl(ArticleRepository articleRepository, FournisseurRepository fournisseurRepository, CommandeFournisseurRepository commandeFournisseurRepository, LigneCommandeFournisseurRepository ligneCommandeFournisseurRepository, MvstkService mvstkService) {
        this.articleRepository = articleRepository;
        this.ligneCommandeFournisseurRepository = ligneCommandeFournisseurRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.commandeFournisseurRepository = commandeFournisseurRepository;
        this.mvstkService = mvstkService;
    }

    @Override
    public CommandeFournisseurDto save(CommandeFournisseurDto dto) {
        List<String> errors = CommandeFournisseurValidator.validate(dto);

        if (!errors.isEmpty()){
            log.error("commandeFournisseur is not valid {}", dto);
            throw new InvalidEntityException("la commande fournisseur n'est pas valide",
                    ErrorCodes.COMMANDE_FOURNISSEUR_NOT_VALID, errors);
        }
        Optional<Fournisseur> fournisseur = fournisseurRepository.findById(dto.getFournisseur().getId());

        if (!fournisseur.isPresent()){
            log.warn("fournisseur with ID {} was not found in the DB", dto.getFournisseur().getId());
            throw new EntityNotFoundException("aucun fournisseur avec L'ID "+ dto.getFournisseur().getId() + "n'a ete trouve dans la BDD");
        }

        List<String> articleErrors = new ArrayList<>();

        if (dto.getLigneCommandeFournisseurs() != null){
            dto.getLigneCommandeFournisseurs().forEach(ligCodClt ->{
                        if (ligCodClt.getArticle() != null){
                            Optional<Article> article = articleRepository.findById(ligCodClt.getArticle().getId());
                            if (article.isEmpty()){
                                articleErrors.add("l'article avec l'ID " + ligCodClt.getArticle().getId() + "n'existe pas");
                            }
                        }else {
                            articleErrors.add("impossible d'enregistere une commande avec un article null");
                        }
                    }
            );
        }

        if (!articleErrors.isEmpty()){
            throw new InvalidEntityException("article n'existe pas dans la BDD",ErrorCodes.ARTICLE_NOT_FOUND, articleErrors);
        }

        CommandeFournisseur saveCodClt = commandeFournisseurRepository.save(CommandeFournisseurDto.toEntity(dto));

        if (dto.getLigneCommandeFournisseurs() != null){
            dto.getLigneCommandeFournisseurs().forEach(ligCodClt -> {
                LigneCommandeFournisseur ligneCommandeFournisseur = LigneCommandeFournisseurDto.toEntity(ligCodClt);
                ligneCommandeFournisseur.setCommandeFournisseur(saveCodClt);
                ligneCommandeFournisseurRepository.save(ligneCommandeFournisseur);
            });
        }

        return CommandeFournisseurDto.fromEntity(saveCodClt);
    }

    @Override
    public CommandeFournisseurDto updateEtatCommande(Integer idCommande, EtatCommande etatCommande) {
        checkIdCommande(idCommande);
        if (!StringUtils.hasLength(String.valueOf(etatCommande))) {
            log.error("l'état de la commade fournisseur est null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec un etat null", ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        }
        CommandeFournisseurDto commandeFournisseurDto = findById(idCommande);

        if (commandeFournisseurDto.isCommandeLivree()){
            throw new InvalidOperationException("Impossible de modifier la commande lorsqu'elle est livrée", ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        }
        commandeFournisseurDto.setEtatCommande(etatCommande);
        CommandeFournisseur saveComdClt = commandeFournisseurRepository.save(CommandeFournisseurDto.toEntity(commandeFournisseurDto));
        if (commandeFournisseurDto.isCommandeLivree()){
            updateMvstk(idCommande);
        }
        return CommandeFournisseurDto.fromEntity(saveComdClt);
    }

    @Override
    public CommandeFournisseurDto updateQuantiteCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite) {
        checkIdCommande(idCommande);
        checkIdLigneCommande(idLigneCommande);
        if (quantite == null || quantite.compareTo(BigDecimal.ZERO) == 0){
            log.error("la quantité de la commande client is null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec une quantité null ou  zero", ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        }
        CommandeFournisseurDto commandeFournisseur = findById(idCommande);

        if (commandeFournisseur.isCommandeLivree()){
            throw new InvalidOperationException("Impossible de modifier la commande lorsqu'elle est livrée", ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        }
        Optional<LigneCommandeFournisseur> ligneCommandeFournisseurOptional  = ligneCommandeFournisseurRepository.findById(idLigneCommande);
        if (ligneCommandeFournisseurOptional.isEmpty()){
            throw new EntityNotFoundException("aucun ligne commande fournisseur n'a été trouvé avec L'ID "+idLigneCommande, ErrorCodes.FOURNISSEUR_NOT_FOUND);
        }

        LigneCommandeFournisseur ligneCommandeFournisseur = ligneCommandeFournisseurOptional.get();
        ligneCommandeFournisseur.setQuantite(quantite);
        ligneCommandeFournisseurRepository.save(ligneCommandeFournisseur);
        return commandeFournisseur;
    }

    @Override
    public CommandeFournisseurDto updateFournisseur(Integer idCommande, Integer idFournisseur) {
        if (idCommande == null){
            log.error("ID commande client  is null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec un ID null", ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        }
        if (idFournisseur == null) {
            log.error("l'ID du client est null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec un ID du client null", ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        }
        CommandeFournisseurDto commandeFournisseurDto = findById(idCommande);

        if (commandeFournisseurDto.isCommandeLivree()){
            throw new InvalidOperationException("Impossible de modifier la commande lorsqu'elle est livrée", ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        }
        Optional<Fournisseur> fournisseurOptional  = fournisseurRepository.findById(idFournisseur);
        if (fournisseurOptional.isEmpty()){
            throw new EntityNotFoundException("aucun fournisseur n'a été trouvé avec l'ID = "+idFournisseur, ErrorCodes.FOURNISSEUR_NOT_FOUND);
        }
        commandeFournisseurDto.setFournisseur(FournisseurDto.fromEntity(fournisseurOptional.get()));
        return  CommandeFournisseurDto.fromEntity(
                commandeFournisseurRepository.save(CommandeFournisseurDto.toEntity(commandeFournisseurDto))
        );
    }

    @Override
    public CommandeFournisseurDto updateArticle(Integer idCommande, Integer idLigneCommande, Integer newIdArticle) {
        checkIdCommande(idCommande);
        checkIdLigneCommande(idLigneCommande);
        checkIdArticle(newIdArticle, "nouvel");

        CommandeFournisseurDto commandeFournisseurDto = checkEtatCommmande(idCommande);

        Optional<LigneCommandeFournisseur> ligneCommandeFournisseurOptional  = ligneCommandeFournisseurRepository.findById(idLigneCommande);
        if (ligneCommandeFournisseurOptional.isEmpty()){
            throw new EntityNotFoundException("aucun ligne commande fournisseur n'a été trouvé avec L'ID "+idLigneCommande, ErrorCodes.FOURNISSEUR_NOT_FOUND);
        }

        Optional<Article> articleOptional = articleRepository.findById(newIdArticle);
        if (articleOptional.isEmpty()){
            throw new EntityNotFoundException("aucun article n'a été trouvé avec l'ID = "+newIdArticle, ErrorCodes.ARTICLE_NOT_FOUND);
        }
        List<String> errors = ArticleValidator.validate(ArticleDto.fromEntity(articleOptional.get()));

        if (!errors.isEmpty()){
            throw new InvalidEntityException("Article invalide",ErrorCodes.ARTICLE_NOT_VALID, errors);
        }
        LigneCommandeFournisseur ligneCommandeFournisseurToSaved = ligneCommandeFournisseurOptional.get();
        ligneCommandeFournisseurToSaved.setArticle(articleOptional.get());
        ligneCommandeFournisseurRepository.save(ligneCommandeFournisseurToSaved);
        return commandeFournisseurDto;
    }

    @Override
    public CommandeFournisseurDto deleteArticle(Integer idCommande, Integer idLigneCommande) {
        checkIdCommande(idCommande);
        checkIdLigneCommande(idLigneCommande);
        CommandeFournisseurDto commandeFournisseurDto = checkEtatCommmande(idCommande);

        Optional<LigneCommandeFournisseur> ligneCommandeFournisseurOptional  = ligneCommandeFournisseurRepository.findById(idLigneCommande);
        if (ligneCommandeFournisseurOptional.isEmpty()){
            throw new EntityNotFoundException("aucun ligne commande fournisseur n'a été trouvé avec L'ID "+idLigneCommande, ErrorCodes.FOURNISSEUR_NOT_FOUND);
        }
        ligneCommandeFournisseurRepository.deleteById(idLigneCommande);
        return commandeFournisseurDto;
    }

    @Override
    public CommandeFournisseurDto findById(Integer id) {
        if (id == null){
            log.error("commandeFournisseur ID is null");
            return null;
        }
        return commandeFournisseurRepository.findById(id)
                .map(CommandeFournisseurDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(
                        "aucune commande fournisseur n'a ete trouve avec l'ID" +id, ErrorCodes.COMMANDE_FOURNISSEUR_NOT_FOUND
                ));
    }

    @Override
    public CommandeFournisseurDto findByCode(String code) {
        if (StringUtils.hasLength(code)){
            return null;
        }
        return commandeFournisseurRepository.findCommandeFournisseurByCode(code)
                .map(CommandeFournisseurDto::fromEntity)
                .orElseThrow(()-> new EntityNotFoundException(
                        "aucune commande Fournisseur n'a ete trouve avec le code" +code, ErrorCodes.COMMANDE_FOURNISSEUR_NOT_FOUND
                ));
    }

    @Override
    public List<CommandeFournisseurDto> findAll() {
        return commandeFournisseurRepository.findAll().stream()
                .map(CommandeFournisseurDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        if (id == null){
            log.error("commanddeFournisseur ID is null");
            return;
        }
        List<LigneCommandeFournisseur> ligneCommandeFournisseurs = ligneCommandeFournisseurRepository.findAllByCommandeFournisseurId(id);
        if (!ligneCommandeFournisseurs.isEmpty()){
            throw new InvalidOperationException("Impossible de supprimer une commande fournisseur déjà utilisé",ErrorCodes.COMMANDE_FOURNISSEUR_ALREADY_IN_USE);
        }
        commandeFournisseurRepository.deleteById(id);
    }

    @Override
    public List<LigneCommandeFournisseurDto> findAllLigneCommandeFournisseurByCommandeFournisseurId(Integer idCommande) {
        return ligneCommandeFournisseurRepository.findAllByCommandeFournisseurId(idCommande)
                .stream().map(LigneCommandeFournisseurDto::fromEntity)
                .collect(Collectors.toList());
    }

    private void checkIdCommande(Integer idCommande){
        if (idCommande == null){
            log.error("commande fournsseur ID is null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec un ID null", ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        }
    }
    private void checkIdLigneCommande(Integer idLigneCommande){
        if (idLigneCommande == null) {
            log.error("l'ID de la ligne commande est null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec une ligne de commande null", ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        }
    }
    private void checkIdArticle(Integer idArticle, String message){
        if (idArticle == null) {
            log.error("l'ID de " + message +  "est null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec un" + message +"ID article null", ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        }
    }
    private CommandeFournisseurDto checkEtatCommmande(Integer idCommande){

        CommandeFournisseurDto commandeFournisseur = findById(idCommande);
        if (commandeFournisseur.isCommandeLivree()){
            throw new InvalidOperationException("Impossible de modifier la commande lorsqu'elle est livrée", ErrorCodes.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        }
        return commandeFournisseur;
    }


    private void updateMvstk(Integer idCommande) {
        List<LigneCommandeFournisseur> ligneCommandeFournisseurs = ligneCommandeFournisseurRepository.findAllByCommandeFournisseurId(idCommande);
        ligneCommandeFournisseurs.forEach(lig -> {
            MvstkDto mvstkDto = MvstkDto.builder()
                    .article(ArticleDto.fromEntity(lig.getArticle()))
                    .dateMvt(Instant.now())
                    .typeMvstkt(TypeMvstk.ENTREE)
                    .sourceMvstk(SourceMvstk.COMMANDE_FOURNISSEUR)
                    .quantite(lig.getQuantite())
                    .idEntreprise(lig.getIdEntreprise())
                    .build();
            mvstkService.entreeStock(mvstkDto);
        });
    }
}
