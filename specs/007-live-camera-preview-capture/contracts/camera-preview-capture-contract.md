# Contract: Camera Preview and Capture UI Flow

## Purpose

Définir le contrat fonctionnel interne entre UI caméra, contrôleur de preview/capture et gestion des erreurs, sans API réseau.

## Preview Start Contract

### `StartPreviewCommand`

```json
{
  "sessionId": "uuid",
  "requestedAtEpochMs": 0,
  "cameraFacing": "back",
  "permissionGranted": true
}
```

### `PreviewStateEvent`

```json
{
  "sessionId": "uuid",
  "state": "initializing|active|failed|stopped",
  "message": "string",
  "timestampEpochMs": 0
}
```

## Capture Contract

### `CaptureFrameCommand`

```json
{
  "sessionId": "uuid",
  "actionId": "uuid",
  "triggeredBy": "capture_button",
  "triggeredAtEpochMs": 0
}
```

### `CaptureFrameResult`

```json
{
  "sessionId": "uuid",
  "actionId": "uuid",
  "status": "success|ignored|failed",
  "imageUri": "file://cache/...",
  "feedbackMessage": "string",
  "errorCode": "string|null"
}
```

## Error Handling Contract

### `CameraErrorState`

```json
{
  "sessionId": "uuid",
  "errorType": "permission_denied|camera_unavailable|capture_failed",
  "userMessage": "string",
  "canRetry": true
}
```

## Invariants

- Aucun état ne doit afficher un faux aperçu si la caméra réelle n'est pas active.
- Une capture image ne peut être tentée que sur action utilisateur explicite.
- En cas d'échec preview/capture, un message utilisateur explicite doit être émis.
