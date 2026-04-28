# Research: Ancrage fiable de la liste ingredients

## Decision 1: Prioriser `ingredients\s*:` comme ancre canonique

- **Decision**: Considerer `ingredients:` et `ingredients :` comme formes canoniques de debut de liste.
- **Rationale**: Cette regle elimine les faux positifs lies aux phrases intro sans deux-points et couvre le cas utilisateur explicite.
- **Alternatives considered**:
  - Ancre sur simple mot `ingredients` (rejetee: trop ambigu).
  - Detection semantique de phrase de liste (rejetee: complexite inutile pour ce scope).

## Decision 2: En cas de multiples ancres valides, retenir la premiere

- **Decision**: Selectionner la premiere occurrence valide dans l ordre du texte.
- **Rationale**: Regle deterministic et facilement testable, alignée avec la clarification.
- **Alternatives considered**:
  - Retenir la derniere ancre (rejetee: risque d ignorer la liste principale).
  - Choix interactif utilisateur (rejetee: surcharge UX).

## Decision 3: Tolerer variations faibles de format

- **Decision**: Supporter variations de casse et espaces autour des deux-points.
- **Rationale**: Les resultats OCR sont heterogenes; la robustesse limite les blocages.
- **Alternatives considered**:
  - Format strict exact seulement (rejetee: fragile).
  - Support de synonymes etendus (rejetee: hors perimetre).

## Decision 4: Bloquer l analyse sans ancre exploitable

- **Decision**: Si aucune ancre valide n est trouvee, bloquer et demander recapture/edition.
- **Rationale**: Evite les analyses hors sujet et preserve la confiance utilisateur.
- **Alternatives considered**:
  - Analyser tout le texte OCR (rejetee: bruit eleve).
  - Generer un resultat partiel (rejetee: risque d interpretation trompeuse).
