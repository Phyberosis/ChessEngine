package Game;

/**
 * Created by Phyberosis on 1/9/2017.
 * x y data set
 */

public class Location {
    int mX;
    int mY;
    //boolean isAtt;

    public Location(int x, int y)
    {
        mX = x;
        mY = y;
        //isAtt = false;
    }

    public Location(int x, int y, boolean isAtt)
    {
        mX = x;
        mY = y;
        //this.isAtt = isAtt;
    }
}
