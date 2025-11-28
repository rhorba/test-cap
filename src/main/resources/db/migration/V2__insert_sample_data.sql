-- ============================================================================
-- MIGRATION FLYWAY V2 - Données de test initiales
-- ============================================================================

-- Insertion de modèles de véhicules
INSERT INTO modeles_vehicules (id, nom_modele, brand, description) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Zoe', 'Renault', 'Véhicule électrique urbain'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Clio', 'Renault', 'Citadine polyvalente'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Megane E-Tech', 'Renault', 'Berline électrique');

-- Insertion de garages de test
INSERT INTO garages (id, name, rue, ville, code_postal, pays, telephone, email) VALUES
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'Renault Paris Nord', '123 Avenue Jean Jaurès', 'Paris', '75019', 'France', '+33140256789', 'paris.nord@renault.fr'),
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Renault Lyon Centre', '45 Rue de la République', 'Lyon', '69002', 'France', '+33478945612', 'lyon.centre@renault.fr');

-- Insertion des horaires d'ouverture
INSERT INTO garage_horaires (garage_id, day_of_week, horaires) VALUES
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'MONDAY', '[{"startTime":"08:00:00","endTime":"12:00:00"},{"startTime":"14:00:00","endTime":"18:00:00"}]'),
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'TUESDAY', '[{"startTime":"08:00:00","endTime":"18:00:00"}]');
