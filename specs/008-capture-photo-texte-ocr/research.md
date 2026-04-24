# Research: Capture photo et affichage du texte reconnu

## Décision 1: Pipeline OCR 100% on-device

- **Decision**: Exécuter la lecture du texte localement sur l'appareil, sans envoi de l'image ni dépendance réseau pour l'extraction.
- **Rationale**: Alignement direct avec FR-008, cohérence avec clarifications 001/006/008, meilleure confidentialité, fonctionnement robuste hors-ligne.
- **Alternatives considered**:
  - Traitement distant: rejeté (contradictoire avec clarifications et contraintes privacy/offline).
  - Mode hybride: rejeté pour MVP (complexité supplémentaire sans besoin immédiat).

## Décision 2: Réutiliser CameraX + état d'orchestration ViewModel

- **Decision**: Garder CameraX pour capture réelle et piloter le flux `Idle -> Capturing -> Processing -> Success/Empty/Error` depuis le ViewModel.
- **Rationale**: La base caméra existe déjà; minimiser les régressions et rendre les transitions testables pour ATDD.
- **Alternatives considered**:
  - Réécriture complète du module caméra: rejetée (coût élevé, valeur faible).
  - Capturer hors workflow ViewModel: rejeté (états plus difficiles à tester et tracer).

## Décision 3: Sortie texte lisible avec états explicites

- **Decision**: Afficher soit le texte extrait, soit un message explicite "aucun texte détecté" / erreur locale + action de relance.
- **Rationale**: Respect des User Stories P1/P2 et FR-006/FR-007; évite le "silence" UX.
- **Alternatives considered**:
  - Affichage vide sans message: rejeté (mauvaise UX, ambiguïté pour l'utilisateur).
  - Messages techniques détaillés: rejeté pour MVP (bruit inutile côté utilisateur final).

## Décision 4: Cible de performance harmonisée

- **Decision**: Cible "résultat affiché < 10 secondes" en conditions normales, alignée sur la spécification harmonisée.
- **Rationale**: Contrat produit cohérent entre parcours photo; critère mesurable pour tests d'acceptation.
- **Alternatives considered**:
  - Conserver 15 secondes: rejeté (incohérence documentaire introduite précédemment).
  - Cible plus agressive (< 5s): rejetée (risque élevé selon qualité image/device hétérogène).

## Décision 5: Nettoyage des artefacts image temporaires

- **Decision**: Utiliser le cache privé uniquement pendant traitement, puis supprimer après résultat (succès ou échec), sauf conservation explicitement demandée par un autre flux.
- **Rationale**: Réduction risque confidentialité + stockage, cohérence avec pratiques définies sur les autres specs photo.
- **Alternatives considered**:
  - Conservation prolongée systématique: rejetée (risque données inutiles).
  - Suppression immédiate avant affichage: rejetée (empêche reprise/diagnostic en cas d'erreur intermédiaire).
