package Game;

import android.util.Log;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Phyberosis on 2/18/2017.
 * move gen thread
 */

class MoveGen implements Runnable
{

    private LinkedList<byte[][]> mHistory;
    private byte[][] mScenario;
    private ScenarioHandler mScenarioHandler;
    private final GameEngine geRef;

    int progress;
    final LinkedList<Location> mScoreData;
    private final String TAG = "@@@@@ MoveGen @@@@@";

    MoveGen(byte[][] s, LinkedList<byte[][]> hist, GameEngine ge)
    {
        mHistory = hist;
        mScenario = new byte[9][8];
        mScenarioHandler = new ScenarioHandler();
        mScenario = mScenarioHandler.getNew(s);
        geRef = ge;
        mScoreData = new LinkedList<>();
    }

    void begin()
    {
        Thread me = new Thread(this);
        me.start();
    }

    public void run()
    {
        int depth = 3; /**@@ (0 is one move only)set depth (one depth = one turn of EITHER player or ai, NOT both) @@**/

        byte[][] myHist;
        byte[][] histP;

//        mScenarioHandler.printScenariod(mScenario, "MG -> current board");

        if(mScenario[8][2] %3 == 0)
        {   // just switched sides or start of game
            myHist = mScenario;
            histP = mScenario;
        }else
        {
            myHist = mHistory.get(mHistory.size() - 2);
            histP = mHistory.getLast();
        }


        LinkedList<byte[][]> outcomes = new LinkedList<>();

        for(int x = 0; x < 8; x++)
        {
            for(int y = 0; y < 8; y++)
            {

                if (mScenario[x][y] > 0)//not Ai piece
                    continue;

                for (byte[][] child : mScenarioHandler.getChildScenariosOfTile(x, y, mScenario, myHist)) //if tile -> no children
                {//printScenariod(child);

                    outcomes.add(child);
//                    mScenarioHandler.printScenariod(child);
//                    MinMax mm =
//                    id++;
                }
            }

        }

        //split into two threads
        int midWay = outcomes.size()/2 -1;
        byte[][][] childrenA = new byte[midWay + 1][9][8];
        byte[][][] childrenB = new byte[outcomes.size() - midWay - 1][9][8];

        int i = 0;
        for(byte[][] child : outcomes)
        {
            if(i <= midWay)
            {
                childrenA[i] = child;
            }else
            {
                childrenB[i-midWay-1] = child;
            }
            i++;
        }

//        Log.d(TAG, "run: " + outcomes.size() + " " + midWay);

        //id becomes the number to add to, to get child index in outcome list
        MinMax a = new MinMax(this, 0, childrenA, histP, myHist, depth);
        a.begin();

        MinMax b = new MinMax(this, midWay+1, childrenB, histP, myHist, depth);
        b.begin();

        while (progress < outcomes.size())//report progress as mm threads run
        {
            try
            {
                synchronized (this)
                {
                    this.wait(250);
                }
            } catch (InterruptedException ignored)
            {}

            //report progress
            synchronized (geRef)
            {
                geRef.progress = (int) Math.round((progress+0.0)/(0.0+outcomes.size())*100);

                int best = Integer.MAX_VALUE;
                synchronized (this)
                {
                    for(Location data : mScoreData)//location x is id, y is score
                    {
                        if (data.mY < best)
                            best = data.mY;
                    }
                }
                geRef.msg = "best outcome has score : " + best;
//                    geRef.progress = (x+1) * (y+1) /64*100;
//                Log.d(TAG, "run: " + progress + " " + outcomes.size() + " " + (progress+0.0)/(0.0+outcomes.size()));
            }
        }

        int bestScore = Integer.MAX_VALUE;
        LinkedList<Location> bestMoves = new LinkedList<>();
        for(Location data : mScoreData)//location x is id, y is score
        {
            if(data.mY < bestScore)
            {
                bestScore = data.mY;
                bestMoves.clear();
                bestMoves.add(data);
            }else if (data.mY == bestScore)
            {
                bestMoves.add(data);
            }

//            Log.d(TAG, "run: mGen " + data.mY);
//            mScenarioHandler.printScenariod(outcomes.get(data.mX), "MGen " + data.mY);
        }

        Log.d(TAG, "run: mGen best score " + bestScore);
        if(bestMoves.isEmpty()) //game over
        {
            synchronized (geRef)
            {
                geRef.myMove = null;
                geRef.done = true;
                geRef.notify();
            }

            return;
        }

        //Log.d(TAG, "run: best score -> " + bestMoves.getFirst().mY);
        for (Location data : bestMoves)
        {
            //mScenarioHandler.printScenariod(outcomes.get(data.mX));
        }

        //no best score found
        Random r = new Random(System.currentTimeMillis());
        int rand = r.nextInt(bestMoves.size());

        // y is score x is index in list
        Location best = bestMoves.get(rand);
        //Log.d(TAG, "run: picked -> " + best.mY);

        synchronized (geRef)
        {
            geRef.myMove = outcomes.get(best.mX);
            geRef.done = true;
            geRef.notify();
        }
    }
}
