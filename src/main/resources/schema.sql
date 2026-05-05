-- =============================================
-- Schema: Visa Back-Office
-- =============================================


CREATE DATABASE visa_backoffice;

\c visa_backoffice
-- Drop existing tables to apply new schema
-- DROP TABLE IF EXISTS demande_piece CASCADE;
-- DROP TABLE IF EXISTS piece_justificative CASCADE;
-- DROP TABLE IF EXISTS demande_visa CASCADE;

CREATE TABLE IF NOT EXISTS demande_visa (
    id SERIAL PRIMARY KEY,
    date_demande DATE NOT NULL,
    categorie VARCHAR(50),
    statut VARCHAR(50) DEFAULT 'DOSSIER_CREE',

    nom VARCHAR(100) NOT NULL,
    prenoms VARCHAR(100) NOT NULL,
    date_naissance DATE,
    lieu_naissance VARCHAR(150),
    nationalite VARCHAR(100),
    email VARCHAR(150),
    contact VARCHAR(50),

    numero_passeport VARCHAR(50) NOT NULL,
    date_delivrance_passeport DATE,
    date_expiration_passeport DATE,

    type_visa VARCHAR(50),
    date_entree_madagascar DATE,
    lieu_reference_visa VARCHAR(150),
    numero_visa VARCHAR(50),
    numero_carte_resident VARCHAR(50),
    date_expiration_visa DATE
);

CREATE TABLE IF NOT EXISTS piece_justificative (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255),
    type_piece VARCHAR(50),
    obligatoire BOOLEAN DEFAULT FALSE,
    type_visa VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS demande_piece (
    id SERIAL PRIMARY KEY,
    demande_id INT REFERENCES demande_visa(id) ON DELETE CASCADE,
    piece_id INT REFERENCES piece_justificative(id) ON DELETE CASCADE,
    fourni BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS scan_document (
    id SERIAL PRIMARY KEY,
    demande_piece_id INT REFERENCES demande_piece(id) ON DELETE CASCADE,
    nom_fichier VARCHAR(255) NOT NULL,
    nom_original VARCHAR(255) NOT NULL,
    type_document VARCHAR(50),
    content_type VARCHAR(100),
    taille_octets BIGINT,
    chemin_fichier VARCHAR(500),
    date_upload TIMESTAMP DEFAULT NOW()
);

