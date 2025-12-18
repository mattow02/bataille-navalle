package View.Component;

import View.Model.CellState;

import javax.swing.*;
import java.awt.*;

/** Cellule graphique d'une grille Swing. */
public class GridCell {
    private final JPanel cellPanel;

    public GridCell() {
        cellPanel = new JPanel();
        cellPanel.setPreferredSize(new Dimension(30, 30));
        cellPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        setState(CellState.WATER);
    }

    public JPanel getCellPanel() {
        return cellPanel;
    }

    public void setState(CellState state) {
        var color = switch (state) {
            case WATER -> Color.BLUE;
            case BOAT -> Color.LIGHT_GRAY;
            case HIT -> Color.RED;
            case MISS -> Color.WHITE;
            case SUNK -> Color.BLACK;
            case ISLAND -> new Color(139, 69, 19);
            case EXPLORED -> new Color(205, 133, 63); // île explorée sans objet
            case ITEM_FOUND -> Color.GREEN;           // objet découvert sur l'île
            default -> Color.MAGENTA;
        };
        cellPanel.setBackground(color);
    }
}
