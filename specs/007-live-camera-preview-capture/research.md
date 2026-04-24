# Phase 0 Research - Live Camera Preview Capture

## Decision 1: Utiliser CameraX PreviewView intégré à l'UI Compose

- **Decision**: Afficher le flux objectif via CameraX (`Preview` + `PreviewView`) encapsulé dans un composant UI dédié.
- **Rationale**: CameraX est déjà utilisé dans le projet, stable sur large parc Android, et adapté au lifecycle Activity.
- **Alternatives considered**:
  - Camera2 natif: plus verbeux et coûteux à maintenir pour le MVP.
  - Placeholder vidéo local: rejeté car ne montre pas le vrai objectif et viole FR-002.

## Decision 2: Capture pilotée uniquement par événement utilisateur explicite

- **Decision**: Déclencher `ImageCapture.takePicture()` uniquement lors d'un clic bouton.
- **Rationale**: Garantit l'intention utilisateur et évite les captures involontaires.
- **Alternatives considered**:
  - Auto-capture périodique: augmente le risque d'erreurs et friction UX.
  - Capture à l'ouverture écran: contraire à l'exigence de contrôle explicite.

## Decision 3: Une machine d'états UI claire pour preview/capture/erreur

- **Decision**: Structurer les états `CameraReady`, `PreviewActive`, `Capturing`, `Success`, `Error`, `PermissionDenied`.
- **Rationale**: Rend le comportement testable et facilite les messages explicites.
- **Alternatives considered**:
  - État unique booléen (`isLoading`): trop pauvre pour couvrir les cas permission/indisponibilité.

## Decision 4: Gestion permission et indisponibilité sans fallback trompeur

- **Decision**: En cas d'erreur caméra, afficher un message/action de reprise, sans faux flux.
- **Rationale**: Alignement strict avec FR-002 et FR-005.
- **Alternatives considered**:
  - Afficher image statique d'exemple: améliore visuellement mais trompe l'utilisateur.

## Decision 5: Cible performance orientée perception utilisateur

- **Decision**: Mesurer le temps d'apparition de l'aperçu et le feedback post-capture.
- **Rationale**: Conforme à la constitution (performance mesurable) et aux critères SC-001/SC-002.
- **Alternatives considered**:
  - Mesure uniquement technique interne: moins lisible côté valeur utilisateur.
