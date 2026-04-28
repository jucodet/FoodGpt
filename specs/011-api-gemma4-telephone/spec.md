# Feature Specification: Utilisation API Gemma4 telephone

**Feature Branch**: `[011-api-gemma4-telephone]`  
**Created**: 2026-04-27  
**Status**: Draft  
**Input**: User description: "plutot que d'embarquer un modele de langage Gemma 4 tres volumineux, utiliser l'API permettant d'utiliser le modele Gemma4 deja installe sur le telephone"

## Clarifications

### Session 2026-04-27

- Q: Faut-il un consentement explicite avant envoi vers l'API locale Gemma4 ? → A: Aucun consentement explicite (analyse directe si API locale disponible).
- Q: Quel comportement si l'API locale est indisponible ? → A: Aucun fallback ; l'analyse echoue avec message utilisateur.
- Q: Quel objectif de performance selon la classe d'appareil ? → A: Deux niveaux : 95% < 3s (appareils recents), 95% < 6s (appareils compatibles minimum).
- Q: Quel perimetre de types de requetes en v1 ? → A: v1 texte uniquement.
- Q: Quelle politique de conservation des donnees analysees ? → A: Aucune conservation du contenu analyse ; conservation des seules metriques et erreurs techniques.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Basculer vers le modele local du telephone (Priority: P1)

En tant qu'utilisateur, je veux que l'application utilise le modele Gemma4 deja present sur mon telephone via une API locale, afin d'eviter de telecharger ou embarquer un modele trop lourd dans l'application.

**Why this priority**: Cette histoire supprime le principal frein de taille applicative et de consommation de ressources, et debloque la valeur principale du besoin.

**Independent Test**: Peut etre testee en configurant un telephone avec Gemma4 installe, en envoyant une requete d'analyse depuis l'application, puis en verifiant que la reponse provient de l'API locale du telephone sans chargement de modele embarque.

**Acceptance Scenarios**:

1. **Given** un telephone compatible avec Gemma4 local disponible, **When** l'utilisateur lance une analyse depuis l'application, **Then** l'application recupere le resultat via l'API locale du telephone.
2. **Given** une application nouvellement installee, **When** l'utilisateur utilise la fonctionnalite d'analyse, **Then** aucun telechargement de modele volumineux n'est requis pour produire une reponse.

---

### User Story 2 - Informer clairement en cas d'indisponibilite de l'API locale (Priority: P2)

En tant qu'utilisateur, je veux recevoir un message clair quand l'API Gemma4 du telephone n'est pas accessible, afin de comprendre pourquoi l'analyse ne peut pas se lancer.

**Why this priority**: Une indisponibilite est probable (service arrete, permissions, version incompatible). Une information claire evite la confusion et les abandons.

**Independent Test**: Peut etre testee en desactivant l'API locale sur un appareil compatible puis en declenchant une analyse pour verifier que l'utilisateur recoit une explication comprenable et actionnable.

**Acceptance Scenarios**:

1. **Given** l'API Gemma4 locale est indisponible, **When** l'utilisateur lance une analyse, **Then** l'application affiche un message expliquant l'indisponibilite et la prochaine action possible.

---

### User Story 3 - Preserver une experience d'analyse fluide (Priority: P3)

En tant qu'utilisateur, je veux obtenir un resultat d'analyse dans un delai raisonnable avec le modele local du telephone, afin de conserver une experience fluide.

**Why this priority**: Le changement de source de modele ne doit pas degrader l'experience utilisateur sur le parcours principal.

**Independent Test**: Peut etre testee en executant un lot de requetes representatif et en mesurant les delais observes du point de vue utilisateur.

**Acceptance Scenarios**:

1. **Given** l'API locale est disponible, **When** l'utilisateur soumet une requete d'analyse standard, **Then** il recoit un resultat dans un delai conforme aux criteres de succes.

---

### Edge Cases

- L'API locale repond mais renvoie une erreur metier (requete invalide ou limite depassee).
- Gemma4 est installe sur le telephone mais desactive ou non accessible par l'application.
- La connexion entre application et API locale est interrompue pendant une analyse en cours.
- Le format de reponse retourne par l'API locale est incomplet ou non exploitable.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le systeme MUST utiliser l'API locale du telephone pour executer les analyses avec le modele Gemma4 deja installe sur l'appareil.
- **FR-002**: Le systeme MUST eviter toute dependance a un modele Gemma4 embarque volumineux dans le paquet applicatif pour les analyses couvertes par cette fonctionnalite.
- **FR-003**: Le systeme MUST verifier la disponibilite de l'API locale avant chaque tentative d'analyse et gerer explicitement les indisponibilites.
- **FR-004**: Le systeme MUST fournir un retour utilisateur clair lorsqu'une analyse echoue a cause de l'API locale (indisponible, erreur de communication ou reponse invalide).
- **FR-005**: Les utilisateurs MUST pouvoir lancer une analyse depuis le parcours existant sans etapes supplementaires liees au telechargement de modele.
- **FR-006**: Le systeme MUST journaliser les resultats de tentative d'appel (succes/echec et cause) afin de permettre le suivi de fiabilite de la fonctionnalite.
- **FR-007**: Le systeme MUST conserver un comportement fonctionnel de l'application en cas d'indisponibilite de l'API locale, sans blocage global de l'interface.
- **FR-008**: Le systeme MUST lancer l'analyse automatiquement via l'API locale disponible sans etape de consentement explicite additionnelle.
- **FR-009**: Le systeme MUST ne pas basculer vers un moteur alternatif (local ou distant) lorsque l'API locale Gemma4 est indisponible.
- **FR-010**: Le systeme MUST limiter le perimetre v1 aux requetes textuelles uniquement.
- **FR-011**: Le systeme MUST ne pas conserver le contenu textuel analyse au-dela du traitement de la requete.
- **FR-012**: Le systeme MUST limiter la journalisation aux metriques operationnelles et erreurs techniques sans contenu utilisateur.

### Key Entities *(include if feature involves data)*

- **Contexte d'analyse**: Donnees fonctionnelles envoyees pour demander une analyse (ex. contenu a analyser, metadonnees de session).
- **Endpoint API locale Gemma4**: Point d'acces local expose par le telephone pour traiter les requetes d'inference.
- **Resultat d'analyse**: Sortie interpretable renvoyee au parcours utilisateur (etat, contenu, message eventuel).
- **Statut d'appel API**: Trace d'execution indiquant succes ou type d'echec pour chaque tentative.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% des analyses prises en charge par cette fonctionnalite s'executent sans telechargement de modele additionnel pendant l'usage normal.
- **SC-002**: Sur appareils recents, au moins 95% des analyses standards aboutissent a un resultat exploitable en moins de 3 secondes.
- **SC-003**: Sur appareils compatibles minimum, au moins 95% des analyses standards aboutissent a un resultat exploitable en moins de 6 secondes.
- **SC-004**: 100% des echecs lies a l'API locale affichent un message utilisateur explicite en moins de 2 secondes apres detection de l'echec.
- **SC-005**: Le taux de completion du parcours d'analyse reste au minimum equivalent a celui observe avant la migration vers l'API locale.

## Assumptions

- Les utilisateurs cibles disposent d'un telephone compatible avec Gemma4 localement installe.
- Une API locale stable est exposee par l'environnement du telephone pour acceder a Gemma4.
- Le perimetre v1 couvre le remplacement de la source de modele pour les analyses existantes, sans ajout de nouveaux cas d'usage d'analyse.
- Le perimetre fonctionnel v1 ne couvre que les requetes textuelles (pas d'image ni d'audio).
- Les indicateurs de suivi n'incluent pas le contenu brut des requetes utilisateur.
- Les parcours non relies a l'analyse Gemma4 restent inchanges.
