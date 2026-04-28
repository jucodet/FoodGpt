# Data Model: Utilisation API Gemma4 telephone

## Entity: AnalyseTextuelleRequest

- **Description**: Charge utile fonctionnelle envoyee au module d'analyse via API locale.
- **Fields**:
  - `requestId` (string, requis): identifiant unique de requete pour correlation technique.
  - `inputText` (string, requis): texte utilisateur a analyser.
  - `requestedAt` (datetime, requis): horodatage de creation.
  - `sourceScreen` (string, optionnel): origine du declenchement dans l'application.
- **Validation Rules**:
  - `inputText` non vide apres trim.
  - taille maximale definie par les contraintes produit existantes.
  - `requestId` unique par tentative.

## Entity: Gemma4ApiAvailability

- **Description**: Etat de disponibilite de l'API locale du telephone.
- **Fields**:
  - `isAvailable` (boolean, requis): API locale joignable et exploitable.
  - `checkedAt` (datetime, requis): moment du controle.
  - `reasonCode` (enum, optionnel): cause d'indisponibilite (`SERVICE_OFF`, `PERMISSION_DENIED`, `TIMEOUT`, `UNKNOWN`).
- **State Transitions**:
  - `unknown` -> `available` apres verification reussie.
  - `unknown` -> `unavailable` apres verification en echec.
  - `available` -> `unavailable` si echec runtime.
  - `unavailable` -> `available` apres nouvelle verification valide.

## Entity: AnalyseTextuelleResult

- **Description**: Resultat interpretable renvoye au parcours utilisateur.
- **Fields**:
  - `requestId` (string, requis): identifiant de correlation.
  - `status` (enum, requis): `SUCCESS` ou `FAILED`.
  - `outputText` (string, optionnel): resultat produit si succes.
  - `errorType` (enum, optionnel): `API_UNAVAILABLE`, `NETWORK_LOCAL`, `INVALID_RESPONSE`, `TIMEOUT`, `UNKNOWN`.
  - `userMessage` (string, requis si echec): message explicite pour l'utilisateur.
  - `completedAt` (datetime, requis): horodatage de fin.
- **Validation Rules**:
  - si `status=SUCCESS`, `outputText` requis et `errorType` absent.
  - si `status=FAILED`, `errorType` et `userMessage` requis.

## Entity: ApiCallMetric

- **Description**: Trace technique non sensible d'un appel API locale.
- **Fields**:
  - `requestId` (string, requis)
  - `outcome` (enum, requis): `SUCCESS` ou `FAILED`
  - `latencyMs` (integer, requis)
  - `errorType` (enum, optionnel)
  - `deviceClass` (enum, requis): `RECENT` ou `MIN_COMPAT`
  - `recordedAt` (datetime, requis)
- **Validation Rules**:
  - aucune donnee de contenu utilisateur (`inputText`, `outputText`) ne doit etre stockee.
  - `latencyMs` >= 0.

## Relationships

- `AnalyseTextuelleRequest` 1 -> 1 `AnalyseTextuelleResult`
- `AnalyseTextuelleRequest` 1 -> 1..n `ApiCallMetric` (tentative initiale + eventuelles tentatives techniques internes, sans fallback moteur)
- `Gemma4ApiAvailability` influence l'issue de `AnalyseTextuelleResult`
