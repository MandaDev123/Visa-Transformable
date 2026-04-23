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
    private String nom;
    private String prenoms;
    private String numeroPasseport;
    private String typeVisa;
    private List<PieceResponseDTO> pieces;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PieceResponseDTO {
        private Long pieceId;
        private String nomPiece;
        private String typePiece;
        private Boolean obligatoire;
        private Boolean fourni;
    }
}
