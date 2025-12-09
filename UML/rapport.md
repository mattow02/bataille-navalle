# **Rapport de Conception - Bataille Navale**

## **Architecture et Organisation**

Nous avons structuré notre application selon une architecture **MVC**, mais avec des choix délibérés qui distinguent notre approche.

La séparation des responsabilités a guidé chaque décision. Le **Modèle** contient exclusivement la logique métier et les données, sans aucune connaissance de l'interface. La **Vue** se concentre sur l'affichage et la capture des interactions utilisateur. Le **Contrôleur** dirige l'ensemble, faisant le lien entre l'affichage et les données.

## **Communication et Événements**

Le système d'observateurs a été placé délibérément dans le **Contrôleur**. Souvent ce n'est pas le cas, mais au moins cela garantit que le **Modèle** reste parfaitement indépendant. Le Modèle émet des événements sans savoir qui les écoute, préservant ainsi sa réutilisabilité.

Les contrôleurs spécialisés représentent un autre choix significatif. Plutôt que d'avoir un seul contrôleur, nous avons choisi de diviser par phase de jeu : configuration, placement, combat. Chaque contrôleur maîtrise parfaitement son domaine, simplifiant la maintenance et les tests.

## **Conception Technique**

La grille de jeu est décomposée en deux entités complémentaires : la **Grid** pour la vision globale et les opérations d'ensemble, la **Cell** pour l'état individuel et le comportement de chaque cellule. Cette séparation permet d'isoler les modifications et d'optimiser les performances.

Le système d'armes et de bateaux utilise massivement les interfaces. Chaque nouvelle arme ou type de bateau implémente un contrat clair, permettant des extensions sans modification du code existant. Cette approche par contrat assure la cohérence tout en autorisant une grande flexibilité.

## **Bilan des Choix**

Notre approche privilégie la robustesse à long terme sur la simplicité immédiate. Chaque décision s'inscrit dans une vision cohérente où la séparation des **concerns**, la modularité et l'extensibilité guident le développement.

Le résultat est une architecture qui peut évoluer sereinement, accueillir de nouvelles fonctionnalités et résister à l'épreuve du temps, tout en fournissant une base solide pour le développement des fonctionnalités actuelles et futures.
