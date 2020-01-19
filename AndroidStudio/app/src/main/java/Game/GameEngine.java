package Game;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.LinkedList;

/**
 * Created by Phyberosis on 1/9/2017.
 * handles Game activities
 * == 0 is BLANK
 * < 0 is AI since all AI values are negative : see ScenarioHandler
 * <p>
 * index 9 of scenarios:
 * 0 is for castling of AI
 * 1 is for player
 * king moves + 1
 * left rook + 6
 * right rook + 10
 * <p>
 * mod 2 discovers if king has moved
 * mod 3 for left rook
 * mod 5 right rook
 */

public class GameEngine
{

    private GameBoard mGameBoard;
    private ScenarioHandler mScenarioHandler;
    private byte[][] mCurrentScenario;
    private boolean mIamWhite;
    private LinkedList<Location> mPlayerMoves;
    private LinkedList<byte[][]> mHistory;
    private final Handler mTimerHandler = new Handler();
    private Runnable mCheckStatus;

    private final int CHECKDELAY = 350;

    //private final int MYATTDIR = 1;

    private final int RED = Color.rgb(255, 15, 15);
    private final int GREEN = Color.rgb(0, 204, 0);
    private final int BLUE = Color.rgb(0, 128, 255);

    //private final byte CASTLELEFT = -1;
    //private final byte CASTLERIGHT = -2;

    private final byte BLANK = 0;

    private final byte PAWN = 2;
    private final byte BISHOP = 7;
    private final byte KNIGHT = 6;
    private final byte ROOK = 10;
    private final byte QUEEN = 18;
    private final byte KING = 127;

    private final byte PLAYER = 1;
    private final byte AI = -1;

    private boolean pcSelected;
    private Location selectedLoc;
    private boolean pawnPromotion;
    private byte[] pawnColumn;

    byte[][] myMove;
    boolean done;
    int progress;
    String msg;

    //debug
    private byte[][] testSen = new byte[9][8];

    private final String TAG = "Phyberosis";

    //ini
    public GameEngine(GameBoard board)
    {
        mIamWhite = true;
        mCurrentScenario = board.getStartingScenario();
        mHistory = new LinkedList<>();
        mGameBoard = board;
        mScenarioHandler = new ScenarioHandler();
        mGameBoard.resetColours();

        myMove = new byte[9][8];

        pcSelected = false;
        done = true;

        mCheckStatus = new Runnable() {
            @Override
            public void run() {
                if (!checkMyMove())
                    mTimerHandler.postDelayed(this, CHECKDELAY);
            }
        };

        //debug
        //Log.d(TAG, "GameEngine: " + calculateScore(mCurrentScenario));
//        testd();
        ////Log.d(TAG, "GameEngine: test " + mCurrentScenario[0][0]);

        //Log.d(TAG, "GameEngine: " + mCurrentScenario[8][0]);
        if (mIamWhite)
        {
            myTurn();
        }
    }

    //absolute value
    private int abs(int x)
    {
        if (x < 0)
            return -x;

        return x;
    }

    /**
     * debug
     */

    private void testd()
    {
        //mCurrentScenario = testSen;

        /*mIamWhite = false;
        mGameBoard.swapColours();
        resetBoardColors();*/

        //mGameBoard.updateBoard(testSen);
        setupTestsd();
        runTestd();
    }

    private void setupTestsd()
    {

//        testSen = mGameBoard.getStartingScenario();
//        for (int x = 0; x < 8; x++)
//        {
//            for (int y = 0; y < 8; y++)
//            {
//                testSen[x][y] = 0;
//            }
//        }
//
//        testSen[8][1] = 15;
//        testSen[8][0] = 15;
        {

            testSen = mCurrentScenario;

            testSen = mScenarioHandler.movePiece(4, 7, 4, 5, testSen);
            testSen = mScenarioHandler.movePiece(5, 7, 3, 5, testSen);
            testSen = mScenarioHandler.movePiece(4, 6, 4, 4, testSen);

            testSen = mScenarioHandler.movePiece(0, 1, 0, 3, testSen);
            testSen = mScenarioHandler.movePiece(6, 1, 6, 3, testSen);
            testSen = mScenarioHandler.movePiece(5, 0, 6, 1, testSen);

//            testSen[7][6] = ROOK * AI;
//            testSen[7][0] = ROOK * AI;
//            testSen[0][7] = ROOK * PLAYER;
//            testSen[7][7] = ROOK * PLAYER;
//
//            testSen[1][7] = KNIGHT * PLAYER;
//            testSen[2][4] = KNIGHT * PLAYER;
//            testSen[3][3] = KNIGHT * PLAYER;
//            testSen[5][4] = KNIGHT * PLAYER;
//            testSen[2][0] = KNIGHT * AI;
//            testSen[5][1] = KNIGHT * AI;
//            testSen[0][7] = KNIGHT * AI;
//
//            testSen[3][3] = BISHOP * PLAYER;
//            testSen[6][6] = BISHOP * PLAYER;
//            testSen[2][7] = BISHOP * AI;
//            testSen[5][7] = BISHOP * AI;
//
//            testSen[0][7] = KING * PLAYER;
//            testSen[1][0] = KING * AI;
//            testSen[4][4] = QUEEN * AI;
//            testSen[4][7] = QUEEN * PLAYER;

//            testSen[6][6] = PAWN * AI;
//            testSen[1][6] = PAWN * PLAYER;
//            testSen[3][6] = PAWN * PLAYER;

            mHistory.clear();
            mHistory.add(testSen);
            done = true;
        }
    }

    private void runTestd()
    {

        //printScenariod(Castle(3, 0, 5, 0, testSen));
/*
        printScenariod(mScenarioHandler.movePiece(0, 0, 2, 0, testSen));
        byte[][] ss = new byte[9][8];
        System.arraycopy(testSen, 0, ss, 0, 9);
        printScenariod(ss);
*//*
        //move test
        Log.d(TAG, "runTest:");
        printScenariod(testSen);

        Log.d(TAG, "===========> commencing test");
        printMoveListd(getMoves(testSen[0][0], new Location(0, 0), testSen, testSen));
*/
        /*LinkedList<byte[][]> sen = getChildScenariosOfTile(6, 1, testSen, testSen);

        for (byte[][] s : sen)
        {
            printScenariod(s);
        }*/

        mCurrentScenario = testSen;
        mGameBoard.updateBoard(mCurrentScenario);
    }
    /**end debug*/

    private void switchSides()
    {
        pcSelected = false;
        mIamWhite = !mIamWhite;
        mHistory.add(mScenarioHandler.getNew(mCurrentScenario));

        //swap king and queen + update undo info
        mCurrentScenario = mGameBoard.getStartingScenario();
        if(mCurrentScenario[8][2] %3 != 0)
            mCurrentScenario[8][2] *= 3;
        if(!mIamWhite)
        {
            mCurrentScenario[8][2] /= 2;   // i am black

            mCurrentScenario[3][0] = QUEEN*AI;
            mCurrentScenario[4][0] = KING*AI;
            mCurrentScenario[3][7] = QUEEN*PLAYER;
            mCurrentScenario[4][7] = KING*PLAYER;

            mHistory.add(mScenarioHandler.getNew(mCurrentScenario));    //maintain move pairs

        }else{
            mCurrentScenario[8][2] *= 2;   // i am white

            mCurrentScenario[3][0] = KING*AI;
            mCurrentScenario[4][0] = QUEEN*AI;
            mCurrentScenario[3][7] = KING*PLAYER;
            mCurrentScenario[4][7] = QUEEN*PLAYER;
        }

        mGameBoard.swapColours();
        mGameBoard.resetColours();
        mGameBoard.updateBoard(mCurrentScenario);

//        mHistory.clear();
//        mHistory.add(mScenarioHandler.getNew(mCurrentScenario));

        if(mIamWhite)
        {
            myTurn();
        }

//        //debug
//        for(byte[][] sen : mHistory)
//        {
//            mScenarioHandler.printScenariod(sen);
//        }
//        Log.d(TAG, "switchSides: current");
//        mScenarioHandler.printScenariod(mCurrentScenario);
    }

    //turns tiles that pc is able to move to, to the color green
    private void displayMoves(byte pc, Location l)
    {
        //printScenariod(mCurrentScenario); //debug
        if(pc == BLANK)
        {
            pcSelected = false;
            return;
        }
        if (mCurrentScenario[l.mX][l.mY] < 0)
        {
            mGameBoard.lightUp(l, RED);

        }else{
            //save move list for when player chooses move;
            mPlayerMoves = mScenarioHandler.getMoves(pc, l, mCurrentScenario, mHistory.getLast());

            if(mPlayerMoves != null)
            {
                for(Location move : mPlayerMoves)//lights all all possible moves from moveList
                {
                    mGameBoard.lightUp(move, BLUE);
                    ////Log.d("Phyberosis", "displayMoves lit tile " + l.mX + "," + l.mY);
                }
            }
            mGameBoard.lightUp(l, GREEN);
        }
    }

    private Location getLocFromID(int id)
    {
        int x = id / 10;
        int y = id - (x * 10);

        return new Location(x, y);
    }

    public void handleButton(View v)
    {
        if(!done)
            return;

        if ((v.getTag() == null) || (!v.getTag().getClass().equals(String.class)))
        {
            Location l = getLocFromID(v.getId());

            if (pcSelected)
            {

                //Log.d(TAG, "handleButton: " + l.mX + ",  " + l.mY);
                mGameBoard.resetColours();
                playerMadeMove(selectedLoc, l);

                pcSelected = false;

            }else if(pawnPromotion)//chosen pc to promote to
            {
                Drawable bg = mGameBoard.getBoard()[l.mX][l.mY].getBackground();
                if(bg instanceof ColorDrawable)
                {
                    int c = ((ColorDrawable) bg).getColor();
                    if (c == GREEN)
                    {
                        byte pc = mCurrentScenario[l.mX][l.mY];
                        System.arraycopy(pawnColumn, 0, mCurrentScenario[l.mX], 0, 8);
                        mCurrentScenario[pawnColumn[8]][0] = pc;
                        pawnPromotion = false;
                        mGameBoard.updateBoard(mCurrentScenario);
                        mGameBoard.resetColours();

                        myTurn();
                    }
                }

            }else{//move request
                //Log.d(TAG, "handleButton: " + mScenarioHandler.getType(l, mCurrentScenario).toString());
                mGameBoard.resetColours();
                pcSelected = true;
                displayMoves(mCurrentScenario[l.mX][l.mY], l);
                selectedLoc = l;
            }

        }else if (v.getTag().equals("switchSides"))
        {
            switchSides();

        }else if (v.getTag().equals("undo"))
        {
            if (pcSelected)
            {
                mGameBoard.resetColours();
            }
            pcSelected = false;

            undo();

        }/*else{
            mEngine.resetBoardColors();
            pc = (Piece) v.getTag();
            mEngine.displayMoves(pc);
            pcSelected = true;
            selectedPc = pc;
        }*/
    }

    private void undo()
    {
        if(mHistory.size() == 1)
        {
            return;
        }

        boolean ss = mHistory.getLast()[8][2] %3 == 0;

        if(ss)
        {   // if switched sides
            mIamWhite = !mIamWhite;
            mGameBoard.swapColours();
            mGameBoard.resetColours();
        }

        mHistory.removeLast();                      //skip ai move
        byte[][] last = mHistory.removeLast();      //set to player's move
        for(int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                if (mCurrentScenario[x][y] != last[x][y])
                {
                    mGameBoard.lightUp(new Location(x, y), Color.GRAY);
                }
            }
        }
        mCurrentScenario = mScenarioHandler.getNew(last);

        mGameBoard.updateBoard(mCurrentScenario);

//        //debug
//        int i = 1;
//        for(byte[][] sen : mHistory)
//        {
//            Log.d(TAG, "undo: @@" + i + "@@");i++;
//            mScenarioHandler.printScenariod(sen);
//        }
//        Log.d(TAG, "undo: current:");
//        mScenarioHandler.printScenariod(mCurrentScenario);
    }

    private boolean playerMadeMove(Location from, Location to)
    {
        //debug
        //printMoveListd();
        //Log.d(TAG, "playerMadeMove: " + mCurrentScenario[8][0] + " " + mCurrentScenario[8][1]);

        int fromX = from.mX;
        int fromY = from.mY;

        int toX = to.mX;
        int toY = to.mY;

        boolean moved = false;

        if (mPlayerMoves == null)
        {
            return false;
        }

//        //debug
//        int iii = 1;
//        for(byte[][] sen : mHistory)
//        {
//            Log.d(TAG, "plrM: @@" + iii + "@@");iii++;
//            mScenarioHandler.printScenariod(sen);
//        }
//        Log.d(TAG, "plrM: current:");
//        mScenarioHandler.printScenariod(mCurrentScenario);

        mHistory.add(mScenarioHandler.getNew(mCurrentScenario));
        for(Location l : mPlayerMoves)
        {

            if(l.mX == toX && l.mY == toY)
            {

                byte pc = mCurrentScenario[fromX][fromY];
                switch (pc)
                {
                    case PAWN:
                        if(toY == 0)
                        {
                            pawnPromotion = true;

                            pawnColumn = new byte[9];
                            mCurrentScenario[fromX][fromY] = BLANK;
                            System.arraycopy(mCurrentScenario[fromX], 0, pawnColumn, 0, 8);
                            mCurrentScenario[fromX][0] = KNIGHT;
                            mCurrentScenario[fromX][1] = BISHOP;
                            mCurrentScenario[fromX][2] = ROOK;
                            mCurrentScenario[fromX][3] = QUEEN;

                            mGameBoard.updateBoard(mCurrentScenario);
                            pawnColumn[8] = (byte)toX;
                            for(int i = 0; i < 4; i++)
                            {
                                mGameBoard.getBoard()[fromX][i].setBackgroundColor(GREEN);
                            }
                            return false;
                        }
                        break;
                }
                mCurrentScenario = mScenarioHandler.movePiece(fromX, fromY, toX, toY, mCurrentScenario);

                moved = true;
                break;
            }
        }

        if (moved)
        {
            mPlayerMoves.clear();
            mGameBoard.updateBoard(mCurrentScenario);
            if(mCurrentScenario[8][2] %3 == 0) //reset switched sides flag
                mCurrentScenario[8][2] /= 3;

            manageHistory();
            if(checkReset())
                return true;

            //start my move
            myTurn();
            return true;
        }


        ////Log.d(TAG, "the move: player's " + pc.getType().toString() + " from ("
        //        + fromX + ", " + fromY + ") to (" + toX + ", " + toY + ")" + " is not a valid move!" );
        mPlayerMoves.clear();
        return false;
    }

    //very naive -> purely materialistic
    private void myTurn()
    {
        done = false;

        //debug
//        int iii = 1;
//        for(byte[][] sen : mHistory)
//        {
//            Log.d(TAG, "MT: @@" + iii + "@@");iii++;
//            mScenarioHandler.printScenariod(sen);
//        }
//        Log.d(TAG, "MT: current:");
//        mScenarioHandler.printScenariod(mCurrentScenario);

        MoveGen mg = new MoveGen(mCurrentScenario, mHistory, this);
        mg.begin();

//        //debug
//        long start = System.currentTimeMillis();
//        synchronized (this)
//        {
//            while (!done)
//            {
//                try
//                {
//                    this.wait();
//                } catch (InterruptedException ignored)
//                {}
//            }
//        }

        progress = 0;
        mTimerHandler.postDelayed(mCheckStatus, CHECKDELAY);
    }

    private boolean checkMyMove()
    {

        boolean finished;
        synchronized (this)
        {
//            mGameBoard.swapColours();
//            mGameBoard.updateBoard(mCurrentScenario);

            if(done)
            {
                myMove();
                mGameBoard.reportProgress(100);
                finished = true;
            }else
            {
                mGameBoard.reportProgress(progress);
                finished = false;
            }
            mGameBoard.setLabel(msg);
        }

        return finished;
    }

    public void myMove()
    {
        //debug
        //Log.d(TAG, "myTurn took: " + (System.currentTimeMillis() - start) + "ms");

        byte[][] next;
        synchronized (this)
        {
            next = myMove;
        }
        if(next != null)//null returned if no moves available
        {
            //LinkedList<Location> bestMoves = new LinkedList<>();
            for(int x = 0; x < 8; x++)
            {
                for (int y = 0; y < 8; y++)
                {
                    if (mCurrentScenario[x][y] != next[x][y])
                    {
                        mGameBoard.lightUp(new Location(x, y), RED);
                    }
                }
            }
        }else
        {
            //maintain move pair and reset, since no moves means plr won
            mHistory.add(mScenarioHandler.getNew(mCurrentScenario));
            mCurrentScenario = mScenarioHandler.getNew(mCurrentScenario);
            switchSides();
            return;
        }

        /*Log.d(TAG, "myTurn: best score-> " + score);
        for (int s : scores)
        {
            Log.d(TAG, "myTurn: " + s);
        }*/

        mHistory.add(mScenarioHandler.getNew(mCurrentScenario));
        mCurrentScenario = mScenarioHandler.getNew(next);
        mGameBoard.updateBoard(mCurrentScenario);

        if(mCurrentScenario[8][2] %3 == 0) //reset switched sides flag
            mCurrentScenario[8][2] /= 3;

        manageHistory();
        checkReset();
    }

    private void manageHistory()
    {
        if(mHistory.size() > 50)
        {
            mHistory.removeFirst();
            mHistory.removeFirst();
        }
    }

    private boolean checkReset()
    {

        if(!(mScenarioHandler.checkKing(mCurrentScenario, PLAYER) && mScenarioHandler.checkKing(mCurrentScenario, AI)))
        {
//            //debug
//            int v = 1;
//            for(byte[][] sen : mHistory)
//            {
//                Log.d(TAG, "CR: @@" + v + "@@");v++;
//                mScenarioHandler.printScenariod(sen);
//            }
//            Log.d(TAG, "CR: current:");
//            mScenarioHandler.printScenariod(mCurrentScenario);

            mHistory.removeLast();
            mCurrentScenario = mHistory.removeLast();

//            mHistory.remove(mHistory.size()-3);
//            mHistory.remove(mHistory.size()-3);

            mGameBoard.mbox("Game Over!");
            switchSides();
            return true;
        }
        return false;
    }

    {

    /*private void checkCastle(int x, int y, byte attacker, LinkedList<Location> moveList)
    {
        if (kingMoved)
            return;

        boolean canCastleLeft = false;
        boolean canCastleRight = false;

        if (attacker == PLAYERATTDIR)
        {

        }

        int dx;

        int rookY;
        if (attacker == PLAYERATTDIR)
        {
            rookY = 7;
        }

        if (mGBoard.getBoard())
        //sets new search direction
        dx = 1; //right

        int ii = 0;
        while (true)
        {
            if (isOutOfBounds(x + (dx * ii), y))
            {
                break;
            }

            if (!mGBoard.getAllegiance(x + (dx * ii), y).equals(Type.BLANK) && x < 7)//if scanned tile is not blank && not Rook
            {
                canCastleRight = false;
            }
        }

        //sets new search direction
        dx = -1; //left

        ii = 0;
        while (true)
        {
            if (isOutOfBounds(x + (dx * ii), y))
            {
                break;
            }
        }

        if(attacker == PLAYERATTDIR && (!((Rook)mGBoard.getBoard()[0][7].getTag()).canCastle() || !((Rook)mGBoard.getBoard()[7][7].getTag()).canCastle()))
        {

        }

        if(attacker == MYATTDIR && (!((Rook)mGBoard.getBoard()[0][0].getTag()).canCastle() || !((Rook)mGBoard.getBoard()[7][0].getTag()).canCastle()))
            return;
    }
}


//pawn capture pawn after captured pawn move 2 spaces, past the capturing pawn's capture area
/**must check if equals() works**//*
    private void checkEnPassant(int x, int y, byte attacker, LinkedList<Location> moveList, byte[][] scenario)
    {
        if (x - 1 >= 0)
        {
            Type tileLeft = mScenarioHandler.getAllegiance(x - 1, y);
            if(!tileLeft.equals(Type.BLANK) && attacker != tileLeft.getValue())
            {

                if(((Pawn)scenario.getBoard()[x - 1][y].getTag()).canEnPassant())

                for(Pawn p : enPassantEligible)
                {
                    if (p.equals(scenario.getBoard()[x][y].getTag()));/**must check if equals() works**//*
                    {
                        moveList.add(new Location(x - 1, y + attacker));
                        break;
                    }
                }
            }
        }

        if (x + 1 < 8)
        {
            Type tileRight = mGBoard.getAllegiance(x + 1, y);
            if(!tileRight.equals(Type.BLANK) && attacker != tileRight.getValue())
            {
                for(Pawn p : enPassantEligible)
                {
                    if (p.equals(mGBoard.getBoard()[x][y].getTag()));/**must check if equals() works**//*
                    {
                        moveList.add(new Location(x + 1, y+attacker));
                        break;
                    }
                }
            }
        }
    }*/

/*    private boolean isInCheck(Location location, byte attacker, byte[][] scenario)
    {
        int x = location.mX;
        int y = location.mY;

        int dx, dy;//tiles to increment from location of bishop
        //search direction in {x, y} -> amount to increment in x and y directions to reach each tile to scan
        final int[][] searchDirection = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, -1}, {-1, -1}, {-1, 1}, {1, 1}
        };

        //for rook bishop and queen
        for (int i = 0; i < 8; i++) //per direction loop
        {
            //sets new search direction
            dx = searchDirection[i][0];
            dy = searchDirection[i][1];

            int ii = 0; //per tile loop
            while (true) //add moves until no more moves in current direction
            {

                ii++;//increases distance from current position, in current direction
                if (isOutOfBounds(x + (dx * ii), y + (dy * ii)))
                {
                    break;
                }
                ////Log.d(TAG, "isInCheck: scanning tile " + (x + (dx * ii)) + ", " + (y + (dy * ii)));
                if (!mScenarioHandler.getAllegiance(x + (dx * ii), y + (dy * ii), scenario).equals(Type.BLANK)//if scanned tile is not blank
                        && mScenarioHandler.getAllegiance(x + (dx * ii), y + (dy * ii), scenario).getValue() != attacker)//and scanned tile contains an enemy piece
                {
                    //check if enemy pc can att king
                    if(((i < 4) && (mScenarioHandler.getAllegiance(x + (dx * ii), y + (dy * ii), scenario).equals(Type.ROOK))) ||
                       ((i > 3) && (mScenarioHandler.getAllegiance(x + (dx * ii), y + (dy * ii), scenario).equals(Type.BISHOP))) ||
                                   (mScenarioHandler.getAllegiance(x + (dx * ii), y + (dy * ii), scenario).equals(Type.QUEEN)))
                    {
                        return true;
                    }
                }
            }
        }
    }*/}

}
