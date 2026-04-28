# Data Model - Message de bienvenue souriant

## Entity: WelcomeMessage

- **Purpose**: Representer un message de bienvenue eligible a l'affichage.
- **Fields**:
  - `id` (string, unique): identifiant stable du message.
  - `text` (string): contenu du message affiche a l'utilisateur.
  - `language` (string): langue du message, `fr` pour le scope v1.
  - `toneTags` (string[]): tags de validation de ton (ex. positif, chaleureux).
  - `isActive` (boolean): message eligible ou non a la selection.

## Entity: WelcomeCatalog

- **Purpose**: Ensemble des messages valides disponibles pour la selection.
- **Fields**:
  - `messages` (WelcomeMessage[]): liste complete.
  - `version` (string, optional): version de lot pour suivi contenu.
- **Rules**:
  - un message est selectionnable si `isActive=true` et `language=fr`.
  - si aucun message selectionnable n'existe, aucun message n'est affiche.

## Entity: WelcomeDisplayEvent

- **Purpose**: Resultat d'une tentative d'affichage apres connexion reussie.
- **Fields**:
  - `userId` (string): utilisateur connecte.
  - `connectionTimestamp` (datetime): moment de connexion.
  - `displayStatus` (enum): `DISPLAYED` | `NOT_DISPLAYED_EMPTY_CATALOG`.
  - `messageId` (string, optional): renseigne si un message a ete affiche.

## State Transition

- `AfterLoginSuccess` -> `CatalogLoaded`
- `CatalogLoaded` -> `MessageDisplayed` (si au moins 1 message eligible)
- `CatalogLoaded` -> `NoMessageDisplayed` (si 0 message eligible)
