# Contract: Ingredient Photo Recognition (On-device)

## Purpose

Définir le contrat fonctionnel interne entre UI, pipeline de reconnaissance et module de validation pour la feature
"identify photo ingredients", sans API réseau.

## Input Contract

### `StartIngredientRecognitionCommand`

```json
{
  "scanId": "uuid",
  "imageUri": "content:// or file:// private cache",
  "requestedAtEpochMs": 0,
  "source": "camera|gallery"
}
```

## Capability Detection Contract

### `RecognitionCapabilityResponse`

```json
{
  "scanId": "uuid",
  "aiEdgeGalleryAvailable": true,
  "selectedEngine": "ai_edge_gallery|local_ocr_fallback",
  "reason": "string"
}
```

## Output Contract

### `IngredientRecognitionResult`

```json
{
  "scanId": "uuid",
  "outcome": "success|partial|failure",
  "ocrConfidenceGlobal": 0.0,
  "autoValidated": false,
  "items": [
    {
      "position": 0,
      "rawText": "INGREDIENTS: sucre",
      "normalizedText": "sucre",
      "confidence": 0.91,
      "languageTag": "fr",
      "isAllergenMarked": false
    }
  ],
  "userMessage": "string"
}
```

## Validation Contract

### `ValidateIngredientListCommand`

```json
{
  "scanId": "uuid",
  "finalItems": [
    "sucre",
    "farine de ble"
  ],
  "editedByUser": true
}
```

### `ValidateIngredientListResult`

```json
{
  "scanId": "uuid",
  "accepted": true,
  "validatedAtEpochMs": 0,
  "errors": []
}
```

## Invariants

- Aucune donnée image/texte n'est envoyée vers un service distant.
- Suppression automatique de l'image temporaire après `IngredientRecognitionResult`.
- `autoValidated=true` seulement si `ocrConfidenceGlobal >= 0.70`.
- Si `outcome=failure`, `items` doit être vide.
