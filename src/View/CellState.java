package View;

public enum CellState {
    WATER,      // Bleu
    BOAT,       // Gris (bateau intact)
    HIT,        // Rouge (bateau touché)
    MISS,       // Blanc (dans l'eau)
    SUNK,       // Noir (bateau coulé)
    ISLAND,
    EXPLORED
}