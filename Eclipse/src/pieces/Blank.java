package pieces;

import game.Location;

/**
 * Created by Phyberosis on 1/9/2017.
 */

public class Blank extends Piece {
    public Blank(int x, int y)
    {
        mLocation = new Location(x, y);

        mType = Type.BLANK;
    }
}

