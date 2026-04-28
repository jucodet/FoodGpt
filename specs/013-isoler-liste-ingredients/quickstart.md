# Quickstart: Isolation de la ligne ingredients

## Objectif

Verifier rapidement que seul le segment ingredients est prepare et soumis a l analyse selon les regles clarifiees.

## Prerequis

- Build Android operationnel.
- Parcours de scan OCR actif.
- Jeu de textes OCR de test (nominal + cas limites).

## Parcours 1 - Extraction nominale

1. Fournir un texte OCR avec `ingredients: sucre, farine, sel` puis une nouvelle ligne.
2. Lancer la preparation.
3. Verifier que le segment extrait commence a `ingredients` et s arrete au premier retour a la ligne.
4. Verifier que le segment est affiche avant envoi et demande confirmation.

## Parcours 2 - Absence d ancre ingredients

1. Fournir un texte OCR sans mot `ingredients`.
2. Lancer la preparation.
3. Verifier que l analyse est bloquee.
4. Verifier qu une action de recapture ou edition est proposee.

## Parcours 3 - Absence de retour a la ligne

1. Fournir un texte OCR ou `ingredients` est present mais sans retour a la ligne ensuite.
2. Lancer la preparation.
3. Verifier que la borne de fin est la fin du texte OCR.
4. Verifier que la confirmation utilisateur reste obligatoire avant envoi.

## Parcours 4 - Occurrences multiples

1. Fournir un texte OCR avec plusieurs occurrences de `ingredients`.
2. Lancer la preparation.
3. Verifier que la premiere occurrence est toujours retenue comme ancre.
4. Verifier que le reste du comportement (borne, confirmation, envoi) est inchangé.

## Verification executee (2026-04-28)

- Implementation effectuee pour extraction, gate de soumission, et confirmation utilisateur avant analyse.
- Jeux de tests unitaires et instrumentation ajoutes pour US1/US2/US3.
- Execution automatisee locale non realisee: `gradle/wrapper/gradle-wrapper.jar` manquant dans le depot au moment de la verification.
