package View;

import Controller.GameController;
import javax.swing.*;
import java.awt.*;

public class EndGameView implements GameView {
    private String result;
    private GameController controller;
    private JFrame frame;

    public EndGameView(String result, GameController controller) {
        this.result = result;
        this.controller = controller;
    }


    public void display() {
        frame = new JFrame("Partie Terminée");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        // Message de résultat
        JLabel resultLabel = new JLabel(result, JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(resultLabel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("Rejouer");
        JButton quitButton = new JButton("Quitter");

        restartButton.addActionListener(e -> restartGame());
        quitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(restartButton);
        buttonPanel.add(quitButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void restartGame() {
        frame.dispose();
        controller.startApplication(); // Redémarrer une nouvelle partie
    }


}