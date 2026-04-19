# Data Model: Démarrage caméra et scan temporaire

## Entity: ScanSession
- **Purpose**: Tracer une tentative de scan de bout en bout.
- **Fields**:
  - `session_id` (UUID, primary key)
  - `started_at` (datetime, required)
  - `finished_at` (datetime, nullable)
  - `status` (enum: `idle|capturing|analyzing|success|error|cancelled|permission_denied`)
  - `error_code` (string, nullable)
  - `analysis_duration_ms` (integer, nullable)
  - `temp_image_deleted` (boolean, required, default `false`)
  - `image_fingerprint_sha256` (string, nullable, jamais l’image brute)
- **Validation Rules**:
  - `finished_at >= started_at` si non null
  - `analysis_duration_ms >= 0` si non null
  - `temp_image_deleted = true` obligatoire quand `status` terminal (`success|error|cancelled`)

## Entity: TemporaryScanImage
- **Purpose**: Représenter le fichier image éphémère utilisé pendant le scan.
- **Fields**:
  - `temp_image_id` (UUID, primary key logique runtime)
  - `session_id` (foreign key -> `ScanSession.session_id`)
  - `cache_path` (string, required, dossier cache privé)
  - `created_at` (datetime, required)
  - `deleted_at` (datetime, nullable)
  - `byte_size` (integer, required)
- **Validation Rules**:
  - `cache_path` doit pointer vers le cache interne applicatif
  - `deleted_at` requis à la fin d’un cycle
  - suppression idempotente (double suppression sans erreur fatale)

## Entity: ScanFeedbackState (View Model State)
- **Purpose**: Etat UI visible pour guider l’utilisateur.
- **Fields**:
  - `state` (enum: `camera_ready|capturing|analyzing|success|error|permission_denied`)
  - `message` (string, nullable)
  - `can_retry` (boolean, default false)
  - `show_scan_button` (boolean, default true)
- **Validation Rules**:
  - `can_retry = true` uniquement pour `error|permission_denied`
  - `show_scan_button = false` pendant `capturing|analyzing`

## Relationships
- `ScanSession` 1---0..1 `TemporaryScanImage`
- `ScanSession` 1---N transitions de `ScanFeedbackState` (runtime, non persisté)

## State Transitions
- `camera_ready -> capturing -> analyzing -> success`
- `camera_ready -> capturing -> analyzing -> error -> camera_ready`
- `camera_ready -> permission_denied` (si refus permission)
- Toute sortie terminale DOIT déclencher suppression image temporaire avant retour `camera_ready`

