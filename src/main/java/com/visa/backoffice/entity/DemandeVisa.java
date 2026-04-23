package com.visa.backoffice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "demande_visa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeVisa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_demande", nullable = false)
    private LocalDate dateDemande;

    @Column(length = 50)
    private String categorie;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private StatutDemande statut;

    @Column(length = 100)
    private String nom;

    @Column(length = 100)
    private String prenoms;

    @Column(name = "numero_passeport", nullable = false, length = 50)
    private String numeroPasseport;

    @Column(name = "type_visa", length = 50)
    private String typeVisa;

    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DemandePiece> pieces = new ArrayList<>();
}
