package com.visa.backoffice.controller.api;

import com.visa.backoffice.dto.DemandeVisaResponseDTO;
import com.visa.backoffice.service.DemandeVisaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/suivi")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class SuiviApiController {

    private final DemandeVisaService demandeVisaService;

    /**
     * Obtenir le suivi d'une demande par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DemandeVisaResponseDTO> getSuiviById(@PathVariable("id") Long id) {
        try {
            DemandeVisaResponseDTO response = demandeVisaService.getDemande(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Rechercher une demande par numéro de passeport ou de visa
     */
    @GetMapping("/search")
    public ResponseEntity<List<DemandeVisaResponseDTO>> searchDemande(
            @RequestParam("numero") String numero) {
        
        List<DemandeVisaResponseDTO> result = demandeVisaService.rechercherParNumero(numero);
        return ResponseEntity.ok(result);
    }
}
