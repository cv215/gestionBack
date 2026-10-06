package org.gestion.gestionstock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cathegory")
public class Cathegory extends AbstractEntity{
    @Column(name = "identreprise")
    private Integer idEntreprise;

    @Column(name = "code")
    private String code;

    @Column(name = "designation")
    private  String designation;

    @OneToMany(mappedBy = "cathegory")
    private List<Article> articles;
}
