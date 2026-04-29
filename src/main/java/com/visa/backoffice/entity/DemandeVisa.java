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

    // ===== État Civil =====
    @Column(length = 100, nullable = false)
    private String nom;

    @Column(length = 100, nullable = false)
    private String prenoms;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "lieu_naissance", length = 150)
    private String lieuNaissance;

    @Column(length = 100)
    private String nationalite;

    @Column(length = 150)
    private String email;

    @Column(length = 50)
    private String contact;

    // ===== Passeport =====
    @Column(name = "numero_passeport", nullable = false, length = 50)
    private String numeroPasseport;

    @Column(name = "date_delivrance_passeport")
    private LocalDate dateDelivrancePasseport;

    @Column(name = "date_expiration_passeport")
    private LocalDate dateExpirationPasseport;

    // ===== Visa Transformable =====
    @Column(name = "type_visa", length = 50)
    private String typeVisa;

    @Column(name = "date_entree_madagascar")
    private LocalDate dateEntreeMadagascar;

    @Column(name = "lieu_reference_visa", length = 150)
    private String lieuReferenceVisa;

    @Column(name = "numero_visa", length = 50)
    private String numeroVisa;

    @Column(name = "numero_carte_resident", length = 50)
    private String numeroCarteResident;

    @Column(name = "date_expiration_visa")
    private LocalDate dateExpirationVisa;

    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DemandePiece> pieces = new ArrayList<>();
}
