package pieces;

import game.Location;

/**
 * Created by Phyberosis on 1/9/2017.
 */

public class Bishop extends Piece {

    public Bishop(int x, int y, boolean isMine)
    {
        mLocation = new Location(x, y);
        mType = Type.BISHOP;

        if (isMine)
        {
            mAllegiance = Type.AI;
        }else{
            mAllegiance = Type.PLAYER;
        }
    }

}
