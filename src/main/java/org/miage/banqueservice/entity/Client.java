package org.miage.banqueservice.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Client {
    @Id
    private String id;
    private String nom;
    private Float montantCompte;

}
