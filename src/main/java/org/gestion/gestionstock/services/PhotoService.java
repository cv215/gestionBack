package org.gestion.gestionstock.services;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.config.CurrentTenant;
import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.gestion.gestionstock.model.Photo;
import org.gestion.gestionstock.model.Utilisateur;
import org.gestion.gestionstock.repository.*;
import org.gestion.gestionstock.utils.TypeRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enregistre les photos (articles, profils, clients, fournisseurs, entreprise) en base de données
 * et met à jour le champ "photo" de l'élément concerné avec l'URL relative /photos/{id}.
 */
@Service
@Slf4j
public class PhotoService {

    public static final long TAILLE_MAX = 5L * 1024 * 1024;
    private static final Set<String> TYPES_AUTORISES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final Pattern PHOTO_URL = Pattern.compile("^/photos/(\\d+)$");

    private final PhotoRepository photoRepository;
    private final ArticleRepository articleRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;
    private final FournisseurRepository fournisseurRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final CurrentTenant currentTenant;

    public PhotoService(PhotoRepository photoRepository, ArticleRepository articleRepository,
                        UtilisateurRepository utilisateurRepository, ClientRepository clientRepository,
                        FournisseurRepository fournisseurRepository, EntrepriseRepository entrepriseRepository,
                        CurrentTenant currentTenant) {
        this.photoRepository = photoRepository;
        this.articleRepository = articleRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.clientRepository = clientRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.entrepriseRepository = entrepriseRepository;
        this.currentTenant = currentTenant;
    }

    @Transactional
    public String enregistrer(String context, Integer id, MultipartFile fichier) {
        valider(fichier);
        if (id == null) {
            throw new InvalidOperationException("Identifiant manquant", ErrorCodes.UPDATE_PHOTO_EXCEPTION);
        }
        Utilisateur moi = currentTenant.currentUser()
                .orElseThrow(() -> new AccessDeniedException("Connexion requise"));

        switch (context == null ? "" : context.toLowerCase(Locale.ROOT)) {
            case "article" -> {
                exiger(TypeRole.ADMIN_ENTREPRISE, TypeRole.GESTIONNAIRE_STOCK);
                var article = articleRepository.findById(id).filter(a -> currentTenant.owns(a.getIdEntreprise()))
                        .orElseThrow(() -> introuvable("article", id));
                return remplacer(fichier, article.getPhoto(), url -> { article.setPhoto(url); articleRepository.save(article); });
            }
            case "utilisateur" -> {
                boolean soiMeme = Objects.equals(moi.getId(), id);
                if (!soiMeme) {
                    exiger(TypeRole.ADMIN_ENTREPRISE);
                }
                var utilisateur = utilisateurRepository.findById(id)
                        .filter(u -> soiMeme || (u.getEntreprise() != null && currentTenant.owns(u.getEntreprise().getId())))
                        .orElseThrow(() -> introuvable("utilisateur", id));
                return remplacer(fichier, utilisateur.getPhoto(), url -> { utilisateur.setPhoto(url); utilisateurRepository.save(utilisateur); });
            }
            case "client" -> {
                exiger(TypeRole.ADMIN_ENTREPRISE, TypeRole.COMMERCIAL);
                var client = clientRepository.findById(id).filter(c -> currentTenant.owns(c.getIdEntreprise()))
                        .orElseThrow(() -> introuvable("client", id));
                return remplacer(fichier, client.getPhoto(), url -> { client.setPhoto(url); clientRepository.save(client); });
            }
            case "fournisseur" -> {
                exiger(TypeRole.ADMIN_ENTREPRISE, TypeRole.GESTIONNAIRE_STOCK);
                var fournisseur = fournisseurRepository.findById(id).filter(f -> currentTenant.owns(f.getIdEntreprise()))
                        .orElseThrow(() -> introuvable("fournisseur", id));
                return remplacer(fichier, fournisseur.getPhoto(), url -> { fournisseur.setPhoto(url); fournisseurRepository.save(fournisseur); });
            }
            case "entreprise" -> {
                exiger(TypeRole.ADMIN_ENTREPRISE);
                var entreprise = entrepriseRepository.findById(id).filter(e -> currentTenant.owns(e.getId()))
                        .orElseThrow(() -> introuvable("entreprise", id));
                return remplacer(fichier, entreprise.getPhoto(), url -> { entreprise.setPhoto(url); entrepriseRepository.save(entreprise); });
            }
            default -> throw new InvalidOperationException("Contexte inconnu pour l'enregistrement de la photo : " + context,
                    ErrorCodes.UNKNOW_CONTEXT);
        }
    }

    @Transactional(readOnly = true)
    public Photo lire(Integer id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Photo introuvable", ErrorCodes.UPDATE_PHOTO_EXCEPTION));
    }

    private String remplacer(MultipartFile fichier, String ancienneUrl, Consumer<String> appliquer) {
        Photo photo = new Photo();
        photo.setContentType(fichier.getContentType());
        photo.setNomFichier(fichier.getOriginalFilename());
        photo.setIdEntreprise(currentTenant.idEntreprise().orElse(null));
        try {
            photo.setData(fichier.getBytes());
        } catch (IOException e) {
            throw new InvalidOperationException("Impossible de lire le fichier envoyé", e, ErrorCodes.UPDATE_PHOTO_EXCEPTION);
        }
        photo = photoRepository.save(photo);
        String url = "/photos/" + photo.getId();
        appliquer.accept(url);
        supprimerAncienne(ancienneUrl);
        return url;
    }

    private void supprimerAncienne(String ancienneUrl) {
        if (ancienneUrl == null) {
            return;
        }
        Matcher m = PHOTO_URL.matcher(ancienneUrl);
        if (m.matches()) {
            photoRepository.findById(Integer.valueOf(m.group(1))).ifPresent(photoRepository::delete);
        }
    }

    private void valider(MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            throw new InvalidOperationException("Aucun fichier n'a été envoyé", ErrorCodes.UPDATE_PHOTO_EXCEPTION);
        }
        if (fichier.getSize() > TAILLE_MAX) {
            throw new InvalidOperationException("La photo ne doit pas dépasser 5 Mo", ErrorCodes.UPDATE_PHOTO_EXCEPTION);
        }
        String type = fichier.getContentType() == null ? "" : fichier.getContentType().toLowerCase(Locale.ROOT);
        if (!TYPES_AUTORISES.contains(type)) {
            throw new InvalidOperationException("Format non accepté : utilisez une image JPEG, PNG, WebP ou GIF",
                    ErrorCodes.UPDATE_PHOTO_EXCEPTION);
        }
    }

    private void exiger(TypeRole... roles) {
        if (!currentTenant.hasAnyRole(roles)) {
            throw new AccessDeniedException("Vous n'avez pas les droits nécessaires pour cette action");
        }
    }

    private static EntityNotFoundException introuvable(String type, Integer id) {
        return new EntityNotFoundException("Aucun " + type + " avec l'ID " + id, ErrorCodes.UPDATE_PHOTO_EXCEPTION);
    }
}
