package com.visa.backoffice.service;

import com.visa.backoffice.entity.DemandePiece;
import com.visa.backoffice.entity.DemandeVisa;
import com.visa.backoffice.entity.ScanDocument;
import com.visa.backoffice.entity.StatutDemande;
import com.visa.backoffice.repository.DemandePieceRepository;
import com.visa.backoffice.repository.DemandeVisaRepository;
import com.visa.backoffice.repository.ScanDocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScanDocumentService {

    private final ScanDocumentRepository scanDocumentRepository;
    private final DemandeVisaRepository demandeVisaRepository;
    private final DemandePieceRepository demandePieceRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Upload un fichier et l'attache à la pièce justificative.
     */
    @Transactional
    public ScanDocument uploadDocument(Long demandeId, Long demandePieceId, MultipartFile file, String typeDocument) throws IOException {
        DemandeVisa demande = demandeVisaRepository.findById(demandeId)
                .orElseThrow(() -> new EntityNotFoundException("Demande non trouvée : " + demandeId));

        if (demande.getStatut() == StatutDemande.SCAN_TERMINE) {
            throw new IllegalStateException("Le dossier est déjà scanné et verrouillé. Aucun document supplémentaire ne peut être ajouté.");
        }
        if (demande.getStatut() == StatutDemande.VISA_APPROUVE) {
            throw new IllegalStateException("Le dossier est approuvé. Aucune modification n'est permise.");
        }

        DemandePiece demandePiece = demandePieceRepository.findById(demandePieceId)
                .orElseThrow(() -> new EntityNotFoundException("Pièce non trouvée : " + demandePieceId));

        if (!demandePiece.getDemande().getId().equals(demande.getId())) {
            throw new IllegalArgumentException("La pièce n'appartient pas à la demande.");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
            throw new IllegalArgumentException("Type de fichier non supporté. Veuillez envoyer une image (JPG, PNG) ou un PDF.");
        }

        // Create upload directory per demande
        Path uploadPath = Paths.get(uploadDir, "demande_" + demandeId, "piece_" + demandePieceId);
        Files.createDirectories(uploadPath);

        // Generate unique filename
        String extension = getExtension(file.getOriginalFilename());
        String nomFichier = UUID.randomUUID().toString() + extension;
        Path destination = uploadPath.resolve(nomFichier);

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        ScanDocument doc = ScanDocument.builder()
                .demandePiece(demandePiece)
                .nomFichier(nomFichier)
                .nomOriginal(file.getOriginalFilename() != null ? file.getOriginalFilename() : nomFichier)
                .typeDocument(typeDocument != null ? typeDocument : "AUTRE")
                .contentType(contentType)
                .tailleOctets(file.getSize())
                .cheminFichier(destination.toString())
                .dateUpload(LocalDateTime.now())
                .build();

        doc = scanDocumentRepository.save(doc);

        // Auto-check as "fourni"
        demandePiece.setFourni(true);
        demandePieceRepository.save(demandePiece);

        return doc;
    }

    /**
     * Supprime un document scanné.
     */
    @Transactional
    public void supprimerDocument(Long documentId) throws IOException {
        ScanDocument doc = scanDocumentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document non trouvé : " + documentId));

        DemandeVisa demande = doc.getDemandePiece().getDemande();

        if (demande.getStatut() == StatutDemande.SCAN_TERMINE) {
            throw new IllegalStateException("Le dossier est verrouillé. Impossible de supprimer ce document.");
        }

        // Delete physical file
        Path filePath = Paths.get(doc.getCheminFichier());
        Files.deleteIfExists(filePath);

        DemandePiece piece = doc.getDemandePiece();
        scanDocumentRepository.delete(doc);
        
        // If no more documents for this piece, uncheck "fourni"
        long countDocs = scanDocumentRepository.findByDemandePieceIdOrderByDateUploadDesc(piece.getId()).size() - 1;
        if (countDocs <= 0) {
            piece.setFourni(false);
            demandePieceRepository.save(piece);
        }
    }

    /**
     * Retourne le contenu d'un document pour le téléchargement.
     */
    @Transactional(readOnly = true)
    public ScanDocument getDocument(Long documentId) {
        return scanDocumentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document non trouvé : " + documentId));
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".bin";
        return filename.substring(filename.lastIndexOf('.'));
    }
}
