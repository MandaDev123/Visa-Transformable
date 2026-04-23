package com.visa.backoffice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeVisaDTO {

    private LocalDate dateDemande;

    private String categorie;

    private String nom;

    private String prenoms;

    @NotBlank(message = "Le numéro de passeport est obligatoire")
    private String numeroPasseport;

    private String typeVisa;

    private List<DemandePieceDTO> pieces;
}
