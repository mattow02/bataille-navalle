package View;

import Controller.BattleController;
import Controller.GameController;
import Controller.Observer;
import Model.Player.HumanPlayer;

import javax.swing.*;
import java.awt.*;

public class BattleView implements Observer {
    private GameController controller;
    private int gridSize;
    private JFrame frame;

    private BattleGrid playerBattleGrid;
    private BattleGrid enemyBattleGrid;
    private InfoPanel infoPanel;

    private JRadioButton missileBtn;
    private JRadioButton bombBtn;
    private JRadioButton sonarBtn;
    private JPanel weaponPanel;

    public BattleView(GameController controller, int gridSize) {
        this.controller = controller;
        this.gridSize = gridSize;
        controller.addObserver(this);
    }

    // Construit et affiche l'interface de bataille complète
    public void display() {
        frame = new JFrame("Bataille Navale - Mode Avancé");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("BATAILLE NAVALE - Votre flotte vs Flotte ennemie", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        frame.add(title, BorderLayout.NORTH);

        // Deux grilles côte à côte : joueur et ennemi
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel playerPanel = createGridPanel("VOTRE FLOTTE", true);
        mainPanel.add(playerPanel);

        JPanel enemyPanel = createGridPanel("FLOTTE ENNEMIE", false);
        mainPanel.add(enemyPanel);

        frame.add(mainPanel, BorderLayout.CENTER);

        // Panneau d'infos à droite (stats et historique)
        infoPanel = new InfoPanel();
        frame.add(infoPanel, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        weaponPanel = createWeaponSelector();
        bottomPanel.add(weaponPanel, BorderLayout.NORTH);

        JLabel instructions = new JLabel("Cliquez sur la grille ENNEMIE pour attaquer", JLabel.CENTER);
        instructions.setFont(new Font("Arial", Font.ITALIC, 12));
        instructions.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        bottomPanel.add(instructions, BorderLayout.SOUTH);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        refreshGrids();
        updateWeaponCounts();
        updateInfoPanel();
    }

    private JPanel createWeaponSelector() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("ARSENAL"));

        missileBtn = new JRadioButton("Missile (Infini)");
        bombBtn = new JRadioButton("Bombe (0)");
        sonarBtn = new JRadioButton("Sonar (0)");

        missileBtn.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(missileBtn);
        group.add(bombBtn);
        group.add(sonarBtn);

        missileBtn.addActionListener(e -> controller.getBattleController().selectWeapon(BattleController.WeaponMode.MISSILE));
        bombBtn.addActionListener(e -> controller.getBattleController().selectWeapon(BattleController.WeaponMode.BOMB));
        sonarBtn.addActionListener(e -> controller.getBattleController().selectWeapon(BattleController.WeaponMode.SONAR));

        panel.add(missileBtn);
        panel.add(bombBtn);
        panel.add(sonarBtn);

        return panel;
    }

    private JPanel createGridPanel(String title, boolean isPlayerGrid) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        BattleGrid battleGrid = new BattleGrid(gridSize, controller, isPlayerGrid);

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
                refreshGrids();
                updateWeaponCounts();
                updateInfoPanel();
            }
            else if (message.startsWith("GAME_OVER:")) {
                String result = message.substring(10);
                showEndGameView(result);
            }
            else if (message.startsWith("LOG:")) {
                String logText = message.substring(4);
                if (infoPanel != null) {
                    infoPanel.addLog(logText);
                }
            }
        }
    }

    private void refreshGrids() {
        if (playerBattleGrid != null) playerBattleGrid.refreshGrid();
        if (enemyBattleGrid != null) enemyBattleGrid.refreshGrid();
    }

    private void updateWeaponCounts() {
        HumanPlayer player = controller.getHumanPlayer();
        if (player != null && bombBtn != null) {
            bombBtn.setText("Bombe (" + player.getBombCount() + ")");
            sonarBtn.setText("Sonar (" + player.getSonarCount() + ")");

            bombBtn.setEnabled(player.hasBomb());
            sonarBtn.setEnabled(player.hasSonar());

            if (bombBtn.isSelected() && !player.hasBomb()) missileBtn.doClick();
            if (sonarBtn.isSelected() && !player.hasSonar()) missileBtn.doClick();
        }
    }

    private void updateInfoPanel() {
        if (infoPanel != null && controller != null) {
            int currentTurn = controller.getTurnCount();
            infoPanel.updateStats(currentTurn, controller.getHumanPlayer(), controller.getComputerPlayer());
        }
    }

    private void showEndGameView(String result) {
        if (frame != null) {
            frame.setVisible(false);
        }
        View.EndGameView endGameView = new View.EndGameView(result, controller);
        endGameView.display();
    }
}