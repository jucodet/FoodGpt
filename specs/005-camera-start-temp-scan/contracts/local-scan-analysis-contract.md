# Contract: Local Scan Analysis Pipeline

## Purpose
Définir le contrat entre la couche caméra, la couche d’analyse locale AI Edge et la persistance SQLite.

## Input Contract (`StartScanRequest`)
```json
{
  "sessionId": "uuid",
  "capturedAt": "ISO-8601 datetime",
  "tempImagePath": "internal-cache-path",
  "languageHint": "fr",
  "maxProcessingMs": 10000
}
```

### Input Rules
- `tempImagePath` DOIT référencer un chemin de cache interne privé.
- Le fichier image DOIT exister au moment de l’appel.
- Aucune URL distante n’est autorisée.

## Output Contract (`StartScanResponse`)
```json
{
  "sessionId": "uuid",
  "status": "success|error",
  "transcriptText": "string|null",
  "errorCode": "string|null",
  "analyzedAt": "ISO-8601 datetime",
  "processingMs": 1250,
  "tempImageDeleted": true
}
```

### Output Rules
- `tempImageDeleted` DOIT être `true` pour tout statut terminal.
- `transcriptText` requis si `status=success`.
- `errorCode` requis si `status=error`.

## Persistence Contract (`ScanSessionRecord`)
```json
{
  "sessionId": "uuid",
  "status": "capturing|analyzing|success|error|cancelled|permission_denied",
  "startedAt": "ISO-8601 datetime",
  "finishedAt": "ISO-8601 datetime|null",
  "processingMs": 1250,
  "tempImageDeleted": true,
  "imageFingerprintSha256": "hex|null"
}
```

### Persistence Rules
- Ne jamais stocker l’image brute en base.
- Ne stocker que métadonnées, statuts et empreinte éventuelle.
- Maintenir cohérence transactionnelle entre `status` final et `tempImageDeleted=true`.

