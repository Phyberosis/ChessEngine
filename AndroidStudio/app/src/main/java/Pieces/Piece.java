package Pieces;

import Game.Location;

/**
 * Created by Phyberosis on 1/9/2017.
 */

public class Piece {

    protected Location mLocation;
    protected Type mType;
    protected Type mAllegiance; //I am the ai

    public Piece(Type t, int x, int y, boolean isMine)
    {
        mLocation = new Location(x, y);
        mType = t;

        if (isMine)
        {
            mAllegiance = Type.AI;
        }else{
            mAllegiance = Type.PLAYER;
        }
    }

    public Piece()
    {

    }

    public Type getType()
    {
        return mType;
    }

    public void setLocation(int x, int y)
    {
        mLocation = new Location(x, y);
    }

    public Location getLocation()
    {
        return mLocation;
    }

    public Type getAllegiance()
    {
        return mAllegiance;
    }
}
