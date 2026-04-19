<!--
Sync Impact Report

- Version change: N/A → 0.1.0
- Modified principles: N/A (template filled)
- Added sections: N/A (template filled)
- Removed sections: N/A
- Templates requiring updates:
  - ✅ e:\Dev\projects\FoodGPT\.specify\templates\tasks-template.md (ATDD/tests non optionnels + wording)
  - ⚠ e:\Dev\projects\FoodGPT\.specify\templates\plan-template.md (Constitution Check: à renseigner dynamiquement par /speckit.plan)
  - ⚠ e:\Dev\projects\FoodGPT\.specify\templates\spec-template.md (déjà aligné ATDD; rien à changer)
  - ⚠ e:\Dev\projects\FoodGPT\.specify\templates\commands\*.md (dossier absent)
- Follow-up TODOs:
  - TODO(RATIFICATION_DATE): date de ratification initiale inconnue.
-->

# FoodGPT Constitution
<!-- Constitution de gouvernance & qualité du projet FoodGPT -->

## Core Principles

### I. Qualité produit et code (non négociable)
Le projet DOIT rester fiable, maintenable et prédictible.

- Toute modification DOIT être traçable (spec → tests d’acceptation → code).
- Toute PR DOIT maintenir ou améliorer la lisibilité, la cohérence et la robustesse.
- Les régressions (fonctionnelles, UX, performance) sont des bugs bloquants.

### II. Acceptance Test Driven Development (ATDD) d’abord
On développe à partir de scénarios d’acceptation observables par l’utilisateur.

- Chaque user story DOIT avoir des scénarios d’acceptation **Given/When/Then** avant implémentation.
- Les tests d’acceptation (ou tests de parcours/intégration équivalents) DOIVENT échouer avant le code, puis passer.
- Un incrément livrable DOIT être démontrable et testable indépendamment (MVP par story).

### III. UX moderne et optimale par défaut
Chaque fonctionnalité DOIT viser une expérience claire, rapide, et cohérente.

- Les parcours principaux DOIVENT être simples (réduction des frictions, feedback immédiat, états vides/erreurs soignés).
- L’UI DOIT être moderne (accessibilité, responsive si applicable, micro-copies utiles).
- Les choix UX DOIVENT être validés par des scénarios d’acceptation (incluant erreurs/edge cases).

### IV. Performance comme exigence produit
La performance est une fonctionnalité.

- Toute feature DOIT définir des objectifs mesurables (latence p95, temps d’interaction, mémoire, etc.) quand pertinent.
- Les régressions de performance sont des bugs et DOIVENT être corrigées avant livraison.
- Les optimisations DOIVENT être guidées par des mesures (benchmarks/profiling), pas par intuition.

### V. Simplicité, lisibilité, et évolutivité contrôlée
On privilégie des solutions simples, testables, et faciles à faire évoluer.

- Éviter la sur-architecture; chaque complexité DOIT être justifiée par un besoin présent.
- Les interfaces (API internes/externes) DOIVENT être stables et couvertes par des tests d’acceptation/contrat.
- Le refactor est encouragé lorsqu’il réduit le risque ou accélère la livraison future, sans casser les scénarios.

## Standards de livraison (Qualité, ATDD, Performance)

- Chaque feature DOIT être décrite dans une spec avec:
  - user stories priorisées,
  - scénarios d’acceptation Given/When/Then,
  - critères de succès mesurables (incluant performance si applicable).
- Chaque PR DOIT inclure:
  - tests d’acceptation/parcours pertinents,
  - une vérification des erreurs/edge cases,
  - une note de risque (si changement sensible) et un plan de rollback si nécessaire.

## Workflow de développement (gates)

- Avant implémentation: scénarios d’acceptation rédigés (et alignés sur l’UX attendue).
- Pendant implémentation: itérations courtes; maintenir une branche livrable à tout moment.
- Avant merge:
  - scénarios d’acceptation passent,
  - pas de régression UX/perf détectée,
  - PR relue avec focus sur lisibilité, tests, et risques.

## Governance

- Cette constitution prévaut sur les templates/specs/plans en cas de conflit.
- Toute PR DOIT vérifier la conformité (ATDD, UX, performance, qualité).
- Politique de version:
  - **MAJOR**: suppression/redéfinition incompatible d’un principe ou d’une règle de gouvernance.
  - **MINOR**: ajout d’un principe/section ou extension substantielle des exigences.
  - **PATCH**: clarifications, reformulations, corrections sans changement de sens.
- Amendements:
  - proposer un changement avec justification + impact (templates, process),
  - approuver via PR,
  - mettre à jour le Sync Impact Report en tête de fichier.

**Version**: 0.1.0 | **Ratified**: TODO(RATIFICATION_DATE) | **Last Amended**: 2026-04-19
