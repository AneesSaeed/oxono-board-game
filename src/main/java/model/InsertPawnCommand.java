package model;


import model.command.Command;

class InsertPawnCommand implements Command {

    private final Game game;
    private final Pawn pawn;
    private final Position pawnPos;

    public InsertPawnCommand(Game game, Pawn pawn, Position newPos) {
        this.game = game;
        this.pawn = pawn;
        this.pawnPos = newPos;
    }

    @Override
    public void execute() {
        game.insertPawnInBoard(pawn, pawnPos);
    }

    @Override
    public void unexecute() {
        game.changePlayer(); //reverts to previous player
        System.out.println(game.getCurrPlayerColor());
        game.restorePawnToCurrentPlayer(this.pawn);
        game.removeToken(this.pawnPos);
        game.setTotemMoved(true);// Set hasMovedTotem to true, allowing the player to insert another pawn.
    }
}
