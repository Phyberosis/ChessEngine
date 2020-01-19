package pieces;

import game.Location;


/**
 * Created by Phyberosis on 1/9/2017.
 */

public class Queen extends Piece {

    public Queen(int x, int y, boolean isMine)
    {
        mLocation = new Location(x, y);
        mType = Type.QUEEN;

        if (isMine)
        {
            mAllegiance = Type.AI;
        }else{
            mAllegiance = Type.PLAYER;
        }
    }

}
