# 📚 Index de la Documentation

Bienvenue dans le projet **Renault Garage Management Service** ! Ce document vous guide vers la bonne documentation selon vos besoins.

---

## 🎯 Par Rôle

### 👨‍💼 Chef de Projet / Product Owner
1. **[README.md](README.md)** - Vue d'ensemble complète du projet
2. **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - Résumé exécutif
3. **[ARCHITECTURE.md](ARCHITECTURE.md)** - Architecture du système

### 👨‍💻 Développeur Backend
1. **[README.md](README.md)** - Point d'entrée principal
2. **[ARCHITECTURE.md](ARCHITECTURE.md)** - Architecture hexagonale
3. **[EVENTS.md](EVENTS.md)** - Système d'événements
4. **[API_USAGE_GUIDE.md](API_USAGE_GUIDE.md)** - Utilisation de l'API
5. **[TESTING.md](TESTING.md)** - Guide des tests

### 🧪 QA / Testeur
1. **[TESTING.md](TESTING.md)** - Tests unitaires et d'intégration
2. **[API_USAGE_GUIDE.md](API_USAGE_GUIDE.md)** - Exemples cURL
3. **[QUICKSTART.md](QUICKSTART.md)** - Démarrage rapide
4. **[QUICKSTART_EVENTS.md](QUICKSTART_EVENTS.md)** - Test du système d'événements

### 🚀 DevOps / SRE
1. **[QUICKSTART.md](QUICKSTART.md)** - Installation et déploiement
2. **[docker-compose.yml](docker-compose.yml)** - Infrastructure Docker
3. **[README.md](README.md)** - Configuration et prérequis

### 📊 Architecte Système
1. **[ARCHITECTURE.md](ARCHITECTURE.md)** - Design du système
2. **[EVENTS.md](EVENTS.md)** - Architecture événementielle
3. **[EVENT_SYSTEM_DELIVERY.md](EVENT_SYSTEM_DELIVERY.md)** - Détails d'implémentation

---

## 📖 Par Sujet

### 🏗️ Architecture
| Document | Description | Audience |
|----------|-------------|----------|
| **[ARCHITECTURE.md](ARCHITECTURE.md)** | Architecture hexagonale, DDD, patterns | Développeurs, Architectes |
| **[README.md](README.md#architecture)** | Vue d'ensemble de l'architecture | Tous |

### 🚀 Installation & Démarrage
| Document | Description | Temps |
|----------|-------------|-------|
| **[QUICKSTART.md](QUICKSTART.md)** | Guide de démarrage rapide | 5 min |
| **[README.md](README.md#installation-et-démarrage)** | Installation détaillée | 10 min |

### 📡 API REST
| Document | Description | Contenu |
|----------|-------------|---------|
| **[API_USAGE_GUIDE.md](API_USAGE_GUIDE.md)** | Guide complet avec exemples cURL | Tous les endpoints |
| **[README.md](README.md#api-endpoints)** | Tableau récapitulatif des endpoints | Vue d'ensemble |

### 📡 Système d'Événements
| Document | Description | Niveau |
|----------|-------------|--------|
| **[KAFKA_GUIDE.md](KAFKA_GUIDE.md)** | Guide complet Apache Kafka | Production |
| **[KAFKA_IMPLEMENTATION.md](KAFKA_IMPLEMENTATION.md)** | Récapitulatif implémentation Kafka | Manager |
| **[EVENTS.md](EVENTS.md)** | Documentation Spring Events (ancien) | Référence |
| **[QUICKSTART_EVENTS.md](QUICKSTART_EVENTS.md)** | Test rapide en 5 minutes | Débutant |
| **[test-kafka.ps1](test-kafka.ps1)** | Script de test Kafka | Script |
| **[EVENT_SYSTEM_DELIVERY.md](EVENT_SYSTEM_DELIVERY.md)** | Récapitulatif Spring Events | Archive |
| **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** | Détails Spring Events | Archive |
| **[README.md](README.md#système-dévénements)** | Vue d'ensemble | Tous |

### 🧪 Tests
| Document | Description | Contenu |
|----------|-------------|---------|
| **[TESTING.md](TESTING.md)** | Guide complet des tests | Tests unitaires, intégration, coverage |
| **[README.md](README.md#tests)** | Vue d'ensemble des tests | Résumé |

### 🗄️ Base de Données
| Document | Description | Contenu |
|----------|-------------|---------|
| **[README.md](README.md#modèle-de-données)** | Schéma de base de données | Tables, relations, contraintes |
| **[ARCHITECTURE.md](ARCHITECTURE.md)** | Modèle de domaine | Entities, Value Objects |

### ⚙️ Configuration
| Document | Description | Technologie |
|----------|-------------|-------------|
| **[pom.xml](pom.xml)** | Configuration Maven | Dependencies, plugins |
| **[docker-compose.yml](docker-compose.yml)** | Infrastructure locale | PostgreSQL, pgAdmin |
| **[src/main/resources/application.yml](src/main/resources/application.yml)** | Configuration Spring Boot | Profiles, database, logging |

---

## 🚀 Parcours Recommandés

### 🎓 Nouveau Développeur (1ère fois)
1. **[README.md](README.md)** - Vue d'ensemble (10 min)
2. **[QUICKSTART.md](QUICKSTART.md)** - Installation et démarrage (5 min)
3. **[ARCHITECTURE.md](ARCHITECTURE.md)** - Comprendre l'architecture (15 min)
4. **[API_USAGE_GUIDE.md](API_USAGE_GUIDE.md)** - Tester l'API (10 min)
5. **[TESTING.md](TESTING.md)** - Lancer les tests (5 min)

**Temps total : 45 minutes**

### 🔧 Développement d'une Nouvelle Feature
1. **[ARCHITECTURE.md](ARCHITECTURE.md)** - Comprendre les layers
2. **[API_USAGE_GUIDE.md](API_USAGE_GUIDE.md)** - Endpoints existants
3. **[TESTING.md](TESTING.md)** - Écrire des tests
4. **[EVENTS.md](EVENTS.md)** - Si besoin d'événements

### 🐛 Debugging / Résolution de Problème
1. **[README.md](README.md#gestion-des-erreurs)** - Codes d'erreur
2. **[TESTING.md](TESTING.md)** - Lancer les tests
3. **[API_USAGE_GUIDE.md](API_USAGE_GUIDE.md)** - Exemples de requêtes
4. Logs de l'application (`mvn spring-boot:run`)

### 📊 Revue de Code / Audit
1. **[ARCHITECTURE.md](ARCHITECTURE.md)** - Design patterns
2. **[EVENTS.md](EVENTS.md)** - Système d'événements
3. **[TESTING.md](TESTING.md)** - Couverture de tests
4. **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - État du projet

---

## 📄 Documents par Ordre Alphabétique

| Document | Description | Taille |
|----------|-------------|--------|
| **[API_USAGE_GUIDE.md](API_USAGE_GUIDE.md)** | Guide d'utilisation de l'API avec exemples cURL | ~300 lignes |
| **[ARCHITECTURE.md](ARCHITECTURE.md)** | Architecture hexagonale et design patterns | ~400 lignes |
| **[docker-compose.yml](docker-compose.yml)** | Configuration Docker (PostgreSQL + pgAdmin) | ~50 lignes |
| **[EVENTS.md](EVENTS.md)** | Documentation du système d'événements | ~350 lignes |
| **[EVENT_SYSTEM_DELIVERY.md](EVENT_SYSTEM_DELIVERY.md)** | Récapitulatif de livraison du système d'événements | ~200 lignes |
| **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** | Détails d'implémentation du système d'événements | ~250 lignes |
| **[INDEX.md](INDEX.md)** | Ce document - index de navigation | ~150 lignes |
| **[pom.xml](pom.xml)** | Configuration Maven du projet | ~200 lignes |
| **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** | Résumé exécutif du projet | ~150 lignes |
| **[QUICKSTART.md](QUICKSTART.md)** | Guide de démarrage rapide | ~100 lignes |
| **[QUICKSTART_EVENTS.md](QUICKSTART_EVENTS.md)** | Test rapide du système d'événements | ~200 lignes |
| **[README.md](README.md)** | Documentation principale du projet | ~600 lignes |
| **[test-events.ps1](test-events.ps1)** | Script PowerShell de test des événements | ~80 lignes |
| **[test-events.sh](test-events.sh)** | Script Bash de test des événements | ~60 lignes |
| **[TESTING.md](TESTING.md)** | Guide complet des tests | ~200 lignes |

**Total : ~3,300 lignes de documentation** 📚

---

## 🔍 Recherche Rapide

### Je veux...
- **Démarrer le projet** → [QUICKSTART.md](QUICKSTART.md)
- **Comprendre l'architecture** → [ARCHITECTURE.md](ARCHITECTURE.md)
- **Tester l'API** → [API_USAGE_GUIDE.md](API_USAGE_GUIDE.md)
- **Lancer les tests** → [TESTING.md](TESTING.md)
- **Comprendre les événements** → [EVENTS.md](EVENTS.md)
- **Tester les événements** → [QUICKSTART_EVENTS.md](QUICKSTART_EVENTS.md)
- **Voir les endpoints** → [README.md](README.md#api-endpoints)
- **Configurer Docker** → [docker-compose.yml](docker-compose.yml)
- **Voir les dépendances** → [pom.xml](pom.xml)
- **Comprendre le domaine** → [ARCHITECTURE.md](ARCHITECTURE.md)

---

## 📊 Statistiques du Projet

| Métrique | Valeur |
|----------|--------|
| **Lignes de documentation** | ~3,300 |
| **Documents Markdown** | 12 |
| **Scripts de test** | 2 (PowerShell + Bash) |
| **Fichiers de configuration** | 2 (pom.xml + docker-compose.yml) |
| **Endpoints API** | ~20 |
| **Tests unitaires** | 17 (100% passing) |
| **Tests d'intégration** | 5+ |
| **Couverture de code** | ~80% |

---

## 🆘 Besoin d'Aide ?

### 💬 Questions Fréquentes
Consultez la section **Dépannage** dans :
- [QUICKSTART.md](QUICKSTART.md#dépannage)
- [QUICKSTART_EVENTS.md](QUICKSTART_EVENTS.md#dépannage)

### 📧 Support
- Email: support@renault.fr
- Équipe: Renault IT Team

### 🐛 Reporter un Bug
1. Vérifier les logs (`mvn spring-boot:run`)
2. Consulter [README.md](README.md#gestion-des-erreurs)
3. Lancer les tests : `mvn test`
4. Créer une issue avec les détails

---

## 🎯 Navigation Rapide

```
📦 Renault Garage Management Service
├── 📖 README.md ........................ Documentation principale ⭐
├── 🚀 QUICKSTART.md .................... Démarrage rapide (5 min)
├── 🏗️ ARCHITECTURE.md ................. Architecture hexagonale
├── 📡 API_USAGE_GUIDE.md ............... Guide API avec exemples
├── 🧪 TESTING.md ....................... Tests unitaires & intégration
├── 📊 PROJECT_SUMMARY.md ............... Résumé exécutif
│
├── 📡 Système d'Événements
│   ├── EVENTS.md ....................... Documentation technique
│   ├── QUICKSTART_EVENTS.md ............ Test rapide (5 min)
│   ├── EVENT_SYSTEM_DELIVERY.md ........ Récapitulatif livraison
│   ├── IMPLEMENTATION_SUMMARY.md ....... Détails implémentation
│   ├── test-events.ps1 ................. Script PowerShell
│   └── test-events.sh .................. Script Bash
│
├── ⚙️ Configuration
│   ├── pom.xml ......................... Configuration Maven
│   ├── docker-compose.yml .............. Infrastructure Docker
│   └── src/main/resources/
│       └── application.yml ............. Config Spring Boot
│
└── 📚 INDEX.md ......................... Ce document
```

---

**🎉 Bonne lecture et bon développement !**

**Dernière mise à jour :** 28 novembre 2024  
**Version :** 1.0.0
