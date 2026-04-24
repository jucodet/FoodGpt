# Quickstart - Live Camera Preview Capture

## Prerequisites

- Android Studio récent + SDK Android API 26+
- Device ou émulateur avec caméra fonctionnelle
- Permission caméra activée sur l'application

## Run

1. Lancer l'application sur l'écran caméra.
2. Vérifier que l'encart affiche un flux réel de l'objectif (pas de placeholder texte).
3. Cadrer une étiquette.
4. Appuyer sur le bouton de capture.
5. Vérifier le feedback de capture (succès ou erreur explicite).

## Acceptance Test Focus

- **AT-1**: Aperçu objectif visible en moins de 3 secondes après ouverture écran.
- **AT-2**: Sans clic sur capture, aucune nouvelle photo n'est créée.
- **AT-3**: Avec un clic capture, une seule image est produite pour l'action.
- **AT-4**: Permission refusée -> message explicite, sans faux aperçu.
- **AT-5**: Caméra indisponible -> état erreur explicite + option de réessai.

## Responsive Focus

- Vérifier lisibilité et accessibilité de l'encart preview + bouton capture sur petits et grands écrans.
- Vérifier que la rotation de l'appareil conserve un parcours utilisable.

## Performance Checks

- Temps d'apparition preview: cible < 3 secondes.
- Feedback visuel post-capture: cible perçue < 300 ms.

## Implementation Notes (2026-04-24)

- L'aperçu utilise CameraX (`PreviewView`) dans un encart Compose; aucun texte de type « placeholder » ne remplace le flux réel lorsque la permission est accordée.
- La capture est déclenchée uniquement par le bouton « Prendre la photo » après `PreviewActive`.
- Les journaux `preview_bind_total_ms` et `capture_feedback_ms` sont émis pour valider les budgets perf en debug.
