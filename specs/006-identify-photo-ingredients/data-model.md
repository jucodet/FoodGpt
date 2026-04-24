# Data Model - Identify Photo Ingredients

## Entity: PhotoIngredientInput

- **Description**: Image soumise pour reconnaissance des ingrédients.
- **Fields**:
  - `scanId: String` (UUID, required)
  - `capturedAt: Instant` (required)
  - `imageUri: String` (required, cache privé)
  - `source: Enum(camera, gallery)` (required)
  - `status: Enum(captured, processing, processed, failed, deleted)` (required)
- **Validation Rules**:
  - `imageUri` doit pointer vers un répertoire privé app.
  - `status=deleted` seulement après suppression physique confirmée.

## Entity: RecognitionEngineSelection

- **Description**: Résultat de sélection du moteur de reconnaissance local.
- **Fields**:
  - `scanId: String` (FK -> PhotoIngredientInput.scanId)
  - `engine: Enum(ai_edge_gallery, local_ocr_fallback)` (required)
  - `availabilityReason: String` (required)
  - `selectedAt: Instant` (required)
- **Validation Rules**:
  - `engine=ai_edge_gallery` uniquement si capacité détectée sur le device.
  - `availabilityReason` non vide pour audit fonctionnel.

## Entity: ExtractedIngredientItem

- **Description**: Ingrédient issu de la phase OCR + parsing.
- **Fields**:
  - `itemId: String` (UUID, required)
  - `scanId: String` (FK -> PhotoIngredientInput.scanId)
  - `position: Int` (required, >= 0)
  - `rawText: String` (required)
  - `normalizedText: String` (required)
  - `confidence: Float` (0..1)
  - `languageTag: String` (optional)
  - `isAllergenMarked: Boolean` (required)
- **Validation Rules**:
  - `position` doit rester strictement ordonné sans doublon par `scanId`.
  - `normalizedText` ne doit pas être vide après trimming.

## Entity: ScanAttemptResult

- **Description**: Synthèse d'une tentative d'extraction.
- **Fields**:
  - `scanId: String` (PK/FK)
  - `outcome: Enum(success, partial, failure)` (required)
  - `ocrConfidenceGlobal: Float` (0..1)
  - `isAutoValidated: Boolean` (required)
  - `userMessage: String` (required)
  - `completedAt: Instant` (required)
- **Validation Rules**:
  - `isAutoValidated=true` uniquement si `ocrConfidenceGlobal >= 0.70`.
  - `outcome=failure` interdit toute liste d'ingrédients finale non vide.

## Entity: ValidatedIngredientList

- **Description**: Liste finale validée après éventuelles corrections utilisateur.
- **Fields**:
  - `scanId: String` (PK/FK)
  - `finalItems: List<String>` (required)
  - `editedByUser: Boolean` (required)
  - `validatedAt: Instant` (required)
- **Validation Rules**:
  - `finalItems` doit contenir au moins 1 entrée si validation réussie.
  - Toute entrée vide est rejetée au moment de la validation.

## Relationships

- `PhotoIngredientInput` 1 -> 1 `RecognitionEngineSelection`
- `PhotoIngredientInput` 1 -> N `ExtractedIngredientItem`
- `PhotoIngredientInput` 1 -> 1 `ScanAttemptResult`
- `PhotoIngredientInput` 1 -> 0..1 `ValidatedIngredientList`

## State Transitions

- `PhotoIngredientInput.status`:
  - `captured -> processing -> processed -> deleted`
  - `captured -> processing -> failed -> deleted`
- `ScanAttemptResult.outcome`:
  - déterminé à la fin du pipeline OCR/parsing selon confiance + complétude détectée.
