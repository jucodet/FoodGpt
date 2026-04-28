# Research: Disposition accueil MediaPipe

## Decision 1: Utiliser un voyant a 3 etats avec libelle explicite

- **Decision**: Afficher un voyant avec etat `Verification...`, puis `Disponible` ou `Indisponible`, accompagne d'un libelle texte.
- **Rationale**: Evite les faux negatifs au demarrage, ameliore l'accessibilite (pas uniquement la couleur), et stabilise les attentes utilisateur.
- **Alternatives considered**:
  - Voyant binaire immediate sans etat intermediaire (rejete: ambiguite pendant l'initialisation).
  - Couleur seule sans texte (rejete: risque accessibilite et comprehension).

## Decision 2: Figer l'ordre vertical de reference dans la spec 012

- **Decision**: Definir et imposer l'ordre UI de l'accueil dans 012: voyant, bienvenue, vue photo, bouton capture.
- **Rationale**: Supprime les contradictions inter-specs (007/010/012) et donne une regle unique pour implementation et tests.
- **Alternatives considered**:
  - Conserver des priorites implicites entre specs (rejete: ambiguite de livraison).
  - Fusionner toutes les specs en une seule (rejete: surcharge documentaire immediate).

## Decision 3: Appliquer un espacement standard fixe entre vue photo et bouton

- **Decision**: Conserver un espacement visuel constant entre l'encart photo et le bouton de capture.
- **Rationale**: Presente un compromis robuste entre lisibilite, coherence responsive et simplicite de validation.
- **Alternatives considered**:
  - Aucun espacement (rejete: lisibilite degradee).
  - Espacement adaptatif complexe (rejete: sur-complexite pour ce perimetre).

## Decision 4: Limiter explicitement le perimetre au mode portrait

- **Decision**: Valider cette iteration uniquement en orientation portrait, avec paysage hors scope.
- **Rationale**: Reduit le risque de regressions et accelere une livraison ciblee sur le parcours principal.
- **Alternatives considered**:
  - Support portrait+paysage des cette iteration (rejete: trop de cas de layout/test additionnels).
  - Paysage best-effort non garanti (rejete: zone grise de qualite produit).

## Decision 5: Conserver les comportements metier existants (welcome/camera)

- **Decision**: Modifier uniquement la composition visuelle de l'accueil sans changer les regles metier des modules welcome et capture.
- **Rationale**: Respecte la constitution (simplicite/evolutivite), minimise la dette et limite les regressions fonctionnelles.
- **Alternatives considered**:
  - Revoir en meme temps la logique de messages et capture (rejete: hors scope de la demande).
  - Introduire une nouvelle couche d'orchestration complete (rejete: sur-architecture).
