package com.visa.backoffice.repository;

import com.visa.backoffice.entity.DemandeVisa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeVisaRepository extends JpaRepository<DemandeVisa, Long> {

    @Query("SELECT d FROM DemandeVisa d LEFT JOIN FETCH d.pieces dp LEFT JOIN FETCH dp.piece WHERE d.id = :id")
    Optional<DemandeVisa> findByIdWithPieces(@Param("id") Long id);

    @Query("SELECT d FROM DemandeVisa d LEFT JOIN FETCH d.pieces dp LEFT JOIN FETCH dp.piece WHERE d.numeroVisa = :numero OR d.numeroCarteResident = :numero OR d.numeroPasseport = :numero ORDER BY d.dateDemande DESC")
    List<DemandeVisa> findHistoriqueByNumero(@Param("numero") String numero);
}
