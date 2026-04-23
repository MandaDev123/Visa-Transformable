-- =============================================
-- Données initiales : Pièces Justificatives
-- =============================================

-- Pièces COMMUNES (obligatoires pour tous les types de visa)
INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Passeport valide', 'COMMUNE', TRUE, NULL
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Passeport valide');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Photo d''identité', 'COMMUNE', TRUE, NULL
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Photo d''identité');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Formulaire de demande signé', 'COMMUNE', TRUE, NULL
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Formulaire de demande signé');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Justificatif de domicile', 'COMMUNE', TRUE, NULL
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Justificatif de domicile');

-- Pièces COMPLEMENTAIRES pour visa TRAVAIL
INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Contrat de travail', 'COMPLEMENTAIRE', TRUE, 'TRAVAIL'
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Contrat de travail' AND type_visa = 'TRAVAIL');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Attestation employeur', 'COMPLEMENTAIRE', TRUE, 'TRAVAIL'
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Attestation employeur' AND type_visa = 'TRAVAIL');

-- Pièces COMPLEMENTAIRES pour visa ETUDE
INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Attestation d''inscription', 'COMPLEMENTAIRE', TRUE, 'ETUDE'
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Attestation d''inscription' AND type_visa = 'ETUDE');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Justificatif de ressources', 'COMPLEMENTAIRE', TRUE, 'ETUDE'
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Justificatif de ressources' AND type_visa = 'ETUDE');

-- Pièces COMPLEMENTAIRES pour visa TOURISME
INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Réservation hôtel', 'COMPLEMENTAIRE', FALSE, 'TOURISME'
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Réservation hôtel' AND type_visa = 'TOURISME');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Billet aller-retour', 'COMPLEMENTAIRE', TRUE, 'TOURISME'
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Billet aller-retour' AND type_visa = 'TOURISME');
