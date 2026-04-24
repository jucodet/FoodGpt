# Quickstart - Identify Photo Ingredients

## Prerequisites

- Android Studio récent avec SDK Android (API 26+)
- JDK compatible Gradle du projet
- Appareil ou émulateur Android avec caméra (ou image de test)
- Optionnel: device compatible Google AI Edge Gallery pour tester le chemin prioritaire

## Run

1. Ouvrir le projet et synchroniser Gradle.
2. Lancer l'application sur un device Android.
3. Autoriser la permission caméra.
4. Capturer une photo d'étiquette contenant une section ingrédients.
5. Vérifier que l'app sélectionne:
   - AI Edge Gallery si disponible, sinon
   - fallback OCR local offline.
6. Corriger la liste si nécessaire puis valider.

## Acceptance Test Focus

- **AT-1**: photo nette -> liste d'ingrédients ordonnée et modifiable.
- **AT-2**: OCR >= 70% -> validation automatique possible.
- **AT-3**: photo floue -> message d'échec clair + action manuelle de relance.
- **AT-4**: mode hors ligne -> résultat inchangé (aucun appel réseau).
- **AT-5**: après traitement -> image temporaire supprimée du cache privé.

## Minimal Dependency Verification

- Vérifier qu'aucune dépendance OCR cloud n'est présente.
- Vérifier qu'un seul fallback OCR local est embarqué.
- Vérifier que l'usage de Google AI Edge Gallery est conditionnel à la disponibilité device.

## Performance Checks

- Extraction complète p95 < 8 secondes sur photo nette.
- Interactions UI (édition/validation) sans jank visible.

## Implementation Notes

- Le moteur de reconnaissance sélectionne AI Edge Gallery si capacité détectée sur le device.
- Le fallback OCR local est utilisé sinon, sans appel réseau.
- La relance automatique est désactivée; seule une action utilisateur relance le scan.
