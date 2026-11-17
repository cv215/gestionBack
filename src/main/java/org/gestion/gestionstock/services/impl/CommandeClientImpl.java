package org.gestion.gestionstock.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.dto.*;
import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.model.*;
import org.gestion.gestionstock.repository.ArticleRepository;
import org.gestion.gestionstock.repository.ClientRepository;
import org.gestion.gestionstock.repository.CommandeClientRepository;
import org.gestion.gestionstock.repository.LigneCommandeClientRepository;
import org.gestion.gestionstock.services.CommandeClientService;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.gestion.gestionstock.services.MvstkService;
import org.gestion.gestionstock.validator.ArticleValidator;
import org.gestion.gestionstock.validator.CommandeClientValodator;
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
public class CommandeClientImpl implements CommandeClientService {

    @org.springframework.beans.factory.annotation.Autowired
    private org.gestion.gestionstock.config.CurrentTenant currentTenant;



    private final CommandeClientRepository commandeClientRepository;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;
    private final ClientRepository clientRepository;
    private final ArticleRepository articleRepository;
    private final MvstkService mvstkService;

    @Autowired
    public CommandeClientImpl(ArticleRepository articleRepository, ClientRepository clientRepository, CommandeClientRepository commandeClientRepository, LigneCommandeClientRepository ligneCommandeClientRepository, MvstkService mvstkService) {
        this.articleRepository = articleRepository;
        this.ligneCommandeClientRepository = ligneCommandeClientRepository;
        this.clientRepository = clientRepository;
        this.commandeClientRepository = commandeClientRepository;
        this.mvstkService = mvstkService;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public CommandeClientDto save(CommandeClientDto dto) {
        if (dto != null) {
            dto.setIdEntreprise(currentTenant.resolve(dto.getIdEntreprise()));
            if (currentTenant.isPartenaireExterne()) {
                // Un client connecté ne peut passer qu'une nouvelle commande, en son propre nom.
                String email = currentTenant.email().orElse("");
                Client moi = clientRepository.findFirstByEmailIgnoreCaseAndIdEntreprise(email, dto.getIdEntreprise())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Aucune fiche client n'est associée à votre compte", ErrorCodes.CLIENT_NOT_FOUND));
                dto.setId(null);
                dto.setEtatCommande(EtatCommande.EN_COURS);
                dto.setClient(ClientDto.fromEntity(moi));
            }
        }
        List<String> errors = CommandeClientValodator.validate(dto);

        if (!errors.isEmpty()){
            log.error("commandeClient is not valid {}", dto);
            throw new InvalidEntityException("la commande client n'est pas valide",
                    ErrorCodes.COMMANDE_CLIENT_NOT_VALID, errors);
        }
        if (dto.getId() !=null && dto.isCommandeLivree()){
            throw new InvalidOperationException("Impossible de modifier la commande lorsqu'elle est livrée", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
        Optional<Client> client = clientRepository.findById(dto.getClient().getId());

        if (client.isEmpty()){
            log.warn("client with ID {} was not found in the DB", dto.getClient().getId());
            throw new EntityNotFoundException("aucun client avec L'ID "+ dto.getClient().getId() + " n'a ete trouve dans la BDD", ErrorCodes.CLIENT_NOT_FOUND);
        }
        Optional<CommandeClient> sameCode = commandeClientRepository.findCommandeClientByCode(dto.getCode().trim());
        if (sameCode.isPresent() && !sameCode.get().getId().equals(dto.getId())) {
            throw new InvalidEntityException("la commande client n'est pas valide", ErrorCodes.COMMANDE_CLIENT_NOT_VALID,
                    List.of("Une commande avec le code " + dto.getCode().trim() + " existe déjà"));
        }
        dto.setCode(dto.getCode().trim());

        List<String> articleErrors = new ArrayList<>();

        if (dto.getLigneCommandeClients() != null){
            dto.getLigneCommandeClients().forEach(ligCodClt ->{
                if (ligCodClt.getArticle() != null && ligCodClt.getArticle().getId() != null){
                    Optional<Article> article = articleRepository.findById(ligCodClt.getArticle().getId());
                    if (article.isEmpty()){
                        articleErrors.add("l'article avec l'ID " + ligCodClt.getArticle().getId() + " n'existe pas");
                    }
                    if (ligCodClt.getQuantite() == null || ligCodClt.getQuantite().compareTo(BigDecimal.ZERO) <= 0){
                        articleErrors.add("la quantité de chaque ligne doit être supérieure à zéro");
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

        CommandeClient saveCodClt = commandeClientRepository.save(CommandeClientDto.toEntity(dto));

        if (dto.getLigneCommandeClients() != null){
            dto.getLigneCommandeClients().forEach(ligCodClt -> {
                LigneCommandeClient ligneCommandeClient = LigneCommandeClientDto.toEntity(ligCodClt);
                ligneCommandeClient.setCommandeClient(saveCodClt);
                ligneCommandeClient.setIdEntreprise(saveCodClt.getIdEntreprise());
                Article article = articleRepository.findById(ligCodClt.getArticle().getId()).orElseThrow();
                ligneCommandeClient.setArticle(article);
                if (ligneCommandeClient.getPrixUnitaire() == null) {
                    ligneCommandeClient.setPrixUnitaire(article.getPrixUnitaireTtc());
                }
                ligneCommandeClientRepository.save(ligneCommandeClient);
            });
        }

        return CommandeClientDto.fromEntity(saveCodClt);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public CommandeClientDto updateEtatCommande(Integer idCommande, EtatCommande etatCommande) {
        checkIdCommande(idCommande);
        if (etatCommande == null) {
            log.error("l'état de la commade client est null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec un etat null", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
        CommandeClientDto commandeClient = findById(idCommande);

        if (commandeClient.isCommandeLivree()){
            throw new InvalidOperationException("Impossible de modifier la commande lorsqu'elle est livrée", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
        commandeClient.setEtatCommande(etatCommande);
        CommandeClient saveComdClt = commandeClientRepository.save(CommandeClientDto.toEntity(commandeClient));
        if (commandeClient.isCommandeLivree()){
        updateMvstk(idCommande);
        }
        return CommandeClientDto.fromEntity(saveComdClt);
    }

    @Override
    public CommandeClientDto updateQuantiteCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite) {
        checkIdCommande(idCommande);
        checkIdLigneCommande(idLigneCommande);
        if (quantite == null || quantite.compareTo(BigDecimal.ZERO) == 0){
            log.error("la quantité de la commande client is null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec une quantité null ou  zero", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
        CommandeClientDto commandeClient = findById(idCommande);

        if (commandeClient.isCommandeLivree()){
            throw new InvalidOperationException("Impossible de modifier la commande lorsqu'elle est livrée", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
        Optional<LigneCommandeClient> ligneCommandeClientOptional  = ligneCommandeClientRepository.findById(idLigneCommande);
        if (ligneCommandeClientOptional.isEmpty()){
            throw new EntityNotFoundException("aucun ligne commande client n'a été trouvé avec L'ID "+idLigneCommande, ErrorCodes.CLIENT_NOT_FOUND);
        }

        LigneCommandeClient ligneCommandeClient = ligneCommandeClientOptional.get();
        ligneCommandeClient.setQuantite(quantite);
        ligneCommandeClientRepository.save(ligneCommandeClient);
        return commandeClient;
    }

    @Override
    public CommandeClientDto updateClient(Integer idCommande, Integer idClient) {

        if (idCommande == null){
            log.error("ID commande client  is null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec un ID null", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
        if (idClient == null) {
            log.error("l'ID du client est null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec un ID du client null", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
        CommandeClientDto commandeClient = findById(idCommande);

        if (commandeClient.isCommandeLivree()){
            throw new InvalidOperationException("Impossible de modifier la commande lorsqu'elle est livrée", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
        Optional<Client> clientOptional  = clientRepository.findById(idClient);
        if (clientOptional.isEmpty()){
            throw new EntityNotFoundException("aucun client n'a été trouvé avec l'ID = "+idClient, ErrorCodes.CLIENT_NOT_FOUND);
        }
        commandeClient.setClient(ClientDto.fromEntity(clientOptional.get()));
        return  CommandeClientDto.fromEntity(
                commandeClientRepository.save(CommandeClientDto.toEntity(commandeClient))
        );
    }

    @Override
    public CommandeClientDto updateArticle(Integer idCommande, Integer idLigneCommande, Integer newIdArticle) {
        checkIdCommande(idCommande);
        checkIdLigneCommande(idLigneCommande);
        checkIdArticle(newIdArticle, "nouvel");

        CommandeClientDto commandeClientDto = checkEtatCommmande(idCommande);

        Optional<LigneCommandeClient> ligneCommandeClientOptional  = ligneCommandeClientRepository.findById(idLigneCommande);
        if (ligneCommandeClientOptional.isEmpty()){
            throw new EntityNotFoundException("aucun ligne commande client n'a été trouvé avec L'ID "+idLigneCommande, ErrorCodes.CLIENT_NOT_FOUND);
        }

        Optional<Article> articleOptional = articleRepository.findById(newIdArticle);

        if (articleOptional.isEmpty()){
            throw new EntityNotFoundException("aucun article n'a été trouvé avec l'ID = "+newIdArticle, ErrorCodes.ARTICLE_NOT_FOUND);
        }
        List<String> errors = ArticleValidator.validate(ArticleDto.fromEntity(articleOptional.get()));

        if (!errors.isEmpty()){
            throw new InvalidEntityException("Article invalide",ErrorCodes.ARTICLE_NOT_VALID, errors);
        }
        LigneCommandeClient ligneCommandeClientToSaved = ligneCommandeClientOptional.get();
        ligneCommandeClientToSaved.setArticle(articleOptional.get());
        ligneCommandeClientRepository.save(ligneCommandeClientToSaved);
        return commandeClientDto;
    }

    @Override
    public CommandeClientDto deleteArticle(Integer idCommande, Integer idLigneCommande) {
        checkIdCommande(idCommande);
        checkIdLigneCommande(idLigneCommande);
        CommandeClientDto commandeClientDto = checkEtatCommmande(idCommande);

        Optional<LigneCommandeClient> ligneCommandeClientOptional  = ligneCommandeClientRepository.findById(idLigneCommande);
        if (ligneCommandeClientOptional.isEmpty()){
            throw new EntityNotFoundException("aucun ligne commande client n'a été trouvé avec L'ID "+idLigneCommande, ErrorCodes.CLIENT_NOT_FOUND);
        }
        ligneCommandeClientRepository.deleteById(idLigneCommande);
        return commandeClientDto;
    }

    @Override
    public CommandeClientDto findById(Integer id) {
        if (id == null){
            log.error("ID commandeClient  is null");
            return null;
        }
        return commandeClientRepository.findById(id)
                .filter(this::visible)
                .map(CommandeClientDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(
                        "aucune commande client n'a ete trouve avec l'ID " +id, ErrorCodes.COMMANDE_CLIENT_NOT_FOUND
                ));
    }

    @Override
    public CommandeClientDto findByCode(String code) {
        if (!StringUtils.hasLength(code)){
            return null;
        }
        return commandeClientRepository.findCommandeClientByCode(code)
                .filter(this::visible)
                .map(CommandeClientDto::fromEntity)
                .orElseThrow(()-> new EntityNotFoundException(
                        "aucune commande client n'a ete trouve avec le code" +code, ErrorCodes.COMMANDE_CLIENT_NOT_FOUND
                ));
    }

    @Override
    public List<CommandeClientDto> findAll() {
        return commandeClientRepository.findAll().stream()
                .filter(this::visible)
                .map(CommandeClientDto::fromEntity)
                .collect(Collectors.toList());
    }

    /** Commande de l'entreprise courante ; pour un client connecté, uniquement ses propres commandes. */
    private boolean visible(CommandeClient commande) {
        if (!currentTenant.owns(commande.getIdEntreprise())) {
            return false;
        }
        if (currentTenant.isPartenaireExterne()) {
            String email = currentTenant.email().orElse("");
            return commande.getClient() != null && email.equalsIgnoreCase(commande.getClient().getEmail());
        }
        return true;
    }

    @Override
    public void delete(Integer id) {
        if (id == null){
            log.error("commandeClient ID is null");
            return;
        }
        CommandeClientDto commande = findById(id);
        if (commande.isCommandeLivree()){
            throw new InvalidOperationException("Impossible de supprimer une commande client déjà livrée",ErrorCodes.COMMANDE_CLIENT_ALREADY_IN_USE);
        }
        // Une commande non livrée n'a généré aucun mouvement de stock : on supprime ses lignes puis la commande.
        ligneCommandeClientRepository.deleteAll(ligneCommandeClientRepository.findAllByCommandeClientId(id));
        commandeClientRepository.deleteById(id);
    }

    @Override
    public List<LigneCommandeClientDto> findAllLigneCommandeClientByCommandeClientId(Integer idCommande){
        findById(idCommande); // vérifie que la commande est visible pour l'utilisateur connecté
        return ligneCommandeClientRepository.findAllByCommandeClientId(idCommande)
                .stream().map(LigneCommandeClientDto::fromEntity)
                .collect(Collectors.toList());
    }

    private void checkIdCommande(Integer idCommande){
        if (idCommande == null){
            log.error("commande client ID is null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec un ID null", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
    }
    private void checkIdLigneCommande(Integer idLigneCommande){
        if (idLigneCommande == null) {
            log.error("l'ID de la ligne commande est null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec une ligne de commande null", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
    }
    private void checkIdArticle(Integer idArticle, String message){
        if (idArticle == null) {
            log.error("l'ID de " + message +  "est null");
            throw new InvalidOperationException("Impossible de modifier l'état de la commande avec un" + message +"ID article null", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
    }
    private CommandeClientDto checkEtatCommmande(Integer idCommande){

        CommandeClientDto commandeClient = findById(idCommande);
        if (commandeClient.isCommandeLivree()){
            throw new InvalidOperationException("Impossible de modifier la commande lorsqu'elle est livrée", ErrorCodes.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
        return commandeClient;
    }

    private void updateMvstk(Integer idCommande) {
        List<LigneCommandeClient> ligneCommandeClients = ligneCommandeClientRepository.findAllByCommandeClientId(idCommande);
        ligneCommandeClients.forEach(lig ->{
            MvstkDto mvstkDto = MvstkDto.builder()
                    .article(ArticleDto.fromEntity(lig.getArticle()))
                    .dateMvt(Instant.now())
                    .typeMvstkt(TypeMvstk.SORTIE)
                    .sourceMvstk(SourceMvstk.COMMANDE_CLIENT)
                    .quantite(lig.getQuantite())
                    .idEntreprise(lig.getIdEntreprise())
                    .build();
            mvstkService.sortieStock(mvstkDto);
        });
    }
}
