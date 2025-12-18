package Model.Games;

import Model.Coordinates;
import Model.GameObserver;
import Model.HitOutcome;
import Model.Map.Grid;
import Model.Player.ComputerPlayer;
import Model.Player.HumanPlayer;
import Model.Player.Player;
import Model.SpecialEffectContext;
import Model.Weapons.WeaponResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/** Ordonne le déroulement des tours et effets spéciaux. */
public class TurnService {

    /** Contexte nécessaire pour exécuter un tour. */
    public interface TurnContext {

        Grid playerGrid();

        Grid enemyGrid();

        HumanPlayer humanPlayer();

        ComputerPlayer computerPlayer();

        int getPlayerAliveBoats();

        int getComputerAliveBoats();

        void notifyObservers(Consumer<GameObserver> notification);
    }

    private final TurnContext context;
    private final ReentrantReadWriteLock lock;
    private boolean isPlayerTurn;
    private int turnCount;
    private int playerTornadoTurnsLeft;
    private int computerTornadoTurnsLeft;
    private HitOutcome lastComputerOutcome;
    private final SpecialEffectContext effectContext;

    /** Initialise le service des tours. */
    public TurnService(TurnContext context, ReentrantReadWriteLock lock) {
        this.context = context;
        this.lock = lock != null ? lock : new ReentrantReadWriteLock();
        this.effectContext = new TurnSpecialEffectContext();
        reset();
    }

    /** Réinitialise l'état des tours. */
    public void reset() {
        lock.writeLock().lock();
        try {
            turnCount = 1;
            isPlayerTurn = true;
            playerTornadoTurnsLeft = 0;
            computerTornadoTurnsLeft = 0;
            lastComputerOutcome = null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** Gère l'attaque du joueur. */
    public void handlePlayerAttack(Model.Weapons.Weapon weapon, Coordinates target) {
        var notifications = new ArrayList<Consumer<GameObserver>>();
        var refreshNeeded = false;

        lock.writeLock().lock();
        try {
            if (!isPlayerTurn) return;
            var enemyGrid = context.enemyGrid();
            var humanPlayer = context.humanPlayer();
            if (enemyGrid == null || humanPlayer == null) return;

            if (playerTornadoTurnsLeft > 0) {
                target = applyTornadoEffect(target, enemyGrid);
                playerTornadoTurnsLeft--;
                var finalTarget = target;
                notifications.add(observer -> observer.onTornadoEffect(finalTarget));
            }

            var result = weapon.use(humanPlayer, enemyGrid, target);
            notifications.add(observer -> observer.onWeaponFired(result));
            refreshNeeded |= handleSpecialEffects(result.primaryOutcome(), target, enemyGrid, true, notifications);
            if (result.requiresRefresh()) {
                refreshNeeded = true;
            }

            if (result.endsTurn()) {
                isPlayerTurn = false;
                turnCount++;
                if (context.computerPlayer() != null && context.computerPlayer().isDefeated()) {
                    notifications.add(observer -> observer.onGameOver(
                            true,
                            turnCount,
                            context.getPlayerAliveBoats(),
                            context.getComputerAliveBoats()
                    ));
                    refreshNeeded = false;
                } else {
                    notifications.add(GameObserver::onPlayerTurnEnded);
                    refreshNeeded = true;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }

        if (refreshNeeded) {
            notifications.add(GameObserver::onRefreshRequested);
        }
        notifyAll(notifications);
    }

    /** Gère le tour de l'ordinateur. */
    public void resolveComputerTurn() {
        var notifications = new ArrayList<Consumer<GameObserver>>();
        var refreshNeeded = false;

        Coordinates chosenTarget;
        lock.readLock().lock();
        try {
            if (isPlayerTurn) return;
            var computer = context.computerPlayer();
            if (computer == null) return;
            chosenTarget = computer.chooseNextShot(lastComputerOutcome);
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            if (isPlayerTurn) return;
            var computer = context.computerPlayer();
            var human = context.humanPlayer();
            var playerGrid = context.playerGrid();
            if (computer == null || human == null || playerGrid == null) return;

            var finalTarget = chosenTarget;
            if (computerTornadoTurnsLeft > 0) {
                finalTarget = applyTornadoEffect(chosenTarget, playerGrid);
                computerTornadoTurnsLeft--;
                var tornadoTarget = finalTarget;
                notifications.add(observer -> observer.onTornadoEffect(tornadoTarget));
            }

            var outcome = computer.fire(finalTarget);
            lastComputerOutcome = outcome;
            computer.notifyShotResult(finalTarget, outcome);
            var shotTarget = finalTarget;
            notifications.add(observer -> observer.onShotResolved(shotTarget, outcome, false));
            refreshNeeded |= handleSpecialEffects(outcome, finalTarget, playerGrid, false, notifications);

            isPlayerTurn = true;
            refreshNeeded = true;
            if (human.isDefeated()) {
                notifications.add(observer -> observer.onGameOver(
                        false,
                        turnCount,
                        context.getPlayerAliveBoats(),
                        context.getComputerAliveBoats()
                ));
                refreshNeeded = false;
            }
        } finally {
            lock.writeLock().unlock();
        }

        if (refreshNeeded) {
            notifications.add(GameObserver::onRefreshRequested);
        }
        notifyAll(notifications);
    }

    /** Indique si c'est le tour du joueur. */
    public boolean isPlayerTurn() {
        lock.readLock().lock();
        try {
            return isPlayerTurn;
        } finally {
            lock.readLock().unlock();
        }
    }

    /** Nombre de tours écoulés. */
    public int getTurnCount() {
        lock.readLock().lock();
        try {
            return turnCount;
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean handleSpecialEffects(HitOutcome outcome, Coordinates target, Grid grid, boolean isHumanAttacker, List<Consumer<GameObserver>> notifications) {
        var hasTrapEffect = outcome == HitOutcome.TRAP_TRIGGERED;
        var hasItemEffect = outcome == HitOutcome.ACQUIRED_WEAPON && isHumanAttacker;
        if (!hasTrapEffect && !hasItemEffect) {
            return false;
        }

        var wasIslandCell = grid.isIslandCell(target);
        var entity = grid.extractEntity(target);
        if (entity != null) {
            entity.applySpecialEffectIfPresent(effectContext, target, isHumanAttacker);
        }
        // Après récupération (piège ou bonus), on laisse une trace "explorée" sur l'île.
        if (wasIslandCell) {
            grid.placeIslandEntity(new Model.Island.IslandEmpty(), target);
        }
        return true;
    }

    private Coordinates applyTornadoEffect(Coordinates original, Grid grid) {
        var size = grid.getSize();
        return new Coordinates((original.row() + 5) % size, (original.column() + 5) % size);
    }

    private class TurnSpecialEffectContext implements SpecialEffectContext {

        @Override
        public Grid playerGrid() {
            return context.playerGrid();
        }

        @Override
        public Grid enemyGrid() {
            return context.enemyGrid();
        }

        @Override
        public Player humanPlayer() {
            return context.humanPlayer();
        }

        @Override
        public Player computerPlayer() {
            return context.computerPlayer();
        }

        @Override
        public void notifyTornadoHit() {
            context.notifyObservers(GameObserver::onTornadoHit);
        }

        @Override
        public void grantTornadoTurnsToAttacker(boolean isHumanAttacker, int turns) {
            if (isHumanAttacker) {
                playerTornadoTurnsLeft = turns;
            } else {
                computerTornadoTurnsLeft = turns;
            }
        }

        @Override
        public void notifyBlackHoleTrigger() {
            context.notifyObservers(GameObserver::onBlackHoleTrigger);
        }

        @Override
        public void notifyBlackHoleBackfire(Coordinates coords) {
            var backfireCoord = coords;
            context.notifyObservers(observer -> observer.onBlackHoleBackfire(backfireCoord));
        }

        @Override
        public void notifyBlackHoleAbsorbed(Coordinates coords) {
            var absorbedCoord = coords;
            context.notifyObservers(observer -> observer.onBlackHoleAbsorbed(absorbedCoord));
        }

        @Override
        public void notifyItemBombFound() {
            context.notifyObservers(GameObserver::onItemBombFound);
        }

        @Override
        public void notifyItemSonarFound() {
            context.notifyObservers(GameObserver::onItemSonarFound);
        }
    }

    private void notifyAll(List<Consumer<GameObserver>> notifications) {
        if (notifications == null || notifications.isEmpty()) return;
        for (var notification : notifications) {
            if (notification != null) {
                context.notifyObservers(notification);
            }
        }
    }
}
