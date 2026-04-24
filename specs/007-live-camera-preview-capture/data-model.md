# Data Model - Live Camera Preview Capture

## Entity: LivePreviewSession

- **Description**: Session d'affichage du flux caméra pour un utilisateur, de l'ouverture de l'écran jusqu'à la fin de capture ou l'abandon.
- **Fields**:
  - `sessionId: String` (UUID, required)
  - `startedAt: Instant` (required)
  - `endedAt: Instant?` (optional)
  - `permissionState: Enum(granted, denied, unavailable)` (required)
  - `previewState: Enum(initializing, active, failed, stopped)` (required)
  - `failureReason: String?` (optional)
- **Validation Rules**:
  - `previewState=active` requiert `permissionState=granted`.
  - `failureReason` requis si `previewState=failed`.

## Entity: CaptureAction

- **Description**: Action explicite utilisateur déclenchant une tentative de capture depuis l'aperçu actif.
- **Fields**:
  - `actionId: String` (UUID, required)
  - `sessionId: String` (FK -> LivePreviewSession.sessionId)
  - `triggeredAt: Instant` (required)
  - `sourceControl: String` (required, ex: capture_button)
  - `status: Enum(accepted, ignored, failed)` (required)
  - `ignoreReason: String?` (optional)
- **Validation Rules**:
  - Une `CaptureAction` ne peut être `accepted` que si `previewState=active` au moment du clic.
  - `ignoreReason` requis si `status=ignored`.

## Entity: CapturedFrame

- **Description**: Résultat image d'une capture acceptée.
- **Fields**:
  - `frameId: String` (UUID, required)
  - `actionId: String` (FK -> CaptureAction.actionId)
  - `capturedAt: Instant` (required)
  - `imageUri: String` (required)
  - `matchesPreviewIntent: Boolean` (required)
  - `captureErrorCode: String?` (optional)
- **Validation Rules**:
  - `imageUri` requis si aucune erreur de capture.
  - `captureErrorCode` requis si l'écriture image échoue.

## Relationships

- `LivePreviewSession` 1 -> N `CaptureAction`
- `CaptureAction` 1 -> 0..1 `CapturedFrame`

## State Transitions

- `LivePreviewSession.previewState`:
  - `initializing -> active -> stopped`
  - `initializing -> failed`
  - `active -> failed`
- `CaptureAction.status`:
  - `accepted -> (CapturedFrame créé)`
  - `ignored` (double clic ou capture non autorisée)
  - `failed` (erreur runtime lors du déclenchement)
