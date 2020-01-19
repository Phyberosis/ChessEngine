package game;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.LogRecord;

import javax.swing.JButton;

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

    //private final int MYATTDIR = 1;

    private final Color LIGHT = new Color(255, 153, 51);
    private final Color DARK = new Color(153, 76, 0);
    private final Color RED = new Color(255, 15, 15);
    private final Color GREEN = new Color(0, 204, 0);
    private final Color BLUE = new Color(0, 128, 255);

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
    //debug
    private final byte[][] testSen = new byte[9][8];

    private final String TAG = "Phyberosis";

    //ini
    public GameEngine(GameBoard board)
    {
        System.out.println("Welcome to a game of chess.\nA negative score means I am winning.");


        mIamWhite = true;
        mCurrentScenario = board.getStartingScenario();
        mHistory = new LinkedList<>();
        mHistory.add(mCurrentScenario);
        mHistory.add(mCurrentScenario);
        mGameBoard = board;
        mScenarioHandler = new ScenarioHandler();
        resetBoardColors();

        myMove = new byte[9][8];

        pcSelected = false;
        //debug
        //System.out.println(TAG + "GameEngine: " + calculateScore(mCurrentScenario));
        //testd();
        ////System.out.println(TAG + "GameEngine: test " + mCurrentScenario[0][0]);

        //System.out.println(TAG + "GameEngine: " + mCurrentScenario[8][0]);
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

    //check if location (x, y) is out of the board
    private boolean isOutOfBounds(int x, int y)
    {
        if (x > 7 || x < 0)
        {
            return true;
        } else if (y > 7 || y < 0)
        {
            return true;
        }

        return false;
    }

    /**
     * debug
     */

    private void printMoveListd(LinkedList<Location> list)
    {
        System.out.println(TAG + "move list contains:");
        for (Location l : list)
        {
            System.out.println(TAG + "(" + l.mX + ", " + l.mY + ")");
        }
    }

    private void testd()
    {
        mCurrentScenario = testSen;

        /*mIamWhite = false;
        mGameBoard.swapColours();
        resetBoardColors();*/

        mGameBoard.updateBoard(testSen);
        setupTestsd();
        runTestd();
    }

    private void setupTestsd()
    {

        for (int x = 0; x < 9; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                testSen[x][y] = 0;
            }
        }
        testSen[8][0] = 2;
        testSen[8][1] = 2;

        {

            //testSen[7][0] = ROOK * AI;
            //testSen[7][0] = ROOK * AI;
            //testSen[0][7] = ROOK * PLAYER;
            //testSen[7][7] = ROOK * PLAYER;
/*
            testSen[1][0] = KNIGHT * PLAYER;
            testSen[6][0] = KNIGHT * PLAYER;*/
            //testSen[1][0] = KNIGHT * AI;
            //testSen[6][7] = KNIGHT * AI;*/
/*
            testSen[2][0] = BISHOP * PLAYER;
            testSen[5][0] = BISHOP * PLAYER;*/
            //testSen[2][0] = BISHOP * AI;
            //testCASTLE[5][7] = BISHOP * AI;*/

            testSen[6][7] = KING * PLAYER;
            testSen[4][4] = QUEEN * AI;
            testSen[6][0] = KING * AI;
            //testSen[4][7] = QUEEN * AI;

            testSen[2][5] = PAWN * PLAYER;

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
        System.out.println(TAG + "runTest:");
        printScenariod(testSen);

        System.out.println(TAG + "===========> commencing test");
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

    private void printScenariod(byte[][] s)
    {
        System.out.println(TAG + "printScenario: ------------------------------------------------");
        for (int asdf = 0; asdf < 8; asdf++)
        {
            String[] a = new String[8];
            for (int i = 0; i < 8; i++)
            {
                byte b = s[i][asdf];
                switch (abs(b))
                {
                    case 0:
                        a[i] = "00";
                        break;
                    case 2:
                        a[i] = "P";
                        break;
                    case 6:
                        a[i] = "N";
                        break;
                    case 7:
                        a[i] = "B";
                        break;
                    case 10:
                        a[i] = "R";
                        break;
                    case 18:
                        a[i] = "Q";
                        break;
                    case 127:
                        a[i] = "K";
                        break;

                }
                if (b > 0)
                {
                    a[i] = a[i].concat("p");
                } else if (b < 0)
                {
                    a[i] = a[i].concat("a");
                }
            }
            System.out.println(TAG + "Scenario print was called: "
                    + a[0] + " "
                    + a[1] + " "
                    + a[2] + " "
                    + a[3] + " "
                    + a[4] + " "
                    + a[5] + " "
                    + a[6] + " "
                    + a[7] + " ");
        }
        System.out.println(TAG + "printScenario: ------------------------------------------------");

    }
    /**end debug*/

    private void switchSides()
    {
        pcSelected = false;

        mCurrentScenario = mGameBoard.getStartingScenario();

        //swap king and queen

        if(!mIamWhite)
        {
            mCurrentScenario[3][0] = QUEEN*AI;
            mCurrentScenario[4][0] = KING*AI;
            mCurrentScenario[3][7] = QUEEN*PLAYER;
            mCurrentScenario[4][7] = KING*PLAYER;
        }else{
            mCurrentScenario[3][0] = KING*AI;
            mCurrentScenario[4][0] = QUEEN*AI;
            mCurrentScenario[3][7] = KING*PLAYER;
            mCurrentScenario[4][7] = QUEEN*PLAYER;
        }

        mGameBoard.swapColours();
        resetBoardColors();
        mGameBoard.updateBoard(mCurrentScenario);

        mHistory.clear();
        mHistory.add(mCurrentScenario);
        mHistory.add(mCurrentScenario);

        if(mIamWhite)
        {
            myTurn();
        }
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
            mPlayerMoves = getMoves(pc, l, mCurrentScenario, mHistory.getLast());

            if(mPlayerMoves != null)
            {
                for(Location move : mPlayerMoves)//lights all all possible moves from moveList
                {
                    mGameBoard.lightUp(move, BLUE);
                    ////System.out.println("Phyberosis", "displayMoves lit tile " + l.mX + "," + l.mY);
                }
            }
            mGameBoard.lightUp(l, GREEN);
        }
    }

    private Location getLocFromID(String strID)
    {
    	
    	int id = Integer.valueOf(strID);
        int x = id / 10;
        int y = id - (x * 10);

        return new Location(x, y);
    }

    public void handleButton(ActionEvent v)
    {
    	JButton src = (JButton) v.getSource();
    	
        if (src.getName() == null || !(src.getName().contains("*")))
        {
            Location l = getLocFromID(src.getName());

            if (pcSelected)
            {

                //System.out.println(TAG + "handleButton: " + l.mX + ",  " + l.mY);
                resetBoardColors();
                playerMadeMove(selectedLoc, l);

                pcSelected = false;

            }else if(pawnPromotion)//chosen pc to promote to
            {
            	Color c = src.getBackground();
                if (c.equals(GREEN))
                {
                    byte pc = mCurrentScenario[l.mX][l.mY];
                    System.arraycopy(pawnColumn, 0, mCurrentScenario[l.mX], 0, 8);
                    mCurrentScenario[pawnColumn[8]][0] = pc;
                    pawnPromotion = false;
                    mGameBoard.updateBoard(mCurrentScenario);
                    resetBoardColors();
                    mHistory.add(mCurrentScenario);

                    myTurn();
                }


            }else{//move request
                //System.out.println(TAG + "handleButton: " + mScenarioHandler.getType(l, mCurrentScenario).toString());
                resetBoardColors();
                pcSelected = true;
                displayMoves(mCurrentScenario[l.mX][l.mY], l);
                selectedLoc = l;
            }

        }else if (src.getText().equals("switch sides"))
        {
            mIamWhite = !mIamWhite;
            switchSides();

        }else if (src.getText().equals("undo"))
        {
            if (pcSelected)
            {
                resetBoardColors();
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
        if(mHistory.size() < 4)
        {
            return;
        }

        mHistory.removeLast();
        mCurrentScenario = mScenarioHandler.getNew(mHistory.getLast());
        mHistory.removeLast();
        mGameBoard.updateBoard(mCurrentScenario);
    }

    private Color getTileDefaultColor(int x, int y)
    {
        if ((x + y) % 2 == 0)
        {
            if (mIamWhite)
            {
                return LIGHT;
            }else{
                return DARK;
            }

        }else{
            if (!mIamWhite)
            {
                return LIGHT;
            }else{
                return DARK;
            }
        }
    }

    private void resetBoardColors()
    {
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                mGameBoard.getBoard()[x][y].setBackground(getTileDefaultColor(x, y));
            }
        }
    }

    private boolean playerMadeMove(Location from, Location to)
    {
        //debug
        //printMoveListd();
        //System.out.println(TAG + "playerMadeMove: " + mCurrentScenario[8][0] + " " + mCurrentScenario[8][1]);

        int fromX = from.mX;
        int fromY = from.mY;

        int toX = to.mX;
        int toY = to.mY;

        boolean moved = false;

        if (mPlayerMoves == null)
        {
            return false;
        }

        for(Location l : mPlayerMoves)
        {

            if(l.mX == toX && l.mY == toY)
            {

                byte pc = mCurrentScenario[fromX][fromY];
                byte attacker = (byte)(abs(pc)/pc);
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
                                mGameBoard.getBoard()[fromX][i].setBackground(GREEN);
                            }

                            return false;
                        }
                        break;

                    case KING:
                        if(abs(toX-fromX) == 2)
                        {
                            // mCurrentScenario = Castle(fromX, fromY, toX, toY, mCurrentScenario);
                            // // mPlayerMoves.clear();
                            // // mGameBoard.updateBoard(mCurrentScenario);
                            // // //start my move
                            // // myTurn();
                            // // return true;
                        }else{
                            mCurrentScenario[8][(attacker+1)/2] = 1;
                        }
                        break;

                    case ROOK:
                        if(mCurrentScenario[8][(attacker+1)/2] % 3 != 0 && fromX == 0)//left Rook hasn't moved && is moving now
                        {
                            mCurrentScenario[8][(attacker+1)/2] *= 3;//moved
                            System.out.println(TAG + "playerMadeMove: LR" + attacker);
                        }else if(mCurrentScenario[8][(attacker+1)/2] % 5 != 0 && fromX == 7)//R hasn't moved && is moving now
                        {
                            mCurrentScenario[8][(attacker+1)/2] *= 5;//moved
                            System.out.println(TAG + "playerMadeMove: RR" + attacker);
                        }
                        break;
                }
                mHistory.add(mScenarioHandler.getNew(mCurrentScenario));
                mCurrentScenario = mScenarioHandler.movePiece(fromX, fromY, toX, toY, mCurrentScenario);

                moved = true;
                break;

            }
        }

        if (moved)
        {
            mPlayerMoves.clear();
            mGameBoard.updateBoard(mCurrentScenario);

            //start my move
            myTurn();
            return true;
        }


        ////System.out.println(TAG + "the move: player's " + pc.getType().toString() + " from ("
        //        + fromX + ", " + fromY + ") to (" + toX + ", " + toY + ")" + " is not a valid move!" );
        mPlayerMoves.clear();
        return false;
    }

    //very naive -> purely materialistic
    private void myTurn()
    {
        if(checkReset())
            reset();

        done = false;
        MoveGen mg = new MoveGen(mCurrentScenario, mHistory, this);
        mg.begin();

        //debug
        long start = System.currentTimeMillis();

        while (!done)
        {
            try
            {
                Thread.sleep(50);
            } catch (InterruptedException ignored)
            {}
        }

        //debug
        System.out.println("Turn took: " + (System.currentTimeMillis() - start) + "ms");

        byte[][] next;
        synchronized (this)
        {
             next = myMove;
        }
        if(next != null)//null returned if no moves available
        {
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
            reset();
        }

        /*System.out.println(TAG + "myTurn: best score-> " + score);
        for (int s : scores)
        {
            System.out.println(TAG + "myTurn: " + s);
        }*/

        mHistory.add(mCurrentScenario);
        mCurrentScenario = mScenarioHandler.getNew(next);
        mGameBoard.updateBoard(mCurrentScenario);

        if(checkReset())
            reset();
    }

    private void reset()
    {
        mCurrentScenario = mGameBoard.getStartingScenario();
        mGameBoard.updateBoard(mCurrentScenario);
        mHistory.clear();
        mHistory.add(mCurrentScenario);
        mHistory.add(mCurrentScenario);

        if(mIamWhite)
        {
            myTurn();
        }
    }

    private boolean checkReset()
    {

        boolean reset = false;
        if(!(checkKing(mCurrentScenario, PLAYER) && checkKing(mCurrentScenario, AI)))
        {
            reset = true;
        }

        return reset;
    }

    private boolean checkKing(byte[][] s, byte allegiance)
    {
        boolean found = false;
        for(int x = 0; x < 8; x++)
        {
            for(int y = 0; y < 8; y++)
            {
                if (s[x][y] == KING*allegiance)
                {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    private byte[][] Castle(int fromX, int fromY, int toX, int toY, byte[][] sen)
    {

        byte[][] ret;
        //update board and current scenario
        //move King
        byte attacker = (byte)(sen[fromX][fromY]/abs(sen[fromX][fromY]));
        ret = mScenarioHandler.movePiece(fromX, fromY, toX, toY, sen);
        ret[8][(attacker+1)/2] = 1; //set castleInfo

        // int rookX;
        // if(toX < 4)
        // {
        //     rookX = 0;
        // }else{
        //     rookX = 7;
        // }

        // //move rook
        // ret = mScenarioHandler.movePiece(rookX, fromY, (fromX-toX)/2+toX, toY, ret);
        return ret;
    }

    //returns a list of moves (x, y) that a piece of type "piece" can move to from location "l"
    private LinkedList<Location> getMoves(byte pc, Location l, byte[][] scenario, byte[][] hist)
    {
        ////System.out.println(TAG + "getMoves getting move for " + attacker.toString() + "'s " + piece.toString());

        //decide which piece it is and calls appropriate method to generate a list of possible moves
        switch (abs(pc))
        {
            case PAWN:
                return getPawnMoves(l, scenario, hist);
            case BISHOP:
                return getBishopMoves(l, scenario);
            case KNIGHT:
                return getKnightMoves(l, scenario);
            case ROOK:
                return getRookMoves(l, scenario);
            case QUEEN:
                return getQueenMoves(l, scenario);
            case KING:
                return getKingMoves(l, scenario);
            default:
                return null;
        }
    }

    private LinkedList<Location> getKingMoves(Location location, byte[][] scenario)
    {
        LinkedList<Location> moveList = new LinkedList<>();
        int x = location.mX;
        int y = location.mY;

        byte attacker = (byte)(scenario[x][y]/(abs(scenario[x][y])));

        int dx, dy;//tiles to increment from location of bishop
        //search direction in {x, y} -> amount to increment in x and y directions to reach each tile to scan
        final int[][] searchDirection = {//direction check list
                {-1, 0}, {1, 0}, {0, 1}, {0, -1},
                {1, -1}, {-1, -1}, {-1, 1}, {1, 1}
        };

        /** 1 **/
        byte castleInfo = scenario[8][(attacker+1)/2]; // see header for use

        for (int i = 0; i < 8; i++) //per direction loop
        {
            //sets new search direction
            dx = searchDirection[i][0];
            dy = searchDirection[i][1];

            if (isOutOfBounds(x + (dx), y + (dy)))
            {continue;}
            //check castle
            {/**
             * 1 the king hasn't moved
             * 2 the respective rook hasn't moved
             * 3 there are no pieces in the way
             * 4 king not in check
             * 5 king would not be in check
             *
             * note 4 and 5 should be handled by the min-max -> checking if king is in check is too hard
             **/}

            if ((i == 0 || i == 1) && castleInfo % 2 == 0 && scenario[i*7][y] != BLANK)/**1**///directions left and right, coded in direction check list && rook exists
            {

                /** 2 **/
                if (castleInfo % (i *2 +3) != 0) /**2**/ //mod 3 is left, mod 5 R
                {
                    /** 3 **/
                    int ii = i*7 - (i*2-1);//starting from one closer than rook position -> x is 1 or 6
                    while(ii != x)
                    {
                        if (scenario[ii][y] != 0)
                            break;

                        ii -= dx;
                    }
                    //early break means found blocking piece -> i != x
                    if (ii == x)
                    {
                        moveList.add(new Location(dx*2 + x, y)); //king moves two spaces
                        //debug
                        //if(Arrays.equals(scenario, mCurrentScenario))
                        //mGameBoard.swapColours();

                    }
                }
            }

            ////System.out.println(TAG + "getKingMoves: scanning tile " + (x + (dx)) + ", " + (y + (dy)));
            if (scenario[x + dx][y + dy] * attacker <= 0)//if scanned tile is blank or occupied by enemy
            {
                moveList.add(new Location(x + (dx), y + (dy)));
            }
        }

        ////System.out.println("Phyberosis", "getKingMoves returned moveList of size " + moveList.size());
        return moveList;
    }

    private LinkedList<Location>  getQueenMoves(Location location, byte[][] scenario)
    {

        LinkedList<Location> moveList = new LinkedList<>();
        int x = location.mX;
        int y = location.mY;

        byte attacker = (byte)(scenario[x][y]/(abs(scenario[x][y])));

        int dx, dy;//tiles to increment from location of bishop
        //search direction in {x, y} -> amount to increment in x and y directions to reach each tile to scan
        final int[][] searchDirection = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, -1}, {-1, -1}, {-1, 1}, {1, 1}
        };

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
                }else if (scenario[x + (dx * ii)][y + (dy * ii)] * attacker <= 0)//if scanned tile is blank or enemy
                {
                    moveList.add(new Location(x + (dx * ii), y + (dy * ii)));
                    if(scenario[x + (dx * ii)][y + (dy * ii)]*attacker < 0) //enemy
                        break;
                }else //not blank and not enemy -> found piece of same allegiance as current bishop
                {
                    break;
                }
            }
        }

        ////System.out.println("Phyberosis", "getQueenMoves returned moveList of size " + moveList.size());
        return moveList;
    }

    //generates a list of moves for a knight at location "location"
    //attacker is an integer, -1 or 1 -> determines if it is a player's knight or ai's knight
    /**see "getBishopMoves" for detailed explanation of method**/
    private LinkedList<Location>  getKnightMoves(Location location, byte[][] scenario)
    {
        LinkedList<Location> moveList = new LinkedList<>();
        int x = location.mX;
        int y = location.mY;

        byte attacker = (byte)(scenario[x][y]/(abs(scenario[x][y])));

        int dx, dy;
        final int[][] searchDirection = {
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2},
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
        };

        for (int i = 0; i < 8; i++) //per direction loop
        {
            //sets new search direction
            dx = searchDirection[i][0];
            dy = searchDirection[i][1];

            byte test;
            if (isOutOfBounds(x + (dx), y + (dy)))
            {
                continue;
            }else{
                test = scenario[x + (dx)][y + (dy)];
            }
            if (test * attacker <= 0)//if scanned tile is blank or enemy
            {
                moveList.add(new Location(x + (dx), y + (dy)));
            }
        }

        return moveList;
    }

    //generates a list of moves for a bishop at location "location"
    //attacker is an integer, -1 or 1 -> determines if it is a player's bishop or ai's bishop
    private LinkedList<Location> getBishopMoves(Location location, byte[][] scenario)
    {
        LinkedList<Location> moveList = new LinkedList<>();
        int x = location.mX;
        int y = location.mY;

        byte attacker = (byte)(scenario[x][y]/(abs(scenario[x][y])));

        int dx, dy;//tiles to increment from location of bishop
        //search direction in {x, y} -> amount to increment in x and y directions to reach each tile to scan
        final int[][] searchDirection = {
                {1, -1}, {-1, -1}, {-1, 1}, {1, 1}
        };

        for (int i = 0; i < 4; i++) //per direction loop
        {
            //sets new search direction
            dx = searchDirection[i][0];
            dy = searchDirection[i][1];

            int ii = 0; //per tile loop
            while (true) //add moves until no more moves in current direction
            {

                ii++;//increases distance from current position, in current direction
                byte test;
                if (isOutOfBounds(x + (dx * ii), y + (dy * ii)))
                {
                    break;
                }else{
                    test = scenario[x + (ii*dx)][y + (ii*dy)];
                }
                if (test * attacker <= 0)//if scanned tile is blank or enemy
                {
                    moveList.add(new Location(x + (dx * ii), y + (dy * ii)));
                    if(test*attacker < 0)//enemy
                        break;
                }else //not blank and not enemy -> found piece of same allegiance as current bishop
                {
                    break;
                }
            }
        }

        return moveList;
    }

    //follows same idea and format as other get_move methods
    private LinkedList<Location>  getRookMoves(Location location, byte[][] scenario)
    {
        //////System.out.println(TAG + "getRookMoves: test " + mGBoard.getAllegiance(6, 7).toString());
        LinkedList<Location> moveList = new LinkedList<>();
        int x = location.mX;
        int y = location.mY;

        byte attacker = (byte)(scenario[x][y]/(abs(scenario[x][y])));

        int dx, dy;//tiles to increment from location of bishop
        //search direction in {x, y} -> amount to increment in x and y directions to reach each tile to scan
        final int[][] searchDirection = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int i = 0; i < 4; i++) //per direction loop
        {
            //sets new search direction
            dx = searchDirection[i][0];
            dy = searchDirection[i][1];

            int ii = 0; //per tile loop
            while (true) //add moves until no more moves in current direction
            {

                ii++;//increases distance from current position, in current direction
                byte test;
                if (isOutOfBounds(x + (dx * ii), y + (dy * ii)))
                {
                    break;
                }else{
                    test = scenario[x + (ii*dx)][y + (ii*dy)];
                }

                if (test * attacker <= 0)//if scanned tile is blank or enemy
                {
                    moveList.add(new Location(x + (dx * ii), y + (dy * ii)));
                    if(test*attacker < 0) //enemy
                        break;
                }else //not blank and not enemy -> found piece of same allegiance as current bishop
                {
                    break;
                }
            }
        }

        ////System.out.println("Phyberosis", "getRookMoves returned moveList of size " + moveList.size());
        return moveList;
    }

    //follows same idea and format as other get_move methods
    private LinkedList<Location>  getPawnMoves(Location location, byte[][] scenario, byte[][] hist)
    {
        LinkedList<Location> moveList = new LinkedList<>();
        int x = location.mX;
        int y = location.mY;

        byte attacker = (byte)(scenario[x][y]/(abs(scenario[x][y])));

        //one forward
        if (y - attacker < 8 && y - attacker >= 0) //in bounds
        {
            if(scenario[x][y - attacker] == 0)  //tile empty?
            {
                moveList.add(new Location(x, y - attacker));

                //if Pawn is in starting position -> double space
                if ((location.mY == 6 && attacker == PLAYER) || (location.mY == 1 && attacker == AI))
                {
                    if (scenario[x][y - 2 * attacker] == 0)      //and tile empty
                    {
                        moveList.add(new Location(x, y - (2 * attacker)));
                    }
                }
            }
            boolean leftInBounds = false, rightInBounds=false;
            //capture left
            if (x - 1 >= 0 ) //in bounds
            {
                leftInBounds = true;
                if (scenario[x-1][y-attacker]*attacker < 0) //tile has enemy
                {
                    moveList.add(new Location(x - 1, y-attacker));
                }
            }

            //capture right
            if (x + 1 >= 0) //in bounds
            {
                rightInBounds = true;
                if (scenario[x+1][y-attacker]*attacker < 0) //tile has enemy
                {
                    moveList.add(new Location(x + 1, y-attacker));
                }
            }


            //if pawn is in location for enPassant -> check enPassant
            /**
             * 1 there is pawn on side
             * 2 there is not respective pawn in starting position on that side
             * 3 there was pawn on starting position
             * 4 there was not pawn on side
             * **/
            if ((location.mY == 3 && attacker == PLAYER) || (location.mY == 3 && attacker == AI))
            {

                for (int i = -1; i < 2; i+=2)
                {
                    //check left then right using the for loop above
                    if (((i == -1 && leftInBounds) || (i == 1 && rightInBounds)) && //left/right side in bounds
                            scenario[x+i][y]*attacker < 0 &&                /**1**/
                            scenario[x+i][y-(2*attacker)]*attacker < 0 &&   /**2**/
                            hist[x+i][y]*attacker == 0 &&                   /**3**/
                            hist[x+i][y-(2*attacker)]*attacker == 0)        /**4**/
                    {
                        moveList.add(new Location(x + i, y+attacker));
                    }
                }
            }
        }

        //////System.out.println("Phyberosis", "getPawnMoves() first returned x value: " + ((Location)moveList.getFirst()).mX);
        return moveList;
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
                ////System.out.println(TAG + "isInCheck: scanning tile " + (x + (dx * ii)) + ", " + (y + (dy * ii)));
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
