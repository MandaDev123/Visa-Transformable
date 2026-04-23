package com.visa.backoffice.service;

import com.visa.backoffice.dto.*;
import com.visa.backoffice.entity.*;
import com.visa.backoffice.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandeVisaService {

    private final DemandeVisaRepository demandeVisaRepository;
    private final PieceJustificativeRepository pieceJustificativeRepository;
    private final DemandePieceRepository demandePieceRepository;

    // =============================================
    // Créer une demande (sauvegarde même incomplète)
    // =============================================
    @Transactional
    public DemandeVisaResponseDTO creerDemande(DemandeVisaDTO dto) {
        DemandeVisa demande = DemandeVisa.builder()
                .dateDemande(dto.getDateDemande())
                .categorie(dto.getCategorie())
                .nom(dto.getNom())
                .prenoms(dto.getPrenoms())
                .numeroPasseport(dto.getNumeroPasseport())
                .typeVisa(dto.getTypeVisa())
                .statut(StatutDemande.BROUILLON)
                .build();

        demande = demandeVisaRepository.save(demande);

        // Initialiser les pièces si le type de visa est défini
        if (dto.getTypeVisa() != null && !dto.getTypeVisa().isBlank()) {
            initialiserPieces(demande, dto.getPieces());
        }

        // Recalculer le statut
        demande.setStatut(calculerStatut(demande));
        demande = demandeVisaRepository.save(demande);

        return toResponseDTO(demande);
    }

    // =============================================
    // Modifier une demande existante
    // =============================================
    @Transactional
    public DemandeVisaResponseDTO modifierDemande(Long id, DemandeVisaDTO dto) {
        DemandeVisa demande = demandeVisaRepository.findByIdWithPieces(id)
                .orElseThrow(() -> new EntityNotFoundException("Demande non trouvée avec l'id : " + id));

        // Mettre à jour les champs
        if (dto.getDateDemande() != null) demande.setDateDemande(dto.getDateDemande());
        if (dto.getCategorie() != null) demande.setCategorie(dto.getCategorie());
        if (dto.getNom() != null) demande.setNom(dto.getNom());
        if (dto.getPrenoms() != null) demande.setPrenoms(dto.getPrenoms());
        if (dto.getNumeroPasseport() != null) demande.setNumeroPasseport(dto.getNumeroPasseport());
        if (dto.getTypeVisa() != null) demande.setTypeVisa(dto.getTypeVisa());

        // Mettre à jour les pièces
        if (dto.getPieces() != null) {
            mettreAJourPieces(demande, dto.getPieces());
        }

        // Recalculer le statut
        demande.setStatut(calculerStatut(demande));
        demande = demandeVisaRepository.save(demande);

        return toResponseDTO(demande);
    }

    // =============================================
    // Récupérer une demande par ID
    // =============================================
    @Transactional(readOnly = true)
    public DemandeVisaResponseDTO getDemande(Long id) {
        DemandeVisa demande = demandeVisaRepository.findByIdWithPieces(id)
                .orElseThrow(() -> new EntityNotFoundException("Demande non trouvée avec l'id : " + id));
        return toResponseDTO(demande);
    }

    // =============================================
    // Vérification des pièces (logique métier du spec)
    // =============================================
    public boolean verifierPieces(DemandeVisa demande) {
        if (demande.getTypeVisa() == null || demande.getTypeVisa().isBlank()) {
            return false;
        }

        List<PieceJustificative> piecesObligatoires =
                pieceJustificativeRepository.findPiecesObligatoires(demande.getTypeVisa());

        List<DemandePiece> piecesFournies = demande.getPieces();

        for (PieceJustificative p : piecesObligatoires) {
            boolean trouve = piecesFournies.stream()
                    .anyMatch(dp ->
                            dp.getPiece().getId().equals(p.getId())
                            && Boolean.TRUE.equals(dp.getFourni())
                    );
            if (!trouve) return false;
        }

        return true;
    }

    // =============================================
    // Calculer le statut automatiquement
    // =============================================
    private StatutDemande calculerStatut(DemandeVisa demande) {
        // Champs obligatoires
        if (demande.getDateDemande() == null) return StatutDemande.BROUILLON;
        if (demande.getNumeroPasseport() == null || demande.getNumeroPasseport().isBlank()) return StatutDemande.BROUILLON;

        // Vérification des pièces
        if (!verifierPieces(demande)) return StatutDemande.BROUILLON;

        return StatutDemande.DOSSIER_CREE;
    }

    // =============================================
    // Helpers
    // =============================================

    private void initialiserPieces(DemandeVisa demande, List<DemandePieceDTO> piecesDTO) {
        List<PieceJustificative> piecesApplicables =
                pieceJustificativeRepository.findPiecesApplicables(demande.getTypeVisa());

        List<DemandePiece> demandePieces = new ArrayList<>();
        for (PieceJustificative piece : piecesApplicables) {
            boolean fourni = false;
            if (piecesDTO != null) {
                fourni = piecesDTO.stream()
                        .anyMatch(dp -> dp.getPieceId().equals(piece.getId())
                                && Boolean.TRUE.equals(dp.getFourni()));
            }
            demandePieces.add(DemandePiece.builder()
                    .demande(demande)
                    .piece(piece)
                    .fourni(fourni)
                    .build());
        }
        demande.setPieces(demandePieces);
        demandeVisaRepository.save(demande);
    }

    private void mettreAJourPieces(DemandeVisa demande, List<DemandePieceDTO> piecesDTO) {
        for (DemandePieceDTO dto : piecesDTO) {
            demande.getPieces().stream()
                    .filter(dp -> dp.getPiece().getId().equals(dto.getPieceId()))
                    .findFirst()
                    .ifPresent(dp -> dp.setFourni(dto.getFourni()));
        }
    }

    private DemandeVisaResponseDTO toResponseDTO(DemandeVisa demande) {
        List<DemandeVisaResponseDTO.PieceResponseDTO> piecesResponse = demande.getPieces().stream()
                .map(dp -> DemandeVisaResponseDTO.PieceResponseDTO.builder()
                        .pieceId(dp.getPiece().getId())
                        .nomPiece(dp.getPiece().getNom())
                        .typePiece(dp.getPiece().getTypePiece().name())
                        .obligatoire(dp.getPiece().getObligatoire())
                        .fourni(dp.getFourni())
                        .build())
                .collect(Collectors.toList());

        return DemandeVisaResponseDTO.builder()
                .id(demande.getId())
                .dateDemande(demande.getDateDemande())
                .categorie(demande.getCategorie())
                .statut(demande.getStatut())
                .nom(demande.getNom())
                .prenoms(demande.getPrenoms())
                .numeroPasseport(demande.getNumeroPasseport())
                .typeVisa(demande.getTypeVisa())
                .pieces(piecesResponse)
                .build();
    }
}
