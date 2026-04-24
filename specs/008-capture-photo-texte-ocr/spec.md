# Feature Specification: Capture photo et affichage du texte reconnu

**Feature Branch**: `008-capture-photo-texte-ocr`  
**Created**: 2026-04-24  
**Status**: Draft  
**Input**: User description: "quand je clique sur Prendre la photo la photo doit être prise, et le texte sur la photo doit être reconnu et affiché"

## Clarifications

### Session 2026-04-24

- Q: Où le traitement d’extraction du texte est-il effectué (appareil uniquement, service distant ou hybride) ? → A: Uniquement sur l’appareil — l’image ne quitte pas l’appareil pour l’extraction du texte.
- Q: Comment harmoniser les specs 001 / 006 / 008 sur le lieu de traitement et les délais « lecture depuis photo » ? → A: Harmonisation globale — traitement sur l’appareil pour la transcription et les extractions associées à la photo dans la vision produit documentée ; mise à jour de la spec 001 ; cible de délai alignée sur **moins de 10 secondes** (référence spec 001).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Capturer puis voir le texte de l’image (Priority: P1)

En tant qu’utilisateur sur l’écran de prise de vue, lorsque j’appuie sur l’action clairement libellée du type « Prendre la photo », le système enregistre une image correspondant à ce que je viens de cadrer, extrait le texte imprimé visible sur cette image, et m’affiche ce texte de façon lisible à l’écran.

**Why this priority**: C’est le parcours principal promis : sans capture réelle ni texte affiché, la fonctionnalité n’apporte aucune valeur.

**Independent Test**: Depuis l’écran de capture, déclencher une seule prise de photo sur une étiquette avec texte connu ; vérifier qu’une image est bien issue du déclenchement et que le texte affiché correspond en substance au texte visible sur l’étiquette (ordre de lecture cohérent).

**Acceptance Scenarios**:

1. **Given** l’utilisateur voit l’interface de capture avec un contrôle explicite du type « Prendre la photo », **When** il appuie une fois sur ce contrôle, **Then** le système enregistre une photo issue du flux de capture au moment de l’action (et non un contenu de substitution factice).
2. **Given** une photo vient d’être enregistrée et contient du texte imprimé lisible à l’œil humain, **When** le traitement de reconnaissance de texte se termine, **Then** l’utilisateur voit à l’écran le texte extrait, présenté de manière lisible (par exemple zones ou blocs de texte, ou flux continu, selon le design du produit).
3. **Given** le traitement de reconnaissance est en cours, **When** l’utilisateur attend après la capture, **Then** le système indique un état de progression ou d’attente explicite (sans laisser croire que rien ne se passe).

---

### User Story 2 - Absence de texte ou échec de reconnaissance (Priority: P2)

En tant qu’utilisateur, si l’image ne contient pas de texte exploitable ou si la reconnaissance échoue, je vois un message clair et je comprends quoi faire ensuite (réessayer, recadrer, améliorer la lumière, etc.), sans texte inventé ni silence confus.

**Why this priority**: Évite la perte de confiance lorsque le résultat est vide ou erroné sans explication.

**Independent Test**: Capturer une surface sans texte ou volontairement floue ; vérifier l’affichage d’un état d’échec ou d’absence de texte explicite, sans remplissage par du contenu fictif.

**Acceptance Scenarios**:

1. **Given** la reconnaissance ne produit aucun texte exploitable, **When** le résultat est présenté, **Then** l’utilisateur voit un message du type « aucun texte détecté » ou équivalent, et non une zone de texte vide sans explication.
2. **Given** une erreur empêche la reconnaissance (par exemple moteur local indisponible ou ressource saturée), **When** l’écran de résultat s’affiche, **Then** l’utilisateur voit une explication compréhensible et une possibilité de réessayer lorsque c’est pertinent.

---

### Edge Cases

- Double appui rapide sur « Prendre la photo » : comportement prévisible (une capture unique ou file d’attente clairement gérée ; pas de doublons silencieux confus).
- Texte très long sur l’étiquette : le résultat reste consultable (défilement ou pagination) sans tronquer silencieusement de manière trompeuse.
- Multilingue sur la même étiquette : le texte reconnu reflète ce qui est détecté ; pas d’obligation de traduction dans cette fonctionnalité.
- Faible luminosité ou flou : message ou qualité de résultat honnête plutôt qu’un texte « inventé ».
- Absence de connexion réseau : l’extraction du texte et son affichage restent possibles sur l’appareil dans la limite des capacités locales (aucune dépendance réseau pour cette étape).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le système DOIT réagir à l’action utilisateur explicitement associée à la prise de photo (par exemple le libellé « Prendre la photo » ou équivalent clairement identifié dans l’interface) en déclenchant une capture d’image à cet instant.
- **FR-002**: L’image enregistrée lors de cette action DOIT correspondre au contenu cadré au moment du déclenchement, dans les limites normales de l’appareil photo.
- **FR-003**: Le système DOIT analyser l’image capturée pour extraire automatiquement le texte imprimé visible sur la photo.
- **FR-004**: Le système DOIT afficher à l’utilisateur le texte issu de l’analyse lorsque du texte exploitable est disponible.
- **FR-005**: Le système NE DOIT PAS afficher de texte de démonstration ou factice à la place du résultat de reconnaissance lorsque l’intention est de montrer le texte réellement lu sur la photo.
- **FR-006**: Lorsque aucun texte exploitable n’est détecté ou lorsque l’analyse échoue, le système DOIT communiquer clairement l’état (message d’absence de texte ou d’erreur) et proposer une suite logique (réessayer, recadrer) lorsque applicable.
- **FR-007**: Pendant l’analyse, le système DOIT indiquer que le traitement est en cours afin que l’utilisateur ne confonde pas un blocage avec une capture ratée.
- **FR-008**: L’extraction du texte à partir de l’image capturée DOIT s’effectuer entièrement sur l’appareil ; ni l’image ni une représentation dérivée à des fins d’analyse de texte ne DOIT être transmise hors de l’appareil pour cette fonctionnalité.

### Key Entities *(include if feature involves data)*

- **PhotoCapture**: Image résultant du déclenchement explicite par l’utilisateur, associée à l’instant et au contexte d’écran de capture ; le flux « capture → lecture du texte » ne s’appuie pas sur un envoi hors appareil pour l’extraction du texte.
- **Texte extrait de la photo**: Ensemble du texte lu sur l’image et des indications utiles à l’affichage (par exemple ordre des blocs, signalisation d’incertitude côté produit), sans imposer de format technique dans cette spécification.
- **CaptureToTextFlow**: Enchaînement utilisateur depuis l’intention de photographier jusqu’à la consultation du texte affiché ou du message d’échec.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Après un déclenchement volontaire de la prise de photo, l’utilisateur perçoit un retour explicite que la capture et/ou l’analyse est en cours, puis voit soit le texte reconnu affiché, soit un message d’absence de texte ou d’erreur, le tout dans un délai perçu comme raisonnable pour une interaction courante (cible indicative : **moins de 10 secondes** dans des conditions d’usage normales, alignée sur la vision produit documentée pour le scan depuis photo), sans que l’étape d’extraction du texte exige une connexion réseau.
- **SC-002**: Sur une étiquette avec texte imprimé net et lisible à l’œil, l’utilisateur peut vérifier visuellement que le texte affiché reprend l’essentiel du contenu photographié (pas de substitution par un texte générique non issu de l’image).
- **SC-003**: Au moins 9 utilisateurs sur 10 accomplissent sans aide externe le scénario « prendre la photo → consulter le résultat textuel ou le message explicite d’échec » lors d’un test utilisateur structuré sur des cas représentatifs.

## Assumptions

- L’utilisateur dispose déjà d’un accès caméra fonctionnel pour arriver à l’écran où « Prendre la photo » est disponible ; les refus de permission sont gérés comme ailleurs dans le produit, avec messages clairs.
- « Texte sur la photo » désigne le texte imprimé sur l’objet ou l’étiquette (pas la reconnaissance d’ingrédients ou d’analyse nutritionnelle structurée, sauf si le produit fusionne ces écrans — ici le périmètre est l’affichage du texte lu sur l’image).
- Aucune obligation de correction orthographique ou de traduction : l’affichage reflète la lecture automatique du texte telle qu’obtenue à partir de l’image, dans la limite des attentes utilisateur raisonnables.
- Le produit peut compléter d’autres parcours (aperçu caméra, analyse d’ingrédients) dans des fonctionnalités distinctes ; cette spécification se concentre sur la chaîne capture → texte affiché.
- L’extraction du texte à partir de la photo est réalisée uniquement sur l’appareil (décision de clarification) : aucun envoi d’image ni de dérivé à cette fin vers un service distant pour cette fonctionnalité.
- Harmonisation documentaire (clarification 2026-04-24) : la vision produit pour les parcours « photo » (transcription / lecture de texte / identification d’ingrédients) est alignée sur le **traitement sur l’appareil** et sur la **cible de temps** « résultat affiché en moins de 10 secondes » dans des conditions normales, telle qu’exprimée pour le scan d’ingrédients de référence ; les fonctionnalités ultérieures qui introduiraient explicitement un traitement distant feront l’objet d’une spec ou d’un addendum dédié.
