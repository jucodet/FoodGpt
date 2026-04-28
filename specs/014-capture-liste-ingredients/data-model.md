# Data Model: Capture liste ingredients avec ancre `ingredients\s*:`

## Entity: OcrRawText

- **Description**: Texte OCR brut issu de la photo.
- **Fields**:
  - `scanId` (string, requis): identifiant unique du scan.
  - `content` (string, requis): texte OCR complet.
  - `capturedAt` (datetime, requis): horodatage capture.
- **Validation Rules**:
  - `content` non vide.
  - `scanId` unique par scan.

## Entity: IngredientAnchorCandidate

- **Description**: Occurrence candidate representant un debut de liste ingredients.
- **Fields**:
  - `scanId` (string, requis): reference scan.
  - `startIndex` (integer, requis): index de debut de l ancre.
  - `rawMatch` (string, requis): texte correspondant (ex: `ingredients:` ou `ingredients :`).
  - `isCanonical` (boolean, requis): vrai si correspond a la forme cible `ingredients\s*:`.
- **Validation Rules**:
  - `startIndex` >= 0.
  - `isCanonical=true` uniquement pour les formes targetees.

## Entity: IngredientAnchorSelection

- **Description**: Resultat de selection d une ancre unique pour le segment final.
- **Fields**:
  - `scanId` (string, requis)
  - `selectedStartIndex` (integer, optionnel)
  - `selectionRule` (enum, requis): `FIRST_CANONICAL_MATCH`.
  - `anchorFound` (boolean, requis)
  - `blockedReason` (enum, optionnel): `NO_CANONICAL_ANCHOR`.
- **Validation Rules**:
  - Si `anchorFound=true`, `selectedStartIndex` obligatoire.
  - Si `anchorFound=false`, `blockedReason` obligatoire.

## Relationships

- `OcrRawText` 1 -> N `IngredientAnchorCandidate`.
- `OcrRawText` 1 -> 1 `IngredientAnchorSelection`.
