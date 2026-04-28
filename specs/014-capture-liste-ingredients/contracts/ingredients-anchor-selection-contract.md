# Contract: Ingredients Anchor Selection

## Purpose

Definir le contrat de detection/selection du debut de liste ingredients avant envoi a l analyse composition.

## Anchor Detection Contract

### `IngredientAnchorDetectionInput`

```json
{
  "scanId": "string",
  "ocrText": "string"
}
```

### `IngredientAnchorDetectionOutput`

```json
{
  "scanId": "string",
  "candidates": [
    {
      "startIndex": 42,
      "rawMatch": "ingredients :",
      "isCanonical": true
    }
  ],
  "selectionRule": "FIRST_CANONICAL_MATCH",
  "selectedStartIndex": 42,
  "anchorFound": true,
  "blockedReason": "NONE|NO_CANONICAL_ANCHOR"
}
```

## Segment Extraction Contract

### `IngredientSegmentExtractionOutput`

```json
{
  "scanId": "string",
  "segmentStartsAt": 42,
  "segmentEndsAt": 98,
  "segmentText": "ingredients : sucre, farine, sel",
  "sourceAnchorPattern": "ingredients\\s*:"
}
```

## Invariants

- L ancre valide doit matcher le motif `ingredients\s*:`.
- Si plusieurs ancres valides existent, la premiere dans l ordre du texte est retenue.
- Une occurrence simple de `ingredients` sans deux-points n est pas suffisante si une ancre canonique est disponible.
- Sans ancre canonique, l analyse doit etre bloquee explicitement.

## Contract Consistency Notes

- Aligne avec la clarification 2026-04-28: selection de la premiere ancre canonique.
- Aligne avec quickstart: support de `ingredients:` et `ingredients :`.
