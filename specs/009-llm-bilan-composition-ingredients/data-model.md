# Data Model: Bilan composition (Gemma local)

## 1) CapturedTextRef

- **Description**: Référence au texte brut issu de l’OCR local, entrée obligatoire du pipeline Gemma.
- **Champs clés**:
  - `captureId` ou `scanId` (UUID / identifiant session aligné Room existant)
  - `rawText` (chaîne concaténée des lignes OCR)
  - `ocrOutcome` (`success` | `partial` | … — réutiliser énumération existante côté code)
  - `capturedAt` (timestamp)
- **Règles**:
  - `rawText` non vide pour lancer Gemma (sinon rester sur flux « empty » OCR sans Gemma).

## 2) GemmaModelDescriptor

- **Description**: Métadonnées du fichier modèle attendu sur l’appareil.
- **Champs clés**:
  - `expectedAssetName` ou `expectedRelativePath` (convention projet)
  - `modelVersionLabel` (ex. `gemma-2-2b-it-litert` — valeur indicative)
  - `maxContextTokens` (borne engineering pour troncature contrôlée du prompt)
- **Règles**:
  - Une seule variante **active** en MVP (documentée dans `quickstart.md`).

## 3) GemmaLoadStatus

- **Description**: Résultat de `GemmaModelLocator` + init LiteRT.
- **Valeurs**:
  - `ready` — modèle présent et runtime initialisable
  - `not_found` — fichier absent ou chemin invalide (**message erreur utilisateur type « Gemma introuvable »**)
  - `load_failed` — présent mais lecture/init impossible (corruption, ABI, mémoire)
  - `inference_unavailable` — état runtime après tentative (optionnel regroupement avec `load_failed` côté UI si message unique)
- **Règles**:
  - `not_found` | `load_failed` ⇒ **aucun** objet `CompositionBilan` marqué « complet » ; conformité FR-012.

## 4) CompositionBilan

- **Description**: Bilan affiché **à la place** du texte brut comme contenu principal.
- **Champs clés**:
  - `bilanId` (UUID)
  - `sourceScanId` (FK logique)
  - `ingredientLines` (liste ordonnée de chaînes — libellés corrigés / harmonisés)
  - `compositionAnalysis` (texte structuré en sections ou Markdown léger — rendu Compose)
  - `disclaimer` (mention informative non médicale, FR-010)
  - `generatedAt` (timestamp)
  - `promptVersion` (traçabilité régénération / A-B tests futurs)
- **Règles**:
  - `ingredientLines` ne contient pas d’éléments sans ancrage plausible dans `rawText` (validation post-Gemma, règle métier FR-003 / FR-007).
  - Présence des **deux** blocs liste + analyse pour état UI « bilan complet » (SC-002).

## 5) CompositionFlowState (orchestration UI)

- **Description**: Machine d’état du sous-flux après OCR réussi.
- **États** (exemples):
  - `ocr_done` — texte disponible, pas encore Gemma
  - `gemma_loading` — chargement modèle / warm-up
  - `gemma_inferring` — génération en cours (FR-008)
  - `bilan_ready` — `CompositionBilan` valide
  - `gemma_error` — `GemmaLoadStatus` terminal ou timeout inférence (FR-009 / FR-012)
- **Transitions**:
  - `ocr_done -> gemma_loading` (décision d’analyser)
  - `gemma_loading -> gemma_inferring | gemma_error`
  - `gemma_inferring -> bilan_ready | gemma_error`
  - `gemma_error -> ocr_done` (réessai) ou affichage texte brut secours

## Relations

- `CapturedTextRef` 1 → 0..1 `CompositionBilan` (une analyse principale par scan MVP)
- `GemmaModelDescriptor` utilisé par `GemmaLoadStatus` / analyseur ; pas d’entité persistante obligatoire hors audit

## Contraintes métier

- Texte capturé + inférence Gemma **restent sur l’appareil** (FR-011).
- Gemma absent ⇒ erreur explicite, pas de bilan simulé (FR-012 + directive plan).
