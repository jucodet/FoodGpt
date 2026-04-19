# Feature Specification: Ecran KPI de risque additifs

**Feature Branch**: `003-additive-kpi-results`  
**Created**: 2026-04-19  
**Status**: Draft  
**Input**: User description: "une fois la réponse du LLM obtenue, je veux afficher des KPI sur un écran résultat : classement des additifs avec pastille de couleur verte, orange ou rouge en fonction de leur toxicité pour l'organisme et un court détail le justifiant par exemple : [pastille rouge] Cyclométhicone : \"Perturbateur endocrinien suspecté\"."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Voir un classement lisible des additifs par niveau de risque (Priority: P1)

En tant qu’utilisateur, je veux voir un écran résultat qui classe les additifs détectés avec une
pastille couleur (verte, orange, rouge) afin d’identifier rapidement les éléments potentiellement
problématiques.

**Why this priority**: C’est la valeur principale du résultat: transformer l’analyse brute en décision
rapide et compréhensible.

**Independent Test**: Depuis une réponse LLM contenant plusieurs additifs, ouvrir l’écran résultat et
vérifier que chaque additif est classé avec une pastille et un ordre de priorité clair.

**Acceptance Scenarios**:

1. **Given** une réponse LLM contenant des additifs identifiés, **When** l’écran résultat s’affiche,
   **Then** chaque additif est visible avec une pastille verte/orange/rouge.
2. **Given** plusieurs additifs de niveaux différents, **When** le classement est affiché,
   **Then** les additifs rouges apparaissent avant les oranges, puis les verts.
3. **Given** aucun additif identifié, **When** l’écran résultat s’affiche,
   **Then** l’application affiche un état vide explicite et compréhensible.

---

### User Story 2 - Comprendre rapidement pourquoi un additif est classé ainsi (Priority: P2)

En tant qu’utilisateur, je veux une justification courte pour chaque additif afin de comprendre le
raisonnement sans lire un long texte.

**Why this priority**: Améliore la confiance et évite les interprétations erronées.

**Independent Test**: Vérifier que chaque ligne d’additif contient une justification brève et claire
du type "Perturbateur endocrinien suspecté".

**Acceptance Scenarios**:

1. **Given** un additif classé en rouge, **When** la ligne est affichée,
   **Then** une justification courte et explicite est visible.
2. **Given** un additif classé en orange ou vert, **When** la ligne est affichée,
   **Then** la justification reste concise et cohérente avec le niveau.

---

### User Story 3 - Consulter des KPI globaux pour interpréter le résultat (Priority: P3)

En tant qu’utilisateur, je veux des KPI de synthèse (nombre total, répartition par couleur, niveau
global) pour interpréter l’ensemble du produit en un coup d’oeil.

**Why this priority**: Offre une vue macro utile avant de lire les détails additif par additif.

**Independent Test**: Sur un résultat avec au moins 5 additifs, vérifier que les KPI affichent des
valeurs cohérentes avec le classement détaillé.

**Acceptance Scenarios**:

1. **Given** une liste d’additifs classés, **When** les KPI sont affichés,
   **Then** le total d’additifs correspond au nombre de lignes visibles.
2. **Given** des pastilles de plusieurs couleurs, **When** les KPI s’affichent,
   **Then** les compteurs par couleur correspondent exactement au détail.

### Edge Cases

- Réponse LLM partielle: certains additifs sans niveau explicite doivent être marqués "à confirmer".
- Additif en double dans la réponse: l’écran doit éviter les doublons non justifiés.
- Justification trop longue: le texte doit être tronqué proprement sans perdre le sens principal.
- Incohérence entre niveau et justification: signaler la ligne comme "incohérente" pour revalidation.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le système MUST parser la réponse LLM pour extraire les additifs, leur niveau de risque
  (vert/orange/rouge) et une justification courte associée.
- **FR-002**: Le système MUST afficher un écran résultat avec une ligne par additif contenant:
  nom de l’additif, pastille couleur, justification courte.
- **FR-003**: Le système MUST trier le classement par criticité décroissante: rouge, puis orange, puis
  vert.
- **FR-004**: Le système MUST afficher des KPI globaux: nombre total d’additifs, nombre de rouges,
  oranges et verts.
- **FR-005**: Le système MUST gérer les cas incomplets/incohérents (niveau manquant, justification
  absente, doublons) avec une indication claire à l’écran.
- **FR-006**: L’utilisateur MUST pouvoir consulter le détail de justification directement depuis l’écran
  résultat sans navigation complexe.

### Key Entities *(include if feature involves data)*

- **AdditiveRiskItem**: un additif classé (nom, niveau couleur, justification courte, statut de
  confiance).
- **RiskSummaryKPI**: synthèse globale (total additifs, compteurs par couleur, niveau global éventuel).
- **AnalysisDisplayResult**: résultat exploitable pour l’UI (liste ordonnée + KPI + états d’erreur).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 95% des résultats affichent correctement une pastille couleur et une justification pour
  chaque additif extrait.
- **SC-002**: 90% des utilisateurs identifient en moins de 10 secondes les 3 additifs les plus critiques.
- **SC-003**: 99% des KPI globaux affichés correspondent exactement aux données du classement détaillé.
- **SC-004**: 90% des utilisateurs jugent l’écran "clair et utile" lors d’un test de lisibilité.

## Assumptions

- La réponse LLM contient suffisamment d’information pour inférer un niveau de risque par additif.
- Le mapping couleur est fixe pour le MVP: vert = vigilance faible, orange = vigilance modérée, rouge =
  vigilance élevée.
- Le niveau affiché informe et n’a pas valeur de diagnostic médical.

