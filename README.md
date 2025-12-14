# 🚢 Bataille Navale (Java MVC)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)
![MVC](https://img.shields.io/badge/Pattern-MVC-green?style=for-the-badge)

Une implémentation avancée du jeu classique de la Bataille Navale en Java, utilisant une architecture **MVC (Modèle-Vue-Contrôleur)** stricte et plusieurs design patterns. Ce projet propose une expérience de jeu enrichie avec des modes spéciaux, des armes variées et une intelligence artificielle.

## 📋 Fonctionnalités

### 🎮 Modes de Jeu
* **Mode Classique** : Les règles traditionnelles.
* **Mode Île (Avancé)** : Ajoute des éléments stratégiques sur la carte :
    * 🏝️ **Îles** : Obstacles naturels.
    * 🎁 **Items** : Bonus à récupérer (Bombes, Sonars).
    * ⚠️ **Pièges** : Trous noirs et Tornades (déplacent les navires).

### 🤖 Intelligence Artificielle
L'ordinateur joue de manière autonome grâce au pattern **Strategy** :
* **RandomShotStrategy** : Tirs aléatoires (début de partie).
* **TargetedShotStrategy** : Mode "Chasse" dès qu'un navire est touché (tire autour de la cible).

### ⚔️ Arsenal
* **Missile** : Tir standard (1 case).
* **Bombe** : Dégâts de zone (3x3 ou croix).
* **Sonar** : Révèle la présence d'entités dans une zone sans faire de dégâts.

---

## 🏗️ Architecture Technique

Le projet respecte scrupuleusement le patron de conception **MVC** pour assurer la maintenabilité et la séparation des responsabilités.

### 1. Modèle (`Model`)
Contient toute la logique métier et les données du jeu. Il ne connaît pas la Vue.
* **Grid & GridCell** : Gestion de la grille et des états des cellules.
* **Entities** : Polymorphisme via l'interface `GridEntity` pour gérer les Bateaux (`Boat`), les Items (`IslandItem`) et les Pièges (`Tornado`, `BlackHole`).
* **Player** : Classe abstraite dérivée en `HumanPlayer` et `ComputerPlayer`.

### 2. Vue (`View`)
Gère l'affichage graphique (Swing). Elle observe le modèle mais ne le modifie jamais directement.
* **GameView** : Interface principale.
* **BattleView, PlacementView, ConfigurationView** : Écrans spécifiques du jeu.
* **Observer Pattern** : Les vues implémentent une interface pour se rafraîchir automatiquement lors des notifications du Contrôleur.

### 3. Contrôleur (`Controller`)
Le chef d'orchestre. Il reçoit les actions de l'utilisateur, met à jour le Modèle et notifie la Vue.
* **GameController** : Contrôleur principal qui gère le flux global.
* **BattleController** : Gère la phase de tir et l'utilisation des armes.
* **PlacementController** : Gère le placement manuel des navires.

### 📐 Design Patterns utilisés
* **MVC** : Structure globale.
* **Observer** : Communication entre le Modèle/Contrôleur et la Vue.
* **Strategy** : Comportement de l'IA (`ShotStrategy`).
* **Factory** : Création des bateaux (`BoatFactory`).
* **State (simplifié)** : Gestion de l'état des cellules (`CellState`).

---

## 🚀 Installation et Lancement

### Prérequis
* JDK 17 ou supérieur.
* Un IDE Java (IntelliJ, Eclipse, VS Code) ou un terminal.

### Comment lancer le projet

1.  **Cloner le dépôt :**
    ```bash
    git clone [https://github.com/mattow02/bataille-navalle.git](https://github.com/mattow02/bataille-navalle.git)
    cd bataille-navalle
    ```

2.  **Compiler et exécuter :**
    * Ouvrez le projet dans votre IDE et lancez la classe `App.java`.
    * Ou via le terminal :
        ```bash
        javac -d bin src/**/*.java
        java -cp bin App
        ```

---

## 📸 Aperçu

*(Tu pourras ajouter ici des captures d'écran de ton jeu une fois l'interface finie, par exemple :)*
* *L'écran de configuration.*
* *La grille de placement.*
* *La grille de bataille.*

---

## 👤 Auteur

**Matthieu** (et collaborateurs éventuels)
* GitHub : [@mattow02](https://github.com/mattow02)

---
*Projet réalisé dans le cadre d'un apprentissage approfondi de la POO et de l'architecture logicielle.*