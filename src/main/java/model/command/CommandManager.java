package model.command;

import java.util.Stack;

/**
 * CommandManager is responsible for executing, undoing, and redoing commands
 * within the application. It maintains a history of commands in stacks to allow
 * reversal (undo) and reapplication (redo) of actions.
 */
public class CommandManager {

    private final Stack<Command> undoStack;
    private final Stack<Command> redoStack;

    /**
     * Constructs a new CommandManager with empty undo and redo stacks.
     */
    public CommandManager(){
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    /**
     * Executes a given command, adds it to the undo stack, and clears the redo stack.
     * This allows only the most recent sequence of actions to be redone after an undo.
     *
     * @param command the command to be executed and stored in the undo history
     */
    public void addCommand(Command command){
        undoStack.push(command);
        redoStack.clear();
    }

    /**
     * Undoes the last executed command, if available. The command is removed from
     * the undo stack, its undo operation is triggered, and it is added to the redo stack.
     * Does nothing if there are no commands in the undo stack.
     */
    public void undo(){
        if (!undoStack.isEmpty()){
            Command command = undoStack.pop();
            command.unexecute();
            redoStack.push(command);
        }
    }

    /**
     * Redoes the last undone command, if available. The command is removed from
     * the redo stack, re-executed, and added back to the undo stack.
     * Does nothing if there are no commands in the redo stack.
     */
    public void redo(){
        if (!redoStack.isEmpty()){
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
}

