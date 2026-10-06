package org.gestion.gestionstock.model;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public enum TypeMvstk {

    ENTREE, SORTIE, CORRECTION_POS, CORRECTION_NEG
}
