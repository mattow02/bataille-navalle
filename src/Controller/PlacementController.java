package Controller;

import Model.Boat.Boat;
import Model.Coordinates;
import Model.Orientation;
import java.util.ArrayList;
import java.util.List;

public class PlacementController implements Observer {
    private GameController mainController;
    private List<Boat> boatsToPlace;
    private List<Boat> placedBoats;

    public PlacementController(GameController mainController, List<Boat> boatsToPlace) {
        this.mainController = mainController;
        this.boatsToPlace = new ArrayList<>(boatsToPlace);
        this.placedBoats = new ArrayList<>();
    }

    public void handleBoatPlaced(Boat boat, Coordinates startCoord, Orientation orientation) {
        System.out.println(" Placement manuel: " + boat.name() + " en " + startCoord + " " + orientation);

        // Vérifier si le placement est valide
        if (mainController.getPlayerGrid().placeBoat(boat, startCoord, orientation)) {
            placedBoats.add(boat);
            System.out.println(boat.name() + " placé avec succès");
        } else {
            System.out.println(" Placement invalide pour " + boat.name());
            // Notifier la vue de l'erreur
        }
    }

    public void handleRandomPlacement(Boat boat) {


        boolean placed = mainController.placeBoatRandomly(boat, mainController.getPlayerGrid());
        if (placed) {
            placedBoats.add(boat);
            System.out.println( boat.name() + " placé aléatoirement");
        } else {
            System.out.println(" Échec placement aléatoire pour " + boat.name());
        }
    }

    public void handleStartBattle() {
        if (isPlacementComplete()) {

            mainController.showBattleView();
        } else {
            System.out.println(" Placement incomplet !");
        }
    }

    public boolean isPlacementComplete() {
        return placedBoats.size() == boatsToPlace.size() + placedBoats.size();
    }

    @Override
    public void update(Object event) {
        // Gérer les événements de placement
    }
}