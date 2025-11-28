package View;

import Controller.GameController;
import Controller.Observer;
import javax.swing.*;
import java.awt.*;

//  IMPLEMENTS OBSERVER
public class BattleView implements Observer {
    private GameController controller;
    private int gridSize;
    private JFrame frame; //  SAUVEGARDER LA RÉFÉRENCE
    private BattleGrid playerBattleGrid; //  RÉFÉRENCE GRILLE JOUEUR
    private BattleGrid enemyBattleGrid;

    public BattleView(GameController controller, int gridSize) {
        this.controller = controller;
        this.gridSize = gridSize;

        //  ENREGISTREMENT OBSERVATEUR
        controller.addObserver(this);
    }

    public void display() {
        System.out.println("Affichage des DEUX grilles " + gridSize + "x" + gridSize);

        frame = new JFrame("Bataille Navale - Votre flotte vs Ennemi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Titre
        JLabel title = new JLabel("BATAILLE NAVALE - Votre flotte vs Flotte ennemie", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        frame.add(title, BorderLayout.NORTH);

        // Panel principal avec deux grilles
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Grille joueur (vos bateaux)
        JPanel playerPanel = createGridPanel("VOTRE FLOTTE", true);
        mainPanel.add(playerPanel);

        // Grille ennemi (où vous tirez)
        JPanel enemyPanel = createGridPanel("FLOTTE ENNEMIE", false);
        mainPanel.add(enemyPanel);

        frame.add(mainPanel, BorderLayout.CENTER);

        // Instructions
        JLabel instructions = new JLabel(" Cliquez sur la grille ENNEMIE pour attaquer", JLabel.CENTER);
        instructions.setFont(new Font("Arial", Font.ITALIC, 12));
        frame.add(instructions, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }

    private JPanel createGridPanel(String title, boolean isPlayerGrid) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        BattleGrid battleGrid = new BattleGrid(gridSize, controller, isPlayerGrid);

        //  SAUVEGARDER LES RÉFÉRENCES
        if (isPlayerGrid) {
            playerBattleGrid = battleGrid;
        } else {
            enemyBattleGrid = battleGrid;
        }

        panel.add(battleGrid.getGridPanel(), BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void update(Object event) {
        if (event instanceof String) {
            String message = (String) event;

            if (message.equals("REFRESH_ALL")) {
                // Rafraîchissement normal
                if (playerBattleGrid != null) playerBattleGrid.refreshGrid();
                if (enemyBattleGrid != null) enemyBattleGrid.refreshGrid();
            }
            else if (message.startsWith("GAME_OVER:")) {
                //  UTILISER ENDGAMEVIEW
                String result = message.substring(10);
                showEndGameView(result);
            }
        }
    }

    //  NOUVELLE MÉTHODE : Ouvrir EndGameView
    private void showEndGameView(String result) {


        // Masquer BattleView
        if (frame != null) {
            frame.setVisible(false);
        }

        // Ouvrir EndGameView
        View.EndGameView endGameView = new View.EndGameView(result, controller);
        endGameView.display();
    }



}