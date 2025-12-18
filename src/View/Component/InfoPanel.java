package View.Component;

import View.Model.BattleInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/** Panneau affichant stats et historique de bataille. */
public class InfoPanel extends JPanel {
    private final JLabel turnLabel;
    private final JLabel playerBoatsLabel;
    private final JLabel enemyBoatsLabel;
    private final JLabel inventoryLabel;
    private final JTextArea logArea;

    public InfoPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 0));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        var statsPanel = new JPanel();
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

        var logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(new TitledBorder("HISTORIQUE"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        var logScrollPane = new JScrollPane(logArea);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        logPanel.add(logScrollPane, BorderLayout.CENTER);

        add(logPanel, BorderLayout.CENTER);
    }

    public void updateStats(BattleInfo info) {
        turnLabel.setText("Tour actuel : " + info.turnCount());
        var invText = "<html><b>Munitions :</b><br/>" +
                "💣 Bombes : " + info.bombCount() + "<br/>" +
                "📡 Sonars : " + info.sonarCount() + "</html>";
        inventoryLabel.setText(invText);
        playerBoatsLabel.setText("Vos navires rescapés : " + info.playerAliveBoats());
        enemyBoatsLabel.setText("Navires ennemis : " + info.enemyAliveBoats());
    }

    public void addLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
