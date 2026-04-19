# Research: Démarrage caméra et scan temporaire

## Decision 1: Utiliser CameraX pour le flux capture Android
- **Decision**: Adopter CameraX comme unique brique caméra.
- **Rationale**: API stable, intégration lifecycle native, réduction du code bas niveau.
- **Alternatives considered**:
  - Camera2 direct: plus complexe, surcoût de maintenance.
  - SDK tiers caméra: contraire à la contrainte "minimum de librairies".

## Decision 2: Exécution OCR 100% locale avec Google AI Edge
- **Decision**: Exécuter l’analyse de transcription on-device via une brique Google AI Edge.
- **Rationale**: Respect strict du "rien n'est uploadé sur un serveur tiers"; latence plus prévisible hors réseau.
- **Alternatives considered**:
  - OCR cloud: rejeté (contrainte confidentialité).
  - OCR open-source additionnel: rejeté pour limiter les dépendances et complexité support modèle.

## Decision 3: Persister uniquement les métadonnées en SQLite (Room)
- **Decision**: Stocker les sessions de scan (timestamps, statuts, hash, durée) en SQLite locale via Room.
- **Rationale**: Traçabilité locale + requêtes simples + conformité à l’exigence utilisateur.
- **Alternatives considered**:
  - SharedPreferences/DataStore: insuffisant pour requêtes structurées et historique.
  - Fichiers JSON: plus fragile, moins robuste pour évolutions.

## Decision 4: Gestion d’image temporaire en cache privé + suppression systématique
- **Decision**: Photo scan enregistrée en cache interne privé puis suppression en `finally`.
- **Rationale**: Réduit le risque de persistance accidentelle; comportement déterministe en succès/échec/annulation.
- **Alternatives considered**:
  - Analyse uniquement en mémoire: possible mais moins robuste sur gros buffers/rotations.
  - Sauvegarde galerie puis suppression: trop risqué pour confidentialité.

## Decision 5: Machine d’états explicite pour le feedback utilisateur
- **Decision**: Etats `idle`, `capturing`, `analyzing`, `success`, `error`, `permission_denied`.
- **Rationale**: UX lisible, testabilité acceptance/instrumentation alignée avec la constitution.
- **Alternatives considered**:
  - Etats implicites via booléens dispersés: ambigu, source de régressions UX.

