# Contract - Welcome Message Display

## Purpose

Definir le contrat fonctionnel entre le flux de connexion reussie et l'affichage du message de bienvenue.

## Trigger

- Evenement entrant: `LoginSucceeded(userId, timestamp)`.
- Point d'integration application: appel `onLoginSucceeded()` dans `MainActivity` apres initialisation du `CameraViewModel`.

## Input Conditions

- Utilisateur authentifie.
- Ecran d'accueil en cours d'affichage.

## Rules

1. Charger la bibliotheque de messages pre-valides.
2. Filtrer les messages actifs en francais.
3. Si la liste filtree est vide:
   - ne rien afficher;
   - retourner statut `NOT_DISPLAYED_EMPTY_CATALOG`.
4. Sinon:
   - selectionner un message aleatoirement;
   - repetition immediate autorisee;
   - afficher le texte selectionne;
   - retourner statut `DISPLAYED` avec `messageId`.

## Output Contract

- **DisplaySuccess**
  - `status`: `DISPLAYED`
  - `messageId`: string
  - `text`: string
- **DisplaySkipped**
  - `status`: `NOT_DISPLAYED_EMPTY_CATALOG`
  - `messageId`: null
  - `text`: null

## Acceptance Mapping

- Couvre FR-001 a FR-007.
- Couvre SC-001, SC-002 et SC-004 via verification du statut et delai d'affichage.
