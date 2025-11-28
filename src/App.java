
public class App {
    public static void main(String[] args) {
        System.out.println("Démarrage de la Bataille Navale...");

        Controller.GameController gameController = new Controller.GameController();

        gameController.startApplication();
    }
}