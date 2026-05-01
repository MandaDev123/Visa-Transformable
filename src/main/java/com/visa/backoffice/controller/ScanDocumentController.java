package com.visa.backoffice.controller;

import com.visa.backoffice.entity.ScanDocument;
import com.visa.backoffice.service.ScanDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/demandes/{demandeId}")
@RequiredArgsConstructor
public class ScanDocumentController {

    private final ScanDocumentService scanDocumentService;

    /**
     * Upload d'un document
     */
    @PostMapping("/pieces/{pieceId}/documents/upload")
    public String uploadDocument(
            @PathVariable("demandeId") Long demandeId,
            @PathVariable("pieceId") Long pieceId,
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam(value = "typeDocument", defaultValue = "AUTRE") String typeDocument,
            RedirectAttributes redirectAttributes) {
        try {
            scanDocumentService.uploadDocument(demandeId, pieceId, fichier, typeDocument);
            redirectAttributes.addFlashAttribute("success", "Document « " + fichier.getOriginalFilename() + " » uploadé avec succès !");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur d'écriture : " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/demandes/" + demandeId + "/modifier";
    }

    /**
     * Suppression d'un document
     */
    @PostMapping("/documents/{documentId}/supprimer")
    public String supprimerDocument(
            @PathVariable("demandeId") Long demandeId,
            @PathVariable("documentId") Long documentId,
            RedirectAttributes redirectAttributes) {
        try {
            scanDocumentService.supprimerDocument(documentId);
            redirectAttributes.addFlashAttribute("success", "Document supprimé avec succès.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression du fichier : " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/demandes/" + demandeId + "/modifier";
    }

    /**
     * Téléchargement d'un document
     */
    @GetMapping("/documents/{documentId}/telecharger")
    public ResponseEntity<Resource> telechargerDocument(
            @PathVariable("demandeId") Long demandeId,
            @PathVariable("documentId") Long documentId) {
        try {
            ScanDocument doc = scanDocumentService.getDocument(documentId);
            Path filePath = Paths.get(doc.getCheminFichier());
            Resource resource = new PathResource(filePath);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = doc.getContentType() != null ? doc.getContentType() : "application/octet-stream";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + doc.getNomOriginal() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
