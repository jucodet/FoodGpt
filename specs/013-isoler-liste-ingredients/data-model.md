# Data Model: Isolation de la liste ingredients

## Entity: OcrRawText

- **Description**: Texte complet produit par OCR a partir de la photo.
- **Fields**:
  - `content` (string, requis): texte OCR brut.
  - `capturedAt` (datetime, requis): horodatage du scan.
  - `scanId` (string, requis): identifiant unique du scan.
- **Validation Rules**:
  - `content` ne doit pas etre null.
  - `scanId` doit etre unique par session de scan.

## Entity: IngredientSegmentExtraction

- **Description**: Resultat de l extraction du segment ingredients.
- **Fields**:
  - `scanId` (string, requis): reference vers le scan source.
  - `anchorFound` (boolean, requis): indique si l ancre ingredients existe.
  - `anchorIndex` (integer, optionnel): index de debut de l ancre retenue.
  - `endIndex` (integer, optionnel): index de fin du segment retenu.
  - `segmentText` (string, optionnel): texte isole pour analyse.
  - `fallbackMode` (enum, requis): `NONE`, `ANCHOR_MISSING_BLOCKED`, `NO_NEWLINE_TO_EOF`.
- **Validation Rules**:
  - Si `anchorFound=false`, `fallbackMode` doit etre `ANCHOR_MISSING_BLOCKED`.
  - Si `anchorFound=true` et pas de retour ligne, `fallbackMode` doit etre `NO_NEWLINE_TO_EOF`.
  - `segmentText` ne doit pas etre vide quand une analyse est autorisee.

## Entity: AnalysisSubmissionDecision

- **Description**: Decision utilisateur et systeme avant envoi a l analyse.
- **Fields**:
  - `scanId` (string, requis): reference scan.
  - `segmentPreview` (string, requis): segment affiche a l utilisateur.
  - `userConfirmed` (boolean, requis): confirmation explicite avant envoi.
  - `submissionAllowed` (boolean, requis): autorisation finale d envoi.
  - `blockedReason` (enum, optionnel): `ANCHOR_MISSING`, `EMPTY_SEGMENT`, `USER_REJECTED`.
- **Validation Rules**:
  - `submissionAllowed=true` exige `userConfirmed=true` et segment non vide.
  - Si `submissionAllowed=false`, `blockedReason` est obligatoire.

## Relationships

- `OcrRawText` 1 -> 1 `IngredientSegmentExtraction` (un resultat d extraction par scan).
- `IngredientSegmentExtraction` 1 -> 1 `AnalysisSubmissionDecision` (une decision de soumission par extraction).
