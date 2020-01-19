package Pieces;

import Game.Location;

/**
 * Created by Phyberosis on 1/9/2017.
 */

public class King extends Piece {

    private boolean moved;

    public King(int x, int y, boolean isMine)
    {
        mLocation = new Location(x, y);
        mType = Type.KING;

        if (isMine)
        {
            mAllegiance = Type.AI;
        }else{
            mAllegiance = Type.PLAYER;
        }

        moved = false;
    }

    public void moved()
    {
        moved = true;
    }

    public boolean hasMoved()
    {
        return moved;
    }
}
