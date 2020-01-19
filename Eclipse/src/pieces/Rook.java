package pieces;

import game.Location;


/**
 * Created by Phyberosis on 1/9/2017.
 */

public class Rook extends Piece {

    private boolean moved;

    public Rook(int x, int y, boolean isMine)
    {
        mLocation = new Location(x, y);
        mType = Type.ROOK;

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
