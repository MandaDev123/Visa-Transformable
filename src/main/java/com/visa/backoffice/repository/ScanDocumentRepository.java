package com.visa.backoffice.repository;

import com.visa.backoffice.entity.ScanDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanDocumentRepository extends JpaRepository<ScanDocument, Long> {
    List<ScanDocument> findByDemandePieceIdOrderByDateUploadDesc(Long demandePieceId);
}
