package View;

import Controller.PlacementController;
import Controller.Observer;
import Model.Boat.Boat;
import Model.Orientation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PlacementView implements Observer {
    private PlacementController controller;
    private JFrame frame;
    private GridCell[][] gridCells;
    private int gridSize;
    private JLabel statusLabel;
    private JButton rotateButton;

    public PlacementView(PlacementController controller) {
        this.controller = controller;
        this.gridSize = controller.getGridSize();
        controller.addObserver(this);
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Placement de vos navires");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(500, 600);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("MODE PLACEMENT", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        statusLabel = new JLabel("Initialisation...", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        topPanel.add(titleLabel);
        topPanel.add(statusLabel);
        frame.add(topPanel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(gridSize, gridSize, 1, 1));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridPanel.setBackground(Color.BLACK);

        gridCells = new GridCell[gridSize][gridSize];

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                GridCell cell = new GridCell();
                cell.setState(CellState.WATER);

                int r = row;
                int c = col;
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

        JPanel bottomPanel = new JPanel();
        rotateButton = new JButton("Pivoter (Horizontal)");
        rotateButton.addActionListener(e -> controller.toggleOrientation());

        bottomPanel.add(rotateButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        refreshView();
    }

    public void display() {
        frame.setVisible(true);
    }

    public void close() {
        frame.dispose();
    }

    private void refreshView() {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                CellState state = controller.getCellState(row, col);
                gridCells[row][col].setState(state);
            }
        }

        String name = controller.getCurrentItemName();
        int size = controller.getCurrentItemSize();

        if (!name.isEmpty()) {
            String orientation = controller.getOrientation() == Orientation.HORIZONTAL ? "Horizontal" : "Vertical";

            statusLabel.setText("Placez : " + name + " (" + size + " case" + (size > 1 ? "s" : "") + ") - " + orientation);

            rotateButton.setText("Pivoter (" + orientation + ")");
            rotateButton.setEnabled(size > 1);

        } else {
            statusLabel.setText("Placement terminé !");
        }
    }
    @Override
    public void update(Object event) {
        if (event instanceof String) {
            String msg = (String) event;

            if (msg.equals("UPDATE_PLACEMENT")) {
                refreshView();
            } else if (msg.equals("INVALID_PLACEMENT")) {
                JOptionPane.showMessageDialog(frame, "Impossible de placer le bateau ici !", "Erreur", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}