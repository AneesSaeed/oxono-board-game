package model.command;

/**
 * Command is an interface for executing and undoing actions in a Command pattern
 * implementation. Classes implementing this interface represent specific actions
 * that can be executed and later reversed (undone).
 */
public interface Command {

    /**
     * Executes the command, performing the associated action.
     */
    void execute();

    /**
     * Reverses the command, undoing the associated action.
     */
    void unexecute();
}