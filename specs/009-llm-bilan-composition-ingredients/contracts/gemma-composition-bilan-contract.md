# Contract: Texte capturé → Bilan Gemma (local)

## Objectif

Définir le contrat du flux **après OCR** : inférence **Gemma** locale (LiteRT) pour produire un **bilan** (liste ingrédients + analyse composition) ou une **erreur** si Gemma n’est pas disponible.

## Préconditions

- `rawText` non vide (sinon rester sur le contrat capture→texte feature 008, état `empty`).
- Aucune étape de ce contrat n’envoie `rawText` ni le bilan vers un service distant.

## Entrée (Command Contract)

- **Command**: `AnalyzeCompositionWithGemma`
- **Payload**:
  - `scanId` (string UUID)
  - `rawText` (string)
  - `languageHint` (optionnel, défaut `fr`)
  - `maxInferenceMs` (long, aligné SC-003 / config produit)

## Sorties attendues (Result Contract)

### 1) Bilan prêt

```json
{
  "state": "bilan_ready",
  "scanId": "uuid",
  "ingredientLines": ["eau", "sucre", "..."],
  "compositionAnalysis": "… texte ou sections markdown contrôlé …",
  "disclaimer": "Information indicative, ne remplace pas un avis médical.",
  "processedAt": "2026-04-24T18:00:00Z"
}
```

### 2) Gemma introuvable / non chargé

```json
{
  "state": "gemma_error",
  "scanId": "uuid",
  "errorCode": "gemma_not_found",
  "message": "Gemma introuvable sur cet appareil. Vérifiez l’installation du modèle.",
  "processedAt": "2026-04-24T18:00:00Z"
}
```

> `errorCode` peut aussi valoir `gemma_load_failed` / `gemma_timeout` selon diagnostic ; l’UI **doit** afficher `message` utilisateur et proposer **réessai** / **repli texte brut** si prévu.

### 3) Texte non analysable (métier, pas technique)

```json
{
  "state": "composition_limit",
  "scanId": "uuid",
  "message": "Impossible d’extraire une liste d’ingrédients fiable à partir du texte.",
  "processedAt": "2026-04-24T18:00:00Z"
}
```

## Contraintes contractuelles

- `state` ∈ `{ bilan_ready, gemma_error, composition_limit }` pour ce contrat (orthogonal à `success|empty|error` OCR).
- `state=bilan_ready` ⇒ `ingredientLines` et `compositionAnalysis` tous deux **non vides** après validation parseur ; sinon traiter comme `gemma_error` ou `composition_limit` (pas de bilan vide « complet » — FR-012).
- Aucune réponse `bilan_ready` sans exécution Gemma locale réussie.
- `errorCode=gemma_not_found` lorsque le fichier modèle attendu est absent ou chemin résolu inexistant.

## Mapping Acceptance Tests (spec 009)

- US1 / liste + analyse ⇒ `bilan_ready` avec deux sections UI.
- US2 / extraction impossible ⇒ `composition_limit`.
- US3 / Gemma absent ⇒ `gemma_error` + `gemma_not_found` (ou `gemma_load_failed`).
- FR-011 / FR-012 ⇒ pas de `bilan_ready` si Gemma non exécuté avec succès.
