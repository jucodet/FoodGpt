# Feature Specification: Disposition accueil MediaPipe

**Feature Branch**: `[012-home-layout-mediapipe-status]`  
**Created**: 2026-04-28  
**Status**: Draft  
**Input**: User description: "sur l'ecran d'accueil, je veux une disposition avec verticalement : en haut un voyant de couleur indiquant si MediaPipe LLM Inference API est detecte sur le device, puis le message de bienvenue complet, puis l'encart avec la vue photo, puis le bouton de prise de photo juste sous l'encart"

## Clarifications

### Session 2026-04-28

- Q: Quel format exact pour indiquer l'etat de detection MediaPipe ? → A: Voyant couleur + libelle texte (Disponible/Indisponible).
- Q: Faut-il un etat intermediaire pendant la detection initiale ? → A: Oui, ajouter un etat "Verification..." avant le resultat final.
- Q: Quel espacement entre encart photo et bouton ? → A: Espacement standard fixe et constant.
- Q: Quel perimetre d'orientation couvrir dans cette feature ? → A: Mode portrait uniquement.
- Q: Quelle priorite inter-specs pour l'ecran d'accueil entre 007, 010 et 012 ? → A: 012 definit l'ordre UI de reference; 010 et 007 conservent leur comportement metier en respectant cet ordre.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Voir l'etat MediaPipe en tete (Priority: P1)

En tant qu'utilisateur, je veux voir en haut de l'ecran d'accueil un voyant de couleur indiquant si MediaPipe LLM Inference API est detecte sur mon appareil, afin de savoir immediatement si la fonctionnalite d'inference locale est disponible.

**Why this priority**: Cette information conditionne la confiance et la comprehension de l'etat du systeme des l'ouverture de l'ecran.

**Independent Test**: Peut etre testee en ouvrant l'ecran d'accueil sur un appareil avec API detectee puis sur un appareil sans API detectee, et en verifiant que le voyant apparait en premiere position avec la bonne couleur dans chaque cas.

**Acceptance Scenarios**:

1. **Given** l'ecran d'accueil est affiche et l'API MediaPipe est detectee, **When** l'utilisateur consulte le haut de la page, **Then** un voyant vert apparait en premier element visuel.
2. **Given** l'ecran d'accueil est affiche et l'API MediaPipe n'est pas detectee, **When** l'utilisateur consulte le haut de la page, **Then** un voyant non vert (etat indisponible) apparait en premier element visuel.

---

### User Story 2 - Conserver un ordre vertical explicite (Priority: P2)

En tant qu'utilisateur, je veux que les elements de l'accueil suivent un ordre vertical stable (voyant, message de bienvenue complet, encart photo, bouton de prise de photo) afin de retrouver instantanement les zones importantes.

**Why this priority**: Un ordre fixe reduit les erreurs de navigation et clarifie le parcours principal de capture.

**Independent Test**: Peut etre testee en affichant l'accueil et en validant visuellement l'ordre des quatre blocs du haut vers le bas, sans recouvrement ni inversion.

**Acceptance Scenarios**:

1. **Given** l'ecran d'accueil est rendu, **When** l'utilisateur parcourt l'ecran de haut en bas, **Then** il voit le voyant en premier, ensuite le message de bienvenue complet, ensuite l'encart avec la vue photo.
2. **Given** l'encart photo est visible, **When** l'utilisateur regarde juste en dessous, **Then** le bouton de prise de photo est place immediatement sous cet encart.

---

### User Story 3 - Lancer rapidement la prise de photo (Priority: P3)

En tant qu'utilisateur, je veux acceder au bouton de prise de photo juste sous l'encart de vue photo, afin de declencher la capture sans chercher l'action principale.

**Why this priority**: La proximite entre la vue photo et l'action de capture fluidifie le parcours et diminue les erreurs de manipulation.

**Independent Test**: Peut etre testee en ouvrant l'accueil, en localisant l'encart photo puis en verifiant que le bouton de capture est directement en dessous et actionnable.

**Acceptance Scenarios**:

1. **Given** l'encart photo est affiche, **When** l'utilisateur veut capturer une image, **Then** il trouve le bouton de prise de photo immediatement sous l'encart et peut l'activer.

---

### Edge Cases

- Le statut de detection MediaPipe change pendant que l'utilisateur est deja sur l'accueil.
- Le message de bienvenue est plus long que la hauteur standard et occupe plusieurs lignes.
- L'encart photo est indisponible temporairement (permissions ou initialisation) mais la structure verticale doit rester lisible.
- L'utilisateur utilise un petit ecran ou une grande taille de police, l'ordre logique des blocs doit rester identique.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le systeme MUST afficher un voyant de statut de detection MediaPipe LLM Inference API en premier element en haut de l'ecran d'accueil.
- **FR-002**: Le systeme MUST faire varier la couleur du voyant selon l'etat de detection (disponible vs indisponible).
- **FR-002a**: Le systeme MUST afficher un libelle texte associe au voyant avec les valeurs "Disponible" ou "Indisponible" selon l'etat detecte.
- **FR-002b**: Le systeme MUST afficher un etat intermediaire "Verification..." avec une couleur neutre tant que la detection initiale n'est pas terminee.
- **FR-003**: Le systeme MUST afficher le message de bienvenue complet juste sous le voyant de statut.
- **FR-004**: Le systeme MUST afficher l'encart contenant la vue photo juste sous le message de bienvenue.
- **FR-005**: Le systeme MUST afficher le bouton de prise de photo immediatement sous l'encart de vue photo.
- **FR-005a**: Le systeme MUST appliquer un espacement visuel standard fixe et constant entre l'encart de vue photo et le bouton de prise de photo.
- **FR-006**: Les utilisateurs MUST pouvoir identifier visuellement l'ordre vertical complet des elements sans ambiguite sur l'ecran d'accueil.
- **FR-007**: Le systeme MUST conserver cet ordre vertical sur les appareils pris en charge et pour les configurations d'affichage courantes (orientation portrait standard, tailles de texte usuelles).
- **FR-008**: Le systeme MUST limiter le perimetre de cette fonctionnalite au mode portrait ; le mode paysage est hors scope pour cette iteration.
- **FR-009**: Le systeme MUST appliquer l'ordre UI de reference defini par cette specification pour l'ecran d'accueil, tout en conservant les comportements metier valides des specifications anterieures sur le message de bienvenue et la capture photo.

### Key Entities *(include if feature involves data)*

- **Etat de detection MediaPipe**: Etat binaire ou equivalent representant la disponibilite de l'API MediaPipe LLM Inference sur l'appareil.
- **Voyant de statut**: Element visuel de couleur place en tete d'ecran, derive de l'etat de detection.
- **Message de bienvenue**: Bloc textuel complet qui introduit l'experience utilisateur sur l'accueil.
- **Encart vue photo**: Zone d'aperçu photo presente avant l'action de capture.
- **Bouton de prise de photo**: Action primaire de capture positionnee directement sous l'encart photo.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Dans 100% des ouvertures de l'ecran d'accueil, le voyant de statut apparait en premiere position verticale.
- **SC-002**: Dans 100% des cas de test de detection disponible vs indisponible, la couleur du voyant correspond a l'etat attendu.
- **SC-002a**: Dans 100% des cas de test de detection disponible vs indisponible, le libelle texte du voyant correspond a l'etat attendu.
- **SC-002b**: Dans 100% des ouvertures d'ecran ou la detection n'est pas immediate, l'etat "Verification..." est affiche avant la transition vers l'etat final.
- **SC-003**: Au moins 95% des utilisateurs testeurs identifient correctement l'ordre des 4 blocs (voyant, bienvenue, vue photo, bouton) en moins de 5 secondes lors d'un test de comprehension.
- **SC-004**: Dans au moins 95% des sessions de test, les utilisateurs trouvent le bouton de prise de photo sans hesitation (moins de 3 secondes) apres avoir repere l'encart photo.
- **SC-005**: Dans 100% des captures d'ecran de reference sur appareils cibles, l'espacement entre encart photo et bouton reste constant et conforme au standard defini.
- **SC-006**: Dans 100% des tests de validation de cette feature, l'ordre et la disposition exiges sont verifies en mode portrait uniquement.

## Assumptions

- L'ecran d'accueil cible est la vue principale en orientation portrait.
- Le mode paysage est explicitement hors perimetre de cette iteration.
- La signification des couleurs du voyant (etat disponible/non disponible) est deja comprise par les utilisateurs cibles ou coherente avec les conventions de l'application.
- Le message de bienvenue complet est deja defini fonctionnellement et doit etre conserve sans reduction de contenu.
- La fonctionnalite demandee modifie la disposition et la lisibilite de l'accueil, sans changer le comportement metier de la prise de photo.
- En cas de divergence documentaire entre specs d'accueil, la presente specification fait autorite sur l'ordre d'affichage des blocs UI.
