package org.gestion.gestionstock.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Image stockée en base de données (photo d'article, de profil, etc.).
 * Le stockage en base survit aux redéploiements (Render efface le disque local).
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "photo")
public class Photo extends AbstractEntity {

    @Column(name = "identreprise")
    private Integer idEntreprise;

    @Column(name = "contenttype", nullable = false, length = 100)
    private String contentType;

    @Column(name = "nomfichier")
    private String nomFichier;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data", nullable = false, columnDefinition = "LONGBLOB")
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private byte[] data;
}
