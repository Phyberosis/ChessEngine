package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Created by Phyberosis on 1/8/2017.
 *
 * game board
 */

public class GameBoard {

    private JButton mBoard[][];

    private final byte BLANK = 0;

    private final byte PAWN=2;
    private final byte BISHOP=7;
    private final byte KNIGHT=6;
    private final byte ROOK=10;
    private final byte QUEEN=18;
    private final byte KING=127;

    private final Color BLACK = Color.BLACK;
    private final Color WHITE = Color.WHITE;

    private Color playerColor;
    private Color myColor;

    private byte[][] mStartingScenario = new byte[9][8];
    
    public GameBoard(JPanel screen, int l)
    {
        //ini
        mBoard = new JButton[8][8];

        playerColor = BLACK;
        myColor = WHITE;

        Font f = new Font(Font.DIALOG, Font.PLAIN, 25);
        
        //padding top so the status bar doesn't block the game
        //LinearLayout paddingTop = new LinearLayout(gameActivity);
        //paddingTop.setLayoutParams(new LinearLayout.LayoutParams(length * 8, length));
        //screen.addView(paddingTop);

        ScenarioHandler h = new ScenarioHandler(); //for setup tile

        //grid for board
        for (int y = 0; y < 8; y++)
        {
            for (int x = 0; x < 8; x++) {
                mBoard[x][y] = new JButton();
                //mBoard[x][y].addActionListener(gui);
                mBoard[x][y].setPreferredSize(new Dimension(l, l));
                //mBoard[x][y].setText(Integer.toString(x) + "," + Integer.toString(y)); //debug
                mBoard[x][y].setFont(f);
                mBoard[x][y].setName(Integer.toString(x*10 +y));

                setupTile(x, y, h);

                screen.add(mBoard[x][y]);

            }
        }
        
        for (int y = 2; y < 8; y++)
        {
            mStartingScenario[8][y] = 0;
        }
        mStartingScenario[8][0]=2;
        mStartingScenario[8][1]=2;
    }

    
    private boolean setupIsMine(int y)
    {
        return y < 2;
    }

    void swapColours()
    {
        if(playerColor == WHITE)
        {
            playerColor = BLACK;
            myColor = WHITE;
        }else
        {
            playerColor = WHITE;
            myColor = BLACK;
        }
    }

    private String getCharID(byte pc)
    {
        switch (abs(pc))
        {
            case PAWN:
                return "p";
            case KNIGHT:
                return "N";
            case BISHOP:
                return "B";
            case ROOK:
                return "R";
            case QUEEN:
                return "Q";
            case KING:
                return "K";
        }
        return "";
    }

    private byte abs(byte b)
    {
        if(b < 0)
            return (byte) (b*-1);
        return b;
    }

    //places pc in new spot -> piece exists as ref on two tiles after method
    private void placePieceLabel(byte pc, int x, int y)
    {

        if(pc > 0)
        {
            mBoard[x][y].setForeground(playerColor);
        }else{
            mBoard[x][y].setForeground(myColor);
        }
        mBoard[x][y].setText(getCharID(pc));
    }

    //removes pc from tile -> use with placePiece
    public void removePiece(int x, int y)
    {
        mBoard[x][y].setName(null);
        mBoard[x][y].setText("");

    }

    byte[][] getStartingScenario()
    {
        byte[][] b = new byte[9][8];

        for (int x = 0; x < 9; x++)
        {
            System.arraycopy(mStartingScenario[x], 0, b[x], 0, 8);
        }

        //Log.d("@@@@@@@@@@", "getStartingScenario: " + b[8][0]);
        return b;
    }

    //must call same method in GameEngine and setup blank tile
    private void setupTile(int x, int y, ScenarioHandler h)
    {
        byte t = BLANK;
        switch (y)
        {
            case 1:case 6:
            t = PAWN;
            break;

            case 0:case 7:
            switch (x)
            {
                case 0:case 7:
                t = ROOK;
                break;

                case 1:case 6:
                t = KNIGHT;
                break;

                case 2:case 5:
                t = BISHOP;
                break;
            }
            break;

            default:
                t = BLANK;
                break;
        }

        if (playerColor == BLACK)
        {
            if (y == 0 || y == 7)
            {
                if (x == 3)
                {
                    t = KING;
                }else if (x == 4)
                {
                    t = QUEEN;
                }
            }
        }else{
            if (y == 0 || y == 7)
            {
                if (x == 4)
                {
                    t = QUEEN;
                }else if (x == 3)
                {
                    t = KING;
                }
            }
        }

        byte isMine = 1;
        if (setupIsMine(y))
            isMine = -1;

        mStartingScenario[x][y] = (byte) (t*isMine);
        placePieceLabel((byte)(t*isMine), x, y);
    }

    public JButton[][] getBoard()
    {
        return mBoard;
    }

    //repeat with ints?
    void updateBoard(byte[][] s)
    {
        for (int x = 0; x < 8; x ++)
        {
            for (int y = 0; y < 8; y ++)
            {
                placePieceLabel(s[x][y], x, y);
            }
        }
    }

    void lightUp(Location l, Color color)
    {
        mBoard[l.mX][l.mY].setBackground(color);
    }

}

