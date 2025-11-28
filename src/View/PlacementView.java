package View;

import Controller.PlacementController;
import Model.Boat.Boat;
import Model.Boat.BoatType;
import Model.Coordinates;
import Model.Orientation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PlacementView implements GameView {
    private PlacementController controller;
    private JFrame frame;
    private BattleGrid placementGrid;
    private List<Boat> boatsToPlace;
    private Boat currentBoat;
    private Orientation currentOrientation = Orientation.HORIZONTAL;
    private JLabel instructionLabel;

    public PlacementView(PlacementController controller, List<Boat> boatsToPlace) {
        this.controller = controller;
        this.boatsToPlace = new ArrayList<>(boatsToPlace);
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Placement des Bateaux - Bataille Navale");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600);
        frame.setLayout(new BorderLayout());

        // Titre
        JLabel titleLabel = new JLabel(" Placement de Votre Flotte", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Instructions
        instructionLabel = new JLabel("", JLabel.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        updateInstructions();
        mainPanel.add(instructionLabel, BorderLayout.NORTH);

        // Grille de placement
        placementGrid = new BattleGrid(10, controller, true); // Grille joueur
        mainPanel.add(placementGrid.getGridPanel(), BorderLayout.CENTER);

        // Panel de contrôle
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        frame.add(mainPanel, BorderLayout.CENTER);

        // Ajouter les listeners de souris
        addGridListeners();

        // Commencer avec le premier bateau
        nextBoat();
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        // Bouton rotation
        JButton rotateButton = new JButton("🔄 Rotation");
        rotateButton.addActionListener(e -> rotateBoat());

        // Bouton aléatoire
        JButton randomButton = new JButton("🎲 Placement Aléatoire");
        randomButton.addActionListener(e -> placeRandomly());

        // Bouton validation
        JButton validateButton = new JButton("✅ Démarrer la Bataille");
        validateButton.addActionListener(e -> validatePlacement());

        panel.add(rotateButton);
        panel.add(randomButton);
        panel.add(validateButton);

        return panel;
    }

    private void addGridListeners() {
        // Récupérer le panel de la grille et ajouter les listeners
        // (À adapter selon votre implémentation BattleGrid)
        placementGrid.getGridPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentBoat == null) return;

                // Calculer la case cliquée
                Point point = e.getPoint();
                // Logique pour convertir les coordonnées souris → case grille
                // (À adapter selon votre BattleGrid)

                // Pour l'instant, simulation
                System.out.println("🖱️ Clic sur la grille de placement");
            }
        });
    }

    private void updateInstructions() {
        if (currentBoat != null) {
            instructionLabel.setText("Placez votre " + currentBoat.name() + " (" + currentBoat.size() + " cases) - " +
                    (currentOrientation == Orientation.HORIZONTAL ? "Horizontal" : "Vertical"));
            instructionLabel.setForeground(Color.BLUE);
        } else if (boatsToPlace.isEmpty()) {
            instructionLabel.setText(" Tous les bateaux sont placés ! Cliquez sur 'Démarrer la Bataille'");
            instructionLabel.setForeground(Color.GREEN);
        } else {
            instructionLabel.setText(" Erreur: plus de bateaux à placer");
            instructionLabel.setForeground(Color.RED);
        }
    }

    private void nextBoat() {
        if (!boatsToPlace.isEmpty()) {
            currentBoat = boatsToPlace.remove(0);
            updateInstructions();
        } else {
            currentBoat = null;
            updateInstructions();
        }
    }

    private void rotateBoat() {
        currentOrientation = (currentOrientation == Orientation.HORIZONTAL) ?
                Orientation.VERTICAL : Orientation.HORIZONTAL;
        updateInstructions();
        System.out.println(" Rotation: " + currentOrientation);
    }

    private void placeRandomly() {
        if (currentBoat != null) {
            controller.handleRandomPlacement(currentBoat);
            nextBoat();
        }
    }

    private void validatePlacement() {
        if (controller.isPlacementComplete()) {
            controller.handleStartBattle();
            frame.dispose();
        } else {
            JOptionPane.showMessageDialog(frame,
                    " Vous devez placer tous les bateaux avant de commencer !",
                    "Placement Incomplet", JOptionPane.WARNING_MESSAGE);
        }
    }

    //  Méthode pour afficher un bateau placé
    public void showBoatPlacement(Boat boat, java.util.List<Coordinates> positions) {
        System.out.println(" Affichage du " + boat.name() + " aux positions: " + positions);
        // Ici, vous mettriez à jour la grille visuelle
    }

    //  Méthode pour afficher une erreur de placement
    public void showPlacementError(String error) {
        JOptionPane.showMessageDialog(frame, error, "Erreur de Placement", JOptionPane.ERROR_MESSAGE);
    }

    public void display() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void update(Object data) {
        // Gérer les mises à jour (bateaux placés, etc.)
    }


    public void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }
}