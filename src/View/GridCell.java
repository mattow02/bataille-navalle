package View;

import javax.swing.*;
import java.awt.*;

public class GridCell {
    private JPanel cellPanel;
    private final CellState state;

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



    public void setState(CellState state) {
        Color color = switch (state) {
            case WATER -> Color.BLUE;
            case BOAT -> Color.LIGHT_GRAY;
            case HIT -> Color.RED;
            case MISS -> Color.WHITE;
            case SUNK -> Color.BLACK;
            case ISLAND -> new Color(139, 69, 19);
            case EXPLORED -> Color.GREEN;
            case ITEM_FOUND -> Color.YELLOW;
            default -> Color.MAGENTA;
        };

        if (cellPanel != null) {
            cellPanel.setBackground(color);
        }
    }
}