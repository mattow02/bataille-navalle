package View.Screen;

import Controller.Placement.IPlacementController;
import Controller.Placement.PlacementError;
import View.Component.GridCell;
import View.Model.CellState;
import View.PlacementViewInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Vue Swing pour le placement des entités. */
public class PlacementView implements PlacementViewInterface {
    private final IPlacementController controller;
    private JFrame frame;
    private GridCell[][] gridCells;
    private final int gridSize;
    private JLabel statusLabel;
    private JButton rotateButton;

    public PlacementView(IPlacementController controller) {
        this.controller = controller;
        this.gridSize = controller.getGridSize();
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Placement de vos navires");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(500, 600);

        var topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        var titleLabel = new JLabel("MODE PLACEMENT", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        statusLabel = new JLabel("Initialisation...", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        topPanel.add(titleLabel);
        topPanel.add(statusLabel);
        frame.add(topPanel, BorderLayout.NORTH);

        var gridPanel = new JPanel(new GridLayout(gridSize, gridSize, 1, 1));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridPanel.setBackground(Color.BLACK);

        gridCells = new GridCell[gridSize][gridSize];

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                var cell = new GridCell();
                cell.setState(CellState.WATER);

                var r = row;
                var c = col;
                cell.getCellPanel().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        controller.handleGridClick(r, c);
                    }
                });

                gridCells[row][col] = cell;
                gridPanel.add(cell.getCellPanel());
            }
        }
        frame.add(gridPanel, BorderLayout.CENTER);

        var bottomPanel = new JPanel();
        rotateButton = new JButton("Pivoter (Horizontal)");
        rotateButton.addActionListener(e -> controller.toggleOrientation());

        bottomPanel.add(rotateButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        refreshView();
    }

    @Override
    public void display() {
        frame.setVisible(true);
    }

    @Override
    public void close() {
        if (frame != null) frame.dispose();
    }

    @Override
    public void showPlacementFinished() {
        JOptionPane.showMessageDialog(frame,
                "Flotte et Pièges prêts ! La bataille commence...",
                "Déploiement terminé",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showError(PlacementError error) {
        if (error == PlacementError.INVALID_PLACEMENT) {
            JOptionPane.showMessageDialog(frame, "Impossible de placer le bateau ici !", "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void refreshView() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                var state = controller.getCellState(row, col);
                gridCells[row][col].setState(state);
            }
        }

        var name = controller.getCurrentItemName();
        var size = controller.getCurrentItemSize();

        if (!name.isEmpty()) {
            var horizontal = controller.isHorizontal();
            var orientationStr = horizontal ? "Horizontal" : "Vertical";
            statusLabel.setText("Placez : " + name + " (" + size + " case" + (size > 1 ? "s" : "") + ") - " + orientationStr);
            rotateButton.setText("Pivoter (" + orientationStr + ")");
            rotateButton.setEnabled(size > 1);
        } else {
            statusLabel.setText("Placement terminé !");
        }
    }
}
