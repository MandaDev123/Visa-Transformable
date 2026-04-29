package com.visa.backoffice.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.visa.backoffice.dto.DemandeVisaResponseDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateAttestationRecepisse(DemandeVisaResponseDTO demande) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            // Title
            Paragraph title = new Paragraph("ATTESTATION DE RÉCÉPISSÉ - DEMANDE DE VISA", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // General Info
            document.add(new Paragraph("Informations Générales :", headerFont));
            document.add(new Paragraph("ID Demande : " + demande.getId(), normalFont));
            document.add(new Paragraph("Date de demande : " + (demande.getDateDemande() != null ? demande.getDateDemande().format(DateTimeFormatter.ISO_LOCAL_DATE) : ""), normalFont));
            document.add(new Paragraph("Catégorie : " + (demande.getCategorie() != null ? demande.getCategorie() : "NOUVEAU_TITRE"), normalFont));
            document.add(new Paragraph("Statut : " + demande.getStatut(), normalFont));
            document.add(new Paragraph(" "));

            // État Civil
            document.add(new Paragraph("État Civil :", headerFont));
            document.add(new Paragraph("Nom : " + demande.getNom(), normalFont));
            document.add(new Paragraph("Prénoms : " + demande.getPrenoms(), normalFont));
            document.add(new Paragraph("Date de naissance : " + (demande.getDateNaissance() != null ? demande.getDateNaissance().format(DateTimeFormatter.ISO_LOCAL_DATE) : ""), normalFont));
            document.add(new Paragraph("Nationalité : " + demande.getNationalite(), normalFont));
            document.add(new Paragraph(" "));

            // Visa Transformable
            document.add(new Paragraph("Visa Transformable :", headerFont));
            document.add(new Paragraph("Type de visa : " + demande.getTypeVisa(), normalFont));
            document.add(new Paragraph("Numéro de passeport : " + demande.getNumeroPasseport(), normalFont));
            if (demande.getNumeroVisa() != null && !demande.getNumeroVisa().isBlank()) {
                document.add(new Paragraph("Numéro de visa précédent : " + demande.getNumeroVisa(), normalFont));
            }
            if (demande.getNumeroCarteResident() != null && !demande.getNumeroCarteResident().isBlank()) {
                document.add(new Paragraph("Numéro de carte résident : " + demande.getNumeroCarteResident(), normalFont));
            }
            document.add(new Paragraph(" "));

            // Pièces justificatives
            document.add(new Paragraph("Pièces Justificatives Fournies :", headerFont));
            if (demande.getPieces() != null && !demande.getPieces().isEmpty()) {
                for (DemandeVisaResponseDTO.PieceResponseDTO piece : demande.getPieces()) {
                    String provided = Boolean.TRUE.equals(piece.getFourni()) ? "OUI" : "NON";
                    document.add(new Paragraph("- " + piece.getNomPiece() + " (Obligatoire: " + (piece.getObligatoire() ? "OUI" : "NON") + ") : " + provided, normalFont));
                }
            } else {
                document.add(new Paragraph("Aucune pièce enregistrée.", normalFont));
            }

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
