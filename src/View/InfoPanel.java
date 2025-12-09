package View;

import Model.Player.ComputerPlayer;
import Model.Player.HumanPlayer;
import Model.Boat.Boat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class InfoPanel extends JPanel {
    private JLabel turnLabel;
    private JLabel playerBoatsLabel;
    private JLabel enemyBoatsLabel;
    private JLabel inventoryLabel;

    private JTextArea logArea;
    private JScrollPane logScrollPane;

    public InfoPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 0));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(new TitledBorder("STATISTIQUES"));

        turnLabel = new JLabel("Tour : 1");
        turnLabel.setFont(new Font("Arial", Font.BOLD, 14));
        turnLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        playerBoatsLabel = new JLabel("Vos Bateaux : Calcul...");
        playerBoatsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        enemyBoatsLabel = new JLabel("Bateaux Ennemis : Calcul...");
        enemyBoatsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        inventoryLabel = new JLabel("Inventaire : -");
        inventoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsPanel.add(turnLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statsPanel.add(playerBoatsLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        statsPanel.add(enemyBoatsLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statsPanel.add(inventoryLabel);

        add(statsPanel, BorderLayout.NORTH);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(new TitledBorder("HISTORIQUE"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        logScrollPane = new JScrollPane(logArea);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        logPanel.add(logScrollPane, BorderLayout.CENTER);

        add(logPanel, BorderLayout.CENTER);
    }

    public void updateStats(int turnNumber, HumanPlayer human, ComputerPlayer computer) {
        turnLabel.setText("Tour actuel : " + turnNumber);

        String invText = "<html><b>Munitions :</b><br/>" +
                "💣 Bombes : " + human.getBombCount() + "<br/>" +
                "📡 Sonars : " + human.getSonarCount() + "</html>";
        inventoryLabel.setText(invText);

        playerBoatsLabel.setText("État Joueur : En cours");
        enemyBoatsLabel.setText("État IA : En cours");
    }

    public void addLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private int getSunkCount(Model.Player.Player player) {
        if (player == null) return 0;

        Set<Boat> uniqueBoats = new HashSet<>();
        Model.Map.Grid grid = player.getOwnGrid();
        int size = grid.getSize();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Model.Map.GridCell cell = grid.getCell(new Model.Coordinates(r, c));
                if (cell.isOccupied() && cell.getEntity() instanceof Boat) {
                    uniqueBoats.add((Boat) cell.getEntity());
                }
            }
        }

        int sunkCount = 0;
        for (Boat boat : uniqueBoats) {
            if (boat.isSunk()) {
                sunkCount++;
            }
        }
        return sunkCount;
    }
}