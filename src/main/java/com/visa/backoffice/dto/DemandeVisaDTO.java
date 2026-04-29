package com.visa.backoffice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    // ===== État Civil =====
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenoms;

    @NotNull(message = "La date de naissance est obligatoire")
    private LocalDate dateNaissance;

    @NotBlank(message = "Le lieu de naissance est obligatoire")
    private String lieuNaissance;

    @NotBlank(message = "La nationalité est obligatoire")
    private String nationalite;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le contact est obligatoire")
    private String contact;

    // ===== Passeport =====
    @NotBlank(message = "Le numéro de passeport est obligatoire")
    private String numeroPasseport;

    @NotNull(message = "La date de délivrance du passeport est obligatoire")
    private LocalDate dateDelivrancePasseport;

    @NotNull(message = "La date d'expiration du passeport est obligatoire")
    private LocalDate dateExpirationPasseport;

    // ===== Visa Transformable =====
    private String typeVisa;

    private LocalDate dateEntreeMadagascar;

    private String lieuReferenceVisa;

    private String numeroVisa;

    private String numeroCarteResident;

    private LocalDate dateExpirationVisa;

    private List<DemandePieceDTO> pieces;
}
