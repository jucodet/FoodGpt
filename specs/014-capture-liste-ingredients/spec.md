# Feature Specification: Demarrage capture sur ingredients

**Feature Branch**: `014-capture-liste-ingredients`  
**Created**: 2026-04-28  
**Status**: Draft  
**Input**: User description: "en extension de specs/009-llm-bilan-composition-ingredients, le mot ingredients est intercepté avant la vraie liste a cause d une phrase de contexte (ex: Les ingredients de ce produit sont issus...). Je veux que la capture demarre a la liste des ingredients, en ciblant idealement la chaine ingredients:."

## Clarifications

### Session 2026-04-28

- Q: Quelle regle appliquer s il existe plusieurs ancres valides `ingredients\\s*:` ? → A: Prendre la premiere ancre valide dans l ordre du texte.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Capturer la vraie liste ingredients (Priority: P1)

En tant qu utilisateur, je veux que l extraction demarre sur la vraie liste ingredients et non sur une phrase de presentation du produit, pour obtenir un bilan fiable.

**Why this priority**: Sans ce filtrage, la base envoyee a l analyse est faussee et degrade toute la qualite du resultat.

**Independent Test**: Fournir un texte contenant une phrase "Les ingredients de ce produit..." puis une ligne "ingredients: ..."; verifier que l extraction retient la ligne de liste.

**Acceptance Scenarios**:

1. **Given** un texte avec une phrase introductive contenant "ingredients" et plus bas une chaine "ingredients:", **When** l extraction est lancee, **Then** le debut du segment est ancre sur la chaine "ingredients:" de la liste et ignore la phrase introductive.
2. **Given** un texte avec plusieurs occurrences du mot ingredients, **When** au moins une occurrence est au format liste "ingredients:", **Then** le systeme retient en priorite l occurrence de type liste.
3. **Given** un texte avec plusieurs ancres valides de type "ingredients:" pour des sections distinctes, **When** l extraction est lancee, **Then** la premiere ancre valide dans l ordre du texte est retenue.

---

### User Story 2 - Gerer les variantes de format de liste (Priority: P2)

En tant qu utilisateur, je veux que l extraction reste robuste quand le format varie legerement, afin d eviter des echecs inutiles.

**Why this priority**: Les etiquettes reellement scannees sont heterogenes; un comportement trop strict provoquerait des blocages frequents.

**Independent Test**: Tester des variantes comme "Ingredients :", "INGREDIENTS:", et "ingredients :" avec espaces; verifier que la liste est bien captee.

**Acceptance Scenarios**:

1. **Given** une liste introduite par une variante de casse ou d espacement de "ingredients:", **When** l extraction est lancee, **Then** la liste est detectee comme ancre valide.
2. **Given** une occurrence "ingredients" sans deux-points et une occurrence "ingredients:" ailleurs, **When** l extraction est lancee, **Then** l ancre de type "ingredients:" est privilegiee.

---

### User Story 3 - Eviter les analyses trompeuses si liste introuvable (Priority: P3)

En tant qu utilisateur, je veux un comportement explicite quand aucune ancre de liste credible n est trouvee, afin d eviter une analyse hors sujet.

**Why this priority**: La transparence est preferable a une interpretation incorrecte.

**Independent Test**: Fournir un texte ne contenant que des phrases descriptives sans liste ingredients exploitable; verifier blocage explicite.

**Acceptance Scenarios**:

1. **Given** un texte sans ancre de liste ingredients exploitable, **When** l extraction est lancee, **Then** le systeme bloque l analyse et demande une nouvelle capture ou une correction manuelle.

### Edge Cases

- Le texte contient "ingredients" dans une phrase marketing avant la vraie liste.
- Le separateur est "ingredients :" avec espace avant ou apres le deux-points.
- Le texte contient des erreurs OCR mineures autour de "ingredients:" (ponctuation parasite).
- Plusieurs listes potentielles existent (ingredients, allergenes, conseils); seule la liste ingredients doit etre retenue, en prenant la premiere ancre valide.
- Aucun deux-points n est detecte apres ingredients.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le systeme MUST prioriser comme ancre de debut la chaine de type "ingredients:" correspondant au debut de la liste.
- **FR-002**: Le systeme MUST ignorer les occurrences du mot ingredients integrees dans des phrases descriptives non listees quand une ancre "ingredients:" est presente.
- **FR-003**: Le systeme MUST supporter les variantes usuelles de casse et d espacement autour de "ingredients:".
- **FR-004**: Le systeme MUST conserver un comportement deterministic quand plusieurs ancres valides existent.
- **FR-005**: Le systeme MUST, en cas d ancres valides multiples, retenir la premiere ancre valide dans l ordre du texte et appliquer cette regle de facon constante.
- **FR-006**: Le systeme MUST bloquer l analyse et proposer une action de recuperation lorsqu aucune ancre de liste exploitable n est trouvee.
- **FR-007**: Le systeme MUST prevenir l envoi a l analyse d un segment provenant d une phrase introductive hors liste.
- **FR-008**: Cette fonctionnalite MUST rester compatible avec le bilan ingredients/composition de `009-llm-bilan-composition-ingredients`.

### Key Entities *(include if feature involves data)*

- **Texte OCR brut**: transcription complete capturee depuis la photo du produit.
- **Ancre de liste ingredients**: motif de demarrage identifie comme debut de liste (forme cible "ingredients:").
- **Segment ingredients retenu**: portion du texte OCR envoyee a l analyse apres application des regles d ancrage.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Sur un jeu de tests contenant phrase introductive + vraie liste, au moins 95% des extractions demarrent sur la liste ingredients et non sur la phrase introductive.
- **SC-002**: Sur les variantes de format "ingredients:" (casse/espaces), au moins 95% des cas sont correctement ancres.
- **SC-003**: Le taux de bilans juges hors sujet dus a un mauvais point de depart baisse d au moins 50% par rapport au comportement precedent.
- **SC-004**: Dans 100% des cas sans ancre exploitable, le systeme bloque l analyse et affiche une action de recuperation explicite.

## Assumptions

- Le flux OCR existant fournit deja un texte brut avant etape d analyse.
- La forme "ingredients:" est l indicateur principal le plus fiable pour localiser le debut de liste dans ce perimetre.
- Les variantes faibles de format (casse, espaces, ponctuation simple) doivent etre prises en charge sans etendre le scope aux synonymes metier.
- Le comportement de fin de segment reste gere par les regles de delimitation deja definies dans les specs precedentes.
