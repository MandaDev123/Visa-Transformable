package com.visa.backoffice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "piece_justificative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PieceJustificative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_piece", length = 50)
    private TypePiece typePiece;

    @Column
    private Boolean obligatoire;

    @Column(name = "type_visa", length = 50)
    private String typeVisa;
}
