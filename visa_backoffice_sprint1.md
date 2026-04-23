# 🛂 Projet Back Office - Gestion des Demandes de Visa Transformable (Sprint 1 amélioré)

## 🎯 Objectif
Back-office pour gérer les demandes de visa transformable avec :
- formulaire dynamique
- sauvegarde incomplète
- validation progressive
- gestion des pièces (communes et complémentaires)

---

## 🧱 Stack
- Spring Boot
- PostgreSQL
- JPA / Hibernate
- REST API

---

## 📌 Sprint 1 - Fonctionnalités
- Création / modification de demande
- Sauvegarde même incomplète
- Checkbox pour pièces justificatives
- Validation automatique
- Statut dynamique : BROUILLON / DOSSIER_CREE

---

## 🗄️ Base de Données

### demande_visa
CREATE TABLE demande_visa (
    id SERIAL PRIMARY KEY,
    date_demande DATE NOT NULL,
    categorie VARCHAR(50),
    statut VARCHAR(50),
    nom VARCHAR(100),
    prenoms VARCHAR(100),
    numero_passeport VARCHAR(50) NOT NULL,
    type_visa VARCHAR(50)
);

---

### piece_justificative
CREATE TABLE piece_justificative (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100),
    type_piece VARCHAR(50), -- COMMUNE / COMPLEMENTAIRE
    obligatoire BOOLEAN,
    type_visa VARCHAR(50) -- NULL si commune
);

---

### demande_piece
CREATE TABLE demande_piece (
    id SERIAL PRIMARY KEY,
    demande_id INT REFERENCES demande_visa(id),
    piece_id INT REFERENCES piece_justificative(id),
    fourni BOOLEAN DEFAULT FALSE
);

---

## 🔁 Logique Métier

### Types de pièces
- COMMUNE → toujours obligatoire
- COMPLEMENTAIRE → dépend du type de visa

---

### Validation
- Champs obligatoires :
  - date_demande
  - numero_passeport
- Vérifier :
  - pièces communes obligatoires
  - pièces complémentaires obligatoires selon typeVisa

---

## 🧠 Service Java

public boolean verifierPieces(DemandeVisa d) {

    List<PieceJustificative> piecesObligatoires =
        pieceRepository.findPiecesObligatoires(d.getTypeVisa());

    List<DemandePiece> piecesFournies =
        demandePieceRepository.findByDemande(d.getId());

    for (PieceJustificative p : piecesObligatoires) {

        boolean trouve = piecesFournies.stream()
            .anyMatch(dp ->
                dp.getPiece().getId().equals(p.getId())
                && dp.isFourni()
            );

        if (!trouve) return false;
    }

    return true;
}

---

## 🌐 API

POST /api/demandes  
PUT /api/demandes/{id}  
GET /api/demandes/{id}

---

## 🤖 PROMPT ANTIGRAVITY

Create a Spring Boot back-office visa management system.

Requirements:
- PostgreSQL
- Entities: DemandeVisa, PieceJustificative, DemandePiece
- Handle:
  - Common required documents
  - Complementary documents based on visa type
- Checkbox-based document submission
- Save incomplete requests
- Auto status update (BROUILLON / DOSSIER_CREE)

Generate:
- Entities
- Repositories
- Services
- Controllers
- DTOs

---

