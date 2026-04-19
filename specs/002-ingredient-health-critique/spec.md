# Feature Specification: Critique santé d’une liste d’ingrédients (prompt LLM)

**Feature Branch**: `002-ingredient-health-critique`  
**Created**: 2026-04-19  
**Status**: Draft  
**Input**: User description: "la question suivante doit être posée à un LLM : \"critique de la façon la plus objective possible cette liste d'ingrédients en fonction de son impact pour la santé, en ciblant 1 les enfants, 2 les femmes enceintes, 3 les adultes, 4 les personnes agées\""

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Obtenir une critique santé par population (Priority: P1)

En tant qu’utilisateur, je veux coller une liste d’ingrédients et obtenir une critique aussi objective
que possible de son impact potentiel sur la santé, avec une analyse séparée pour:
1) enfants, 2) femmes enceintes, 3) adultes, 4) personnes âgées.

**Why this priority**: C’est la valeur centrale: une lecture/critique adaptée à des profils différents.

**Independent Test**: Saisir une liste d’ingrédients, lancer l’analyse, puis vérifier que la réponse
contient 4 sections (une par population) et des points concrets (alertes, nuances, recommandations).

**Acceptance Scenarios**:

1. **Given** une liste d’ingrédients saisie, **When** l’utilisateur lance l’analyse,
   **Then** le système retourne une réponse structurée en 4 sections (enfants, femmes enceintes, adultes,
   personnes âgées).
2. **Given** une liste courte (3–6 ingrédients), **When** l’analyse est générée,
   **Then** chaque section contient au minimum: points de vigilance, explication, et niveau de prudence.
3. **Given** une liste vide, **When** l’utilisateur lance l’analyse,
   **Then** le système refuse avec un message clair et demande une liste d’ingrédients.

---

### User Story 2 - Obtenir une réponse “prudente” et non alarmiste (Priority: P2)

En tant qu’utilisateur, je veux que l’analyse distingue clairement faits, incertitudes et hypothèses,
et qu’elle évite les affirmations médicales définitives.

**Why this priority**: Réduit le risque de mauvaise interprétation et améliore la confiance.

**Independent Test**: Vérifier que la réponse inclut des formulations prudentes, et qu’elle ne donne
pas de diagnostic; elle doit inciter à demander un avis professionnel pour les cas à risque.

**Acceptance Scenarios**:

1. **Given** une liste contenant des additifs/termes ambigus, **When** l’analyse est générée,
   **Then** la réponse explicite les incertitudes et évite les conclusions catégoriques.
2. **Given** une population sensible (grossesse), **When** l’analyse est générée,
   **Then** la réponse inclut une recommandation de prudence et de consultation si nécessaire.

---

### User Story 3 - Réutiliser la sortie (copier/partager) (Priority: P3)

En tant qu’utilisateur, je veux pouvoir copier le prompt final (et/ou la réponse) afin de l’utiliser
ailleurs, et conserver un historique minimal de mes analyses.

**Why this priority**: Permet d’exploiter immédiatement le résultat et de garder une trace.

**Independent Test**: Lancer une analyse, puis copier le texte et retrouver la dernière analyse.

**Acceptance Scenarios**:

1. **Given** une analyse affichée, **When** l’utilisateur appuie sur “Copier”,
   **Then** le contenu sélectionné est copié.
2. **Given** une analyse terminée, **When** l’utilisateur revient plus tard,
   **Then** il peut retrouver au moins la dernière analyse (ingrédients + date + résultat).

### Edge Cases

- Ingrédients dans une autre langue ou avec fautes: la réponse doit rester structurée et demander des
  précisions si nécessaire.
- Liste très longue: la réponse doit rester lisible (résumé + détails) et ne pas ignorer des éléments
  critiques.
- Ingrédients ambigus (ex: “arômes”): signaler l’ambiguïté et l’impact sur la confiance de l’analyse.
- Demande de conseils médicaux: refuser poliment le diagnostic et recommander un professionnel.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le système MUST accepter une liste d’ingrédients en texte libre.
- **FR-002**: Le système MUST produire un **prompt** destiné à un LLM qui demande une critique la plus
  objective possible, avec 4 populations cibles (enfants, femmes enceintes, adultes, personnes âgées).
- **FR-003**: Le système MUST inclure dans le prompt une exigence de réponse structurée (4 sections) et
  des consignes de prudence (incertitudes, pas de diagnostic).
- **FR-004**: Le système MUST afficher la réponse générée et la rendre copiable.
- **FR-005**: Le système MUST gérer les entrées invalides (vide, trop courte) avec des messages clairs.
- **FR-006**: Le système MUST conserver un historique minimal (au moins la dernière analyse).

### Key Entities *(include if feature involves data)*

- **IngredientList**: texte fourni par l’utilisateur (brut + éventuellement normalisé).
- **AnalysisRequest**: requête d’analyse (date/heure, populations, prompt final).
- **AnalysisResult**: réponse du LLM (texte, structure détectée, avertissements).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 95% des analyses retournent une réponse structurée en 4 sections sans intervention
  manuelle.
- **SC-002**: 90% des utilisateurs comprennent la différence entre faits/incertitudes (mesuré via un
  test utilisateur: question de compréhension après lecture).
- **SC-003**: 99% des tentatives de copie du résultat réussissent (sans contenu vide).
- **SC-004**: 90% des utilisateurs trouvent l’analyse “claire et non alarmiste” (questionnaire post
  tâche).

## Assumptions

- L’application n’est pas un dispositif médical et ne remplace pas un avis professionnel.
- L’objectif est une aide à la lecture/éducation, pas une prescription.
- Une première version peut se limiter à un historique minimal (dernière analyse) avant d’étendre.

