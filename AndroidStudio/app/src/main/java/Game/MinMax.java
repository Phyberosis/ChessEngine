package Game;

import android.util.Log;

import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Created by Phyberosis on 2/18/2017.
 *
 * Move gen uses this to get moves per pc
 * must set with NEW byte arrays
 */

class MinMax implements Runnable
{

    private final MoveGen mgRef;
    private int ID;
    private ScenarioHandler mScenarioHandler;

    private final byte BLANK = 0;
    private final byte PAWN=2;
    private final byte BISHOP=7;
    private final byte KNIGHT=6;
    private final byte ROOK=10;
    private final byte QUEEN=18;
    private final byte KING=127;

    private byte[][][] mScenarios;
    private byte[][] mPlayerH;
    private byte[][] mMyH;
    private int mDepth;

    private final String TAG = "@ MinMax @";

    MinMax(MoveGen mg, int id, byte[][][] sList, byte[][] histP, byte[][] histA, int depth)
    {
        mgRef = mg;
        mScenarios = sList;
        mPlayerH = histP;
        mMyH = histA;
        mDepth = depth;
        this.ID = id;
        mScenarioHandler = new ScenarioHandler();

    }

    private int max(int i , int j)
    {
        if(i > j)
            return i;

        return j;
    }

    private int min(int i , int j)
    {
        if(i < j)
            return i;

        return j;
    }

    private int abs(int a)
    {
        if(a < 0)
        {
            return a*-1;
        }else{
            return a;
        }
    }

//    //check if location (x, y) is out of the board
//    private boolean isOutOfBounds(int x, int y)
//    {
//        if (x > 7 || x < 0)
//        {
//            return true;
//        }else if(y > 7 || y < 0)
//        {
//            return true;
//        }
//
//        return false;
//    }
//
//    private LinkedList<Location> getConflicts(byte[][] s, boolean maxPlayer)
//    {
//        LinkedList<Location> conflicts = new LinkedList<>();
//
//        for(int x = 0; x < 8; x++)
//        {
//            for(int y = 0; y < 8; y++)
//            {
//                if(s[x][y] == BLANK)
//                {
//                    continue;
//                }else{
//
//                }
//            }
//        }
//
//        return conflicts;
//    }
//
//    private int evalConflicts(LinkedList<Location> con)
//    {
//        return 0;
//    }
//
//
//    private int deepSearch(byte[][] s, byte[][] histP, byte[][] histA, int depth, int a, int b, boolean maxPlayer)
//    {
//        if (depth == -4) //arrived at specified depth
//        {
//            //Log.d(TAG, "alphaBeta: depth reached, score:" + calculateScore(s));
//            return calculateScore(s); //the score of the scenario, pos is player advantage, neg is my advantage
//        }
//
//        int v;
//        if(maxPlayer)//max player means its player's turn in move tree
//        {
//            v = Integer.MIN_VALUE;
//            //the two for loops get the child of each tile -> total is all children of scenario
//            for(int x = 0; x < 8; x++)
//            {
//                for(int y = 0; y < 8; y++)
//                {
//                    if(s[x][y] < 0)
//                        continue; //not plr piece
//
//                    LinkedList<byte[][]> children = getChildScenariosOfTile(x, y, s, histP);
//                    for (byte[][] child : children)
//                    {
//                        v = max(v, alphaBeta(child, histP, s, depth - 1, a, b, false));
//                        a = max(a, v);
//
//                        //debug
//                        //i++;
//                        /*if(b < a)
//                        {
//                            Log.d(TAG, "alphaBeta: alpha cut " + a);
//                            return v;
//                        }*/
//                    }
//                }
//            }
//
//        }else{
//            v = Integer.MAX_VALUE;
//            for(int x = 0; x < 8; x++)
//            {
//                for(int y = 0; y < 8; y++)
//                {
//                    if(s[x][y] > 0)
//                        continue;//not AI's piece
//
//                    LinkedList<byte[][]> children = getChildScenariosOfTile(x, y, s, histA);
//                    for (byte[][] child : children)
//                    {
//                        v = min(v, alphaBeta(child, s, histA, depth-1, a, b, true));
//                        b = min(b, v);
//
//                        //debug
//                        //i++;
//
//                        /*if(b < a)
//                        {
//                            Log.d(TAG, "alphaBeta: beta cut " + b + " " + a);
//                            return v;
//                        }*/
//                    }
//                }
//            }
//        }
//        /*
//        //debug
//        if (depth == 3)
//        {
//            synchronized (mgRef)
//            {
//                Log.d(TAG, "alphaBeta: ran through " + i + " moves, id:" + ID);
//            }
//        }*/
//        return v;
//    }

    //histP and histA are player last move and AI last move
    //a and b are best possible scores for this move branch
    private int alphaBeta(byte[][] s, byte[][] histP, byte[][] histA, int depth, int a, int b, boolean maxPlayer)
    {
        /*
        if (depth == 2)
        {
            Log.d(TAG, "alphaBeta: testing scenario");
            printScenariod(s);
            Log.d(TAG, "alphaBeta: depth 2, score:" + calculateScore(s));
        }*/

        //int i = 0;

        if (depth == 0) //arrived at specified depth
        {

//            for(Location conflictLoc : getConflicts(s, maxPlayer))
//            {
//                if (maxPlayer)
//                {
//
//                }
//            }
            //Log.d(TAG, "alphaBeta: depth reached, score:" + calculateScore(s));
            return mScenarioHandler.calculateScore(s); //the score of the scenario, pos is player advantage, neg is my advantage
        }

        int v;
        if(maxPlayer)//max player means its player's turn in move tree
        {
            v = Integer.MIN_VALUE;
            //the two for loops get the child of each tile -> total is all children of scenario
            for(int x = 0; x < 8; x++)
            {
                for(int y = 0; y < 8; y++)
                {
                    if(s[x][y] < 0)
                        continue; //not plr piece

                    for (byte[][] child : mScenarioHandler.getChildScenariosOfTile(x, y, s, histP))
                    {
                        v = max(v, alphaBeta(child, histP, s, depth - 1, a, b, false));
                        a = max(a, v);

                        //debug
                        //i++;
                        /*if(b < a)
                        {
                            Log.d(TAG, "alphaBeta: alpha cut " + a);
                            return v;
                        }*/
                    }
                }
            }

        }else{
            v = Integer.MAX_VALUE;
            for(int x = 0; x < 8; x++)
            {
                for(int y = 0; y < 8; y++)
                {
                    if(s[x][y] > 0)
                        continue;//not AI's piece

                    for (byte[][] child : mScenarioHandler.getChildScenariosOfTile(x, y, s, histA))
                    {
                        v = min(v, alphaBeta(child, s, histA, depth-1, a, b, true));
                        b = min(b, v);

                        //debug
                       //i++;

                        /*if(b < a)
                        {
                            Log.d(TAG, "alphaBeta: beta cut " + b + " " + a);
                            return v;
                        }*/
                    }
                }
            }
        }
        /*
        //debug
        if (depth == 3)
        {
            synchronized (mgRef)
            {
                Log.d(TAG, "alphaBeta: ran through " + i + " moves, id:" + ID);
            }
        }*/
        return v;
    }

    public void run()
    {
        //long start = System.currentTimeMillis();
        /*synchronized (mgRef)
        {
            //printScenariod(mScenario);
            Log.d(TAG, "run: " + ID);
        }*/

        int i = 0;
        for(byte[][] sen : mScenarios)
        {
            int score = alphaBeta(sen, mPlayerH, mMyH, mDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
//            synchronized (mgRef)
//            {
//                //printScenariod(mScenario);
//                Log.d(TAG, "run: " + ID + " has score: " + score);
//            }

            synchronized (mgRef)
            {
                mgRef.progress += 1;
                mgRef.mScoreData.add(new Location(ID + i, score));
//                mScenarioHandler.printScenariod(sen, ID + " MM " + score);
            }
            i++;
        }
        synchronized (mgRef)
        {
            mgRef.notify();
        }

        //debug
        /*synchronized (mgRef)
        {
            long time = System.currentTimeMillis() - start;

            Log.d(TAG, "minMax Total-> " + time + "ms for " + ID);
        }*/
    }

    void begin()
    {
        Thread me = new Thread(this);
        me.start();
    }

}
