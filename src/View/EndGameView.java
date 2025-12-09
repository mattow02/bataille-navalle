package View;

import Controller.GameController;
import Model.Boat.Boat;
import Model.Player.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class EndGameView {
    private String resultMessage;
    private GameController controller;
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
        int playerBoatsLeft = countAliveBoats(controller.getHumanPlayer());
        int enemyBoatsLeft = countAliveBoats(controller.getComputerPlayer());

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

        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("Rejouer une partie");
        JButton quitButton = new JButton("Quitter");

        restartButton.setFont(new Font("Arial", Font.BOLD, 14));
        quitButton.setFont(new Font("Arial", Font.PLAIN, 14));

        restartButton.addActionListener(e -> restartGame());
        quitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(restartButton);
        buttonPanel.add(quitButton);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 20, 10));

        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void addStatLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private int countAliveBoats(Player player) {
        if (player == null) return 0;
        Set<Boat> uniqueBoats = new HashSet<>();
        int size = player.getOwnGrid().getSize();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Model.Map.GridCell cell = player.getOwnGrid().getCell(new Model.Coordinates(r, c));
                if (cell.isOccupied() && cell.getEntity() instanceof Boat) {
                    uniqueBoats.add((Boat) cell.getEntity());
                }
            }
        }

        int aliveCount = 0;
        for (Boat boat : uniqueBoats) {
            if (!boat.isSunk()) {
                aliveCount++;
            }
        }
        return aliveCount;
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