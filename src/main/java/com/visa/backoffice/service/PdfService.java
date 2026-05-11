package com.visa.backoffice.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.visa.backoffice.dto.DemandeVisaResponseDTO;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@Service
public class PdfService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String ENTETE_PATH = "c:\\Manda\\Semestre6\\Visa-Transformable-Sprint-1-nouvelle-demande\\Entete.jpg";
    private static final String SIGNATURE_PATH = "c:\\Manda\\Semestre6\\Visa-Transformable-Sprint-1-nouvelle-demande\\signature.jpg";

    // ─────────────────────────────────────────────
    // Helpers : polices Unicode (CP1252 = latin-1 étendu)
    // ─────────────────────────────────────────────
    private BaseFont baseFont() {
        try {
            return BaseFont.createFont(BaseFont.HELVETICA, "Cp1252", BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            try {
                return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            } catch (Exception ex) {
                throw new RuntimeException("Impossible de créer la police", ex);
            }
        }
    }

    private Font font(float size, int style, Color color) {
        return new Font(baseFont(), size, style, color);
    }

    private Font fontNormal(float size) {
        return font(size, Font.NORMAL, Color.BLACK);
    }

    private Font fontBold(float size) {
        return font(size, Font.BOLD, Color.BLACK);
    }

    private Font fontBoldC(float size, Color c) {
        return font(size, Font.BOLD, c);
    }

    private String fmt(String v) {
        return v != null && !v.isBlank() ? v : "\u2014";
    }

    private String fmtDate(LocalDate d) {
        return d != null ? d.format(DATE_FMT) : "\u2014";
    }

    // ─────────────────────────────────────────────
    // ATTESTATION DE RÉCÉPISSÉ
    // ─────────────────────────────────────────────
    public byte[] generateAttestationRecepisse(DemandeVisaResponseDTO d) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 30, 30);
        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            // En-tête image
            addHeaderImage(doc);

            // Ligne de séparation
            addSeparator(doc, new Color(0, 80, 160), 2f);

            // Titre
            Paragraph title = new Paragraph("ATTESTATION DE RECEPISSE", fontBoldC(20, new Color(0, 80, 160)));
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(4);
            doc.add(title);
            Paragraph sub = new Paragraph("Demande de Visa Transformable", fontBold(12));
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(16);
            doc.add(sub);

            addSeparator(doc, Color.LIGHT_GRAY, 0.5f);
            doc.add(new Paragraph(" "));

            // Tableau infos (sans photo dans l'attestation)
            PdfPTable topTable = new PdfPTable(2);
            topTable.setWidthPercentage(100);
            topTable.setWidths(new float[] { 1f, 1f });
            topTable.setSpacingAfter(14);

            PdfPCell infoEtatCell = noBorderCell();
            infoEtatCell.addElement(sectionHeader("Etat Civil"));
            infoEtatCell.addElement(fieldRow("Nom", fmt(d.getNom())));
            infoEtatCell.addElement(fieldRow("Prenoms", fmt(d.getPrenoms())));
            infoEtatCell.addElement(fieldRow("Date de naissance", fmtDate(d.getDateNaissance())));
            infoEtatCell.addElement(fieldRow("Nationalite", fmt(d.getNationalite())));
            infoEtatCell.addElement(fieldRow("Email", fmt(d.getEmail())));
            infoEtatCell.addElement(fieldRow("Contact", fmt(d.getContact())));


            PdfPCell infoGenCell = noBorderCell();
            infoGenCell.addElement(sectionHeader("Informations Generales"));
            infoGenCell.addElement(fieldRow("Date de demande", fmtDate(d.getDateDemande())));
            infoGenCell.addElement(fieldRow("Categorie", fmt(d.getCategorie())));
            infoGenCell.addElement(fieldRow("Statut", d.getStatut() != null ? d.getStatut().name() : "-"));

            topTable.addCell(infoEtatCell);
            topTable.addCell(infoGenCell);
            doc.add(topTable);

            // Passeport & Visa
            PdfPTable visaTable = new PdfPTable(2);
            visaTable.setWidthPercentage(100);
            visaTable.setWidths(new float[] { 1f, 1f });
            visaTable.setSpacingAfter(14);

            PdfPCell passCell = boxCell(new Color(230, 240, 255));
            passCell.addElement(sectionHeader("Passeport"));
            passCell.addElement(fieldRow("Numero", fmt(d.getNumeroPasseport())));
            passCell.addElement(fieldRow("Delivre le", fmtDate(d.getDateDelivrancePasseport())));
            passCell.addElement(fieldRow("Expire le", fmtDate(d.getDateExpirationPasseport())));

            PdfPCell visaInfoCell = boxCell(new Color(230, 240, 255));
            visaInfoCell.addElement(sectionHeader("Visa Transformable"));
            visaInfoCell.addElement(fieldRow("Type de visa", fmt(d.getTypeVisa())));
            visaInfoCell.addElement(fieldRow("Entree Madagascar", fmtDate(d.getDateEntreeMadagascar())));
            if (d.getNumeroVisa() != null && !d.getNumeroVisa().isBlank())
                visaInfoCell.addElement(fieldRow("N° Visa precedent", d.getNumeroVisa()));
            if (d.getNumeroCarteResident() != null && !d.getNumeroCarteResident().isBlank())
                visaInfoCell.addElement(fieldRow("N° Carte resident", d.getNumeroCarteResident()));

            visaTable.addCell(passCell);
            visaTable.addCell(visaInfoCell);
            doc.add(visaTable);

            // Pieces fournies
            Paragraph pieceTitle = new Paragraph("Pieces Justificatives Fournies",
                    fontBoldC(12, new Color(0, 80, 160)));
            pieceTitle.setSpacingAfter(6);
            doc.add(pieceTitle);

            if (d.getPieces() != null) {
                com.lowagie.text.List list = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
                list.setListSymbol(new Chunk("  ", fontNormal(10)));
                boolean found = false;
                for (DemandeVisaResponseDTO.PieceResponseDTO p : d.getPieces()) {
                    if (Boolean.TRUE.equals(p.getFourni())) {
                        Chunk bullet = new Chunk("\u2713  ", fontBoldC(10, new Color(0, 150, 80)));
                        Chunk txt = new Chunk(p.getNomPiece(), fontNormal(10));
                        Paragraph item = new Paragraph();
                        item.add(bullet);
                        item.add(txt);
                        item.setSpacingAfter(3);
                        doc.add(item);
                        found = true;
                    }
                }
                if (!found)
                    doc.add(new Paragraph("Aucune piece fournie.", fontNormal(10)));
            }

            // Signature
            doc.add(new Paragraph("\n\n"));
            addSeparator(doc, Color.LIGHT_GRAY, 0.5f);
            Paragraph sig = new Paragraph("Le Directeur de l'Immigration", fontBold(10));
            sig.setAlignment(Element.ALIGN_RIGHT);
            sig.setSpacingBefore(8);
            doc.add(sig);
            try {
                Image sigImg = Image.getInstance(SIGNATURE_PATH);
                sigImg.setAlignment(Image.ALIGN_RIGHT);
                sigImg.scaleToFit(100, 60);
                doc.add(sigImg);
            } catch (Exception ignore) {
            }

            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    // ─────────────────────────────────────────────
    // VISA OFFICIEL (page A4 format diplomatique)
    // ─────────────────────────────────────────────
    public byte[] generateVisa(DemandeVisaResponseDTO d) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document doc = new Document(PageSize.A4, 50, 50, 40, 40);

        try {

            PdfWriter writer = PdfWriter.getInstance(doc, out);

            doc.open();



            // Fond bleu très clair

            PdfContentByte canvas = writer.getDirectContentUnder();

            canvas.setColorFill(new Color(235, 242, 255));

            canvas.rectangle(0, 0, PageSize.A4.getWidth(), PageSize.A4.getHeight());

            canvas.fill();



            // En-tête image

            addHeaderImage(doc);

            addSeparator(doc, new Color(0, 80, 160), 3f);



            Paragraph titre = new Paragraph("REPUBLIQUE DE MADAGASCAR", fontBoldC(14, new Color(0, 80, 160)));

            titre.setAlignment(Element.ALIGN_CENTER);

            doc.add(titre);

            Paragraph sous = new Paragraph("VISA TRANSFORMABLE", fontBoldC(22, new Color(180, 0, 0)));

            sous.setAlignment(Element.ALIGN_CENTER);

            sous.setSpacingAfter(20);

            doc.add(sous);



            addSeparator(doc, new Color(0, 80, 160), 1.5f);

            doc.add(new Paragraph(" "));



            // Corps : photo + données

            PdfPTable mainTable = new PdfPTable(2);

            mainTable.setWidthPercentage(100);

            mainTable.setWidths(new float[]{1.5f, 1f});

            mainTable.setSpacingAfter(20);



            PdfPCell dataCell = boxCell(Color.WHITE);

            dataCell.addElement(visaField("NOM / SURNAME", d.getNom()));

            dataCell.addElement(visaField("PRENOMS / GIVEN NAMES", d.getPrenoms()));

            dataCell.addElement(visaField("NATIONALITE / NATIONALITY", d.getNationalite()));

            dataCell.addElement(visaField("DATE DE NAISSANCE / DATE OF BIRTH", fmtDate(d.getDateNaissance())));

            dataCell.addElement(visaField("N° PASSEPORT / PASSPORT NO.", d.getNumeroPasseport()));

            dataCell.addElement(visaField("TYPE DE VISA / VISA TYPE", d.getTypeVisa()));

            dataCell.addElement(visaField("LIEU DE REFERENCE / REF. PLACE", d.getLieuReferenceVisa()));

            dataCell.addElement(visaField("DATE ENTREE / ENTRY DATE", fmtDate(d.getDateEntreeMadagascar())));

            dataCell.addElement(visaField("EXPIRE LE / EXPIRES ON", fmtDate(d.getDateExpirationVisa())));

            dataCell.addElement(visaField("N° VISA", generateVisaNumber(d)));



            PdfPCell photoCell = boxCell(new Color(220, 230, 250));
            photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            photoCell.setFixedHeight(180);

            // Priorité 1 : photo capturée par webcam (base64)
            Image photoImg = loadImageFromBase64OrPath(d.getPhotoIdentiteBase64(), findPhotoChemin(d));
            if (photoImg != null) {
                photoImg.scaleToFit(130, 170);
                photoImg.setAlignment(Image.ALIGN_CENTER);
                photoCell.addElement(photoImg);
            }


            mainTable.addCell(dataCell);
            mainTable.addCell(photoCell);
            doc.add(mainTable);

            // Signature du demandeur
            if (d.getSignatureBase64() != null && !d.getSignatureBase64().isBlank()) {
                try {
                    PdfPTable sigTable = new PdfPTable(2);
                    sigTable.setWidthPercentage(100);
                    sigTable.setWidths(new float[]{1f, 1f});

                    PdfPCell labelSig = noBorderCell();
                    labelSig.addElement(new Paragraph("SIGNATURE DU TITULAIRE", fontBoldC(8, new Color(80,80,80))));
                    sigTable.addCell(labelSig);

                    PdfPCell imgSigCell = noBorderCell();
                    imgSigCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    Image sigImg = loadImageFromBase64(d.getSignatureBase64());
                    if (sigImg != null) {
                        sigImg.scaleToFit(150, 60);
                        imgSigCell.addElement(sigImg);
                    }
                    sigTable.addCell(imgSigCell);
                    doc.add(sigTable);
                } catch (Exception ignored) {}
            }

            // Bande MRZ-like
            addMrzBand(doc, d, "VISA");

            doc.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return out.toByteArray();

    }

    // ─────────────────────────────────────────────
    // CARTE DE RÉSIDENT (format ID card A6 landscape)
    // ─────────────────────────────────────────────
    public byte[] generateCarteResident(DemandeVisaResponseDTO d) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // Format A5 paysage (210 x 148 mm)
        Rectangle pageSize = new Rectangle(595, 421); // A5 landscape pts approx
        Document doc = new Document(pageSize, 28, 28, 20, 20);
        try {
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            doc.open();

            // Fond dégradé bleu foncé (simulation via rectangle)
            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.setColorFill(new Color(0, 50, 120));
            canvas.rectangle(0, 0, pageSize.getWidth(), pageSize.getHeight());
            canvas.fill();
            // Bande décorative bas
            canvas.setColorFill(new Color(180, 0, 0));
            canvas.rectangle(0, 0, pageSize.getWidth(), 30);
            canvas.fill();

            // En-tête
            try {
                Image img = Image.getInstance(ENTETE_PATH);
                img.setAlignment(Image.ALIGN_CENTER);
                img.scaleToFit(350, 55);
                doc.add(img);
            } catch (Exception ignore) {
            }

            Paragraph titre = new Paragraph("CARTE DE RESIDENT", fontBoldC(16, Color.WHITE));
            titre.setAlignment(Element.ALIGN_CENTER);
            titre.setSpacingAfter(8);
            doc.add(titre);

            // Corps : photo + infos
            PdfPTable mainTable = new PdfPTable(2);
            mainTable.setWidthPercentage(100);
            mainTable.setWidths(new float[] { 1f, 2f });
            mainTable.setSpacingAfter(10);

            // Photo du titulaire
            PdfPCell photoCell = new PdfPCell();
            photoCell.setBorder(PdfPCell.BOX);
            photoCell.setBorderColor(new Color(255, 255, 255, 80));
            photoCell.setBorderWidth(1f);
            photoCell.setBackgroundColor(new Color(0, 40, 100));
            photoCell.setPadding(4);
            photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            photoCell.setFixedHeight(140);

            // Priorité 1 : photo webcam base64
            Image photoImgCR = loadImageFromBase64OrPath(d.getPhotoIdentiteBase64(), findPhotoChemin(d));
            if (photoImgCR != null) {
                photoImgCR.scaleToFit(100, 130);
                photoImgCR.setAlignment(Image.ALIGN_CENTER);
                photoCell.addElement(photoImgCR);
            }

            // Données
            PdfPCell dataCell = transparentCell();
            dataCell.addElement(crFieldW("NOM", fmt(d.getNom())));
            dataCell.addElement(crFieldW("PRENOMS", fmt(d.getPrenoms())));
            dataCell.addElement(crFieldW("NATIONALITE", fmt(d.getNationalite())));
            dataCell.addElement(crFieldW("NE(E) LE", fmtDate(d.getDateNaissance())));
            dataCell.addElement(crFieldW("N° PASSEPORT", fmt(d.getNumeroPasseport())));
            dataCell.addElement(crFieldW("TYPE", fmt(d.getTypeVisa())));
            dataCell.addElement(crFieldW("EXPIRE LE", fmtDate(d.getDateExpirationVisa())));
            dataCell.addElement(crFieldW("N° CARTE", generateCarteNumber(d)));

            mainTable.addCell(photoCell);
            mainTable.addCell(dataCell);
            doc.add(mainTable);

            // Signature du demandeur sur la carte
            if (d.getSignatureBase64() != null && !d.getSignatureBase64().isBlank()) {
                try {
                    PdfPTable sigTable = new PdfPTable(2);
                    sigTable.setWidthPercentage(100);
                    sigTable.setWidths(new float[]{1.5f, 1f});
                    PdfPCell lblCell = transparentCell();
                    lblCell.addElement(new Paragraph("SIGNATURE", fontBoldC(7, new Color(180,200,255))));
                    sigTable.addCell(lblCell);
                    PdfPCell sigCell = transparentCell();
                    sigCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    Image sigImg = loadImageFromBase64(d.getSignatureBase64());
                    if (sigImg != null) {
                        sigImg.scaleToFit(120, 40);
                        sigCell.addElement(sigImg);
                    }
                    sigTable.addCell(sigCell);
                    doc.add(sigTable);
                } catch (Exception ignored) {}
            }

            // Bande MRZ
            addMrzBandWhite(doc, d, "CR");

            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    // ─────────────────────────────────────────────
    // Utilitaires privés
    // ─────────────────────────────────────────────
    private void addHeaderImage(Document doc) {
        try {
            Image img = Image.getInstance(ENTETE_PATH);
            img.setAlignment(Image.ALIGN_CENTER);
            img.scaleToFit(480, 110);
            doc.add(img);
            doc.add(new Paragraph(" "));
        } catch (Exception ignore) {
        }
    }

    private void addSeparator(Document doc, Color color, float width) {
        try {
            PdfPTable line = new PdfPTable(1);
            line.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell();
            cell.setBorderColor(color);
            cell.setBorderWidthTop(width);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setFixedHeight(4);
            line.addCell(cell);
            doc.add(line);
        } catch (Exception ignore) {
        }
    }

    private Paragraph sectionHeader(String text) {
        Paragraph p = new Paragraph(text, fontBoldC(11, new Color(0, 80, 160)));
        p.setSpacingBefore(6);
        p.setSpacingAfter(4);
        return p;
    }

    private Paragraph fieldRow(String label, String value) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " : ", fontBold(9)));
        p.add(new Chunk(value, fontNormal(9)));
        p.setSpacingAfter(2);
        return p;
    }

    private Paragraph visaField(String label, String value) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "\n", fontBoldC(7, new Color(80, 80, 80))));
        p.add(new Chunk(value != null && !value.isBlank() ? value : "\u2014", fontBold(11)));
        p.setSpacingAfter(6);
        return p;
    }

    private Paragraph crFieldW(String label, String value) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "  ", fontBoldC(7, new Color(200, 220, 255))));
        p.add(new Chunk(value != null && !value.isBlank() ? value : "\u2014", fontBoldC(10, Color.WHITE)));
        p.setSpacingAfter(4);
        return p;
    }

    private PdfPCell noBorderCell() {
        PdfPCell c = new PdfPCell();
        c.setBorder(PdfPCell.NO_BORDER);
        c.setPadding(4);
        return c;
    }

    private PdfPCell boxCell(Color bg) {
        PdfPCell c = new PdfPCell();
        c.setBorder(PdfPCell.BOX);
        c.setBorderColor(new Color(180, 200, 230));
        c.setBorderWidth(0.8f);
        c.setBackgroundColor(bg);
        c.setPadding(10);
        return c;
    }

    private PdfPCell transparentCell() {
        PdfPCell c = new PdfPCell();
        c.setBorder(PdfPCell.NO_BORDER);
        c.setPadding(6);
        return c;
    }

    private void addMrzBand(Document doc, DemandeVisaResponseDTO d, String prefix) {
        try {
            PdfPTable mrz = new PdfPTable(1);
            mrz.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new Color(20, 20, 60));
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(8);
            String nom = (d.getNom() != null ? d.getNom().toUpperCase() : "").replace(" ", "<");
            String nat = (d.getNationalite() != null
                    ? d.getNationalite().substring(0, Math.min(3, d.getNationalite().length())).toUpperCase()
                    : "MDG");
            String pass = (d.getNumeroPasseport() != null ? d.getNumeroPasseport().toUpperCase() : "");
            cell.addElement(new Paragraph(
                    prefix + "<<" + nat + "<<" + nom + "<<<<<<<<<<<<<<",
                    fontBoldC(9, Color.WHITE)));
            cell.addElement(new Paragraph(
                    pass + "<<<<<<" + nat + "<<<<<<<<<<<<<<<",
                    fontBoldC(9, Color.WHITE)));
            mrz.addCell(cell);
            doc.add(mrz);
        } catch (Exception ignore) {
        }
    }

    private void addMrzBandWhite(Document doc, DemandeVisaResponseDTO d, String prefix) {
        try {
            PdfPTable mrz = new PdfPTable(1);
            mrz.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new Color(180, 0, 0));
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(5);
            String nom = (d.getNom() != null ? d.getNom().toUpperCase() : "").replace(" ", "<");
            String nat = (d.getNationalite() != null
                    ? d.getNationalite().substring(0, Math.min(3, d.getNationalite().length())).toUpperCase()
                    : "MDG");
            String pass = (d.getNumeroPasseport() != null ? d.getNumeroPasseport().toUpperCase() : "");
            cell.addElement(new Paragraph(
                    prefix + "<<" + nat + "<<" + nom + "<<<<<<<<<<<<<<",
                    fontBoldC(8, Color.WHITE)));
            cell.addElement(new Paragraph(
                    pass + "<<<" + nat + "<<<<<<<<<<<<<<<<<<",
                    fontBoldC(8, Color.WHITE)));
            mrz.addCell(cell);
            doc.add(mrz);
        } catch (Exception ignore) {
        }
    }

    private String generateVisaNumber(DemandeVisaResponseDTO d) {
        if (d.getNumeroVisa() != null && !d.getNumeroVisa().isBlank())
            return d.getNumeroVisa();
        // Génère un numéro basé sur l'ID si pas encore assigné
        return "VTM-" + String.format("%06d", d.getId());
    }

    private String generateCarteNumber(DemandeVisaResponseDTO d) {
        if (d.getNumeroCarteResident() != null && !d.getNumeroCarteResident().isBlank())
            return d.getNumeroCarteResident();
        return "CR-MDG-" + String.format("%06d", d.getId());
    }

    /**
     * Trouve le chemin du fichier photo d'identite dans les pieces justificatives.
     * Priorite : piece dont le nom contient "photo" → sinon premier fichier image
     * trouve.
     */
    private String findPhotoChemin(DemandeVisaResponseDTO d) {
        if (d.getPieces() == null)
            return null;

        // 1ere passe : piece dont le nom contient "photo"
        for (DemandeVisaResponseDTO.PieceResponseDTO piece : d.getPieces()) {
            if (piece.getNomPiece() != null
                    && piece.getNomPiece().toLowerCase().contains("photo")
                    && piece.getDocuments() != null) {
                for (DemandeVisaResponseDTO.ScanDocumentResponseDTO docScan : piece.getDocuments()) {
                    String path = validImagePath(docScan);
                    if (path != null) {
                        System.out.println("[PdfService] Photo trouvee (piece photo) : " + path);
                        return path;
                    }
                }
            }
        }

        // 2eme passe (fallback) : premier fichier image dans toutes les pieces fournies
        for (DemandeVisaResponseDTO.PieceResponseDTO piece : d.getPieces()) {
            if (Boolean.TRUE.equals(piece.getFourni()) && piece.getDocuments() != null) {
                for (DemandeVisaResponseDTO.ScanDocumentResponseDTO docScan : piece.getDocuments()) {
                    String path = validImagePath(docScan);
                    if (path != null) {
                        System.out.println("[PdfService] Photo trouvee (fallback) : " + path);
                        return path;
                    }
                }
            }
        }

        System.out.println("[PdfService] Aucune photo trouvee pour la demande.");
        return null;
    }

    /**
     * Retourne le chemin du fichier si c'est une image et que le fichier existe sur
     * le disque,
     * null sinon.
     */
    private String validImagePath(DemandeVisaResponseDTO.ScanDocumentResponseDTO docScan) {
        if (docScan == null || docScan.getCheminFichier() == null)
            return null;

        boolean extensionOk = isImage(docScan.getNomOriginal());
        boolean contentTypeOk = docScan.getTypeDocument() != null
                && (docScan.getTypeDocument().toLowerCase().contains("photo")
                        || docScan.getTypeDocument().toLowerCase().contains("image"));

        if (!extensionOk && !contentTypeOk)
            return null;

        java.io.File f = new java.io.File(docScan.getCheminFichier());
        if (!f.exists() || !f.isFile()) {
            System.out.println("[PdfService] Fichier image introuvable : " + docScan.getCheminFichier());
            return null;
        }
        return docScan.getCheminFichier();
    }

    private boolean isImage(String filename) {
        if (filename == null)
            return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                || lower.endsWith(".png") || lower.endsWith(".gif")
                || lower.endsWith(".bmp") || lower.endsWith(".webp");
    }

    // ─────────────────────────────────────────────
    // Chargement image depuis base64 ou chemin fichier
    // ─────────────────────────────────────────────
    private Image loadImageFromBase64OrPath(String base64DataUri, String filePath) {
        if (base64DataUri != null && !base64DataUri.isBlank()) {
            Image img = loadImageFromBase64(base64DataUri);
            if (img != null) return img;
        }
        if (filePath != null) {
            try {
                return Image.getInstance(filePath);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private Image loadImageFromBase64(String base64DataUri) {
        try {
            String data = base64DataUri;
            if (data.contains(",")) {
                data = data.substring(data.indexOf(',') + 1);
            }
            byte[] bytes = Base64.getDecoder().decode(data);
            return Image.getInstance(bytes);
        } catch (Exception e) {
            return null;
        }
    }
}
