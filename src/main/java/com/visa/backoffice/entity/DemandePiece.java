package com.visa.backoffice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "demande_piece")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandePiece {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_id", nullable = false)
    private DemandeVisa demande;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "piece_id", nullable = false)
    private PieceJustificative piece;

    @Column
    @Builder.Default
    private Boolean fourni = false;
}
