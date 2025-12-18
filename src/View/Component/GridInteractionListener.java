package View.Component;

@FunctionalInterface
/** Callback sur clic de cellule de grille. */
public interface GridInteractionListener {

    void onCellClicked(int row, int col);
}
