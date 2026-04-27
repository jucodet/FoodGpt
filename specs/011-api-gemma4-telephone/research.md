# Research: Utilisation API Gemma4 telephone

## Decision 1: Utiliser une couche client dediee pour l'API locale Gemma4

- **Decision**: Introduire un client d'acces API locale isole du flux d'analyse metier.
- **Rationale**: Cette separation limite le couplage, simplifie les tests et permet de gerer proprement disponibilite, timeouts et mapping d'erreurs.
- **Alternatives considered**:
  - Appel direct depuis le ViewModel (rejete: forte dette de maintenabilite et testabilite reduite).
  - Adaptation minimale dans le service existant sans abstraction (rejete: diffusion de la logique de transport dans plusieurs couches).

## Decision 2: Aucun fallback moteur en v1

- **Decision**: En cas d'indisponibilite API locale, la requete echoue avec message utilisateur explicite.
- **Rationale**: Le besoin fonctionnel clarifie exclut explicitement les fallbacks, ce qui reduit la complexite et le risque de comportement non deterministe.
- **Alternatives considered**:
  - Fallback vers moteur local alternatif (rejete: comportement heterogene et effort d'integration supplementaire).
  - Fallback cloud (rejete: impact vie privee, connectivite, cout, et hors perimetre).

## Decision 3: Observabilite minimale sans donnees utilisateur

- **Decision**: Ne journaliser que metriques techniques et erreurs (codes, latence, statut), sans contenu textuel soumis.
- **Rationale**: Respecte la clarification de non-conservation du contenu et maintient une capacite de diagnostic suffisante.
- **Alternatives considered**:
  - Journalisation complete des requetes/reponses (rejete: risque de confidentialite).
  - Aucune journalisation (rejete: incapacité de suivi de fiabilite/performance).

## Decision 4: SLO de latence par classe d'appareil

- **Decision**: Definir deux objectifs de latence (appareils recents vs minimum compatibles), conformes au spec.
- **Rationale**: Permet une cible realiste et testable face a l'heterogeneite des performances Android.
- **Alternatives considered**:
  - Une seule cible stricte pour tous (rejete: risque de non atteinte sur appareils faibles).
  - Cible unique plus laxiste (rejete: degrade l'experience sur appareils performants).

## Decision 5: Perimetre v1 texte uniquement

- **Decision**: Limiter v1 aux requetes textuelles, sans image/audio.
- **Rationale**: Minimise les inconnues fonctionnelles et techniques et accelere une livraison stable.
- **Alternatives considered**:
  - Texte + image (rejete: pipeline media et validation additionnels).
  - Texte + image + audio (rejete: hors perimetre et risque eleve sur planning).
