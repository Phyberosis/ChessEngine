package game;

/**
 * Created by Phyberosis on 1/9/2017.
 */

public class Scenario {

    public byte[][] mMap = new byte[8][8];

    public Scenario(){
        /*for (int i = 0; i < 8; i++)
        {
            for (int ii = 0; ii < 8; ii++)
            {
                mMap[i][ii] = 0;
            }
        }*/
    }

    public Scenario(Scenario s)
    {
        for (int x = 0; x < 9; x++)
        {
            System.arraycopy(s.mMap[x], 0, mMap[x], 0, 8);
        }
    }
}
