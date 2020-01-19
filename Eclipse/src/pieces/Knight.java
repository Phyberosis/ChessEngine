package pieces;

import game.Location;

/**
 * Created by Phyberosis on 1/9/2017.
 */

public class Knight extends Piece {
    public Knight(int x, int y, boolean isMine)
    {
        mLocation = new Location(x, y);
        mType = Type.KNIGHT;

        if (isMine)
        {
            mAllegiance = Type.AI;
        }else{
            mAllegiance = Type.PLAYER;
        }
    }
}
