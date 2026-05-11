package com.visa.backoffice.dto;

import com.visa.backoffice.entity.StatutDemande;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeVisaResponseDTO {

    private Long id;
    private LocalDate dateDemande;
    private String categorie;
    private StatutDemande statut;

    // État Civil
    private String nom;
    private String prenoms;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String nationalite;
    private String email;
    private String contact;

    // Passeport
    private String numeroPasseport;
    private LocalDate dateDelivrancePasseport;
    private LocalDate dateExpirationPasseport;

    // Visa Transformable
    private String typeVisa;
    private LocalDate dateEntreeMadagascar;
    private String lieuReferenceVisa;
    private String numeroVisa;
    private String numeroCarteResident;
    private LocalDate dateExpirationVisa;

    private List<PieceResponseDTO> pieces;

    // Photo d'identité et signature (base64 data-URI)
    private String photoIdentiteBase64;
    private String signatureBase64;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PieceResponseDTO {
        private Long demandePieceId;
        private Long pieceId;
        private String nomPiece;
        private String typePiece;
        private Boolean obligatoire;
        private Boolean fourni;
        private List<ScanDocumentResponseDTO> documents;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScanDocumentResponseDTO {
        private Long id;
        private String nomOriginal;
        private String typeDocument;
        private Long tailleOctets;
        private java.time.LocalDateTime dateUpload;
        private String cheminFichier;
    }
}
