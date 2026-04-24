# Contract: Photo Capture to Text Display

## Objectif

Définir le contrat fonctionnel du flux UI "Prendre la photo -> reconnaître le texte -> afficher le résultat", en traitement local.

## Entrée (UI Action Contract)

- **Action**: `TakePhotoTapped`
- **Préconditions**:
  - Permission caméra accordée
  - Session preview active
- **Payload minimal**:
  - `sessionId`
  - `timestamp`

## Sorties attendues (Result Contract)

### 1) Success

```json
{
  "state": "success",
  "captureId": "uuid",
  "displayText": "INGRÉDIENTS : sucre, farine, ...",
  "processedAt": "2026-04-24T18:00:00Z"
}
```

### 2) Empty

```json
{
  "state": "empty",
  "captureId": "uuid",
  "message": "Aucun texte détecté",
  "processedAt": "2026-04-24T18:00:00Z"
}
```

### 3) Error

```json
{
  "state": "error",
  "captureId": "uuid",
  "message": "Impossible de lire le texte, réessayez",
  "errorReason": "engine_unavailable",
  "processedAt": "2026-04-24T18:00:00Z"
}
```

## Contraintes contractuelles

- `state` appartient strictement à `success | empty | error`.
- Aucune réponse ne contient un texte factice.
- Aucune étape de ce contrat ne requiert un envoi image vers un service distant.
- Le résultat final (`success|empty|error`) doit être émis en moins de 10 secondes en conditions normales.

## Mapping Acceptance Tests

- US1/Scenario1 -> transition `TakePhotoTapped` vers capture réelle.
- US1/Scenario2 -> `success` avec `displayText` non vide.
- US2/Scenario1 -> `empty` avec message explicite.
- US2/Scenario2 -> `error` avec message + action de relance.
