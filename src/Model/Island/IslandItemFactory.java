package Model.Island;

import Model.GridEntity;

import java.util.List;

/** Fabrique les items présents sur l'île. */
public interface IslandItemFactory {

    List<GridEntity> createIslandItems();
}
