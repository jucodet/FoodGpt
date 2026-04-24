# Feature Specification: Bilan ingrédients et composition à partir du texte capturé

**Feature Branch**: `009-llm-bilan-composition-ingredients`  
**Created**: 2026-04-24  
**Status**: Draft  
**Input**: User description: "dans le texte capturé, je veux qu'un LLM identifie la liste des ingrédients en corrigeant les éventuelles typographies, puis qu'il fasse une analyse de la composition en donnant les effets objectifs et connus pour la santé. Le bilan doit s'afficher ensuite sur l'écran à la place du texte capturé sur la photo"

## Clarifications

### Session 2026-04-24

- Q: Lieu d’exécution du modèle de langage sur le texte capturé (appareil vs service distant) ? → A: **Uniquement sur l’appareil** — aucun envoi du texte capturé hors de l’appareil pour l’analyse par modèle de langage.
- Q: Comportement lorsque le traitement local par modèle de langage est indisponible (ressource absente, erreur locale, délai dépassé) ? → A: **Blocage explicite** — message clair, réessai sur l’appareil, repli possible vers le texte brut seul ; **pas** de bilan partiel « santé » simulé sans traitement local réussi.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Voir le bilan à la place du texte brut (Priority: P1)

En tant qu’utilisateur qui vient d’obtenir le texte issu de la lecture d’une photo (liste d’ingrédients ou étiquette), je veux que l’écran me présente un **bilan structuré** : liste d’ingrédients corrigée pour la lisibilité (fautes de frappe ou d’OCR évidentes), puis une **analyse de la composition** qui décrit les **effets objectifs et documentés** sur la santé pour les constituants pertinents, **à la place** du simple défilement du texte brut tel qu’extrait de l’image.

**Why this priority**: C’est la promesse centrale : passer du texte brut à une lecture interprétée utile pour la décision alimentaire, sans exiger de l’utilisateur qu’il reparse lui-même l’étiquette.

**Independent Test**: À partir d’un texte capturé représentatif (liste d’ingrédients connue), vérifier que l’écran de résultat affiche d’abord ou clairement une liste d’ingrédients normalisée et un bloc d’analyse de composition, et que le texte brut OCR n’est plus l’unique contenu principal affiché.

**Acceptance Scenarios**:

1. **Given** un texte capturé exploitable contenant une liste d’ingrédients, **When** l’analyse est terminée, **Then** l’utilisateur voit un bilan qui inclut une liste d’ingrédients dont les libellés ont été corrigés ou harmonisés lorsque des erreurs typographiques ou de reconnaissance évidentes peuvent l’être sans inventer des ingrédients absents du texte source.
2. **Given** le même contexte, **When** le bilan s’affiche, **Then** l’écran présente une analyse de la composition qui relie les ingrédients ou familles d’ingrédients aux **effets sur la santé** formulés de manière factuelle et **fondée sur des connaissances reconnues** (par exemple consensus nutritionnels ou réglementation des allégations), sans présenter des opinions personnelles comme des faits.
3. **Given** le bilan est affiché, **When** l’utilisateur parcourt l’écran de résultat, **Then** le texte brut capturé n’est plus le contenu principal unique : soit il est remplacé par le bilan, soit il est accessible de façon secondaire (par exemple section repliable ou action « voir le texte original ») sans occuper la même place visuelle dominante qu’avant.

---

### User Story 2 - Transparence, limites et absence d’hallucination grave (Priority: P2)

En tant qu’utilisateur, je dois comprendre que l’analyse est **informative**, distinguer ce qui est **certain** du texte de ce qui est **interprété**, et ne pas voir d’ingrédients ou d’effets santé **inventés** lorsque le texte source ne le permet pas.

**Why this priority**: La confiance et la sécurité d’usage (éviter des conclusions santé infondées) sont essentielles pour une fonctionnalité d’aide à la lecture d’étiquette.

**Independent Test**: Fournir un texte capturé minimal ou ambigu ; vérifier messages de limite, absence de liste fabriquée, et formulation prudente lorsque l’information publique ne permet pas d’affirmer un effet.

**Acceptance Scenarios**:

1. **Given** le texte capturé ne permet pas d’isoler une liste d’ingrédients crédible, **When** le résultat est présenté, **Then** l’utilisateur voit une explication claire (par exemple impossible d’extraire une liste fiable) plutôt qu’une liste fictive.
2. **Given** un ingrédient ou additif n’a pas d’effet documenté de manière consensuelle dans les sources visées par le produit, **When** l’analyse est affichée, **Then** le système l’indique avec prudence ou s’abstient d’allégations fortes plutôt que d’affirmer des bénéfices ou risques non étayés.
3. **Given** le bilan inclut des éléments déduits du texte, **When** l’utilisateur lit l’écran, **Then** une formulation ou un encart rappelle le caractère informatif non médical de l’analyse (sans imposer de jargon juridique excessif).

---

### User Story 3 - Attente et récupération en cas d’échec (Priority: P3)

En tant qu’utilisateur, pendant que le modèle de langage traite le texte capturé, je vois un **état de progression** clair ; en cas d’échec, je peux **réessayer** ou revenir au texte brut si le produit le propose.

**Why this priority**: Complète le parcours principal sans lequel l’expérience peut sembler bloquée ou frustrante.

**Independent Test**: Simuler indisponibilité ou timeout du traitement ; vérifier message, possibilité de réessai, et absence de bilan vide présenté comme complet.

**Acceptance Scenarios**:

1. **Given** l’analyse par modèle de langage est en cours, **When** l’utilisateur attend, **Then** un indicateur explicite de traitement est visible.
2. **Given** l’analyse échoue, **When** l’écran d’erreur s’affiche, **Then** l’utilisateur comprend la cause en termes compréhensibles et dispose d’une action de réessai ou d’un retour au texte capturé lorsque le produit prévoit ce secours.
3. **Given** le traitement local par modèle de langage est **indisponible** ou **n’aboutit pas** (ressource manquante, erreur locale, délai dépassé selon les règles produit), **When** le résultat est présenté, **Then** l’utilisateur voit un **blocage explicite** (message d’indisponibilité ou d’échec) avec **réessai** sur l’appareil et/ou **repli** vers le **texte brut** uniquement, et **pas** un bilan liste + analyse composition présenté comme complet ou crédible sans analyse locale réussie.

---

### Edge Cases

- Texte capturé très long ou multilingue : le bilan reste lisible (structure, titres, défilement) ; pas de troncature silencieuse trompeuse.
- Liste d’ingrédients partiellement illisible dans le texte source : seuls les éléments raisonnablement identifiables sont listés ; le reste est signalé comme incertain.
- Doublons ou ordre inversé par rapport à l’étiquette : la liste corrigée préserve l’ordre réglementaire lorsqu’il est déductible du texte ; sinon l’ordre est présenté comme « tel que déduit » sans falsifier la réglementation.
- Additifs codés (E xxx) vs noms : le bilan peut harmoniser les libellés tout en restant aligné sur le texte source.
- Absence ou instabilité de connexion réseau : l’analyse par modèle de langage **ne repose pas** sur un service distant pour le texte capturé ; l’utilisateur obtient un bilan produit localement, ou un message d’indisponibilité / ressource locale manquante clair, avec possibilité de réessai sur l’appareil.
- Modèle local absent, en erreur ou temps de traitement dépassé : **pas** de bilan « liste + analyse » partiel ou cosmétique ; **blocage explicite**, réessai sur l’appareil, repli vers le texte brut si le produit le prévoit — conformément à la clarification session 2026-04-24.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le système DOIT accepter en entrée le **texte capturé** issu du flux de lecture sur photo (tel que défini par la vision produit existante pour la capture et l’extraction de texte).
- **FR-002**: Le système DOIT utiliser un **modèle de langage** pour identifier, à partir de ce texte, une **liste d’ingrédients** exploitable pour l’affichage utilisateur.
- **FR-003**: Lorsque le texte contient des erreurs typographiques ou de reconnaissance évidentes sur les noms d’ingrédients, le système DOIT **corriger ou harmoniser** ces libellés dans la liste présentée, **sans ajouter** d’ingrédients non soutenus par le texte source.
- **FR-004**: Le système DOIT produire une **analyse de la composition** qui décrit, pour les ingrédients ou catégories pertinents, les **effets sur la santé** présentés comme **objectifs et documentés** (par exemple s’appuyant sur des faits nutritionnels ou réglementaires largement acceptés, formulés de manière prudente lorsque la littérature est mitigée).
- **FR-005**: L’écran de résultat après analyse DOIT afficher le **bilan** (liste + analyse) comme **contenu principal**, **à la place** du seul affichage du texte brut capturé tel qu’à l’étape précédente ; si le texte brut reste accessible, ce DOIT être via un mode secondaire explicitement secondaire sur le plan de l’attention visuelle.
- **FR-006**: Le système NE DOIT PAS présenter comme certitudes des effets santé **non étayés** par des connaissances reconnues accessibles au produit ; en cas de doute, le système DOIT **nuancer ou s’abstenir** plutôt que d’affirmer.
- **FR-007**: Lorsque le texte ne permet pas une extraction fiable d’ingrédients, le système DOIT communiquer clairement cette limite et NE DOIT PAS fabriquer une liste plausible mais fausse.
- **FR-008**: Pendant le traitement par le modèle de langage, le système DOIT indiquer un **état d’attente ou de progression** explicite.
- **FR-009**: En cas d’échec du traitement, le système DOIT fournir un **message compréhensible** et, lorsque pertinent, une action de **réessai** ou un **repli** vers le texte capturé.
- **FR-010**: Le bilan DOIT inclure ou renvoyer vers une **mention d’usage informatif** (non diagnostic, non prescription) lorsque l’analyse touche à la santé.
- **FR-011**: L’identification des ingrédients, les corrections de libellés pertinentes et l’**analyse de la composition** par modèle de langage DOIVENT s’exécuter **entièrement sur l’appareil** ; le **texte capturé** NE DOIT PAS être transmis hors de l’appareil à des fins d’analyse par modèle de langage.
- **FR-012**: Lorsque le traitement local par modèle de langage est **indisponible** ou **n’aboutit pas** avant la fin du parcours attendu (ressource absente, erreur locale, délai dépassé selon les règles produit), le système NE DOIT **PAS** substituer un **bilan partiel ou simulé** (par exemple analyse santé vide ou générique) donnant l’illusion d’une analyse complète ; le système DOIT maintenir l’**honnêteté** du résultat : échec explicite, **réessai** sur l’appareil, et **repli** vers le **texte brut** uniquement lorsque le produit prévoit ce secours.

### Key Entities *(include if feature involves data)*

- **Texte capturé**: Chaîne (ou segments) issus de la lecture d’image, servant de seule base factuelle pour l’extraction d’ingrédients et l’analyse.
- **Liste d’ingrédients présentée**: Ensemble ordonné ou structuré de libellés corrigés ou harmonisés, traçable vers des passages du texte capturé.
- **Analyse de composition**: Synthèse reliant ingrédients ou familles à des effets santé documentés, avec niveau de confiance ou prudence lorsque nécessaire.
- **Bilan écran**: Vue utilisateur unifiant liste et analyse, conçue pour remplacer le texte brut comme lecture principale après succès du traitement.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Sur un jeu d’étiquettes de référence avec listes d’ingrédients lisibles, **au moins 90 %** des cas permettent à un observateur indépendant de constater que la liste affichée **reprend les ingrédients du texte source** sans ingrédient manifestement inventé, et que les corrections typographiques **améliorent la lisibilité** sans altérer le sens pour plus de **95 %** des libellés corrigés (échantillon contrôlé en conditions de test).
- **SC-002**: **100 %** des écrans de bilan affichés après succès présentent à la fois une **section liste** et une **section analyse** identifiables sans action cachée, et le texte brut seul n’occupe plus la zone de contenu principal par défaut.
- **SC-003**: Dans un scénario d’usage standard, l’utilisateur perçoit un retour de progression pendant l’analyse et reçoit soit un bilan complet, soit un message d’échec ou de limite explicite, dans un **délai perçu comme acceptable** pour une interaction mobile (cible indicative alignée sur la vision produit pour les analyses post-capture : **moins de 30 secondes** en conditions d’usage courantes, validée par test utilisateur sans référence à une technologie particulière).
- **SC-004**: Lors d’audits de contenu sur cas limites (texte ambigu, additifs controversés), **aucun** écran ne contient d’allégation santé **catégorique** non soutenue par une formulation prudente ou une absence d’affirmation, conformément aux critères de révision définis pour le produit.
- **SC-005**: En **absence de connexion réseau**, lorsque les ressources locales nécessaires sont disponibles, l’utilisateur obtient un **bilan complet** ou un **message d’indisponibilité** explicite — sans étape obligatoire consistant à envoyer le texte capturé vers un service externe pour l’analyse.
- **SC-006**: Dans des scénarios de test où le traitement local par modèle de langage **échoue ou est indisponible**, **100 %** des écrans respectent un **blocage explicite** (message + réessai ou repli texte brut) et **aucun** n’affiche un **bilan** liste + analyse composition **présenté comme réussi** sans traitement local complet.

## Assumptions

- Le texte capturé est déjà disponible via le parcours « photo → texte » ; cette fonctionnalité se concentre sur l’**interprétation** et l’**affichage bilan**, pas sur l’amélioration du moteur OCR lui-même.
- Les **contraintes de confidentialité** sont alignées avec la vision « traitement sur l’appareil » : le **texte capturé** utilisé pour le modèle de langage **ne quitte pas** l’appareil ; l’**image** ne quitte pas l’appareil non plus pour cette chaîne (déjà couverte par le flux capture → texte). Le modèle de langage et les données d’appui à l’analyse santé **résident ou s’exécutent localement** selon les choix de planification, sans transmission du texte capturé vers un service distant pour cette fonctionnalité.
- Les formulations sur la santé suivent une **politique éditoriale** du produit (sources de référence, niveau de prudence, glossaire) détaillée hors spécification fonctionnelle mais appliquée lors des tests de contenu.
- L’utilisateur cible est un consommateur cherchant une **aide à la lecture** d’étiquette, pas un professionnel de santé ; le bilan reste **informatif**.
