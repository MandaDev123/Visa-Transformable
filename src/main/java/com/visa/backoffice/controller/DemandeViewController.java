package com.visa.backoffice.controller;

import com.visa.backoffice.dto.DemandeVisaDTO;
import com.visa.backoffice.dto.DemandeVisaResponseDTO;
import com.visa.backoffice.entity.PieceJustificative;
import com.visa.backoffice.repository.DemandeVisaRepository;
import com.visa.backoffice.repository.PieceJustificativeRepository;
import com.visa.backoffice.service.DemandeVisaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/demandes")
@RequiredArgsConstructor
public class DemandeViewController {

    private final DemandeVisaService demandeVisaService;
    private final DemandeVisaRepository demandeVisaRepository;
    private final PieceJustificativeRepository pieceJustificativeRepository;

    /**
     * Liste de toutes les demandes
     */
    @GetMapping
    public String listDemandes(Model model) {
        var demandes = demandeVisaRepository.findAll();
        model.addAttribute("demandes", demandes);

        // Pre-compute stats for the template
        long total = demandes.size();
        long dossiersCrees = demandes.stream()
                .filter(d -> d.getStatut() != null && d.getStatut().name().equals("DOSSIER_CREE"))
                .count();
        long scanTermines = demandes.stream()
                .filter(d -> d.getStatut() != null && d.getStatut().name().equals("SCAN_TERMINE"))
                .count();
        long visaApprouves = demandes.stream()
                .filter(d -> d.getStatut() != null && d.getStatut().name().equals("VISA_APPROUVE"))
                .count();
        model.addAttribute("totalDemandes", total);
        model.addAttribute("totalDossiersCrees", dossiersCrees);
        model.addAttribute("totalScanTermines", scanTermines);
        model.addAttribute("totalVisaApprouve", visaApprouves);

        return "demandes/list";
    }

    /**
     * Formulaire de nouvelle demande
     */
    @GetMapping("/nouveau")
    public String nouveauFormulaire(@RequestParam(value = "categorie", required = false) String categorie, Model model) {
        DemandeVisaDTO dto = new DemandeVisaDTO();
        if (categorie != null) {
            dto.setCategorie(categorie);
        } else {
            dto.setCategorie("NOUVEAU_TITRE");
        }
        model.addAttribute("demande", dto);
        return "demandes/form";
    }

    /**
     * Formulaire de modification
     */
    @GetMapping("/{id}/modifier")
    public String modifierFormulaire(@PathVariable("id") Long id, Model model) {
        DemandeVisaResponseDTO demande = demandeVisaService.getDemande(id);
        model.addAttribute("demandeResponse", demande);
        model.addAttribute("demande", new DemandeVisaDTO());
        return "demandes/form";
    }

    /**
     * Traitement du formulaire de création
     */
    @PostMapping
    public String creerDemande(@ModelAttribute DemandeVisaDTO dto, RedirectAttributes redirectAttributes) {
        try {
            DemandeVisaResponseDTO response = demandeVisaService.creerDemande(dto);
            redirectAttributes.addFlashAttribute("success", "Demande #" + response.getId() + " créée avec succès !");
            return "redirect:/demandes/" + response.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création : " + e.getMessage());
            return "redirect:/demandes/nouveau";
        }
    }

    /**
     * Traitement du formulaire de modification
     */
    @PostMapping("/{id}")
    public String modifierDemande(@PathVariable("id") Long id, @ModelAttribute DemandeVisaDTO dto,
                                   RedirectAttributes redirectAttributes) {
        try {
            DemandeVisaResponseDTO response = demandeVisaService.modifierDemande(id, dto);
            redirectAttributes.addFlashAttribute("success", "Demande #" + id + " mise à jour avec succès !");
            return "redirect:/demandes/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            return "redirect:/demandes/" + id + "/modifier";
        }
    }

    /**
     * Terminer le scan
     */
    @PostMapping("/{id}/terminer-scan")
    public String terminerScan(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            demandeVisaService.terminerScan(id);
            redirectAttributes.addFlashAttribute("success", "Scan terminé avec succès pour le dossier #" + id + ".");
            return "redirect:/demandes/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
            return "redirect:/demandes/" + id;
        }
    }

    /**
     * Rechercher antécédent
     */
    @PostMapping("/rechercher-antecedent")
    public String rechercherAntecedent(@RequestParam("numero") String numero, RedirectAttributes redirectAttributes) {
        List<DemandeVisaResponseDTO> resultats = demandeVisaService.rechercherParNumero(numero);
        if (resultats.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Aucun antécédent trouvé pour ce numéro. Création d'une nouvelle demande de Duplicata.");
            return "redirect:/demandes/nouveau?categorie=DUPLICATA";
        } else {
            Long idFound = resultats.get(0).getId();
            redirectAttributes.addFlashAttribute("success", "Antécédent trouvé ! Vous pouvez créer un duplicata ou un transfert à partir de ce dossier.");
            return "redirect:/demandes/" + idFound;
        }
    }

    /**
     * Créer un duplicata ou transfert depuis un existant
     */
    @PostMapping("/{id}/creer-derive")
    public String creerDerive(@PathVariable("id") Long id, @RequestParam("categorie") String categorie, RedirectAttributes redirectAttributes) {
        try {
            DemandeVisaResponseDTO old = demandeVisaService.getDemande(id);
            DemandeVisaDTO dto = new DemandeVisaDTO();
            dto.setCategorie(categorie);
            dto.setDateDemande(java.time.LocalDate.now());
            dto.setNom(old.getNom());
            dto.setPrenoms(old.getPrenoms());
            dto.setDateNaissance(old.getDateNaissance());
            dto.setLieuNaissance(old.getLieuNaissance());
            dto.setNationalite(old.getNationalite());
            dto.setEmail(old.getEmail());
            dto.setContact(old.getContact());
            dto.setNumeroPasseport(old.getNumeroPasseport());
            dto.setDateDelivrancePasseport(old.getDateDelivrancePasseport());
            dto.setDateExpirationPasseport(old.getDateExpirationPasseport());
            dto.setTypeVisa(old.getTypeVisa());
            dto.setDateEntreeMadagascar(old.getDateEntreeMadagascar());
            dto.setLieuReferenceVisa(old.getLieuReferenceVisa());
            dto.setNumeroVisa(old.getNumeroVisa());
            dto.setNumeroCarteResident(old.getNumeroCarteResident());
            dto.setDateExpirationVisa(old.getDateExpirationVisa());

            DemandeVisaResponseDTO response = demandeVisaService.creerDemande(dto);
            redirectAttributes.addFlashAttribute("success", "Nouvelle demande de " + categorie + " créée. Veuillez vérifier les informations et cocher les pièces justificatives.");
            return "redirect:/demandes/" + response.getId() + "/modifier";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création : " + e.getMessage());
            return "redirect:/demandes/" + id;
        }
    }

    /**
     * Détail d'une demande
     */
    @GetMapping("/{id}")
    public String detailDemande(@PathVariable("id") Long id, Model model) {
        DemandeVisaResponseDTO demande = demandeVisaService.getDemande(id);
        model.addAttribute("demande", demande);

        // Pre-compute pieces stats
        if (demande.getPieces() != null) {
            long piecesFournies = demande.getPieces().stream()
                    .filter(p -> Boolean.TRUE.equals(p.getFourni()))
                    .count();
            long totalPieces = demande.getPieces().size();
            long progressPercent = totalPieces > 0 ? (piecesFournies * 100 / totalPieces) : 0;
            boolean allFournies = piecesFournies == totalPieces;

            model.addAttribute("piecesFournies", piecesFournies);
            model.addAttribute("totalPieces", totalPieces);
            model.addAttribute("progressPercent", progressPercent);
            model.addAttribute("allFournies", allFournies);
        }

        return "demandes/detail";
    }

    /**
     * Approuver la demande
     */
    @PostMapping("/{id}/approuver")
    public String approuverDemande(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            demandeVisaService.approuverDemande(id);
            redirectAttributes.addFlashAttribute("success", "La demande a été approuvée avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/demandes/" + id;
    }

    /**
     * Page d'accueil → redirige vers la liste
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/demandes";
    }
}
