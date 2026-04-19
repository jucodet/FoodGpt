# Feature Specification: Démarrage caméra et scan temporaire

**Feature Branch**: `005-camera-start-temp-scan`  
**Created**: 2026-04-19  
**Status**: Draft  
**Input**: User description: "au démarrage de l'application, j'arrive en mode appareil photo et un bouton de scan. Quand j'appuie sur le bouton, la photo temporaire (ne doit pas être stockée en permanence dans l'appareil) doit être analysée"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Arriver directement en mode caméra au lancement (Priority: P1)

En tant qu’utilisateur, je veux arriver immédiatement sur l’écran caméra au démarrage de l’application,
avec un bouton de scan clairement visible, pour lancer l’analyse rapidement.

**Why this priority**: C’est le point d’entrée principal et la base de l’expérience utilisateur.

**Independent Test**: Fermer puis relancer l’application; vérifier que l’écran caméra est affiché en
premier avec le bouton de scan disponible sans navigation supplémentaire.

**Acceptance Scenarios**:

1. **Given** l’utilisateur ouvre l’application, **When** l’écran initial s’affiche,
   **Then** la vue caméra est active avec un bouton de scan visible.
2. **Given** l’autorisation caméra n’est pas accordée, **When** l’application démarre,
   **Then** l’utilisateur voit une demande d’autorisation et une alternative claire en cas de refus.

---

### User Story 2 - Lancer l’analyse via une photo temporaire (Priority: P2)

En tant qu’utilisateur, je veux que la photo capturée pour le scan soit utilisée uniquement pour
l’analyse en cours, sans stockage permanent sur l’appareil.

**Why this priority**: Répond à l’exigence de confidentialité et limite la persistance de données.

**Independent Test**: Capturer une photo via le bouton scan, vérifier que l’analyse démarre, puis
confirmer qu’aucun média permanent n’est conservé après traitement.

**Acceptance Scenarios**:

1. **Given** la caméra est active, **When** l’utilisateur appuie sur le bouton scan,
   **Then** une photo temporaire est capturée et envoyée pour analyse.
2. **Given** l’analyse est terminée ou annulée, **When** le cycle de scan se termine,
   **Then** la photo temporaire est supprimée et non stockée durablement.
3. **Given** une erreur d’analyse, **When** le processus échoue,
   **Then** la photo temporaire est également supprimée et un message de réessai est affiché.

---

### User Story 3 - Comprendre l’état du scan pendant l’analyse (Priority: P3)

En tant qu’utilisateur, je veux un retour visuel clair pendant l’analyse (en cours, succès, échec)
pour comprendre ce qu’il se passe après avoir appuyé sur scan.

**Why this priority**: Réduit l’incertitude et améliore la perception de fiabilité.

**Independent Test**: Lancer un scan dans différents cas (réussi, lent, erreur) et vérifier les états
affichés et les actions possibles.

**Acceptance Scenarios**:

1. **Given** un scan en cours, **When** l’utilisateur attend le résultat,
   **Then** un indicateur d’analyse en cours est affiché.
2. **Given** un échec d’analyse, **When** l’erreur survient,
   **Then** l’utilisateur voit un message compréhensible et peut relancer un scan.

### Edge Cases

- Refus d’autorisation caméra au premier lancement.
- Appui multiple sur le bouton scan: éviter les analyses concurrentes non voulues.
- Fermeture de l’application pendant l’analyse: nettoyer toute photo temporaire restante.
- Espace disque faible ou indisponible pour le tampon temporaire: afficher une erreur claire.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le système MUST ouvrir l’application directement sur un écran caméra avec un bouton scan.
- **FR-002**: Le système MUST capturer une photo temporaire lors de l’appui sur scan et déclencher
  immédiatement l’analyse.
- **FR-003**: Le système MUST garantir que la photo utilisée pour le scan n’est pas stockée de façon
  permanente sur l’appareil.
- **FR-004**: Le système MUST supprimer la photo temporaire après analyse, annulation ou erreur.
- **FR-005**: Le système MUST afficher des états explicites du processus (prêt, analyse en cours,
  succès, échec).
- **FR-006**: Le système MUST gérer les permissions caméra et proposer un parcours de récupération en cas
  de refus.

### Key Entities *(include if feature involves data)*

- **TemporaryScanImage**: image de scan éphémère (identifiant, horodatage, statut de suppression).
- **ScanSession**: session de capture/analyse (état, timestamps, issue).
- **ScanFeedbackState**: état utilisateur visible (idle, analyzing, success, error).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 95% des démarrages affichent l’écran caméra et le bouton scan en moins de 2 secondes.
- **SC-002**: 99% des scans n’entraînent aucun stockage permanent de la photo sur l’appareil.
- **SC-003**: 95% des photos temporaires sont supprimées immédiatement à la fin du cycle (succès/échec/annulation).
- **SC-004**: 90% des utilisateurs comprennent l’état du scan sans assistance.

## Assumptions

- L’application dispose d’un mécanisme de stockage temporaire non persistant.
- L’analyse peut nécessiter un court délai réseau ou local selon le contexte.
- Le besoin prioritaire est la confidentialité des images capturées.

