package View;

import Controller.GameController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BattleGrid {
    private JPanel gridPanel;
    private GridCell[][] gridCells;
    private int gridSize;
    private GameController controller;
    private boolean isPlayerGrid; //  TRUE = grille joueur, FALSE = grille ennemie

    //  NOUVEAU CONSTRUCTEUR avec type de grille
    public BattleGrid(int gridSize, GameController controller, boolean isPlayerGrid) {
        this.gridSize = gridSize;
        this.controller = controller;
        this.isPlayerGrid = isPlayerGrid;
        this.gridCells = new GridCell[gridSize][gridSize];
        createGrid();
    }

    private void createGrid() {
        gridPanel = new JPanel(new BorderLayout());
        JPanel centeredGrid = createGridWithCoordinates();
        gridPanel.add(centeredGrid, BorderLayout.CENTER);
    }

    private JPanel createGridWithCoordinates() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // COORDONNÉES EN HAUT
        JPanel topCoordinates = new JPanel(new GridLayout(1, gridSize + 1));
        topCoordinates.setBackground(Color.WHITE);
        topCoordinates.add(new JLabel(""));

        for (int col = 0; col < gridSize; col++) {
            JLabel coordLabel = new JLabel(String.valueOf(col + 1), JLabel.CENTER);
            coordLabel.setFont(new Font("Arial", Font.BOLD, 14));
            coordLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            topCoordinates.add(coordLabel);
        }
        mainPanel.add(topCoordinates, BorderLayout.NORTH);

        // COORDONNÉES À GAUCHE
        JPanel leftCoordinates = new JPanel(new GridLayout(gridSize, 1));
        leftCoordinates.setBackground(Color.WHITE);

        for (int row = 0; row < gridSize; row++) {
            char letter = (char)('A' + row);
            JLabel coordLabel = new JLabel(String.valueOf(letter), JLabel.CENTER);
            coordLabel.setFont(new Font("Arial", Font.BOLD, 14));
            coordLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            leftCoordinates.add(coordLabel);
        }

        // GRILLE CENTRALE
        JPanel gridContainer = new JPanel(new GridLayout(gridSize, gridSize, 1, 1));
        gridContainer.setBackground(Color.BLACK);

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                GridCell gridCell = new GridCell();

                //  ÉTAT INITIAL DIFFÉRENT SELON LE TYPE DE GRILLE
                CellState initialState;
                if (isPlayerGrid) {
                    // Grille joueur : bateaux visibles
                    initialState = controller.getPlayerCellState(row, col);
                } else {
                    // Grille ennemie : toujours eau (bateaux cachés)
                    initialState = controller.getEnemyCellState(row, col);
                }
                gridCell.setState(initialState);

                // AJOUTER LISTENER UNIQUEMENT SUR GRILLE ENNEMIE
                if (!isPlayerGrid) {
                    addClickListener(gridCell, row, col);
                }

                gridCells[row][col] = gridCell;
                gridContainer.add(gridCell.getCellPanel());
            }
        }

        // ASSEMBLAGE
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(leftCoordinates, BorderLayout.WEST);
        centerPanel.add(gridContainer, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private void addClickListener(GridCell gridCell, int row, int col) {
        gridCell.getCellPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(" Clic sur case ennemie (" + row + "," + col + ")");
                handleCellClick(row, col);
            }
        });
    }

    private void handleCellClick(int row, int col) {
        char letter = (char)('A' + row);
        int number = col + 1;
        System.out.println(" Tir ennemi en " + letter + number);

        // Controller gère la logique métier
        controller.getBattleController().handlePlayerAttack(row, col);

        // Mettre à jour l'affichage de la case ennemie
        CellState newState = controller.getEnemyCellStateAfterAttack(row, col);
        gridCells[row][col].setState(newState);
    }

    public JPanel getGridPanel() {
        return gridPanel;
    }

    public void refreshGrid() {


        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                GridCell gridCell = gridCells[row][col];

                //  FORCER LA MISE À JOUR DE TOUTES LES CASES
                CellState currentState;
                if (isPlayerGrid) {
                    currentState = controller.getPlayerCellState(row, col);
                } else {
                    currentState = controller.getEnemyCellState(row, col);
                }

                //  TOUJOURS METTRE À JOUR, MÊME SI L'ÉTAT N'A PAS CHANGÉ
                gridCell.setState(currentState);

                //  DEBUG : Afficher les cases de bateaux coulés
                if (currentState == CellState.SUNK) {

                }
            }
        }

        //  FORCER LE RAFRAÎCHISSEMENT VISUEL
        if (gridPanel != null) {
            gridPanel.revalidate();
            gridPanel.repaint();
        }
    }


}