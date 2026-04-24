# Data Model: Capture photo et affichage du texte reconnu

## 1) PhotoCapture

- **Description**: Image capturée par action explicite utilisateur ("Prendre la photo").
- **Champs clés**:
  - `captureId` (UUID local)
  - `capturedAt` (timestamp)
  - `source` (camera only pour ce flux)
  - `tempPath` (chemin cache privé temporaire)
  - `status` (`captured`, `processing`, `processed`, `failed`, `deleted`)
- **Règles de validation**:
  - Une capture doit provenir d'une action explicite.
  - `tempPath` doit référencer un stockage privé app.
  - La capture ne doit pas être transmise hors appareil pour OCR.

## 2) TextRecognitionResult

- **Description**: Résultat de lecture de texte associé à une `PhotoCapture`.
- **Champs clés**:
  - `resultId` (UUID local)
  - `captureId` (FK logique vers PhotoCapture)
  - `rawText` (texte brut extrait)
  - `displayText` (texte prêt à l'affichage utilisateur)
  - `confidenceHint` (optionnel, ex: `high|medium|low`)
  - `outcome` (`success`, `empty`, `error`)
  - `errorReason` (optionnel, ex: `blur`, `low_light`, `engine_unavailable`)
  - `processedAt` (timestamp)
- **Règles de validation**:
  - `outcome=success` => `displayText` non vide.
  - `outcome=empty` => message explicite affichable côté UI.
  - `outcome=error` => `errorReason` présent.

## 3) CaptureToTextFlow

- **Description**: État d'orchestration du parcours utilisateur de bout en bout.
- **États**:
  - `idle`
  - `capturing`
  - `processing`
  - `success`
  - `empty`
  - `error`
- **Transitions**:
  - `idle -> capturing` (tap bouton)
  - `capturing -> processing` (image validée)
  - `processing -> success|empty|error` (fin OCR local)
  - `success|empty|error -> idle` (nouvelle tentative)

## Relations

- `PhotoCapture` 1 -> 0..1 `TextRecognitionResult` (MVP: un résultat final par capture)
- `CaptureToTextFlow` orchestre `PhotoCapture` puis `TextRecognitionResult`

## Contraintes métier

- Lecture texte strictement on-device.
- Pas de contenu factice en cas d'échec.
- Résultat ou message d'échec visible en moins de 10 secondes (conditions normales).
