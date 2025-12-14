package View;

import Controller.GameController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EndGameView {
    private final String resultMessage;
    private final GameController controller;
    private JFrame frame;

    public EndGameView(String resultMessage, GameController controller) {
        this.resultMessage = resultMessage;
        this.controller = controller;
    }

    public void display() {
        frame = new JFrame("Fin de Partie");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 400);
        frame.setLayout(new BorderLayout());

        JLabel resultLabel = new JLabel(resultMessage, JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 24));
        resultLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        if (resultMessage.contains("GAGNÉ")) {
            resultLabel.setForeground(new Color(34, 139, 34));
        } else {
            resultLabel.setForeground(Color.RED);
        }
        frame.add(resultLabel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(new EmptyBorder(10, 40, 10, 40));

        int tours = controller.getTurnCount();
        int playerBoatsLeft = controller.getPlayerAliveBoatsCount();
        int enemyBoatsLeft = controller.getComputerAliveBoatsCount();

        addStatLabel(statsPanel, "Durée de la bataille : " + tours + " tours");
        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        addStatLabel(statsPanel, "--- Bilan des Flottes ---");
        addStatLabel(statsPanel, "Vos navires rescapés : " + playerBoatsLeft);
        addStatLabel(statsPanel, "Navires ennemis restants : " + enemyBoatsLeft);

        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        String comment;
        if (playerBoatsLeft == 0) comment = "Votre flotte gît au fond de l'océan...";
        else if (playerBoatsLeft == 5) comment = "Victoire parfaite ! Aucun navire perdu.";
        else comment = "Une bataille acharnée !";

        JLabel commentLabel = new JLabel("<html><i>" + comment + "</i></html>");
        commentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPanel.add(commentLabel);

        frame.add(statsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = getJPanel();

        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel getJPanel() {
        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("Rejouer une partie");
        JButton quitButton = new JButton("Quitter");

        restartButton.setFont(new Font("Arial", Font.BOLD, 14));
        quitButton.setFont(new Font("Arial", Font.PLAIN, 14));

        restartButton.addActionListener(_ -> restartGame());
        quitButton.addActionListener(_ -> System.exit(0));

        buttonPanel.add(restartButton);
        buttonPanel.add(quitButton);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 20, 10));
        return buttonPanel;
    }

    private void addStatLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }


    private void restartGame() {
        if (frame != null) {
            frame.dispose();
        }
        if (controller != null) {
            controller.startApplication();
        }
    }
}