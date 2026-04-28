# Research: Isolation de la liste ingredients avant analyse

## Decision 1: Ancrer l extraction sur la premiere occurrence de ingredients

- **Decision**: Utiliser la premiere occurrence detectee de `ingredients` comme point de depart du segment.
- **Rationale**: Regle simple et deterministe, coherente avec la clarification utilisateur, et facile a tester.
- **Alternatives considered**:
  - Derniere occurrence (rejetee: risque d ignorer la vraie liste principale).
  - Choix manuel de l occurrence (rejetee: friction UX inutile pour le MVP).

## Decision 2: Delimiter par premier retour a la ligne, sinon fin du texte

- **Decision**: Fin de segment au premier retour a la ligne apres l ancre; en absence de retour a la ligne, fin au dernier caractere OCR.
- **Rationale**: Aligne la spec et couvre les OCR mono-ligne frequents sans bloquer inutilement.
- **Alternatives considered**:
  - Longueur maximale arbitraire (rejetee: coupe potentiellement des ingredients valides).
  - Blocage systematique sans retour ligne (rejetee: trop de faux blocages).

## Decision 3: Bloquer l analyse si ancre ingredients absente

- **Decision**: Si `ingredients` n est pas detecte, ne rien envoyer a l analyse et demander recapture ou edition.
- **Rationale**: Evite des resultats hors sujet et protege la qualite percue.
- **Alternatives considered**:
  - Envoyer tout le texte OCR (rejetee: bruit important et analyses trompeuses).
  - Envoyer une chaine vide (rejetee: peu actionnable pour l utilisateur).

## Decision 4: Exiger une confirmation utilisateur avant envoi

- **Decision**: Afficher le segment extrait avant analyse et demander confirmation explicite.
- **Rationale**: Transparence et controle utilisateur, tout en reduisant les erreurs en aval.
- **Alternatives considered**:
  - Analyse directe avec affichage apres coup (rejetee: detection tardive des erreurs).
  - Analyse directe sans affichage segment (rejetee: manque de confiance et debuggage difficile).

## Decision 5: Normaliser la detection du mot ingredients

- **Decision**: Detection robuste a la casse et aux variantes accentuees usuelles.
- **Rationale**: Le texte OCR varie selon qualite photo/police/langue; cette robustesse est necessaire au taux de succes cible.
- **Alternatives considered**:
  - Correspondance stricte exacte (rejetee: trop fragile).
  - Liste etendue de synonymes (rejetee: hors scope MVP, a reevaluer plus tard).
