package model;

import model.command.Command;

class MoveTotemCommand implements Command {
    private final Game game;
    private final Totem totem;
    private final Position newPos;
    private final Position oldPos;

    public MoveTotemCommand(Game game, Totem totem, Position newPos, Position oldPos) {
        this.game = game;
        this.totem = totem;
        this.newPos = newPos;
        this.oldPos = oldPos;
    }

    @Override
    public void execute() {
        game.moveInBoard(totem, newPos);
    }

    @Override
    public void unexecute() {
        if (oldPos != null){
            game.moveInBoard(this.totem, this.oldPos);
            game.setTotemMoved(false); //To allow another move
        }
    }
}
