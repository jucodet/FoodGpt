# Feature Specification: Aperçu caméra réel et capture par bouton

**Feature Branch**: `007-live-camera-preview-capture`  
**Created**: 2026-04-24  
**Status**: Draft  
**Input**: User description: "A l'exécution je vois un placeholder et la prise de photo semble mockée ("sucre"). Je voudrais voir un encart affichant ce que voit l'objectif, avec un bouton pour prendre la photo"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Voir l'objectif en direct avant de capturer (Priority: P1)

En tant qu'utilisateur, je vois dans un encart dédié l'image en temps réel de ce que filme l'objectif (aperçu caméra), et non un texte ou une image statique de substitution.

**Why this priority**: Sans aperçu réel, l'utilisateur ne peut pas cadrer l'étiquette ni vérifier que la caméra fonctionne; c'est le prérequis minimal d'un scan fiable.

**Independent Test**: Ouvrir l'écran de prise de vue avec permission accordée et vérifier que l'encart affiche un flux visuel continu reflétant les mouvements devant l'objectif.

**Acceptance Scenarios**:

1. **Given** la permission d'accès à la caméra est accordée, **When** l'utilisateur ouvre l'écran de capture, **Then** un encart visible affiche l'aperçu en direct de l'objectif (mise à jour continue du champ de vision).
2. **Given** l'aperçu en direct est affiché, **When** l'utilisateur déplace l'appareil ou l'objet devant l'objectif, **Then** le contenu de l'encart reflète visuellement ce mouvement sans nécessiter de rechargement manuel de l'écran.

---

### User Story 2 - Déclencher la capture uniquement par action explicite (Priority: P1)

En tant qu'utilisateur, je déclenche la prise de photo uniquement en appuyant sur un bouton dédié (ou équivalent clairement libellé), après avoir cadré grâce à l'aperçu.

**Why this priority**: La capture volontaire évite les clichés accidentels et aligne l'expérience sur l'intention utilisateur (photo quand je suis prêt).

**Independent Test**: Depuis l'aperçu actif, appuyer une fois sur le bouton de capture et vérifier qu'une seule image est enregistrée pour cette action, correspondant au cadre visible au moment du déclenchement.

**Acceptance Scenarios**:

1. **Given** l'aperçu en direct est affiché, **When** l'utilisateur appuie sur le bouton de prise de photo, **Then** le système enregistre une image issue du flux au moment du déclenchement (et non une image ou un texte factice prédéfini comme substitut de la réalité).
2. **Given** l'aperçu en direct est affiché, **When** l'utilisateur n'appuie pas sur le bouton de capture, **Then** aucune nouvelle photo n'est enregistrée pour cette session d'aperçu.

---

### User Story 3 - Gérer l'absence de permission ou l'indisponibilité de la caméra (Priority: P2)

En tant qu'utilisateur, si la caméra n'est pas utilisable ou si j'ai refusé l'accès, je reçois un message clair et des indications pour corriger la situation, sans afficher un faux aperçu « réussi ».

**Why this priority**: Évite la frustration et les erreurs de diagnostic (croire que la caméra marche alors qu'elle est bloquée).

**Independent Test**: Refuser la permission ou simuler l'indisponibilité de la caméra et vérifier l'affichage d'un état d'erreur explicite sans encart d'aperçu factice.

**Acceptance Scenarios**:

1. **Given** l'accès à la caméra est refusé ou révoqué, **When** l'utilisateur ouvre l'écran de capture, **Then** le système affiche un message compréhensible et une voie pour réessayer (par exemple ouvrir les réglages ou redemander la permission), sans prétendre afficher un aperçu réel.
2. **Given** aucune caméra n'est disponible sur l'appareil, **When** l'utilisateur ouvre l'écran de capture, **Then** le système indique que la caméra est indisponible et ne propose pas un substitut visuel trompeur.

---

### Edge Cases

- Passage rapide entre arrière-plan et premier plan de l'application : l'aperçu reprend ou l'utilisateur est informé si la caméra est temporairement indisponible.
- Rotation ou changement de taille d'écran : l'encart reste utilisable (aperçu et bouton accessibles sans recouvrement bloquant).
- Faible luminosité : l'aperçu peut être sombre mais reste un flux réel ; aucun contenu de démonstration ne remplace le flux.
- Double appui rapide sur le bouton capture : comportement prévisible (une seule capture ou file d'attente explicite — documenté en hypothèse ci-dessous).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le système DOIT afficher un encart dédié contenant l'aperçu en direct issu de l'objectif lorsque la caméra est autorisée et disponible.
- **FR-002**: Le système NE DOIT PAS utiliser de texte, image fixe ou donnée factice pour simuler l'aperçu de l'objectif lorsque l'aperçu réel est attendu.
- **FR-003**: Le système DOIT proposer un contrôle explicite (bouton ou action équivalente clairement identifiable) pour déclencher la capture d'une photo à partir du flux affiché.
- **FR-004**: La photo capturée DOIT correspondre au contenu visible dans l'encart au moment du déclenchement (même cadrage perçu par l'utilisateur, dans les limites du matériel).
- **FR-005**: Le système DOIT indiquer clairement lorsque l'aperçu réel ne peut pas être affiché (permission refusée, caméra indisponible, erreur matérielle).
- **FR-006**: Le système DOIT permettre à l'utilisateur de réessayer ou de corriger la situation après un refus de permission, sans redémarrage obligatoire de l'application.
- **FR-007**: Lorsque la capture est déclenchée, le système DOIT conserver une trace utilisateur claire que l'action a été prise (retour visuel ou transition d'état), sans masquer une erreur de capture par un résultat fictif.

### Key Entities *(include if feature involves data)*

- **LivePreviewSession**: Représente la session d'affichage du flux caméra jusqu'à capture ou abandon.
- **CapturedFrame**: Représente l'image résultant du déclenchement explicite, associée à l'instant de capture et à l'état de l'aperçu au moment du clic.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Au moins 90 % des utilisateurs disposant d'une caméra fonctionnelle et ayant accordé la permission voient l'aperçu en direct dans l'encart en moins de 3 secondes après ouverture de l'écran de capture.
- **SC-002**: Au moins 95 % des tentatives de capture déclenchées via le bouton produisent une image perçue par l'utilisateur comme correspondant au cadre affiché (évaluation sur scénarios de test guidés).
- **SC-003**: Dans 100 % des cas de permission refusée ou de caméra indisponible, aucun faux aperçu « réussi » n'est affiché ; un message explicite est montré.
- **SC-004**: Au moins 85 % des testeurs qualifient le parcours « cadrer puis capturer » de compréhensible sans aide externe (enquête courte ou test utilisateur).

## Assumptions

- L'appareil dispose d'au moins une caméra orientée vers la scène à photographier (généralement caméra arrière pour les étiquettes produits).
- La première version peut ne pas inclure le choix entre plusieurs objectifs (grand-angle / télé) tant que l'aperçu par défaut est réel et utilisable.
- En cas d'appuis multiples rapides sur le bouton capture, le comportement par défaut est **une seule capture par appui** avec retour visuel immédiat ; les appuis supplémentaires pendant le traitement sont ignorés ou mis en file d'attente de manière explicite dans les messages utilisateur (détail laissé à la conception UX sans changer l'exigence d'absence de contenu factice).
