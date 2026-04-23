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
        long brouillons = demandes.stream()
                .filter(d -> d.getStatut() != null && d.getStatut().name().equals("BROUILLON"))
                .count();
        long dossiersCrees = demandes.stream()
                .filter(d -> d.getStatut() != null && d.getStatut().name().equals("DOSSIER_CREE"))
                .count();
        model.addAttribute("totalDemandes", total);
        model.addAttribute("totalBrouillons", brouillons);
        model.addAttribute("totalDossiersCrees", dossiersCrees);

        return "demandes/list";
    }

    /**
     * Formulaire de nouvelle demande
     */
    @GetMapping("/nouveau")
    public String nouveauFormulaire(Model model) {
        model.addAttribute("demande", new DemandeVisaDTO());
        model.addAttribute("piecesCommunes", pieceJustificativeRepository.findPiecesApplicables(null));
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
     * Page d'accueil → redirige vers la liste
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/demandes";
    }
}
