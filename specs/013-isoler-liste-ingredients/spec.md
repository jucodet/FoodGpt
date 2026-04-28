# Feature Specification: Isolation de la liste d'ingredients

**Feature Branch**: `013-isoler-liste-ingredients`  
**Created**: 2026-04-28  
**Status**: Draft  
**Input**: User description: "en extension de specs/001-scan-ingredients je veux que la liste des ingrédients soit isolée du reste du texte, avant l'envoi au LLM. Il est possible d'ancrer le début du texte à l'apparition du mot 'ingrédients' et la fin au retour chariot."

## Clarifications

### Session 2026-04-28

- Q: Quelle strategie de repli appliquer si le mot ingredients est absent ? → A: Bloquer l'analyse et demander une nouvelle capture ou une edition utilisateur.
- Q: Quand valider visuellement le segment extrait par l'utilisateur ? → A: Afficher le segment extrait avant analyse et demander confirmation.
- Q: Que faire si aucun retour a la ligne n'apparait apres ingredients ? → A: Prendre tout le texte restant jusqu'a la fin.
- Q: Quelle occurrence de ingredients utiliser si plusieurs sont detectees ? → A: Utiliser systematiquement la premiere occurrence.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Isoler automatiquement la ligne ingredients (Priority: P1)

En tant qu'utilisateur, je veux que seule la partie ingredients soit envoyee pour analyse, afin d'eviter que d'autres textes d'emballage parasitent le resultat.

**Why this priority**: C'est la valeur coeur de l'extension: augmenter la pertinence de l'analyse en reduisant le bruit.

**Independent Test**: Avec un texte OCR contenant ingredients + slogans + informations marketing, verifier que la zone envoyee a l'analyse ne contient que la ligne ingredients.

**Acceptance Scenarios**:

1. **Given** un texte OCR contenant "ingredients: sucre, farine, sel" suivi d'autres lignes, **When** le systeme prepare le contenu pour l'analyse, **Then** seule la portion de texte de "ingredients" jusqu'au premier retour a la ligne est retenue.
2. **Given** un texte OCR contenant plusieurs blocs non alimentaires avant la ligne ingredients, **When** la preparation est lancee, **Then** les blocs precedents sont exclus du contenu envoye a l'analyse.

---

### User Story 2 - Conserver un comportement previsible en cas de texte incomplet (Priority: P2)

En tant qu'utilisateur, je veux que l'application gere proprement les cas ou la ligne ingredients est absente ou invalide, afin d'obtenir un resultat coherent plutot qu'une analyse erronnee.

**Why this priority**: Les photos reelles sont parfois imparfaites; cette robustesse evite des interpretations incoherentes.

**Independent Test**: Fournir un texte OCR sans le mot ingredients puis verifier que le systeme applique une strategie de repli explicite et stable.

**Acceptance Scenarios**:

1. **Given** un texte OCR sans occurrence du mot ingredients, **When** la preparation est lancee, **Then** le systeme bloque l'analyse, n'isole aucune section arbitraire et demande une nouvelle capture ou une edition utilisateur.
2. **Given** une occurrence de ingredients sans contenu exploitable avant retour a la ligne, **When** la preparation est lancee, **Then** le systeme signale un contenu ingredients non exploitable et evite l'envoi d'une analyse trompeuse.

---

### User Story 3 - Rendre l'extraction verifiable par l'utilisateur (Priority: P3)

En tant qu'utilisateur, je veux pouvoir constater que l'analyse se base bien sur la ligne ingredients extraite, afin de comprendre la logique du resultat.

**Why this priority**: La transparence renforce la confiance et facilite la correction manuelle si besoin.

**Independent Test**: Apres scan, verifier que le texte ingredients retenu est visible ou recuperable pour controle humain.

**Acceptance Scenarios**:

1. **Given** une extraction ingredients reussie, **When** le systeme prepare l'analyse, **Then** la portion retenue est affichee avant analyse et l'utilisateur doit la confirmer.

### Edge Cases

- Le mot ingredients apparait avec casse variable (INGREDIENTS, Ingrédients, ingredients).
- Le mot ingredients est suivi d'espaces, de deux-points ou d'une ponctuation avant la liste.
- Le premier retour a la ligne arrive immediatement apres ingredients (liste vide).
- Plusieurs occurrences du mot ingredients existent dans le texte OCR : la premiere occurrence est retenue comme ancrage.
- Aucun retour a la ligne n'apparait apres ingredients dans le texte OCR.
- Le texte OCR contient des caracteres parasites autour de la ligne cible.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le systeme MUST isoler le segment destine a l'analyse a partir de la premiere occurrence detectee du mot ingredients dans le texte OCR.
- **FR-002**: Le systeme MUST borner la fin du segment isole au premier retour a la ligne qui suit cette occurrence.
- **FR-002a**: Si aucun retour a la ligne n'apparait apres l'ancrage ingredients, le systeme MUST utiliser la fin du texte OCR comme borne de fin.
- **FR-003**: Le systeme MUST exclure du segment isole tout contenu situe avant l'ancrage ingredients.
- **FR-004**: Le systeme MUST exclure du segment isole tout contenu situe apres le retour a la ligne de fin.
- **FR-005**: Le systeme MUST appliquer une detection robuste du mot ingredients malgre les variations usuelles de casse et d'accentuation.
- **FR-006**: Le systeme MUST bloquer l'analyse et demander une nouvelle capture ou une edition utilisateur quand aucun ancrage ingredients n'est detecte.
- **FR-007**: Le systeme MUST eviter d'envoyer au moteur d'analyse un segment vide ou manifestement inexploitable.
- **FR-008**: Le systeme MUST permettre de verifier, dans le parcours utilisateur, la portion exacte de texte ingredients retenue pour l'analyse.
- **FR-010**: Le systeme MUST afficher le segment ingredients isole avant analyse et exiger une confirmation utilisateur explicite avant envoi.
- **FR-009**: Cette extension MUST rester compatible avec le flux fonctionnel de `001-scan-ingredients` sans modifier son objectif principal de lecture et exploitation d'une liste d'ingredients.

### Key Entities *(include if feature involves data)*

- **Texte OCR brut**: transcription complete issue de la photo, incluant ingredients et autres contenus potentiels.
- **Segment ingredients isole**: sous-portion du texte OCR delimitee par l'ancrage ingredients et le premier retour a la ligne suivant.
- **Resultat d'analyse**: interpretation produite a partir du segment ingredients isole.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Dans au moins 95% des scans contenant une ligne ingredients exploitable, seul le segment ingredients est retenu pour l'analyse.
- **SC-002**: Dans 100% des cas ou ingredients est absent, le systeme applique la strategie de repli definie sans extraction arbitraire.
- **SC-003**: Le taux de resultats juges hors sujet par les utilisateurs baisse d'au moins 30% par rapport au comportement precedent.
- **SC-004**: Au moins 90% des utilisateurs de test confirment comprendre quel texte a ete utilise pour produire le resultat.

## Assumptions

- Le texte OCR est deja disponible via le flux existant de scan avant l'etape d'analyse.
- Le terme ingredients constitue l'ancre fonctionnelle principale pour delimter la zone utile dans cette extension.
- La delimitation par premier retour a la ligne est suffisante pour la majorite des etiquettes cibles du MVP.
- Les evolutions futures (listes multi-lignes, delimitations plus complexes) sont hors perimetre de cette iteration.
