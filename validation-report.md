# Rapport de Validation - Service Garage

- Date : 2025-11-30 20:11:19
- BaseUrl : http://localhost:8082
- Statut de santÃ© : 200
- ID du garage : e413d19c-5ce1-4ebb-b5aa-e7df8e8f3da8
- VÃ©hicules demandÃ©s : 3
- VÃ©hicules crÃ©Ã©s : 3
- Nombre de vÃ©hicules dans le garage : 3
- Logs du consumer Kafka trouvÃ©s : True
- Tentatives max (capacitÃ©) : 120
- CapacitÃ© appliquÃ©e : True
- CapacitÃ© appliquÃ©e Ã  la tentative : 51

## RÃ©sumÃ©
- CRUD Garage : crÃ©ation=201, mise Ã  jour=200, suppression=voir ci-dessous
- CRUD VÃ©hicule : crÃ©ation=3x, mise Ã  jour=200, liste OK
- CRUD Accessoires : crÃ©ation/liste/mise Ã  jour dÃ©taillÃ©s ci-dessus
- Recherche : carburant+accessoire dÃ©taillÃ©s ci-dessus
- CapacitÃ© : appliquÃ©e=True
- Kafka : logs consumer trouvÃ©s=True

## Statut CRUD
- Statut mise Ã  jour garage : 200
- Statut mise Ã  jour vÃ©hicule : 200

## VÃ©hicules par ModÃ¨le
- RequÃªte modeleId : ce95dae2-d5f0-46a3-8d87-ed01e5e9a371
- Nombre de vÃ©hicules pour ce modÃ¨le : 0

## Liste des Garages (PaginÃ©e)
- Total garages : 1
- Page : 
- Taille : 
- Renvois : 1
- Nom du premier garage : Garage Central Updated

## Accessoires
- ID de l'accessoire crÃ©Ã© : 31978ae4-544a-430c-82b1-f6da658000af
- Nom de l'accessoire crÃ©Ã© : GPS
- Nombre d'accessoires : 1
- Statut mise Ã  jour accessoire : 200

## Recherche de Garages par Carburant et Accessoire
- Nombre de rÃ©sultats de recherche : 1
- ID du premier garage : e413d19c-5ce1-4ebb-b5aa-e7df8e8f3da8
- Nom du premier garage : Garage Central Updated

## Prochaines Ã‰tapes
- Inspecter Kafka UI Ã  http://localhost:8090 pour le topic vehicule.created.
- VÃ©rifier les logs de l'app pour les accusÃ©s de rÃ©ception du consumer.
- Ajuster MaxAttempt si la capacitÃ© est plus Ã©levÃ©e que prÃ©vu.
- Statut suppression vÃ©hicule : 500
- Statut suppression garage : 204
