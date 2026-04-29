package com.visa.backoffice.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
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

            // Ajout de l'image d'en-tête
            try {
                Image img = Image.getInstance("c:\\Manda\\Semestre6\\Visa-Transformable-Sprint-1-nouvelle-demande\\Entete.jpg");
                img.setAlignment(Image.ALIGN_CENTER);
                img.scaleToFit(500, 120);
                document.add(img);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image d'en-tête: " + e.getMessage());
            }

            document.add(new Paragraph("\n"));

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            // Titre
            Paragraph title = new Paragraph("ATTESTATION DE RÉCÉPISSÉ", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            Paragraph subtitle = new Paragraph("DEMANDE DE VISA TRANSFORMABLE", headerFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(25);
            document.add(subtitle);

            // Tableau de présentation pour un aspect plus professionnel
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(20f);
            table.setWidths(new float[]{1f, 1f});

            // Ligne 1: Informations Générales
            PdfPCell cellLeft = new PdfPCell();
            cellLeft.setBorder(PdfPCell.NO_BORDER);
            cellLeft.addElement(new Paragraph("Informations Générales", headerFont));
            cellLeft.addElement(new Paragraph("Date : " + (demande.getDateDemande() != null ? demande.getDateDemande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""), normalFont));
            cellLeft.addElement(new Paragraph("Catégorie : " + (demande.getCategorie() != null ? demande.getCategorie() : "NOUVEAU_TITRE"), normalFont));
            cellLeft.addElement(new Paragraph("Statut : " + demande.getStatut(), normalFont));
            
            // Ligne 1: État Civil
            PdfPCell cellRight = new PdfPCell();
            cellRight.setBorder(PdfPCell.NO_BORDER);
            cellRight.addElement(new Paragraph("État Civil", headerFont));
            cellRight.addElement(new Paragraph("Nom : " + demande.getNom(), boldFont));
            cellRight.addElement(new Paragraph("Prénoms : " + demande.getPrenoms(), normalFont));
            cellRight.addElement(new Paragraph("Né(e) le : " + (demande.getDateNaissance() != null ? demande.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""), normalFont));
            cellRight.addElement(new Paragraph("Nationalité : " + demande.getNationalite(), normalFont));
            
            table.addCell(cellLeft);
            table.addCell(cellRight);
            document.add(table);

            // Section : Visa Transformable
            document.add(new Paragraph("Détails du Visa", headerFont));
            PdfPTable visaTable = new PdfPTable(1);
            visaTable.setWidthPercentage(100);
            visaTable.setSpacingBefore(5f);
            visaTable.setSpacingAfter(20f);
            PdfPCell visaCell = new PdfPCell();
            visaCell.setBorder(PdfPCell.BOX);
            visaCell.setPadding(10f);
            visaCell.addElement(new Paragraph("Type de visa demandé : " + demande.getTypeVisa(), boldFont));
            visaCell.addElement(new Paragraph("Numéro de passeport : " + demande.getNumeroPasseport(), normalFont));
            if (demande.getNumeroVisa() != null && !demande.getNumeroVisa().isBlank()) {
                visaCell.addElement(new Paragraph("Numéro de visa précédent : " + demande.getNumeroVisa(), normalFont));
            }
            if (demande.getNumeroCarteResident() != null && !demande.getNumeroCarteResident().isBlank()) {
                visaCell.addElement(new Paragraph("Numéro de carte résident : " + demande.getNumeroCarteResident(), normalFont));
            }
            visaTable.addCell(visaCell);
            document.add(visaTable);

            // Pièces justificatives
            document.add(new Paragraph("Pièces Justificatives Fournies", headerFont));
            document.add(new Paragraph("Les documents suivants ont été dûment vérifiés et scannés :", normalFont));
            document.add(new Paragraph("\n"));
            
            if (demande.getPieces() != null && !demande.getPieces().isEmpty()) {
                com.lowagie.text.List list = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
                list.setListSymbol("✓ ");
                boolean aumoinsUnePiece = false;
                for (DemandeVisaResponseDTO.PieceResponseDTO piece : demande.getPieces()) {
                    if (Boolean.TRUE.equals(piece.getFourni())) {
                        list.add(new com.lowagie.text.ListItem(piece.getNomPiece(), normalFont));
                        aumoinsUnePiece = true;
                    }
                }
                if (aumoinsUnePiece) {
                    document.add(list);
                } else {
                    document.add(new Paragraph("Aucune pièce n'a été fournie pour le moment.", normalFont));
                }
            } else {
                document.add(new Paragraph("Aucune pièce enregistrée.", normalFont));
            }

            document.add(new Paragraph("\n\n\n\n"));
            Paragraph signature = new Paragraph("Signature du responsable", normalFont);
            signature.setAlignment(Element.ALIGN_RIGHT);
            document.add(signature);

            try {
                Image img = Image.getInstance("c:\\Manda\\Semestre6\\Visa-Transformable-Sprint-1-nouvelle-demande\\signature.jpg");
                img.setAlignment(Image.ALIGN_RIGHT);
                img.scaleToFit(100, 100);
                document.add(img);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image d'en-tête: " + e.getMessage());
            }

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
