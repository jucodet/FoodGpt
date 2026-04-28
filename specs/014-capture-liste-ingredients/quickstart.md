# Quickstart: Capture liste ingredients avec espace avant `:`

## Objectif

Verifier que la detection de debut de liste ingredients fonctionne avec `ingredients:` et `ingredients :`, sans capturer les phrases introductives.

## Prerequis

- Build Android operationnel.
- Parcours OCR actif.
- Jeux d exemples OCR couvrant phrase introductive + liste.

## Parcours 1 - Eviter phrase introductive

1. Fournir un texte contenant `Les ingredients de ce produit...` puis une ligne `ingredients: ...`.
2. Lancer l extraction.
3. Verifier que le segment commence sur la ligne de liste, pas sur la phrase introductive.

## Parcours 2 - Support espace avant deux-points

1. Fournir un texte avec `ingredients : sucre, farine`.
2. Lancer l extraction.
3. Verifier que l ancre est detectee comme valide et que la liste est capturee.

## Parcours 3 - Multiples ancres valides

1. Fournir un texte avec plusieurs ancres de type `ingredients\s*:`.
2. Lancer l extraction.
3. Verifier que la premiere ancre valide est retenue.

## Parcours 4 - Aucune ancre canonique

1. Fournir un texte sans `ingredients:` ni `ingredients :`.
2. Lancer l extraction.
3. Verifier blocage explicite de l analyse et proposition de recapture/edition.

## Verification executee (2026-04-28)

- Couverture unitaire ajoutee pour phrase introductive, variante `ingredients :`, ancres multiples, et absence d ancre canonique.
- Contrat d ancrage aligne sur la regle `FIRST_CANONICAL_MATCH`.
- Verification d execution automatisée locale non completee (wrapper Gradle incomplet dans l environnement courant).
