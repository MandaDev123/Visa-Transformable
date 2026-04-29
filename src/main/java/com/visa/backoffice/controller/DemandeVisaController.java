package com.visa.backoffice.controller;

import com.visa.backoffice.dto.DemandeVisaDTO;
import com.visa.backoffice.dto.DemandeVisaResponseDTO;
import com.visa.backoffice.service.DemandeVisaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
public class DemandeVisaController {

    private final DemandeVisaService demandeVisaService;

    /**
     * POST /api/demandes
     * Créer une nouvelle demande de visa (sauvegarde même incomplète)
     */
    @PostMapping
    public ResponseEntity<DemandeVisaResponseDTO> creerDemande(@Valid @RequestBody DemandeVisaDTO dto) {
        DemandeVisaResponseDTO response = demandeVisaService.creerDemande(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/demandes/{id}
     * Modifier une demande existante
     */
    @PutMapping("/{id}")
    public ResponseEntity<DemandeVisaResponseDTO> modifierDemande(
            @PathVariable("id") Long id,
            @Valid @RequestBody DemandeVisaDTO dto) {
        DemandeVisaResponseDTO response = demandeVisaService.modifierDemande(id, dto);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/demandes/{id}
     * Consulter une demande
     */
    @GetMapping("/{id}")
    public ResponseEntity<DemandeVisaResponseDTO> getDemande(@PathVariable("id") Long id) {
        DemandeVisaResponseDTO response = demandeVisaService.getDemande(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/demandes/search?numero={numero}
     * Rechercher par numéro de visa ou carte résident
     */
    @GetMapping("/search")
    public ResponseEntity<java.util.List<DemandeVisaResponseDTO>> rechercherParNumero(@RequestParam("numero") String numero) {
        return ResponseEntity.ok(demandeVisaService.rechercherParNumero(numero));
    }

    /**
     * GET /api/demandes/{id}/pdf
     * Générer l'attestation de récépissé PDF
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> genererPdf(@PathVariable("id") Long id,
                                             @org.springframework.beans.factory.annotation.Autowired com.visa.backoffice.service.PdfService pdfService) {
        DemandeVisaResponseDTO demande = demandeVisaService.getDemande(id);
        
        byte[] pdf = pdfService.generateAttestationRecepisse(demande);
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "attestation_" + demande.getId() + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
