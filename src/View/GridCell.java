package View;

import javax.swing.*;
import java.awt.*;

public class GridCell {
    private JPanel cellPanel;
    private CellState state;

    public GridCell() {
        this.state = CellState.WATER; // Par défaut, eau
        createCell();
    }

    private void createCell() {
        cellPanel = new JPanel();
        cellPanel.setPreferredSize(new Dimension(30, 30));
        cellPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        updateAppearance();
    }

    public void setState(CellState newState) {
        this.state = newState;
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
}