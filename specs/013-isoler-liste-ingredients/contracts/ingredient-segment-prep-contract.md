# Contract: Ingredient Segment Preparation Before Analysis

## Purpose

Definir le contrat fonctionnel entre OCR, preparation du segment ingredients, confirmation utilisateur, et envoi a l analyse.

## Extraction Contract

### `IngredientSegmentPreparationInput`

```json
{
  "scanId": "string",
  "ocrText": "string"
}
```

### `IngredientSegmentPreparationOutput`

```json
{
  "scanId": "string",
  "anchorWord": "ingredients",
  "anchorOccurrenceStrategy": "FIRST",
  "anchorFound": true,
  "startIndex": 42,
  "endBoundary": "FIRST_NEWLINE_OR_EOF",
  "endIndex": 93,
  "segmentText": "ingredients: sucre, farine, sel",
  "fallbackMode": "NONE|ANCHOR_MISSING_BLOCKED|NO_NEWLINE_TO_EOF"
}
```

## Submission Gate Contract

### `AnalysisSubmissionGateInput`

```json
{
  "scanId": "string",
  "segmentText": "string",
  "anchorFound": true,
  "userConfirmed": true
}
```

### `AnalysisSubmissionGateOutput`

```json
{
  "scanId": "string",
  "submissionAllowed": true,
  "blockedReason": "NONE|ANCHOR_MISSING|EMPTY_SEGMENT|USER_REJECTED",
  "message": "string"
}
```

## Invariants

- La premiere occurrence de `ingredients` est l ancre retenue.
- La borne de fin est le premier retour a la ligne suivant l ancre; sinon fin du texte OCR.
- Si l ancre est absente, l analyse est bloquee et une action utilisateur est requise.
- Le segment extrait doit etre affiche et confirme avant tout envoi a l analyse.
- Aucun envoi n est autorise avec segment vide ou inexploitable.
