package console;

import controller.ConsoleController;
import model.Game;
import model.strategy.RandomStrategy;
import view.ConsoleView;

class ConsoleMode {
    public static void main(String[] args) {
        Game model = new Game(6,6, new RandomStrategy());
        ConsoleView view = new ConsoleView();
        ConsoleController controller = new ConsoleController(model, view);

        view.showTitle();
        model.notifyObservers();
        controller.start();
    }
}
