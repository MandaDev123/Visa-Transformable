-- =============================================
-- Schema: Visa Back-Office
-- =============================================


CREATE DATABASE visa_backoffice;

CREATE TABLE IF NOT EXISTS demande_visa (
    id SERIAL PRIMARY KEY,
    date_demande DATE NOT NULL,
    categorie VARCHAR(50),
    statut VARCHAR(50) DEFAULT 'BROUILLON',
    nom VARCHAR(100),
    prenoms VARCHAR(100),
    numero_passeport VARCHAR(50) NOT NULL,
    type_visa VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS piece_justificative (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100),
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
