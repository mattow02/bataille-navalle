package View;

import Controller.ConfigurationController;
import Model.Boat.BoatType;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationView implements GameView {
    private ConfigurationController controller;
    private JFrame frame;
    private Map<BoatType, JSpinner> boatSpinners;

    public ConfigurationView(ConfigurationController controller) {
        this.controller = controller;
        this.boatSpinners = new HashMap<>();
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Configuration - Bataille Navale");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());

        // Titre
        JLabel titleLabel = new JLabel("Configuration des Bateaux", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Panel configuration
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Instructions
        JLabel instructions = new JLabel("Choisissez le nombre de bateaux (1-3 par type, max 35 cases)");
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        configPanel.add(instructions);
        configPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Sélection des bateaux
        addBoatSelection(configPanel);

        // Compteur de cases
        JLabel counterLabel = new JLabel("Cases utilisées: 0/35", JLabel.CENTER);
        counterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        configPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        configPanel.add(counterLabel);

        // Boutons
        JPanel buttonPanel = new JPanel();
        JButton startButton = new JButton("Démarrer la Partie");
        JButton defaultButton = new JButton("Configuration Par Défaut");

        startButton.addActionListener(e -> startGame(counterLabel));
        defaultButton.addActionListener(e -> setDefaultConfiguration(counterLabel));

        buttonPanel.add(defaultButton);
        buttonPanel.add(startButton);

        frame.add(configPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Mettre à jour le compteur initial
        updateCellCounter(counterLabel);
    }

    private void addBoatSelection(JPanel panel) {
        for (BoatType type : BoatType.values()) {
            JPanel boatRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            boatRow.setMaximumSize(new Dimension(400, 40));

            JLabel nameLabel = new JLabel(type.getName() + " (" + type.getSize() + " cases):");
            nameLabel.setPreferredSize(new Dimension(200, 25));

            JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 0, 3, 1));
            spinner.setPreferredSize(new Dimension(60, 25));

            // Listener pour mettre à jour le compteur
            spinner.addChangeListener(e -> updateCellCounter());

            boatRow.add(nameLabel);
            boatRow.add(spinner);
            panel.add(boatRow);

            boatSpinners.put(type, spinner);
        }
    }

    private void updateCellCounter() {
        // Trouver le label de compteur
        Component[] components = ((JPanel)frame.getContentPane().getComponent(1)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel && ((JLabel)comp).getText().startsWith("Cases utilisées:")) {
                updateCellCounter((JLabel)comp);
                break;
            }
        }
    }

    private void updateCellCounter(JLabel counterLabel) {
        int totalCells = 0;
        for (Map.Entry<BoatType, JSpinner> entry : boatSpinners.entrySet()) {
            int count = (Integer) entry.getValue().getValue();
            totalCells += entry.getKey().getSize() * count;
        }

        counterLabel.setText("Cases utilisées: " + totalCells + "/35");
        counterLabel.setForeground(totalCells > 35 ? Color.RED : Color.BLACK);
    }

    private void startGame(JLabel counterLabel) {
        Map<BoatType, Integer> boatCounts = new HashMap<>();
        int totalCells = 0;

        for (Map.Entry<BoatType, JSpinner> entry : boatSpinners.entrySet()) {
            int count = (Integer) entry.getValue().getValue();
            boatCounts.put(entry.getKey(), count);
            totalCells += entry.getKey().getSize() * count;
        }

        if (totalCells > 35) {
            JOptionPane.showMessageDialog(frame,
                    " Trop de cases ! Maximum 35.\nActuel: " + totalCells + " cases",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (totalCells == 0) {
            JOptionPane.showMessageDialog(frame,
                    " Vous devez avoir au moins 1 bateau !",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controller.handleConfigurationComplete(boatCounts);
        frame.dispose();
    }

    private void setDefaultConfiguration(JLabel counterLabel) {
        // Configuration par défaut : 1 de chaque type
        for (JSpinner spinner : boatSpinners.values()) {
            spinner.setValue(1);
        }
        updateCellCounter(counterLabel);
    }


    public void display() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    public void update(Object data) {
        // Pas utilisé pour l'instant
    }


    public void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }
}