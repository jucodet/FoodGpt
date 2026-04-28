# Data Model: Disposition accueil MediaPipe

## Entity: HomeLayoutSection

- **Description**: Definition d'un bloc UI de l'ecran d'accueil dans un ordre vertical de reference.
- **Fields**:
  - `sectionId` (enum, requis): `STATUS_INDICATOR`, `WELCOME_MESSAGE`, `PHOTO_PREVIEW`, `CAPTURE_BUTTON`.
  - `orderIndex` (integer, requis): position verticale attendue.
  - `isVisible` (boolean, requis): indique si le bloc est affiche.
- **Validation Rules**:
  - `orderIndex` doit etre unique pour chaque `sectionId`.
  - ordre attendu strict: 1=STATUS_INDICATOR, 2=WELCOME_MESSAGE, 3=PHOTO_PREVIEW, 4=CAPTURE_BUTTON.

## Entity: MediaPipeDetectionState

- **Description**: Etat fonctionnel de detection MediaPipe pour le voyant d'accueil.
- **Fields**:
  - `state` (enum, requis): `CHECKING`, `AVAILABLE`, `UNAVAILABLE`.
  - `label` (string, requis): `Verification...`, `Disponible`, `Indisponible`.
  - `colorToken` (string, requis): jeton de couleur associe a l'etat.
  - `updatedAt` (datetime, requis): horodatage du dernier changement.
- **Validation Rules**:
  - `label` doit correspondre exactement a la valeur autorisee de l'etat.
  - `colorToken` doit etre present pour chaque etat.

## Entity: HomeSpacingRule

- **Description**: Regle d'espacement visuel entre sections de l'accueil.
- **Fields**:
  - `betweenSections` (string, requis): identifiant de la paire de blocs (`PHOTO_PREVIEW->CAPTURE_BUTTON`).
  - `spacingType` (enum, requis): `STANDARD_FIXED`.
  - `appliesInOrientation` (enum, requis): `PORTRAIT_ONLY`.
- **Validation Rules**:
  - Une seule regle active pour `PHOTO_PREVIEW->CAPTURE_BUTTON`.
  - `spacingType` pour cette feature reste `STANDARD_FIXED`.

## Entity: HomeSpecPriorityRule

- **Description**: Regle de precedence documentaire entre specs affectant l'accueil.
- **Fields**:
  - `scope` (string, requis): `HOME_UI_ORDER`.
  - `authoritativeSpec` (string, requis): `012-home-layout-mediapipe-status`.
  - `compatibleSpecs` (list, requis): `007-live-camera-preview-capture`, `010-message-bienvenue-sourire`.
- **Validation Rules**:
  - Toute divergence d'ordre UI est resolue en faveur de `authoritativeSpec`.
  - Les specs compatibles conservent leur comportement metier tant que l'ordre UI de reference est respecte.

## Relationships

- `HomeLayoutSection` 1 -> 1 `MediaPipeDetectionState` (pour `STATUS_INDICATOR`).
- `HomeLayoutSection` 1 -> 1 `HomeSpacingRule` (pour la transition `PHOTO_PREVIEW` vers `CAPTURE_BUTTON`).
- `HomeSpecPriorityRule` govern l'application conjointe de `HomeLayoutSection` avec les specs `007` et `010`.
