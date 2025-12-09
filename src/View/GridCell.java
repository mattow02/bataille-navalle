package View;

import javax.swing.*;
import java.awt.*;

public class GridCell {
    private JPanel cellPanel;
    private CellState state;
    private CellState currentState;

    public GridCell() {
        this.state = CellState.WATER;
        createCell();
    }

    private void createCell() {
        cellPanel = new JPanel();
        cellPanel.setPreferredSize(new Dimension(30, 30));
        cellPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        updateAppearance();
    }


    private void updateAppearance() {
        switch(state) {
            case WATER:
                cellPanel.setBackground(Color.BLUE);
                break;
            case BOAT:
                cellPanel.setBackground(Color.GRAY);
                break;
            case HIT:
                cellPanel.setBackground(Color.RED);
                break;
            case MISS:
                cellPanel.setBackground(Color.WHITE);
                break;
            case SUNK:
                cellPanel.setBackground(Color.BLACK);
                break;
            default:
                cellPanel.setBackground(Color.BLUE);
        }
    }

    public JPanel getCellPanel() {
        return cellPanel;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.currentState = state;
        Color color;

        switch (state) {
            case WATER:
                color = Color.BLUE;
                break;
            case BOAT:
                color = Color.LIGHT_GRAY;
                break;
            case HIT:
                color = Color.RED;
                break;
            case MISS:
                color = Color.WHITE;
                break;
            case SUNK:
                color = Color.BLACK;
                break;
            case ISLAND:
                color = new Color(139, 69, 19);
                break;
            case EXPLORED:
                color = new Color(220, 220, 220);
                break;
            case ITEM_FOUND:
                color = Color.YELLOW;
                break;
            default:
                color = Color.MAGENTA;
                break;
            case TRAP:
                color = Color.MAGENTA;
                break;
        }

        if (cellPanel != null) {
            cellPanel.setBackground(color);
        }
    }
}