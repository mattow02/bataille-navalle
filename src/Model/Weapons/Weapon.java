package Model.Weapons;

import Model.Coordinates;
import Model.Map.Grid;
import Model.Player.Player;

/** Décrit une arme utilisable en combat. */
public interface Weapon {

    WeaponResult use(Player attacker, Grid targetGrid, Coordinates target);
}
