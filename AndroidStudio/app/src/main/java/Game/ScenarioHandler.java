package Game;

import android.util.Log;

import java.util.LinkedList;

import Pieces.Type;

/**
 * Created by Phyberosis on 1/11/2017.
 */

public class ScenarioHandler
{
    //debug
    private final String TAG = "Phyberosis SH";

    private final byte BLANK = 0;

    private final byte PAWN=2;
    private final byte BISHOP=7;
    private final byte KNIGHT=6;
    private final byte ROOK=10;
    private final byte QUEEN=18;
    private final byte KING=127;

    private final byte PLAYER = 1;
    private final byte AI= -1;

    /*
    private final byte BLANK = 0;

    private final byte MYPAWN=1;
    private final byte MYBISHOP=2;
    private final byte MYKNIGHT=3;
    private final byte MYROOK=4;
    private final byte MYQUEEN=5;
    private final byte MYKING=6;

    private final byte PLAYERPAWN=-7;
    private final byte PLAYERBISHOP=8;
    private final byte PLAYERKNIGHT=9;
    private final byte PLAYERROOK=10;
    private final byte PLAYERQUEEN=11;
    private final byte PLAYERKING=12;*/

    private int abs(int x)
    {
        if (x < 0)
            return -x;

        return x;
    }

    /*
    public byte getBLANK(){return BLANK;}
    public byte getPAWN(){return PAWN;}
    public byte getBISHOP(){return BISHOP;}
    public byte getKNIGHT(){return KNIGHT;}
    public byte getROOK(){return ROOK;}
    public byte getQUEEN(){return QUEEN;}
    public byte getKING(){return KING;}*/

    //debug
    public void printScenariod(byte[][] s, String req)
    {
        String msg = "print scenario from :" + req + "\n-------------------------";
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
            msg += "\n "
                    + a[0] + " "
                    + a[1] + " "
                    + a[2] + " "
                    + a[3] + " "
                    + a[4] + " "
                    + a[5] + " "
                    + a[6] + " "
                    + a[7] + " ";
        }
        msg += "\n-------------------------\n";
        for(byte b : s[8])
        {
            msg += b + " ";
        }
        Log.d(TAG, msg);

    }

    public void setBlankTile(int x, int y, byte[][] s)
    {
        s[x][y] = (byte)Type.BLANK.getValue();
    }

    public Type getType(Location l, byte[][] s)
    {
        return getType(l.mX, l.mY, s);
    }

    public Type getType(int x, int y, byte[][] s)
    {
        switch (abs(s[x][y]))
        {
            case PAWN:
                return Type.PAWN;
            case BISHOP:
                return Type.BISHOP;
            case KNIGHT:
                return Type.KNIGHT;
            case ROOK:
                return Type.ROOK;
            case QUEEN:
                return Type.QUEEN;
            case KING:
                return Type.KING;
            default:
                return Type.BLANK;
        }
    }

    //sums the all piece values of board
    int calculateScore(byte[][] s)
    {
        int score;
        int matFactor = 100;
        int pawnFactor = 1;

        if(!checkKing(s, AI))
        {
            return Integer.MAX_VALUE;
        }else if(!checkKing(s, PLAYER))
            return Integer.MIN_VALUE;

        //materialistic calc
        int matScore = 0;
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                matScore += s[x][y];
            }
        }
        score = matScore * matFactor;

        //pawn advancement
        int pawnScore = 0;
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                if (abs(s[x][y]) == 2)
                {
                    pawnScore += (s[x][y] * 7/2) - (y - 7);
                }
            }
        }
        score += pawnScore * pawnFactor;

//        mScenarioHandler.printScenariod(s, "all " + score);
        return score;
    }

     LinkedList<byte[][]> getChildScenariosOfTile(int x, int y, byte[][] scenario, byte[][] hist)
    {
        LinkedList<byte[][]> children = new LinkedList<>();

        byte[][] s = getNew(scenario);

        if(s[x][y] != 0)//not blank
        {
//            // debug
//            if(s[x][y] == -2)
//                Log.d(TAG, "getChildScenariosOfTile: \n\n\n\n\n\n\n\n\n" + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            LinkedList<Location> moves = getMoves(s[x][y], new Location(x, y), s, hist);
            if(moves == null)
            {
                return children;
            }
            for (Location l : moves)
            {
                //debug
//                if(l.mY == 7 && s[x][y] == -2)
//                    Log.d(TAG, "getChildScenariosOfTile: @@@@@@@@@@@@@@@@@@@@@ here");

                byte[][] child = getNew(s);
                if (abs(child[x][y]) == 2 && (child[x][y] - 2) * 7 / -4 == l.mY)//move puts pc to end
                {
//                    Log.d(TAG, "getChildScenariosOfTile: \n\n\n\n\n\n\n\n\n" + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    addPawnPromotions(children, x, l.mX, l.mY, child);
                }
                /*if(l.mX == -1)
                {
                    printScenariod(s);
                }*/
                child = movePiece(x, y, l.mX, l.mY, s);
                children.add(child);//normal move found
            }
        }

        return children;
    }

    private void addPawnPromotions(LinkedList<byte[][]> children, int x, int toX, int toY, byte[][] sen)
    {
        int fromY = toY*5/7 + 1;
        /***/
        byte[][] knight = new byte[9][8];//new sen
        for (int mx = 0; mx < 8; mx++)
        {
            System.arraycopy(sen[mx], 0, knight[mx], 0, 8);
        }
        //set new pc
        knight[toX][toY] = (byte)(KNIGHT*sen[x][fromY]/2);//knight * PLayer or AI (PAWN = 2 so +-2 / 2)
        knight[x][fromY] = BLANK;//erase pawn
        /***/
        byte[][] bishop = new byte[9][8];
        for (int mx = 0; mx < 8; mx++)
        {
            System.arraycopy(sen[mx], 0, bishop[mx], 0, 8);
        }
        bishop[toX][toY] = (byte)(BISHOP*sen[x][fromY]/2);
        bishop[x][fromY] = BLANK;//erase pawn
        /***/
        byte[][] rook = new byte[9][8];
        for (int mx = 0; mx < 8; mx++)
        {
            System.arraycopy(sen[mx], 0, rook[mx], 0, 8);
        }
        rook[toX][toY] = (byte)(ROOK*sen[x][fromY]/2);
        rook[x][fromY] = BLANK;//erase pawn
        /***/
        byte[][] queen = new byte[9][8];
        for (int mx = 0; mx < 8; mx++)
        {
            System.arraycopy(sen[mx], 0, queen[mx], 0, 8);
        }
        queen[toX][toY] = (byte)(QUEEN*sen[x][fromY]/2);
        queen[x][fromY] = BLANK;//erase pawn

        children.add(knight);
        children.add(bishop);
        children.add(rook);
        children.add(queen);
    }

    boolean getIsMine(int x, int y, byte[][] s)
    {
        return s[x][y] < BLANK;
    }

    byte[][] movePiece(int Fx, int Fy, int Tx, int Ty, byte[][] from)
    {
        byte[][] s = new byte[9][8];

        for(int x = 0; x < 9; x++)
        {
            System.arraycopy(from[x], 0, s[x], 0, 8);
        }

        //player castle data at 1, ai at 0
        int attacker = (from[Fx][Fy]/abs(from[Fx][Fy]) + 1)/2;    // ai 0, plr 1
        switch(getType(Fx, Fy, from))
        {
            case KING:
                if(abs(Tx-Fx) == 2)
                {
                    int x = 0;                      //x of rook
                    int dx = Tx-Fx;
                    if(dx > 0)
                        x=7;
                    s[Fx + dx/2][Ty] = s[x][Fy];    //move rook
                    s[x][Fy] = BLANK;

                }
                s[8][attacker] = 15;
                break;

            case ROOK:
                if(s[8][attacker] % 3 != 0 && Fx == 0)//left Rook hasn't moved && is moving now
                {
                    s[8][attacker] *= 3;//moved
                }else if(s[8][attacker] % 5 != 0 && Fx == 7)//R hasn't moved && is moving now
                {
                    s[8][attacker] *= 5;//moved
                }
                break;
        }

        if (Tx == -1)
        {
            Log.d("@@@@@@@@", "movePiece: " + getType(Fx, Fy, s).toString() + " " + s[Fx][Fy]);
        }
        s[Tx][Ty] = s[Fx][Fy];
        s[Fx][Fy] = BLANK;
        return s;

    }

    byte[][] getNew(byte[][] old)
    {
        byte[][] b = new byte[9][8];

        for(int x = 0; x < 9; x++)
        {
            System.arraycopy(old[x], 0, b[x], 0, 8);
        }

        return b;
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

    boolean checkKing(byte[][] s, byte allegiance)
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

//    byte[][] Castle(int fromX, int fromY, int toX, int toY, byte[][] sen)
//    {
//        byte[][] ret;
//        //update board and current scenario
//        //move King
//        int attacker = sen[fromX][fromY]/abs(sen[fromX][fromY]);
//        ret = movePiece(fromX, fromY, toX, toY, sen);
//        ret[8][(attacker+1)/2] = 1; //set castleInfo
//
////        Log.d(TAG, "castle @@@@@@@@@@@@@@@@@@@@@@@" + sen[fromX][fromY]);
////        printScenariod(sen);
//
//        int rookX;
//        if(toX < 4)
//        {
//            rookX = 0;
//        }else{
//            rookX = 7;
//        }
//        //move rook
//        ret = movePiece(rookX, fromY, (fromX-toX)/2+toX, toY, ret);
//        return ret;
//    }

    //returns a list of moves (x, y) that a piece of type "piece" can move to from location "l"
    LinkedList<Location> getMoves(byte pc, Location l, byte[][] scenario, byte[][] hist)
    {
        ////Log.d(TAG, "getMoves getting move for " + attacker.toString() + "'s " + piece.toString());

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

    LinkedList<Location> getKingMoves(Location location, byte[][] scenario)
    {
        LinkedList<Location> moveList = new LinkedList<>();
        int x = location.mX;
        int y = location.mY;

        int attacker = scenario[x][y]/(abs(scenario[x][y]));

        int dx, dy;//tiles to increment from location of bishop
        //search direction in {x, y} -> amount to increment in x and y directions to reach each tile to scan
        final int[][] searchDirection = {//direction check list
                {-1, 0}, {1, 0}, {0, 1}, {0, -1},
                {1, -1}, {-1, -1}, {-1, 1}, {1, 1}
        };

        byte castleInfo = scenario[8][(attacker+1)/2]; // see header for use
        //Log.d(TAG, "getKingMoves: " + Byte.toString(castleInfo) + " " + attacker);
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

            if ((i == 0 || i == 1) && castleInfo % 2 == 0 && abs(scenario[i*7][y]) == ROOK)/**1**///directions left and right, coded in direction check list && rook exists
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

            ////Log.d(TAG, "getKingMoves: scanning tile " + (x + (dx)) + ", " + (y + (dy)));
            if (scenario[x + dx][y + dy] * attacker <= 0)//if scanned tile is blank or occupied by enemy
            {
                moveList.add(new Location(x + (dx), y + (dy)));
            }
        }

        ////Log.d("Phyberosis", "getKingMoves returned moveList of size " + moveList.size());
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

        ////Log.d("Phyberosis", "getQueenMoves returned moveList of size " + moveList.size());
        return moveList;
    }

    //generates a list of moves for a knight at location "location"
    //attacker is an integer, -1 or 1 -> determines if it is a player's knight or ai's knight
    /**see "getBishopMoves" for detailed explanation of method**/
    LinkedList<Location>  getKnightMoves(Location location, byte[][] scenario)
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
    LinkedList<Location> getBishopMoves(Location location, byte[][] scenario)
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
    LinkedList<Location>  getRookMoves(Location location, byte[][] scenario)
    {
        //////Log.d(TAG, "getRookMoves: test " + mGBoard.getAllegiance(6, 7).toString());
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

        ////Log.d("Phyberosis", "getRookMoves returned moveList of size " + moveList.size());
        return moveList;
    }

    //follows same idea and format as other get_move methods
    LinkedList<Location>  getPawnMoves(Location location, byte[][] scenario, byte[][] hist)
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
            if (x + 1 <= 7) //in bounds
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
                    //check left then right using above
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

        //////Log.d("Phyberosis", "getPawnMoves() first returned x value: " + ((Location)moveList.getFirst()).mX);
        return moveList;
    }

    { /*
    void placePiece(byte pc, boolean isMine, int x, int y, byte[][] s)
    {

        switch (pc)
        {
            case PAWN:
                if(isMine)
                {
                    s[x][y] = PAWN*AI;
                }else{
                    s[x][y] = PAWN;
                }break;
            case BISHOP:
                if(isMine)
                {
                    s[x][y] = BISHOP*AI;
                }else{
                    s[x][y] = BISHOP;
                }break;
            case KNIGHT:
                if(isMine)
                {
                    s[x][y] = KNIGHT*AI;
                }else{
                    s[x][y] = KNIGHT;
                }break;
            case ROOK:

                if(isMine)
                {
                    s[x][y] = ROOK*AI;
                }else{
                    s[x][y] = ROOK;
                }break;
            case QUEEN:
                if(isMine)
                {
                    s[x][y] = QUEEN*AI;
                }else{
                    s[x][y] = QUEEN;
                }break;
            case KING:
                if(isMine)
                {
                    s[x][y] = KING*AI;
                }else{
                    s[x][y] = KING;
                }break;
        }
    }*/}

}
