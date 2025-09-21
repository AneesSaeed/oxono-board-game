package model.observer;


/**
 * This interface defines the methods for managing and notifying
 * observers in the weather application.
 * Classes implementing this interface can register objects that
 * implement the {@code Observer} interface and notify them whenever there
 * is an update in the weather data.
 */
public interface Observable {

    /**
     * Registers an observer to receive updates.
     *
     * @param o the {@code Observer} to register
     */
    void addObserver(Observer o);

    /**
     * Removes a previously registered observer.
     *
     * @param o the {@code Observer} to remove
     */
    void removeObserver(Observer o);

    /**
     * Notifies all registered observers with the latest weather data.
     *
     * @param
     */
    void notifyObservers();
}