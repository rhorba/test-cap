-- ============================================================================
-- MIGRATION FLYWAY V1 - Création des tables du système de gestion des garages
-- ============================================================================

-- Création de l'extension UUID si elle n'existe pas
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table des garages
CREATE TABLE garages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    rue VARCHAR(255) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    code_postal VARCHAR(10) NOT NULL,
    pays VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_garage_email UNIQUE (email)
);

-- Table des horaires d'ouverture
CREATE TABLE garage_horaires (
    garage_id UUID NOT NULL,
    day_of_week VARCHAR(10) NOT NULL,
    horaires JSONB NOT NULL,
    PRIMARY KEY (garage_id, day_of_week),
    CONSTRAINT fk_garage_horaires FOREIGN KEY (garage_id) 
        REFERENCES garages(id) ON DELETE CASCADE
);

-- Table des modèles de véhicules (catalogue partagé)
CREATE TABLE modeles_vehicules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nom_modele VARCHAR(255) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    description TEXT,
    specifications JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table des véhicules
CREATE TABLE vehicules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    garage_id UUID NOT NULL,
    modele_id UUID NOT NULL,
    brand VARCHAR(100) NOT NULL,
    annee_fabrication INTEGER NOT NULL,
    type_carburant VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicule_garage FOREIGN KEY (garage_id) 
        REFERENCES garages(id) ON DELETE CASCADE,
    CONSTRAINT fk_vehicule_modele FOREIGN KEY (modele_id) 
        REFERENCES modeles_vehicules(id),
    CONSTRAINT chk_annee_fabrication CHECK (annee_fabrication >= 1900 AND annee_fabrication <= 2100),
    CONSTRAINT chk_type_carburant CHECK (type_carburant IN ('ESSENCE', 'DIESEL', 'ELECTRIQUE', 'HYBRIDE', 'GPL'))
);

-- Table des accessoires
CREATE TABLE accessoires (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vehicule_id UUID NOT NULL,
    nom VARCHAR(255) NOT NULL,
    description TEXT,
    prix DECIMAL(10,2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_accessoire_vehicule FOREIGN KEY (vehicule_id) 
        REFERENCES vehicules(id) ON DELETE CASCADE,
    CONSTRAINT chk_prix CHECK (prix >= 0),
    CONSTRAINT chk_type_accessoire CHECK (type IN ('INTERIEUR', 'EXTERIEUR', 'ELECTRONIQUE', 'SECURITE', 'CONFORT'))
);

-- Index pour optimiser les recherches
CREATE INDEX idx_garage_ville ON garages(ville);
CREATE INDEX idx_garage_email ON garages(email);
CREATE INDEX idx_vehicule_garage ON vehicules(garage_id);
CREATE INDEX idx_vehicule_modele ON vehicules(modele_id);
CREATE INDEX idx_vehicule_carburant ON vehicules(type_carburant);
CREATE INDEX idx_accessoire_vehicule ON accessoires(vehicule_id);
CREATE INDEX idx_accessoire_type ON accessoires(type);

-- Fonction pour vérifier la capacité du garage (max 50 véhicules)
CREATE OR REPLACE FUNCTION check_garage_capacity()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT COUNT(*) FROM vehicules WHERE garage_id = NEW.garage_id) >= 50 THEN
        RAISE EXCEPTION 'Le garage a atteint sa capacité maximale de 50 véhicules';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger pour vérifier la capacité avant l'insertion d'un véhicule
CREATE TRIGGER trg_check_garage_capacity
BEFORE INSERT ON vehicules
FOR EACH ROW
EXECUTE FUNCTION check_garage_capacity();

-- Fonction pour mettre à jour automatiquement updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers pour updated_at
CREATE TRIGGER trg_garages_updated_at
BEFORE UPDATE ON garages
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_vehicules_updated_at
BEFORE UPDATE ON vehicules
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Commentaires sur les tables
COMMENT ON TABLE garages IS 'Table des garages affiliés au réseau Renault';
COMMENT ON TABLE vehicules IS 'Table des véhicules stockés dans les garages';
COMMENT ON TABLE accessoires IS 'Table des accessoires associés aux véhicules';
COMMENT ON COLUMN garages.email IS 'Email unique du garage';
COMMENT ON COLUMN vehicules.type_carburant IS 'Type de carburant: ESSENCE, DIESEL, ELECTRIQUE, HYBRIDE, GPL';
