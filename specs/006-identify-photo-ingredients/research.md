# Phase 0 Research - Identify Photo Ingredients

## Decision 1: Kotlin Android natif avec UI responsive Jetpack Compose

- **Decision**: Implémenter la feature en Kotlin Android natif avec Compose Material3 adaptatif.
- **Rationale**: Cohérent avec le projet existant, excellente maîtrise des tailles d'écran Android, dépendance UI unique et moderne.
- **Alternatives considered**:
  - XML Views classiques: plus verbeux et plus coûteux pour la réactivité multi-formats.
  - Framework multiplateforme: introduit des couches supplémentaires non nécessaires au MVP.

## Decision 2: Prioriser Google AI Edge Gallery si capacité disponible

- **Decision**: Détecter au runtime la disponibilité des capacités Google AI Edge Gallery sur l'appareil et les utiliser en priorité pour la reconnaissance.
- **Rationale**: Exploite les accélérations et capacités IA locales quand elles existent, tout en restant on-device.
- **Alternatives considered**:
  - Forcer une seule implémentation OCR: risque de qualité/perf inférieure sur appareils compatibles AI Edge.
  - OCR cloud: rejeté pour non-conformité à l'exigence de confidentialité locale.

## Decision 3: Fallback OCR offline minimaliste et standalone

- **Decision**: Utiliser un fallback OCR local hors-ligne (ML Kit Text Recognition v2 on-device) si AI Edge Gallery n'est pas disponible.
- **Rationale**: Garantie fonctionnelle sur la majorité des appareils Android, sans réseau, avec dépendance unique de fallback.
- **Alternatives considered**:
  - Tesseract embarqué: poids binaire et maintenance plus élevés.
  - EasyOCR/Python bridge: non natif Android, complexité et footprint trop importants.

## Decision 4: Pipeline d'extraction déterministe post-OCR

- **Decision**: Appliquer un pipeline Kotlin déterministe pour segmentation ingrédients (virgules, points-virgules, parenthèses, allergènes) et fusion multilingue.
- **Rationale**: Réduit les dépendances NLP externes et facilite les tests d'acceptation.
- **Alternatives considered**:
  - Modèle NER embarqué dédié: coût mémoire/CPU supérieur pour gain limité MVP.
  - Parsing regex uniquement sans pré-normalisation: moins robuste aux formats réels d'étiquettes.

## Decision 5: Persistance minimale et cycle de vie image strict

- **Decision**: Conserver la photo uniquement en cache privé temporaire pendant traitement puis suppression automatique; persister uniquement métadonnées et liste validée.
- **Rationale**: Respect des exigences FR-009/FR-011 et réduction du risque sécurité.
- **Alternatives considered**:
  - Stockage permanent des photos: inutile pour la valeur MVP et contraire aux exigences.
  - Aucune persistance de métadonnées: réduit l'observabilité produit et le diagnostic qualité.
