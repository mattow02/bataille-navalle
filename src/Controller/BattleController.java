package Controller;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Map.Grid;
import Model.Map.GridCell;
import Model.Player.HumanPlayer;
import Model.Weapons.Bomb;
import Model.Weapons.Sonar;

public class BattleController implements Observer {
    private final GameController mainController;
    private final Grid targetGrid;
    // Types d'armes disponibles pour le joueur
    public enum WeaponMode { MISSILE, BOMB, SONAR }
    private WeaponMode currentWeaponMode = WeaponMode.MISSILE;

    public BattleController(GameController mainController) {
        this.mainController = mainController;
        this.targetGrid = mainController.getGrid();
    }

    @Override
    public void update(Object event) {
    }

    // Change l'arme sélectionnée avec vérifications (inventaire, sous-marin vivant)
    public void selectWeapon(WeaponMode mode) {
        HumanPlayer player = mainController.getHumanPlayer();

        if (mode == WeaponMode.BOMB) {
            if (player.hasBomb()) {
                currentWeaponMode = WeaponMode.BOMB;
                mainController.log("👉 Mode BOMBE activé (" + player.getBombCount() + " restantes)");
            } else {
                mainController.log("❌ Vous n'avez pas de bombe !");
            }
        } else if (mode == WeaponMode.SONAR) {
            if (player.hasSonar()) {
                // Le sonar nécessite un sous-marin encore en vie
                if (player.isSubmarineAlive()) {
                    currentWeaponMode = WeaponMode.SONAR;
                    mainController.log("👉 Mode SONAR activé (" + player.getSonarCount() + " restants)");
                } else {
                    mainController.log("❌ Impossible : Votre sous-marin est coulé !");
                }

            } else {
                mainController.log("❌ Vous n'avez pas de sonar !");
            }
        }
    }

    // Gère l'attaque du joueur selon l'arme sélectionnée (missile/bombe/sonar)
    public void handlePlayerAttack(int row, int col) {
        if (!mainController.isPlayerTurn()) {
            mainController.log("⏳ Attendez le tour de l'IA...");
            return;
        }

        Coordinates target = new Coordinates(row, col);
        HumanPlayer player = mainController.getHumanPlayer();

        // Utilisation du sonar : scanne une zone 3x3
        if (currentWeaponMode == WeaponMode.SONAR) {
            GridCell cell = targetGrid.getCell(target);
            if (cell != null && cell.isIslandCell()) {
                mainController.log("❌ Impossible d'utiliser le Sonar sur l'île !");
                return;
            }

            Sonar sonar = new Sonar();
            int detected = sonar.scan(targetGrid, target);
            mainController.log("📡 SONAR : " + detected + " entité(s) détectée(s).");

            player.useSonar();
            currentWeaponMode = WeaponMode.MISSILE;
            mainController.playerAttacked(HitOutcome.MISS);
            return;
        }

        // Utilisation de la bombe : explosion en croix (5 cases)
        if (currentWeaponMode == WeaponMode.BOMB) {
            Bomb bomb = new Bomb();
            bomb.use(targetGrid, target, player);
            mainController.log("💥 BOUM ! Bombe larguée en " + (char)('A' + row) + (col + 1));

            player.useBomb();
            currentWeaponMode = WeaponMode.MISSILE;
            mainController.playerAttacked(HitOutcome.HIT);
            return;
        }

        // Tir standard avec gestion de l'effet tornade
        if (mainController.getPlayerTornadoTurnsLeft() > 0) {
            Coordinates shiftedTarget = mainController.applyTornadoEffect(target);
            mainController.setPlayerTornadoTurnsLeft(mainController.getPlayerTornadoTurnsLeft() - 1);

            mainController.log("🌪️ VENT VIOLENT ! Tir dévié de " + target + " vers " + shiftedTarget);
            target = shiftedTarget;
        }

        if (!target.isValid(targetGrid.getSize())) {
            mainController.log("❌ Tir hors zone !");
            return;
        }

        GridCell targetCell = targetGrid.getCell(target);
        if (targetCell == null) return;

        Model.GridEntity targetEntity = targetCell.getEntity();

        String cordStr = (char)('A' + target.getRow()) + "" + (target.getColumn() + 1);
        String prefix = targetCell.isIslandCell() ? "Fouille en " : "Tir en ";

        HitOutcome outcome = targetCell.strike(player);
        HitOutcome finalOutcome = outcome;

        String resultMsg = "";

        switch(outcome) {
            case HIT: resultMsg = " : TOUCHÉ !"; break;
            case SUNK: resultMsg = " : COULÉ !"; break;
            case INVALID: resultMsg = " : Déjà touché."; break;

            case MISS:
                if (targetCell.isIslandCell()) resultMsg = " : Rien trouvé.";
                else resultMsg = " : Manqué (Dans l'eau).";
                break;

            case ACQUIRED_WEAPON:
                if (targetEntity instanceof Model.Island.BombItem) {
                    resultMsg = " : 💣 BOMBE trouvée ! (+1)";
                } else if (targetEntity instanceof Model.Island.SonarItem) {
                    resultMsg = " : 📡 SONAR trouvé ! (+1)";
                } else {
                    resultMsg = " : Objet spécial trouvé !";
                }
                break;

            case TRAP_TRIGGERED: resultMsg = " : ⚠️ PIÈGE DÉCLENCHÉ !"; break;
        }

        mainController.log(prefix + cordStr + resultMsg);

        // Gestion des pièges déclenchés
        if (outcome == HitOutcome.TRAP_TRIGGERED) {
            // Tornade : dévie les 3 prochains tirs
            if (targetEntity instanceof Model.Trap.Tornado) {
                mainController.log("🌪️ TORNADE ! Vos 3 prochains tirs seront déviés !");
                mainController.setPlayerTornadoTurnsLeft(3);
                finalOutcome = HitOutcome.MISS;

            // Trou noir : retourne le tir contre le joueur
            } else if (targetEntity instanceof Model.Trap.BlackHole) {
                mainController.log("⚫ TROU NOIR ! Le tir se retourne contre vous !");

                HitOutcome selfHit = mainController.getHumanPlayer().getOwnGrid().getCell(target).strike(mainController.getHumanPlayer());

                String selfMsg = "-> Auto-dégâts : ";
                switch(selfHit) {
                    case HIT: selfMsg += "Vous êtes TOUCHÉ !"; break;
                    case MISS: selfMsg += "Ouf, dans l'eau."; break;
                    case SUNK: selfMsg += "Vous avez COULÉ votre propre bateau !"; break;
                    default: selfMsg += "Aucun effet.";
                }
                mainController.log(selfMsg);
                finalOutcome = selfHit;
            }
        }

        mainController.playerAttacked(finalOutcome);
    }
}