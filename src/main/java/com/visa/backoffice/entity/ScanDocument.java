package com.visa.backoffice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scan_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_piece_id", nullable = false)
    private DemandePiece demandePiece;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "nom_original", nullable = false)
    private String nomOriginal;

    // Type can be optional now since it's linked to a piece, but we can keep it
    @Column(name = "type_document", length = 50)
    private String typeDocument;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "taille_octets")
    private Long tailleOctets;

    @Column(name = "chemin_fichier", length = 500)
    private String cheminFichier;

    @Column(name = "date_upload")
    private LocalDateTime dateUpload;
}
