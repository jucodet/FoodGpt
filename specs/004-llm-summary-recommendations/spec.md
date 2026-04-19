# Feature Specification: Synthèse LLM et recommandations avant KPI

**Feature Branch**: `004-llm-summary-recommendations`  
**Created**: 2026-04-19  
**Status**: Draft  
**Input**: User description: "avant les KPI je veux afficher la réponse concise et synthétique du LLM sur l'analyse du produit, et ses recommandations (alternative plus saine par laquelle substituer si nécessaire)"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Lire une synthèse claire de l’analyse produit (Priority: P1)

En tant qu’utilisateur, je veux voir une synthèse courte et compréhensible de l’analyse du produit
avant les KPI détaillés, afin d’avoir rapidement une conclusion exploitable.

**Why this priority**: C’est l’information la plus immédiatement utile avant d’entrer dans le détail.

**Independent Test**: Après analyse du produit, l’écran affiche un résumé court avec conclusion globale
et niveau de vigilance compréhensible sans lire les KPI.

**Acceptance Scenarios**:

1. **Given** une analyse LLM disponible, **When** l’utilisateur arrive sur l’écran résultat,
   **Then** une synthèse concise apparaît avant tout bloc KPI.
2. **Given** une synthèse affichée, **When** l’utilisateur la lit,
   **Then** il comprend la conclusion globale en moins de 20 secondes.
3. **Given** aucune analyse disponible, **When** l’écran s’ouvre,
   **Then** un message clair indique qu’aucun résumé ne peut être affiché.

---

### User Story 2 - Voir des recommandations concrètes et actionnables (Priority: P2)

En tant qu’utilisateur, je veux des recommandations pratiques issues de l’analyse (à limiter, à surveiller,
bonnes pratiques) pour pouvoir agir immédiatement.

**Why this priority**: Transforme l’analyse en décisions simples du quotidien.

**Independent Test**: Vérifier qu’au moins 2 recommandations concrètes sont affichées quand des points
de vigilance existent.

**Acceptance Scenarios**:

1. **Given** une analyse avec points de vigilance, **When** l’écran résultat s’affiche,
   **Then** les recommandations prioritaires sont listées de façon lisible.
2. **Given** une analyse favorable, **When** les recommandations sont affichées,
   **Then** elles confirment les points positifs et les précautions minimales.

---

### User Story 3 - Obtenir une alternative plus saine quand nécessaire (Priority: P3)

En tant qu’utilisateur, je veux voir une ou plusieurs alternatives plus saines pour substituer le produit
si l’analyse le recommande.

**Why this priority**: Apporte une solution concrète, pas seulement un constat.

**Independent Test**: Pour un produit à risque modéré/élevé, l’utilisateur voit au moins une alternative
plus saine avec justification courte.

**Acceptance Scenarios**:

1. **Given** un produit jugé défavorable, **When** l’écran résultat s’affiche,
   **Then** une alternative plus saine est proposée avec une justification courte.
2. **Given** aucune alternative pertinente, **When** l’écran résultat s’affiche,
   **Then** le système l’indique explicitement et fournit des conseils de substitution génériques.

### Edge Cases

- Réponse LLM trop verbeuse: la synthèse doit rester courte et priorisée.
- Recommandations contradictoires: afficher la prudence et reformuler une recommandation cohérente.
- Alternative proposée indisponible localement: proposer une catégorie de substitution (ex: sans additif X).
- Analyse incomplète: afficher un avertissement "information partielle" sans bloquer l’écran.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le système MUST afficher une synthèse concise de l’analyse produit avant la section KPI.
- **FR-002**: La synthèse MUST contenir une conclusion globale et un niveau de vigilance compréhensible.
- **FR-003**: Le système MUST afficher des recommandations actionnables lorsque des points de vigilance
  sont identifiés.
- **FR-004**: Le système MUST proposer au moins une alternative plus saine lorsque la substitution est
  jugée nécessaire par l’analyse.
- **FR-005**: Chaque alternative proposée MUST inclure une justification courte orientée utilisateur.
- **FR-006**: Le système MUST gérer les cas où aucune alternative pertinente n’est trouvée avec un
  message explicite et des conseils génériques.

### Key Entities *(include if feature involves data)*

- **AnalysisSummary**: synthèse courte (conclusion, niveau de vigilance, points clés).
- **RecommendationItem**: recommandation actionnable (texte, priorité, justification).
- **SubstitutionOption**: alternative plus saine (nom/catégorie, raison de substitution, contexte).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 90% des utilisateurs comprennent la conclusion globale après lecture de la synthèse seule.
- **SC-002**: 90% des utilisateurs identifient au moins une recommandation actionnable en moins de
  30 secondes.
- **SC-003**: Pour les produits défavorables, au moins une alternative pertinente est affichée dans
  95% des cas.
- **SC-004**: 90% des utilisateurs jugent l’écran "clair, utile et orienté action".

## Assumptions

- La synthèse est prioritairement informative/éducative et ne remplace pas un avis médical.
- Le bloc KPI détaillé reste affiché après la synthèse et les recommandations.
- Les alternatives peuvent être exprimées en produit équivalent ou en catégorie plus saine.

