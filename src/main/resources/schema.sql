-- Table Garages
CREATE TABLE IF NOT EXISTS garages (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    rue VARCHAR(255) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    code_postal VARCHAR(10) NOT NULL,
    pays VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    horaires_ouverture JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Table Vehicules
CREATE TABLE IF NOT EXISTS vehicules (
    id UUID PRIMARY KEY,
    garage_id UUID NOT NULL,
    modele_id UUID NOT NULL,
    brand VARCHAR(100) NOT NULL,
    annee_fabrication INTEGER NOT NULL,
    type_carburant VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_garage FOREIGN KEY (garage_id) REFERENCES garages(id) ON DELETE CASCADE
);

-- Table Accessoires
CREATE TABLE IF NOT EXISTS accessoires (
    id UUID PRIMARY KEY,
    vehicule_id UUID NOT NULL,
    nom VARCHAR(255) NOT NULL,
    description TEXT,
    prix DECIMAL(10,2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_vehicule FOREIGN KEY (vehicule_id) REFERENCES vehicules(id) ON DELETE CASCADE
);

-- Table ModeleVehicule (catalogue partagé)
CREATE TABLE IF NOT EXISTS modeles_vehicules (
    id UUID PRIMARY KEY,
    nom_modele VARCHAR(255) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    description TEXT,
    specifications JSONB
);

-- Index pour optimiser les recherches
CREATE INDEX IF NOT EXISTS idx_garage_ville ON garages(ville);
CREATE INDEX IF NOT EXISTS idx_vehicule_garage ON vehicules(garage_id);
CREATE INDEX IF NOT EXISTS idx_vehicule_modele ON vehicules(modele_id);
CREATE INDEX IF NOT EXISTS idx_vehicule_carburant ON vehicules(type_carburant);
CREATE INDEX IF NOT EXISTS idx_accessoire_vehicule ON accessoires(vehicule_id);
CREATE INDEX IF NOT EXISTS idx_accessoire_type ON accessoires(type);
