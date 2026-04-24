# Feature Specification: Identification des ingrédients par photo

**Feature Branch**: `006-identify-photo-ingredients`  
**Created**: 2026-04-24  
**Status**: Draft  
**Input**: User description: "à partir de la photo, je veux identifier la liste des ingrédients"

## Clarifications

### Session 2026-04-24

- Q: Où doit s'exécuter l'extraction des ingrédients (local vs serveur) ? → A: OCR + extraction entièrement locale sur l'appareil (aucun envoi serveur).
- Q: Quelle stratégie linguistique appliquer aux photos multilingues ? → A: Détecter automatiquement toutes les langues et fusionner les segments ingrédients.
- Q: Quel seuil de confiance OCR autorise une acceptation automatique ? → A: Accepter automatiquement si confiance OCR >= 70%.
- Q: Quelle rétention photo appliquer pendant/après traitement ? → A: Conserver temporairement en cache privé pendant traitement, puis supprimer automatiquement après résultat.
- Q: Quelle politique de relance appliquer après un échec OCR ? → A: Aucune relance automatique, uniquement action manuelle utilisateur.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Extraire les ingrédients d'une photo (Priority: P1)

En tant qu'utilisateur, je prends ou charge une photo d'une étiquette produit et je reçois une liste d'ingrédients lisible.

**Why this priority**: C'est la valeur centrale du produit; sans cette extraction, la fonctionnalité ne sert pas l'objectif principal.

**Independent Test**: Peut être testé de bout en bout en soumettant une photo nette d'une liste d'ingrédients et en vérifiant que la liste retournée est exploitable.

**Acceptance Scenarios**:

1. **Given** une photo lisible contenant une section ingrédients, **When** l'utilisateur lance l'analyse, **Then** le système affiche une liste d'ingrédients extraite dans l'ordre de lecture.
2. **Given** une photo lisible contenant des séparateurs (virgules, points-virgules), **When** l'analyse est terminée, **Then** la sortie sépare correctement les ingrédients en éléments distincts.

---

### User Story 2 - Corriger avant validation (Priority: P2)

En tant qu'utilisateur, je peux corriger le texte extrait si certains ingrédients sont mal détectés avant de valider la liste finale.

**Why this priority**: L'OCR peut produire des erreurs; la correction manuelle garantit une donnée finale fiable.

**Independent Test**: Peut être testé en forçant une erreur d'extraction sur une photo complexe puis en modifiant un ingrédient et en validant la version corrigée.

**Acceptance Scenarios**:

1. **Given** une liste extraite affichée, **When** l'utilisateur modifie un ou plusieurs ingrédients puis valide, **Then** la liste validée correspond exactement aux corrections saisies.

---

### User Story 3 - Gérer les échecs de scan (Priority: P3)

En tant qu'utilisateur, je reçois un message clair quand la photo ne permet pas une extraction fiable et je peux relancer facilement.

**Why this priority**: Une gestion explicite des échecs évite la frustration et augmente le taux de réussite au second essai.

**Independent Test**: Peut être testé en soumettant une photo floue ou sombre et en vérifiant la présence d'un message d'erreur compréhensible avec option de recommencer.

**Acceptance Scenarios**:

1. **Given** une photo trop floue pour lire les ingrédients, **When** l'analyse se termine, **Then** le système n'invente pas de liste et propose de reprendre une photo.

---

### Edge Cases

- La photo contient plusieurs langues; le système doit détecter automatiquement les langues présentes et fusionner les segments ingrédients cohérents sans dupliquer d'éléments.
- La liste d'ingrédients est coupée (début ou fin hors cadre); le système doit signaler que le résultat peut être incomplet.
- L'étiquette contient une police décorative ou un faible contraste; le système doit préférer un échec explicite à une extraction trompeuse.
- Le texte inclut des allergènes en majuscules ou entre parenthèses; la structure doit être conservée dans la sortie.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le système DOIT permettre de soumettre une photo d'étiquette alimentaire pour analyse.
- **FR-002**: Le système DOIT détecter et extraire la liste d'ingrédients présente sur la photo.
- **FR-003**: Le système DOIT présenter le résultat sous forme de liste d'ingrédients lisible et modifiable.
- **FR-004**: L'utilisateur DOIT pouvoir corriger manuellement la liste avant validation finale.
- **FR-005**: Le système DOIT indiquer clairement quand l'extraction est impossible ou peu fiable.
- **FR-006**: Le système DOIT permettre de relancer une analyse après un échec sans étape inutile.
- **FR-007**: Le système DOIT conserver l'ordre des ingrédients tel qu'il apparaît sur l'étiquette lorsque cet ordre est identifiable.
- **FR-008**: Le système DOIT éviter d'ajouter des ingrédients non présents dans le texte détecté.
- **FR-009**: Le système DOIT exécuter l'OCR et l'extraction des ingrédients 100% on-device, sans envoi de photo ni de texte vers un service distant.
- **FR-010**: Le système DOIT autoriser la validation automatique de la liste extraite lorsque la confiance OCR globale est supérieure ou égale à 70%.
- **FR-011**: Le système DOIT conserver la photo uniquement en cache privé le temps du traitement puis la supprimer automatiquement après affichage du résultat (succès ou échec).
- **FR-012**: Le système NE DOIT PAS relancer automatiquement une analyse après échec OCR; seule une action explicite de l'utilisateur peut déclencher une nouvelle tentative.

### Key Entities *(include if feature involves data)*

- **PhotoIngredientInput**: Représente l'image soumise par l'utilisateur pour extraction, avec son état de traitement.
- **ExtractedIngredientItem**: Représente un ingrédient individuel extrait, incluant son texte brut et sa position dans la liste.
- **ValidatedIngredientList**: Représente la liste finale approuvée par l'utilisateur après corrections éventuelles.
- **ScanAttemptResult**: Représente l'issue d'une tentative d'extraction (succès, échec, incomplet) et le message associé.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Au moins 90% des photos nettes et bien cadrées produisent une liste d'ingrédients exploitable en moins de 8 secondes.
- **SC-002**: Au moins 85% des utilisateurs finalisent une première liste validée en moins de 60 secondes.
- **SC-003**: Dans au moins 95% des cas d'échec, l'utilisateur reçoit un message explicite avec action de relance disponible.
- **SC-004**: Au moins 80% des utilisateurs déclarent que la liste proposée nécessite peu ou pas de corrections manuelles.

## Assumptions

- Les utilisateurs ciblent principalement des photos d'emballages alimentaires avec texte suffisamment visible.
- Le périmètre initial couvre une photo à la fois, sans traitement batch de plusieurs produits.
- La sortie attendue est une liste d'ingrédients textuelle, pas une analyse nutritionnelle complète.
- L'utilisateur accepte de corriger manuellement la liste en cas d'extraction partielle.
- La confidentialité impose un traitement local uniquement (pas d'upload image/texte vers un backend tiers).
- Aucune conservation durable de photo n'est autorisée dans le périmètre MVP.
