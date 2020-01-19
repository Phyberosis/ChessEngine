package Pieces;

/**
 * Created by Phyberosis on 1/9/2017.
 */

public enum Type {

    //player and ai ids should match their pawn attack direction for method GameEngine.getPawnMoves
    //work
    PAWN(2), KNIGHT(6), BISHOP(7), ROOK(10), QUEEN(18), KING(2000), BLANK(0), PLAYER(-1), AI(1); //these signify board directions, DO NOT CHANGE

    private final int mID;

    Type(int id)
    {
        mID = id;
    }

    public int getValue()
    {
        return mID;
    }

}
