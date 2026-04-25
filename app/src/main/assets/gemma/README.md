# Modèle Gemma (local)

Placez ici le fichier modèle **LiteRT-LM** (extension `.litertlm`), par ex. un Gemma packagé par la communauté LiteRT, sous le nom :

`gemma_model.litertlm`

- Chemin attendu dans l’APK : `assets/gemma/gemma_model.litertlm`
- Les fichiers volumineux sont ignorés par Git (voir `.gitignore`) : copie locale requise pour les builds de démo.
- Sans ce fichier, l’application affiche **« Gemma introuvable »** (parcours spécification 009).
²
Référence : `specs/009-llm-bilan-composition-ingredients/quickstart.md`.
