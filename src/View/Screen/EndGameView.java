package View.Screen;

import Controller.IGameController;
import View.GameView;
import View.Model.EndGameStats;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Vue de fin de partie. */
public class EndGameView implements GameView {
    private final EndGameStats stats;
    private final IGameController controller;
    private JFrame frame;

    public EndGameView(EndGameStats stats, IGameController controller) {
        this.stats = stats;
        this.controller = controller;
    }

    @Override
    public void display() {
        frame = new JFrame("Fin de Partie");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 400);
        frame.setLayout(new BorderLayout());

        var resultLabel = new JLabel(stats.resultMessage(), JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 24));
        resultLabel.setBorder(new EmptyBorder(20, 0, 20, 0));

        if (stats.resultMessage().contains("GAGNÉ")) {
            resultLabel.setForeground(new Color(34, 139, 34));
        } else {
            resultLabel.setForeground(Color.RED);
        }
        frame.add(resultLabel, BorderLayout.NORTH);

        var statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(new EmptyBorder(10, 40, 10, 40));

        addStatLabel(statsPanel, "Durée de la bataille : " + stats.turnCount() + " tours");
        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        addStatLabel(statsPanel, "--- Bilan des Flottes ---");
        addStatLabel(statsPanel, "Vos navires rescapés : " + stats.playerAliveBoats());
        addStatLabel(statsPanel, "Navires ennemis restants : " + stats.computerAliveBoats());

        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        String comment;
        if (stats.playerAliveBoats() == 0) comment = "Votre flotte gît au fond de l'océan...";
        else if (stats.playerAliveBoats() == 5) comment = "Victoire parfaite ! Aucun navire perdu.";
        else comment = "Une bataille acharnée !";

        var commentLabel = new JLabel("<html><i>" + comment + "</i></html>");
        commentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPanel.add(commentLabel);

        frame.add(statsPanel, BorderLayout.CENTER);

        var buttonPanel = getJPanel();
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void close() {
        if (frame != null) {
            frame.dispose();
        }
    }

    private JPanel getJPanel() {
        var buttonPanel = new JPanel();
        var restartButton = new JButton("Rejouer une partie");
        var quitButton = new JButton("Quitter");

        restartButton.setFont(new Font("Arial", Font.BOLD, 14));
        quitButton.setFont(new Font("Arial", Font.PLAIN, 14));

        restartButton.addActionListener(e -> restartGame());
        quitButton.addActionListener(e -> quitGame());

        buttonPanel.add(restartButton);
        buttonPanel.add(quitButton);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 20, 10));
        return buttonPanel;
    }

    private void addStatLabel(JPanel panel, String text) {
        var label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void restartGame() {
        if (frame != null) {
            frame.dispose();
        }
        if (controller != null) controller.restartApplication();
    }

    private void quitGame() {
        if (frame != null) {
            frame.dispose();
        }
        if (controller != null) controller.quitApplication();
    }
}
