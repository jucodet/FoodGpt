# Quickstart: Disposition accueil MediaPipe

## Objectif

Valider rapidement que l'ecran d'accueil respecte l'ordre vertical impose, l'etat du voyant MediaPipe, et la coherence inter-specs avec message de bienvenue et capture photo.

## Prerequis

- Build Android fonctionnel.
- Appareil ou emulateur Android en mode portrait.
- Parcours d'acces a l'ecran d'accueil disponible.

## Parcours 1 - Ordre vertical nominal

1. Lancer l'application et ouvrir l'ecran d'accueil.
2. Verifier l'ordre visuel du haut vers le bas:
   - voyant MediaPipe,
   - message de bienvenue complet,
   - encart vue photo,
   - bouton de prise de photo.
3. Verifier que le bouton est juste sous l'encart photo avec espacement standard fixe.

## Parcours 2 - Etats du voyant MediaPipe

1. Ouvrir l'accueil lors d'un demarrage standard.
2. Verifier qu'un etat `Verification...` apparait tant que la detection n'est pas finalisee.
3. Verifier la transition vers:
   - `Disponible` si detection reussie,
   - `Indisponible` sinon.
4. Verifier que couleur et libelle restent coherents avec l'etat.

## Parcours 3 - Coherence inter-specs (007, 010, 012)

1. Simuler un cas de message de bienvenue long (comportement 010).
2. Verifier que le message reste complet et positionne en bloc 2.
3. Verifier que la vue photo et le bouton capture (comportement 007) restent en blocs 3 et 4.
4. Verifier qu'aucune logique metier de welcome/capture n'est alteree.

## Parcours 4 - Edge cases UI

1. Modifier la taille de police systeme (taille elevee).
2. Revenir sur l'accueil en portrait.
3. Verifier que l'ordre des blocs reste identique et lisible.
4. Simuler indisponibilite temporaire de la vue photo et verifier que la structure verticale reste stable.

## Verification executee (2026-04-28)

- Validation documentaire completee: ordre UI de reference, etat voyant, espacement et priorite inter-specs formalises.
- Verification implementation executee:
  - ajout des composants home (`MediaPipeStatusViewState`, `HomeLayoutSections`, `HomeSpacingRules`, `HomeSpecPriorityResolver`, `HomeLayoutOrderBuilder`, `MediaPipeStatusIndicator`),
  - integration du voyant MediaPipe et de l'ordre d'affichage dans `CameraScreen`,
  - ajout des tests unitaires/instrumentes de non-regression accueil.
- Verification runtime locale complete (build instrumentation) reste a executer dans un environnement Android configure.
