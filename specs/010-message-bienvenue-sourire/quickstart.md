# Quickstart - Message de bienvenue souriant

## Objectif

Verifier rapidement le comportement attendu de l'affichage de bienvenue apres connexion.

## Prerequis

- Build Android fonctionnel.
- Bibliotheque de messages de bienvenue configuree avec au moins 1 message actif en francais.
- Parcours de connexion disponible en environnement local.

## Maintenance de la bibliotheque de messages

- Fichier source: `app/src/main/assets/welcome/messages_fr.json`
- Regles: conserver `language=fr`, `isActive=true` pour messages exploitables, tags de ton `positif` et `chaleureux`.
- En cas de catalogue vide, l'application n'affiche aucun message de bienvenue (comportement attendu).

## Parcours de verification

1. Lancer l'application et effectuer une connexion reussie.
2. Verifier qu'un message de bienvenue apparait sur l'ecran d'accueil en moins de 2 secondes.
3. Se deconnecter puis se reconnecter 10 fois:
   - confirmer qu'un message valide est affiche a chaque connexion;
   - accepter qu'un message puisse se repeter consecutivement.
4. Vider/desactiver tous les messages actifs puis se reconnecter:
   - verifier qu'aucun message n'est affiche;
   - verifier qu'aucune erreur bloquante n'apparait.
5. Evaluer un echantillon de messages affiches:
   - confirmer le ton positif, chaleureux et motivant.

## Suite de tests recommandee

- Tests d'acceptation parcours connexion -> accueil.
- Tests unitaires de selection aleatoire et filtre des messages actifs.
- Tests UI pour les etats "message affiche" et "aucun message affiche".
