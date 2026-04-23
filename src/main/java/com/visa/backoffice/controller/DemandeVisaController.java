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
}
