# Feature Specification: Lecture d’ingrédients depuis une photo

**Feature Branch**: `001-scan-ingredients`  
**Created**: 2026-04-19  
**Status**: Draft  
**Input**: User description: "je veux créer une application qui lit les ingrédients d'une liste prise en photo par mon smartphone"

## Clarifications

### Session 2026-04-24

- Q: Harmonisation globale des specs (lieu de traitement de la transcription, cohérence avec les parcours photo 006 / 008) ? → A: Transcription et lecture depuis photo **sur l’appareil** pour la vision MVP documentée ; suppression de l’ambiguïté « traitement en ligne » dans les cas limites ; alignement maintenu sur la cible **moins de 10 secondes** jusqu’au résultat affiché (SC-002).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Scanner une liste d’ingrédients et obtenir du texte éditable (Priority: P1)

En tant qu’utilisateur, je veux prendre en photo une liste d’ingrédients (étiquette, recette imprimée)
pour obtenir une transcription texte fiable que je peux corriger, afin de la réutiliser (copier/partager,
analyser, etc.).

**Why this priority**: C’est la valeur principale: transformer une photo en liste d’ingrédients utilisable.

**Independent Test**: Sur un smartphone, prendre une photo d’une étiquette, lancer la lecture, obtenir
un résultat texte affiché et éditable, puis enregistrer le résultat.

**Acceptance Scenarios**:

1. **Given** l’utilisateur est sur l’écran de scan, **When** il prend une photo nette d’une liste
   d’ingrédients, **Then** l’application affiche une transcription texte en moins de 10 secondes.
2. **Given** une transcription affichée, **When** l’utilisateur corrige un mot et enregistre,
   **Then** la version corrigée est conservée et rechargée à l’ouverture suivante.
3. **Given** une photo floue ou illisible, **When** l’utilisateur lance la lecture,
   **Then** l’application indique clairement l’échec et propose de reprendre la photo.

---

### User Story 2 - Structurer la transcription en ingrédients individuels (Priority: P2)

En tant qu’utilisateur, je veux que la liste transcrite soit découpée en éléments (ingrédients) afin de
relire et corriger plus facilement, et d’exploiter la liste plus tard (filtrer, rechercher, etc.).

**Why this priority**: Une liste structurée améliore fortement l’UX (correction, lecture) et prépare les
évolutions (allergènes, nutrition).

**Independent Test**: À partir d’une transcription existante, l’application propose une liste
d’éléments séparés; l’utilisateur peut fusionner/séparer/modifier un élément et enregistrer.

**Acceptance Scenarios**:

1. **Given** une transcription contenant une liste séparée par virgules/points-virgules,
   **When** l’application structure le texte, **Then** l’utilisateur voit des ingrédients séparés.
2. **Given** un découpage incorrect, **When** l’utilisateur fusionne deux éléments ou en sépare un,
   **Then** la liste structurée enregistrée reflète exactement ses modifications.

---

### User Story 3 - Partager / copier le résultat (Priority: P3)

En tant qu’utilisateur, je veux pouvoir copier ou partager la transcription (texte et/ou liste
structurée) pour la coller dans une note, l’envoyer, ou l’utiliser dans une autre appli.

**Why this priority**: Permet d’exploiter immédiatement le résultat sans dépendre d’autres features.

**Independent Test**: Sur un résultat enregistré, l’utilisateur copie le texte; il peut aussi partager
via les options du téléphone.

**Acceptance Scenarios**:

1. **Given** un résultat enregistré, **When** l’utilisateur appuie sur “Copier”,
   **Then** le texte est disponible dans le presse-papiers.
2. **Given** un résultat enregistré, **When** l’utilisateur appuie sur “Partager”,
   **Then** le contenu est proposé aux apps disponibles (messages, mail, notes…).

### Edge Cases

- Photo prise de travers / perspective forte: l’application doit proposer de recadrer ou recommander
  de reprendre.
- Texte avec retours à la ligne, colonnes, ou séparateurs variés (virgules, points-virgules, puces).
- Langues/caractères spéciaux (accents), mélange majuscules/minuscules.
- Ressources locales insuffisantes, saturation du traitement sur l’appareil ou indisponibilité du moteur de lecture : message clair et possibilité de réessayer (la transcription ne dépend pas de la connectivité réseau pour le MVP documenté).
- Données sensibles accidentelles dans la photo (ex: nom/adresse): éviter toute exposition involontaire
  lors du partage (prévisualisation).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: L’application MUST permettre de capturer une photo depuis le smartphone (caméra) et/ou de
  sélectionner une photo existante.
- **FR-002**: L’application MUST produire une transcription texte de la liste d’ingrédients à partir
  de la photo.
- **FR-003**: L’utilisateur MUST pouvoir éditer la transcription avant enregistrement.
- **FR-004**: L’application MUST enregistrer localement au moins le dernier résultat (photo + texte +
  date) et permettre de le rouvrir.
- **FR-005**: L’application MUST afficher des erreurs compréhensibles et des actions de récupération
  (reprendre photo, réessayer, annuler).
- **FR-006**: L’application MUST proposer une liste structurée d’ingrédients (découpage) à partir de
  la transcription et permettre de corriger la structure.
- **FR-007**: L’application MUST permettre de copier et partager le résultat (texte et/ou liste
  structurée).
- **FR-008**: Pour le périmètre MVP documenté, la transcription texte de la liste d’ingrédients à partir
  de la photo MUST s’effectuer sur l’appareil ; l’application NE DOIT PAS exiger l’envoi de l’image ni
  d’une représentation dérivée à des fins de transcription vers un service distant pour produire ce
  résultat.

### Key Entities *(include if feature involves data)*

- **Scan**: une session de lecture (photo source, statut, date/heure, langue supposée).
- **Transcription**: le texte reconnu + éventuelles versions (brut vs corrigé).
- **IngredientItem**: un élément d’ingrédient (texte, ordre, annotations éventuelles).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Sur une photo nette, 90% des scans aboutissent à une transcription exploitable sans
  abandon.
- **SC-002**: Le temps entre “Lancer la lecture” et “Résultat affiché” est inférieur à 10 secondes dans
  95% des cas (conditions normales).
- **SC-003**: 90% des utilisateurs peuvent corriger et enregistrer un résultat sans aide lors d’un
  premier essai.
- **SC-004**: Le partage/copie réussit dans 99% des tentatives (sans crash ni contenu vide).

## Assumptions

- Les utilisateurs disposent d’un smartphone avec appareil photo et autorisations caméra/galerie.
- La première version cible une lecture “ingrédients” générique sans extraction nutritionnelle ni
  détection d’allergènes.
- Le stockage local suffit pour le MVP (pas de compte utilisateur requis).
- Les photos sont principalement des listes horizontales “classiques” (étiquettes, emballages).
- La lecture depuis photo (transcription) est traitée sur l’appareil pour le MVP, en cohérence avec les
  specs associées aux parcours photo (affichage du texte reconnu, identification d’ingrédients) ; toute
  évolution prévoyant un traitement distant fera l’objet d’une décision produit et d’une mise à jour
  explicite de cette spec.

