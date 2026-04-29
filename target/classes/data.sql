-- =============================================
-- Données initiales : Pièces Justificatives
-- =============================================

-- =============================================
-- Pièces COMMUNES (obligatoires pour TOUS les types de visa)
-- =============================================
INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT '02 photos d''identité', 'COMMUNE', FALSE, NULL
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = '02 photos d''identité');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Notice de renseignement', 'COMMUNE', FALSE, NULL
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Notice de renseignement');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Demande adressée à Mr le Ministère de l''Intérieur et de la Décentralisation avec adresse e-mail et numéro téléphone portable', 'COMMUNE', FALSE, NULL
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Demande adressée à Mr le Ministère de l''Intérieur et de la Décentralisation avec adresse e-mail et numéro téléphone portable');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Photocopie certifiée du visa en cours de validité', 'COMMUNE', FALSE, NULL
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Photocopie certifiée du visa en cours de validité');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Photocopie certifiée de la première page du passeport', 'COMMUNE', FALSE, NULL
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Photocopie certifiée de la première page du passeport');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Photocopie certifiée de la carte résident en cours de validité', 'COMMUNE', FALSE, NULL
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Photocopie certifiée de la carte résident en cours de validité');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Certificat de résidence à Madagascar', 'COMMUNE', FALSE, NULL
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Certificat de résidence à Madagascar');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Extrait de casier judiciaire moins de 3 mois', 'COMMUNE', FALSE, NULL
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Extrait de casier judiciaire moins de 3 mois');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Pièces complémentaires selon la catégorie des immigrants', 'COMMUNE', FALSE, NULL
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Pièces complémentaires selon la catégorie des immigrants');

-- =============================================
-- Pièces COMPLÉMENTAIRES pour visa TRAVAILLEUR
-- =============================================
INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Autorisation emploi délivrée à Madagascar par le Ministère de la Fonction publique', 'COMPLEMENTAIRE', FALSE, 'TRAVAILLEUR'
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Autorisation emploi délivrée à Madagascar par le Ministère de la Fonction publique' AND type_visa = 'TRAVAILLEUR');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Attestation d''emploi délivré par l''employeur (Original)', 'COMPLEMENTAIRE', FALSE, 'TRAVAILLEUR'
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Attestation d''emploi délivré par l''employeur (Original)' AND type_visa = 'TRAVAILLEUR');

-- =============================================
-- Pièces COMPLÉMENTAIRES pour visa INVESTISSEUR
-- =============================================
INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Statut de la Société', 'COMPLEMENTAIRE', FALSE, 'INVESTISSEUR'
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Statut de la Société' AND type_visa = 'INVESTISSEUR');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Extrait d''inscription au registre de commerce', 'COMPLEMENTAIRE', FALSE, 'INVESTISSEUR'
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Extrait d''inscription au registre de commerce' AND type_visa = 'INVESTISSEUR');

INSERT INTO piece_justificative (nom, type_piece, obligatoire, type_visa)
SELECT 'Carte fiscale', 'COMPLEMENTAIRE', FALSE, 'INVESTISSEUR'
WHERE NOT EXISTS (SELECT 1 FROM piece_justificative WHERE nom = 'Carte fiscale' AND type_visa = 'INVESTISSEUR');
