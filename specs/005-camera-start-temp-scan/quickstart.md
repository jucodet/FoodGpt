# Quickstart: Démarrage caméra et scan temporaire

## Objectif
Valider rapidement le flux: ouverture app -> caméra -> scan -> analyse locale -> suppression image temporaire.

## Pré-requis
- Android Studio récent
- SDK Android installé (API cible projet)
- Smartphone Android physique recommandé (caméra réelle)
- Modèle/runtime Google AI Edge disponible localement sur l’appareil

## Lancer l’application
1. Ouvrir le projet dans Android Studio.
2. Compiler et installer l’app sur un appareil Android.
3. Autoriser la caméra au premier lancement.

## Scénarios de vérification manuelle

### 1) Démarrage caméra immédiat
1. Fermer complètement l’app.
2. Relancer l’app.
3. Vérifier que l’écran caméra est affiché immédiatement avec bouton `Scan`.

### 2) Scan temporaire + analyse locale
1. Pointer la caméra vers une étiquette d’ingrédients.
2. Appuyer sur `Scan`.
3. Vérifier les états UI: `capturing` puis `analyzing` puis `success` ou `error`.
4. Vérifier qu’aucun upload réseau tiers n’est réalisé pendant l’analyse.

### 3) Nettoyage image temporaire
1. Réaliser un scan réussi.
2. Vérifier dans les logs applicatifs que le fichier temporaire est supprimé.
3. Répéter avec un échec forcé (ex: image très floue) et vérifier la suppression.

### 4) Permission refusée
1. Révoquer la permission caméra dans les paramètres système.
2. Ouvrir l’app.
3. Vérifier l’état `permission_denied` et la possibilité de réessayer après autorisation.

## Critères d’acceptation clés
- Ecran caméra + bouton scan visibles au démarrage.
- Analyse exécutée on-device.
- Photo de scan non persistée durablement.
- Etats utilisateur explicites et cohérents.

