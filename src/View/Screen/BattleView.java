package View.Screen;

import Controller.IGameController;
import View.BattleViewInterface;
import View.Component.BattleGrid;
import View.Component.GridInteractionListener;
import View.Component.InfoPanel;
import View.Model.BattleInfo;
import View.Model.CellState;
import View.Model.EndGameStats;
import View.Model.WeaponViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Vue Swing principale de la bataille. */
public class BattleView implements BattleViewInterface {
    private final IGameController controller;
    private final int gridSize;
    private JFrame frame;

    private BattleGrid playerBattleGrid;
    private BattleGrid enemyBattleGrid;
    private InfoPanel infoPanel;

    private JPanel weaponPanel;
    private ButtonGroup weaponGroup;
    private final Map<String, JRadioButton> weaponButtons = new HashMap<>();

    public BattleView(IGameController controller, int gridSize) {
        this.controller = controller;
        this.gridSize = gridSize;
    }

    @Override
    public void display() {
        frame = new JFrame("Bataille Navale - Mode Avancé");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel title = new JLabel("BATAILLE NAVALE - Votre flotte vs Flotte ennemie", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        frame.add(title, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel playerPanel = createGridPanel("VOTRE FLOTTE", true);
        mainPanel.add(playerPanel);

        JPanel enemyPanel = createGridPanel("FLOTTE ENNEMIE", false);
        mainPanel.add(enemyPanel);

        frame.add(mainPanel, BorderLayout.CENTER);

        infoPanel = new InfoPanel();
        frame.add(infoPanel, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        weaponPanel = createWeaponPanel();
        bottomPanel.add(weaponPanel, BorderLayout.NORTH);

        JLabel instructions = new JLabel("Cliquez sur la grille ENNEMIE pour attaquer", JLabel.CENTER);
        instructions.setFont(new Font("Arial", Font.ITALIC, 12));
        instructions.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        bottomPanel.add(instructions, BorderLayout.SOUTH);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createWeaponPanel() {
        var panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("ARSENAL"));
        weaponGroup = new ButtonGroup();
        return panel;
    }

    @Override
    public void updateGrids(CellState[][] playerStates, CellState[][] enemyStates) {
        if (playerBattleGrid != null) playerBattleGrid.updateGrid(playerStates);
        if (enemyBattleGrid != null) enemyBattleGrid.updateGrid(enemyStates);
    }

    @Override
    public void updateInfo(BattleInfo info) {
        if (infoPanel != null) {
            infoPanel.updateStats(info);
        }
    }

    @Override
    public void addLog(String message) {
        if (infoPanel != null) infoPanel.addLog(message);
    }

    @Override
    public void showEndGame(EndGameStats stats) {
        if (frame != null) frame.setVisible(false);
    }

    @Override
    public void close() {
        if (frame != null) frame.dispose();
    }

    @Override
    public void renderWeapons(List<WeaponViewModel> weapons) {
        if (weaponPanel == null || weaponGroup == null || weapons == null) return;

        weaponPanel.removeAll();
        weaponButtons.clear();
        weaponGroup = new ButtonGroup();

        for (WeaponViewModel vm : weapons) {
            if (vm == null || vm.id() == null) continue;
            var btn = new JRadioButton(vm.label());
            btn.setEnabled(vm.enabled());
            btn.addActionListener(e -> controller.selectWeapon(vm.id()));
            weaponGroup.add(btn);
            weaponPanel.add(btn);
            weaponButtons.put(vm.id(), btn);
        }

        weaponPanel.revalidate();
        weaponPanel.repaint();
    }

    private JPanel createGridPanel(String title, boolean isPlayerGrid) {
        var panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        GridInteractionListener listener = isPlayerGrid ? null : controller::handlePlayerAttack;

        var battleGrid = new BattleGrid(gridSize, listener);

        if (isPlayerGrid) {
            playerBattleGrid = battleGrid;
        } else {
            enemyBattleGrid = battleGrid;
        }

        panel.add(battleGrid.getGridPanel(), BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void setSelectedWeapon(String weaponId) {
        if (weaponId == null) return;
        var btn = weaponButtons.get(weaponId);
        if (btn != null) {
            btn.setSelected(true);
        }
    }

}
