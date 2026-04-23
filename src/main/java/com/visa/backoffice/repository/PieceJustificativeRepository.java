package com.visa.backoffice.repository;

import com.visa.backoffice.entity.PieceJustificative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PieceJustificativeRepository extends JpaRepository<PieceJustificative, Long> {

    /**
     * Retourne toutes les pièces obligatoires pour un type de visa donné :
     * - Pièces COMMUNES obligatoires (type_visa IS NULL)
     * - Pièces COMPLEMENTAIRES obligatoires du type de visa spécifié
     */
    @Query("SELECT p FROM PieceJustificative p WHERE p.obligatoire = true " +
           "AND (p.typePiece = com.visa.backoffice.entity.TypePiece.COMMUNE " +
           "OR (p.typePiece = com.visa.backoffice.entity.TypePiece.COMPLEMENTAIRE AND p.typeVisa = :typeVisa))")
    List<PieceJustificative> findPiecesObligatoires(@Param("typeVisa") String typeVisa);

    /**
     * Retourne toutes les pièces applicables pour un type de visa :
     * - Toutes les pièces COMMUNES
     * - Toutes les pièces COMPLEMENTAIRES du type de visa
     */
    @Query("SELECT p FROM PieceJustificative p WHERE p.typePiece = com.visa.backoffice.entity.TypePiece.COMMUNE " +
           "OR (p.typePiece = com.visa.backoffice.entity.TypePiece.COMPLEMENTAIRE AND p.typeVisa = :typeVisa)")
    List<PieceJustificative> findPiecesApplicables(@Param("typeVisa") String typeVisa);
}
