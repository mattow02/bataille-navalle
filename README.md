# 🚢 Bataille Navale (Java MVC)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-GUI-blue?style=for-the-badge)
![MVC](https://img.shields.io/badge/Pattern-MVC-green?style=for-the-badge)

Implémentation complète et modernisée de la bataille navale en Java 17. L'application suit une architecture **MVC** stricte, propose deux modes de jeu, un arsenal varié, des pièges/bonus, ainsi qu’une IA paramétrable.

---

## 📑 Sommaire
- [Fonctionnalités clés](#-fonctionnalités-clés)
- [Règles et contenus](#-règles-et-contenus)
- [Déroulement d’une partie](#-déroulement-dune-partie)
- [Installation & lancement](#-installation--lancement)
- [Architecture technique](#-architecture-technique)
- [UML & exploration](#-uml--exploration)
- [Auteur](#-auteur)

---

## 🧭 Fonctionnalités clés
- Plateau **10x10**, placement manuel, vérification des collisions et des zones interdites.
- **Deux modes** : Classique (pièges dispersés) ou **Mode Île** (zone centrale 4x4 avec bonus/pièges cachés).
- Arsenal évolutif : **Missile**, **Bombe** en croix, **Sonar** de détection.
- **Bonus d’île** (Bombe, Sonar) et **Pièges** (Tornade, Trou Noir) avec effets spéciaux.
- **IA paramétrable** : niveau Facile (tirs 100% aléatoires) ou Standard (chasse dès un premier impact).
- Interface Swing complète : configuration, placement interactif, bataille en temps réel avec journal d’événements.

---

## ⚓ Règles et contenus

### Plateau et flotte
- Grille fixe **10x10**.
- 5 types de bateaux :
  - Porte-avions (5), Croiseur (4), Contre-torpilleur (3), Sous-marin (3), Torpilleur (2).
- De 0 à 3 exemplaires par type, pour un maximum de **35 cases occupées** au total.
- Condition de victoire : couler tous les navires adverses.

### Modes de jeu
- **Classique**
  - Le joueur place sa flotte et **2 pièges** (1 Trou Noir, 1 Tornade).
  - L’IA place ses pièges aléatoirement.
  - Le joueur reçoit dès le début **1 Bombe** et **1 Sonar**.
- **Mode Île (avancé)**
  - Une zone centrale **4x4** est marquée comme île (placement de bateaux impossible).
  - L’île ennemie contient des **bonus** (Bombe, Sonar) et des **pièges** (Trou Noir, Tornade) cachés.
  - Aucun piège n’est placé côté joueur et aucune munition spéciale n’est donnée au départ : il faut explorer l’île ennemie pour en gagner.

### Arsenal et munitions
- **Missile** : tir standard sur 1 case (munitions illimitées).
- **Bombe** : frappe en **croix (5 cases)** autour de la cible, ignorée si la case est une île. Munition requise.
- **Sonar** : scanne un carré **3x3** autour de la cible et affiche le nombre de cibles détectables. Nécessite 1 munition **et un sous-marin vivant**.
- Les bonus d’île ajoutent des munitions (Bombe ou Sonar).

### Bonus & pièges
- **Bombe (bonus)** : +1 munition de Bombe.
- **Sonar (bonus)** : +1 munition de Sonar.
- **Tornade (piège)** : se déclenche au premier impact, puis pendant **3 tours** du tireur, chaque tir est décalé de `+5` lignes/colonnes (avec bouclage), rendant la visée instable.
- **Trou Noir (piège)** : se déclenche au premier impact. Le tireur subit un tir de retour sur la même coordonnée de **sa propre grille**. Le résultat est journalisé (impact ou absorption).
- Une case d’île révélée devient ensuite « Zone explorée ».

### Niveaux d’IA
- **Facile (niveau 1)** : tirs totalement aléatoires.
- **Standard (niveau 2, par défaut)** : après un premier impact, l’IA passe en mode chasse (tirs ciblés autour de la touche).

---

## 🎮 Déroulement d’une partie

1) **Configuration**
   - Choisissez le nombre de bateaux (0–3 par type, max 35 cases), le mode **Classique** ou **Île**, et le niveau de l’IA.
   - Le bouton « Configuration Par Défaut » pré-remplit 1 bateau de chaque type, mode Classique, IA Standard.

2) **Placement**
   - Placez vos bateaux (et, en mode Classique, vos deux pièges) dans l’ordre proposé.
   - Cliquez sur la grille pour poser l’élément courant ; utilisez « Pivoter » pour basculer Horizontal/Vertical.
   - Placement refusé en cas de chevauchement ou de cellule île.

3) **Bataille**
   - Sélectionnez l’arme (boutons radio) puis cliquez sur la **grille ennemie** pour tirer.
   - Les journaux indiquent chaque action (tirs, pièges, bonus, sonar…).
   - L’écran de fin affiche statistiques et options **Rejouer** ou **Quitter**.

---

## 🚀 Installation & lancement

### Prérequis
- **JDK 17+**
- Un IDE Java (IntelliJ/Eclipse/VS Code) ou un terminal.

### Lancer rapidement
1. Cloner le dépôt :
   ```bash
   git clone https://github.com/mattow02/bataille-navalle.git
   cd bataille-navalle
   ```
2. Compiler puis exécuter :
   ```bash
   javac -d bin src/**/*.java
   java -cp bin App
   ```
   (ou ouvrez le projet dans votre IDE et lancez `App.java`).

---

## 🏗️ Architecture technique
- **MVC strict** : `Model` (règles métier), `View` (Swing), `Controller` (orchestration et navigation).
- **Patterns** :
  - **Observer** : notifications Vue/Contrôleur sur chaque événement de jeu.
  - **Strategy** : IA (`RandomShotStrategy`, `TargetedShotStrategy`) et sélection d’armes.
  - **Factory** : bateaux, pièges, armes, items d’île.
  - **State simplifié** : états des cellules et des entités.
- **Modularité** :
  - `Model` : gestion des grilles, bateaux, armes, pièges/bonus, service de tours.
  - `Controller` : configuration, placement, flux de bataille, gestion UI/threads.
  - `View` : écrans Swing (`ConfigurationView`, `PlacementView`, `BattleView`, `EndGameView`) + composants (grilles, panneau d’info).

---

## 🗺️ UML & exploration
- Tous les diagrammes PlantUML sont dans `uml/` (Model, View, Controller + diagramme maître).
- Point d’entrée applicatif : `App.java` (instancie jeu, contrôleur et Swing).

---

## 👤 Auteur
- **Matteo** — [@mattow02](https://github.com/mattow02)
- **Halil** — [@Miterra](https://github.com/Miterra).

Projet réalisé pour approfondir la POO, les patterns de conception et la structuration MVC en Java.
