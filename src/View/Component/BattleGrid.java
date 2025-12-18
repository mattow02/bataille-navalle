package View.Component;

import View.Model.CellState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Grille graphique interactive pour la bataille. */
public class BattleGrid {
    private JPanel gridPanel;
    private final GridCell[][] gridCells;
    private final int gridSize;
    private final GridInteractionListener listener;

    public BattleGrid(int gridSize, GridInteractionListener listener) {
        this.gridSize = gridSize;
        this.listener = listener;
        this.gridCells = new GridCell[gridSize][gridSize];
        createGrid();
    }

    private void createGrid() {
        gridPanel = new JPanel(new BorderLayout());

        var topCoordinates = new JPanel(new GridLayout(1, gridSize + 1));
        topCoordinates.setBackground(Color.WHITE);
        topCoordinates.add(new JLabel(""));
        for (var col = 0; col < gridSize; col++) {
            var cordLabel = new JLabel(String.valueOf(col + 1), JLabel.CENTER);
            cordLabel.setFont(new Font("Arial", Font.BOLD, 14));
            cordLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            topCoordinates.add(cordLabel);
        }
        gridPanel.add(topCoordinates, BorderLayout.NORTH);

        var mainCenter = new JPanel(new BorderLayout());

        var leftCoordinates = new JPanel(new GridLayout(gridSize, 1));
        leftCoordinates.setBackground(Color.WHITE);
        for (var row = 0; row < gridSize; row++) {
            var letter = (char) ('A' + row);
            var cordLabel = new JLabel(String.valueOf(letter), JLabel.CENTER);
            cordLabel.setFont(new Font("Arial", Font.BOLD, 14));
            cordLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            leftCoordinates.add(cordLabel);
        }
        mainCenter.add(leftCoordinates, BorderLayout.WEST);

        var gridContainer = new JPanel(new GridLayout(gridSize, gridSize, 1, 1));
        gridContainer.setBackground(Color.BLACK);

        for (var row = 0; row < gridSize; row++) {
            for (var col = 0; col < gridSize; col++) {
                var gridCell = new GridCell();
                gridCell.setState(CellState.WATER);

                if (listener != null) {
                    var r = row;
                    var c = col;
                    gridCell.getCellPanel().addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            listener.onCellClicked(r, c);
                        }
                    });
                }

                gridCells[row][col] = gridCell;
                gridContainer.add(gridCell.getCellPanel());
            }
        }
        mainCenter.add(gridContainer, BorderLayout.CENTER);
        gridPanel.add(mainCenter, BorderLayout.CENTER);
    }

    public void updateGrid(CellState[][] newStates) {
        for (var row = 0; row < gridSize; row++) {
            for (var col = 0; col < gridSize; col++) {
                if (newStates[row][col] != null) {
                    gridCells[row][col].setState(newStates[row][col]);
                }
            }
        }
        if (gridPanel != null) {
            gridPanel.revalidate();
            gridPanel.repaint();
        }
    }

    public JPanel getGridPanel() {
        return gridPanel;
    }
}
