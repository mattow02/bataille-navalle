package View;

import Controller.GameController;
import Model.Coordinates;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BattleGrid {
    private JPanel gridPanel;
    private GridCell[][] gridCells;
    private int gridSize;
    private GameController controller;
    private boolean isPlayerGrid;

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

        JPanel leftCoordinates = new JPanel(new GridLayout(gridSize, 1));
        leftCoordinates.setBackground(Color.WHITE);

        for (int row = 0; row < gridSize; row++) {
            char letter = (char)('A' + row);
            JLabel coordLabel = new JLabel(String.valueOf(letter), JLabel.CENTER);
            coordLabel.setFont(new Font("Arial", Font.BOLD, 14));
            coordLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            leftCoordinates.add(coordLabel);
        }

        JPanel gridContainer = new JPanel(new GridLayout(gridSize, gridSize, 1, 1));
        gridContainer.setBackground(Color.BLACK);

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                GridCell gridCell = new GridCell();

                CellState initialState;
                if (isPlayerGrid) {
                    initialState = controller.getPlayerCellState(row, col);
                } else {
                    initialState = controller.getEnemyCellState(row, col);
                }
                gridCell.setState(initialState);

                if (!isPlayerGrid) {
                    addClickListener(gridCell, row, col);
                }

                gridCells[row][col] = gridCell;
                gridContainer.add(gridCell.getCellPanel());
            }
        }

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
                handleCellClick(row, col);
            }
        });
    }

    private void handleCellClick(int row, int col) {
        controller.getBattleController().handlePlayerAttack(row, col);

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

                CellState currentState;
                if (isPlayerGrid) {
                    currentState = controller.getPlayerCellState(row, col);

                    if (currentState == CellState.WATER) {
                        Coordinates coord = new Coordinates(row, col);
                        Model.Map.GridCell modelCell = controller.getPlayerGrid().getCell(coord);

                        if (modelCell != null && modelCell.isIslandCell() && !modelCell.isHit()) {
                            currentState = CellState.ISLAND;
                        }
                    }

                } else {
                    currentState = controller.getEnemyCellState(row, col);
                }

                gridCell.setState(currentState);
            }
        }

        if (gridPanel != null) {
            gridPanel.revalidate();
            gridPanel.repaint();
        }
    }

}