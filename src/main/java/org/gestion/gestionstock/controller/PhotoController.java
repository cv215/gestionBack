package org.gestion.gestionstock.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gestion.gestionstock.model.Photo;
import org.gestion.gestionstock.services.PhotoService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/photos")
@Tag(name = "/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping(value = "/{context}/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Envoyer une photo",
            description = "context : article, utilisateur, client, fournisseur ou entreprise. Image JPEG/PNG/WebP/GIF de 5 Mo maximum.")
    public Map<String, String> envoyer(@PathVariable("context") String context,
                                       @PathVariable("id") Integer id,
                                       @RequestPart("file") MultipartFile file) {
        return Map.of("photo", photoService.enregistrer(context, id, file));
    }

    /** Lecture publique : les balises <img> du navigateur n'envoient pas le jeton. */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> lire(@PathVariable("id") Integer id) {
        Photo photo = photoService.lire(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                .body(photo.getData());
    }
}
