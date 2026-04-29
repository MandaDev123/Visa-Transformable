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
                // État civil
                .nom(dto.getNom())
                .prenoms(dto.getPrenoms())
                .dateNaissance(dto.getDateNaissance())
                .lieuNaissance(dto.getLieuNaissance())
                .nationalite(dto.getNationalite())
                .email(dto.getEmail())
                .contact(dto.getContact())
                // Passeport
                .numeroPasseport(dto.getNumeroPasseport())
                .dateDelivrancePasseport(dto.getDateDelivrancePasseport())
                .dateExpirationPasseport(dto.getDateExpirationPasseport())
                // Visa transformable
                .typeVisa(dto.getTypeVisa())
                .dateEntreeMadagascar(dto.getDateEntreeMadagascar())
                .lieuReferenceVisa(dto.getLieuReferenceVisa())
                .numeroVisa(dto.getNumeroVisa())
                .numeroCarteResident(dto.getNumeroCarteResident())
                .dateExpirationVisa(dto.getDateExpirationVisa())
                .statut(StatutDemande.DOSSIER_CREE)
                .build();

        demande = demandeVisaRepository.save(demande);

        // Initialiser les pièces si le type de visa est défini
        if (dto.getTypeVisa() != null && !dto.getTypeVisa().isBlank()) {
            initialiserPieces(demande, dto.getPieces());
        }

        // Valider les champs obligatoires et pièces
        validerDemande(demande);

        // Définir le statut initial
        demande.setStatut(calculerStatutInitial(demande));
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
        if (dto.getDateDemande() != null)
            demande.setDateDemande(dto.getDateDemande());
        if (dto.getCategorie() != null)
            demande.setCategorie(dto.getCategorie());
        // État civil
        if (dto.getNom() != null)
            demande.setNom(dto.getNom());
        if (dto.getPrenoms() != null)
            demande.setPrenoms(dto.getPrenoms());
        if (dto.getDateNaissance() != null)
            demande.setDateNaissance(dto.getDateNaissance());
        if (dto.getLieuNaissance() != null)
            demande.setLieuNaissance(dto.getLieuNaissance());
        if (dto.getNationalite() != null)
            demande.setNationalite(dto.getNationalite());
        if (dto.getEmail() != null)
            demande.setEmail(dto.getEmail());
        if (dto.getContact() != null)
            demande.setContact(dto.getContact());

        // Passeport
        if (dto.getNumeroPasseport() != null)
            demande.setNumeroPasseport(dto.getNumeroPasseport());
        if (dto.getDateDelivrancePasseport() != null)
            demande.setDateDelivrancePasseport(dto.getDateDelivrancePasseport());
        if (dto.getDateExpirationPasseport() != null)
            demande.setDateExpirationPasseport(dto.getDateExpirationPasseport());

        // Visa transformable
        if (dto.getTypeVisa() != null)
            demande.setTypeVisa(dto.getTypeVisa());
        if (dto.getDateEntreeMadagascar() != null)
            demande.setDateEntreeMadagascar(dto.getDateEntreeMadagascar());
        if (dto.getLieuReferenceVisa() != null)
            demande.setLieuReferenceVisa(dto.getLieuReferenceVisa());
        if (dto.getNumeroVisa() != null)
            demande.setNumeroVisa(dto.getNumeroVisa());
        if (dto.getNumeroCarteResident() != null)
            demande.setNumeroCarteResident(dto.getNumeroCarteResident());
        if (dto.getDateExpirationVisa() != null)
            demande.setDateExpirationVisa(dto.getDateExpirationVisa());

        // Mettre à jour les pièces
        if (dto.getPieces() != null) {
            mettreAJourPieces(demande, dto.getPieces());
        }

        if (demande.getStatut() == StatutDemande.SCAN_TERMINE) {
            throw new IllegalStateException("La demande est au statut SCAN TERMINÉ et ne peut plus être modifiée.");
        }

        // Valider les champs obligatoires et pièces
        validerDemande(demande);

        // Recalculer le statut si ce n'est pas VISA_APPROUVE (au cas où ça aurait été
        // modifié, mais normalement pas besoin)
        if (demande.getStatut() != StatutDemande.VISA_APPROUVE) {
            demande.setStatut(calculerStatutInitial(demande));
        }
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
    // Rechercher par numéro de visa ou carte résident
    // =============================================
    @Transactional(readOnly = true)
    public List<DemandeVisaResponseDTO> rechercherParNumero(String numero) {
        List<DemandeVisa> demandes = demandeVisaRepository.findHistoriqueByNumero(numero);
        return demandes.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    // =============================================
    // Vérification des pièces (logique métier du spec)
    // =============================================
    public boolean verifierPieces(DemandeVisa demande) {
        if (demande.getTypeVisa() == null || demande.getTypeVisa().isBlank()) {
            return false;
        }

        List<PieceJustificative> piecesObligatoires = pieceJustificativeRepository
                .findPiecesObligatoires(demande.getTypeVisa());

        List<DemandePiece> piecesFournies = demande.getPieces();

        for (PieceJustificative p : piecesObligatoires) {
            boolean trouve = piecesFournies.stream()
                    .anyMatch(dp -> dp.getPiece().getId().equals(p.getId())
                            && Boolean.TRUE.equals(dp.getFourni()));
            if (!trouve)
                return false;
        }

        return true;
    }

    // =============================================
    // Terminer le scan (attacher toutes les pièces)
    // =============================================
    @Transactional
    public DemandeVisaResponseDTO terminerScan(Long id) {
        DemandeVisa demande = demandeVisaRepository.findByIdWithPieces(id)
                .orElseThrow(() -> new EntityNotFoundException("Demande non trouvée avec l'id : " + id));

        // Vérifier que TOUTES les pièces (obligatoires ET optionnelles) sont fournies
        boolean allPiecesFournies = demande.getPieces().stream()
                .allMatch(dp -> Boolean.TRUE.equals(dp.getFourni()));

        if (!allPiecesFournies) {
            throw new IllegalArgumentException(
                    "Impossible de terminer le scan : toutes les pièces (obligatoires et optionnelles) doivent être fournies.");
        }

        demande.setStatut(StatutDemande.SCAN_TERMINE);
        demande = demandeVisaRepository.save(demande);

        return toResponseDTO(demande);
    }

    // =============================================
    // Approuver la demande
    // =============================================
    @Transactional
    public DemandeVisaResponseDTO approuverDemande(Long id) {
        DemandeVisa demande = demandeVisaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Demande non trouvée avec l'id : " + id));

        if (demande.getStatut() != StatutDemande.SCAN_TERMINE) {
            throw new IllegalStateException("Impossible d'approuver : le dossier doit d'abord être en statut SCAN_TERMINE.");
        }

        demande.setStatut(StatutDemande.VISA_APPROUVE);
        demande = demandeVisaRepository.save(demande);

        return toResponseDTO(demande);
    }

    // =============================================
    // Validation stricte
    // =============================================
    private void validerDemande(DemandeVisa demande) {
        // Champs obligatoires - État civil
        if (demande.getDateDemande() == null)
            throw new IllegalArgumentException("Date demande requise");
        if (isBlank(demande.getNom()))
            throw new IllegalArgumentException("Nom requis");
        if (isBlank(demande.getPrenoms()))
            throw new IllegalArgumentException("Prénoms requis");
        if (demande.getDateNaissance() == null)
            throw new IllegalArgumentException("Date naissance requise");
        if (isBlank(demande.getLieuNaissance()))
            throw new IllegalArgumentException("Lieu naissance requis");
        if (isBlank(demande.getNationalite()))
            throw new IllegalArgumentException("Nationalité requise");
        if (isBlank(demande.getEmail()))
            throw new IllegalArgumentException("Email requis");
        if (isBlank(demande.getContact()))
            throw new IllegalArgumentException("Contact requis");

        // Champs obligatoires - Passeport
        if (isBlank(demande.getNumeroPasseport()))
            throw new IllegalArgumentException("Numéro passeport requis");
        if (demande.getDateDelivrancePasseport() == null)
            throw new IllegalArgumentException("Date délivrance passeport requise");
        if (demande.getDateExpirationPasseport() == null)
            throw new IllegalArgumentException("Date expiration passeport requise");

        // Champs obligatoires - Visa transformable
        if (isBlank(demande.getTypeVisa()))
            throw new IllegalArgumentException("Type visa requis");
        if (demande.getDateEntreeMadagascar() == null)
            throw new IllegalArgumentException("Date entrée Madagascar requise");
        if (isBlank(demande.getLieuReferenceVisa()))
            throw new IllegalArgumentException("Lieu référence visa requis");
        if (demande.getDateExpirationVisa() == null)
            throw new IllegalArgumentException("Date expiration visa requise");

        // Vérification des pièces obligatoires
        if (!verifierPieces(demande)) {
            throw new IllegalArgumentException("Les pièces justificatives obligatoires doivent toutes être cochées.");
        }
    }

    private StatutDemande calculerStatutInitial(DemandeVisa demande) {
        // "Si on tape son numéro et que ses données n'existent pas dans notre base
        // alors il refait une demande comme au début mais avec une catégorie duplicata
        // ou transfert de visa ,et son statut sera directement VISA APPROUVÉ."
        // We will assume that if the category is DUPLICATA or TRANSFERT_DE_VISA, it
        // goes to VISA_APPROUVE directly when complete.
        if ("DUPLICATA".equalsIgnoreCase(demande.getCategorie())
                || "TRANSFERT_DE_VISA".equalsIgnoreCase(demande.getCategorie())) {
            return StatutDemande.VISA_APPROUVE;
        }

        return StatutDemande.DOSSIER_CREE;
    }

    // =============================================
    // Helpers
    // =============================================

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void initialiserPieces(DemandeVisa demande, List<DemandePieceDTO> piecesDTO) {
        List<PieceJustificative> piecesApplicables = pieceJustificativeRepository
                .findPiecesApplicables(demande.getTypeVisa());

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
                .map(dp -> {
                    List<DemandeVisaResponseDTO.ScanDocumentResponseDTO> docs = new ArrayList<>();
                    if (dp.getDocuments() != null) {
                        docs = dp.getDocuments().stream().map(doc -> DemandeVisaResponseDTO.ScanDocumentResponseDTO.builder()
                                .id(doc.getId())
                                .nomOriginal(doc.getNomOriginal())
                                .typeDocument(doc.getTypeDocument())
                                .tailleOctets(doc.getTailleOctets())
                                .dateUpload(doc.getDateUpload())
                                .build()).collect(Collectors.toList());
                    }
                    return DemandeVisaResponseDTO.PieceResponseDTO.builder()
                        .demandePieceId(dp.getId())
                        .pieceId(dp.getPiece().getId())
                        .nomPiece(dp.getPiece().getNom())
                        .typePiece(dp.getPiece().getTypePiece().name())
                        .obligatoire(dp.getPiece().getObligatoire())
                        .fourni(dp.getFourni())
                        .documents(docs)
                        .build();
                })
                .collect(Collectors.toList());

        return DemandeVisaResponseDTO.builder()
                .id(demande.getId())
                .dateDemande(demande.getDateDemande())
                .categorie(demande.getCategorie())
                .statut(demande.getStatut())
                // État civil
                .nom(demande.getNom())
                .prenoms(demande.getPrenoms())
                .dateNaissance(demande.getDateNaissance())
                .lieuNaissance(demande.getLieuNaissance())
                .nationalite(demande.getNationalite())
                .email(demande.getEmail())
                .contact(demande.getContact())
                // Passeport
                .numeroPasseport(demande.getNumeroPasseport())
                .dateDelivrancePasseport(demande.getDateDelivrancePasseport())
                .dateExpirationPasseport(demande.getDateExpirationPasseport())
                // Visa transformable
                .typeVisa(demande.getTypeVisa())
                .dateEntreeMadagascar(demande.getDateEntreeMadagascar())
                .lieuReferenceVisa(demande.getLieuReferenceVisa())
                .numeroVisa(demande.getNumeroVisa())
                .numeroCarteResident(demande.getNumeroCarteResident())
                .dateExpirationVisa(demande.getDateExpirationVisa())
                .pieces(piecesResponse)
                .build();
    }
}
