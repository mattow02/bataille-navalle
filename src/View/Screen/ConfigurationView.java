package View.Screen;

import Controller.Config.IConfigurationController;
import View.GameView;
import View.Model.BoatTypeViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Vue Swing de configuration de partie. */
public class ConfigurationView implements GameView {
    private final IConfigurationController controller;
    private JFrame frame;
    private final Map<String, JSpinner> boatSpinners;
    private JCheckBox islandModeCheckBox;
    private JCheckBox easyModeCheckBox;
    private final Map<String, Integer> boatSizes;
    private JLabel counterLabel;

    public ConfigurationView(IConfigurationController controller) {
        this.controller = controller;
        this.boatSpinners = new HashMap<>();
        this.boatSizes = new HashMap<>();
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Configuration - Bataille Navale");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 700);
        frame.setLayout(new BorderLayout());

        var titleLabel = new JLabel("Configuration des Bateaux", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(titleLabel, BorderLayout.NORTH);

        var configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        var instructions = new JLabel("Choisissez le nombre de bateaux (1-3 par type, max 35 cases)");
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        configPanel.add(instructions);
        configPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        var boatTypes = controller.getAvailableBoatTypes();
        addBoatSelection(configPanel, boatTypes);

        configPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        islandModeCheckBox = new JCheckBox("Activer le Mode Île (Zone 4x4, items spéciaux)");
        islandModeCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        configPanel.add(islandModeCheckBox);

        configPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        easyModeCheckBox = new JCheckBox("IA Facile (Tirs 100% aléatoires)");
        easyModeCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        configPanel.add(easyModeCheckBox);

        configPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        counterLabel = new JLabel("Cases utilisées: 0/35", JLabel.CENTER);
        counterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        configPanel.add(counterLabel);

        var buttonPanel = new JPanel();
        var startButton = new JButton("Démarrer la Partie");
        var defaultButton = new JButton("Configuration Par Défaut");

        startButton.addActionListener(e -> startGame());
        defaultButton.addActionListener(e -> setDefaultConfiguration(counterLabel));

        buttonPanel.add(defaultButton);
        buttonPanel.add(startButton);

        frame.add(configPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        updateCellCounter(counterLabel);
    }

    private void addBoatSelection(JPanel panel, List<BoatTypeViewModel> boatTypes) {
        for (var type : boatTypes) {
            var boatRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            boatRow.setMaximumSize(new Dimension(400, 40));

            var nameLabel = new JLabel(type.name() + " (" + type.size() + " cases):");
            nameLabel.setPreferredSize(new Dimension(200, 25));

            var spinner = new JSpinner(new SpinnerNumberModel(1, 0, 3, 1));
            spinner.setPreferredSize(new Dimension(60, 25));

            spinner.addChangeListener(e -> updateCellCounter());

            boatRow.add(nameLabel);
            boatRow.add(spinner);
            panel.add(boatRow);

            boatSpinners.put(type.id(), spinner);
            boatSizes.put(type.id(), type.size());
        }
    }

    private void updateCellCounter() {
        if (counterLabel != null) {
            updateCellCounter(counterLabel);
        }
    }

    private Map<String, Integer> collectBoatCounts() {
        var boatCounts = new HashMap<String, Integer>();
        for (var entry : boatSpinners.entrySet()) {
            var spinner = entry.getValue();
            var count = spinner != null ? (Integer) spinner.getValue() : 0;
            boatCounts.put(entry.getKey(), count);
        }
        return boatCounts;
    }

    private void updateCellCounter(JLabel counterLabel) {
        var boatCounts = collectBoatCounts();
        var totalCells = controller.calculateTotalCells(boatCounts);
        counterLabel.setText("Cases utilisées: " + totalCells + "/35");
        counterLabel.setForeground(totalCells > 35 ? Color.RED : Color.BLACK);
    }

    private void startGame() {
        var boatCounts = collectBoatCounts();

        var validation = controller.validateConfiguration(boatCounts);

        if (!validation.isValid()) {
            JOptionPane.showMessageDialog(frame,
                    validation.errorMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        var isIslandMode = islandModeCheckBox.isSelected();
        var aiLevel = easyModeCheckBox.isSelected() ? 1 : 2;

        controller.handleConfigurationComplete(boatCounts, isIslandMode, aiLevel);
        frame.dispose();
    }

    private void setDefaultConfiguration(JLabel counterLabel) {
        for (var spinner : boatSpinners.values()) {
            spinner.setValue(1);
        }

        if (islandModeCheckBox != null) {
            islandModeCheckBox.setSelected(false);
        }

        updateCellCounter(counterLabel);
    }

    @Override
    public void display() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
