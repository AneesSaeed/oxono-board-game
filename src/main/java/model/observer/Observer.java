package model.observer;

import model.Game;

/**
 * This interface represents an observer in the Oxono game.
 * Classes implementing this interface are notified when game state changes.
 */
public interface Observer {
    /**
     * Called when the Observable (Game) state changes.
     */
    void update();
}