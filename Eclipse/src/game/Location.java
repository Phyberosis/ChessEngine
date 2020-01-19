package game;

/**
 * Created by Phyberosis on 1/9/2017.
 * x y data set
 */

public class Location {
    int mX;
    int mY;

    public Location(int x, int y)
    {
        mX = x;
        mY = y;
    }

    public Location(int x, int y, boolean isAtt)
    {
        mX = x;
        mY = y;
    }
}
