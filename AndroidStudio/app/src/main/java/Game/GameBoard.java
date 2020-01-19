package Game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.phyberosis.chess.R;

/**
 * Created by Phyberosis on 1/8/2017.
 *
 * game board
 */

public class GameBoard{

    private ImageButton mBoard[][];
    private Context gaRef;
    private ProgressBar mProgressBar;
    private Button mLabel;

    private final byte BLANK = 0;

    private final byte PAWN=2;
    private final byte BISHOP=7;
    private final byte KNIGHT=6;
    private final byte ROOK=10;
    private final byte QUEEN=18;
    private final byte KING=127;

    private final int LIGHT = Color.rgb(255, 153, 51);
    private final int DARK = Color.rgb(153, 76, 0);

    private boolean mIamWhite;

    private byte[][] mStartingScenario = new byte[9][8];

    public GameBoard(LinearLayout screen, Context gameActivity, int length, ProgressBar p, Button label)
    {
        //ini
        mBoard = new ImageButton[8][8];
        mIamWhite = true;
        gaRef = gameActivity;
        mProgressBar = p;
        mLabel = label;

        mLabel.setTextColor(Color.WHITE);

        //padding top so the status bar doesn't block the game
        LinearLayout paddingTop = new LinearLayout(gameActivity);
        paddingTop.setLayoutParams(new LinearLayout.LayoutParams(length * 8, length * 2));
        paddingTop.setBackgroundColor(Color.WHITE);
        screen.addView(paddingTop);

        //grid for board
        LinearLayout.LayoutParams buttonLayout = new LinearLayout.LayoutParams(length, length);
        LinearLayout row[] = new LinearLayout[8];
        for (int y = 0; y < 8; y++)
        {
            row[y] = new LinearLayout(gameActivity);
            row[y].setOrientation(LinearLayout.HORIZONTAL);
            row[y].setTag("row " + Integer.toString(y));
            //row[y].setPadding(3,3,3,3);//this works for y only, wield
            for (int x = 0; x < 8; x++) {
                mBoard[x][y] = new ImageButton(gameActivity);
                mBoard[x][y].setLayoutParams(buttonLayout);
                //mBoard[x][y].setText(Integer.toString(x) + "," + Integer.toString(y)); //debug
                mBoard[x][y].setId(10 * x + y);

                setupTile(x, y);

                row[y].addView(mBoard[x][y]);

            }
            screen.addView(row[y]);
        }

        for (int y = 0; y < 8; y++)
        {
            mStartingScenario[8][y] = 2;
        }
        mStartingScenario[8][2] *= 3;
        /**
         *  defaults to 2 for all
         *
         *  0 ai castle info, %2 != 0 king moved, %3 is LRook 5 is R
         *  1 plr castle info
         *  2 undo info, %2 = 0 i am white/else black, %3 = 0 marks first or switched sides on this sen
         */
    }

    private boolean setupIsMine(int y)
    {
        return y < 2;
    }

    void swapColours()
    {
        mIamWhite = !mIamWhite;
    }

    void reportProgress(int p)
    {
//        final int pp = p;
//        Runnable post = new Runnable() {
//            @Override
//            public void run() {
//                mBoard[0][3].setBackgroundColor(Color.argb(255,(int)2.5*pp,(int)2.5*pp,(int)2.5*pp));
//            }
//        };
//        pHandler.postDelayed(post, 0);
        if(mProgressBar.getProgress() != p)
            mProgressBar.setProgress(p);
    }

    void setLabel(String s)
    {
        mLabel.setText(s);
    }

    private int getPicID(byte pc)
    {
        boolean mine = pc < 0;

        // im white and is !mine
        if(mIamWhite == !mine)
        {
            switch (abs(pc))
            {
                case PAWN:
                    return R.mipmap.pb;
                case KNIGHT:
                    return R.mipmap.nb;
                case BISHOP:
                    return R.mipmap.bb;
                case ROOK:
                    return R.mipmap.rb;
                case QUEEN:
                    return R.mipmap.qb;
                case KING:
                    return R.mipmap.kb;
            }
        }else{
            switch (abs(pc))
            {
                case PAWN:
                    return R.mipmap.pw;
                case KNIGHT:
                    return R.mipmap.nw;
                case BISHOP:
                    return R.mipmap.bw;
                case ROOK:
                    return R.mipmap.rw;
                case QUEEN:
                    return R.mipmap.qw;
                case KING:
                    return R.mipmap.kw;
            }
        }
        return 0;
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
        int id = getPicID(pc);
        if (id == 0)
        {
            mBoard[x][y].setImageResource(android.R.color.transparent);
        }else{
            mBoard[x][y].setImageResource(id);
        }
    }

    //removes pc from tile -> use with placePiece
    public void removePiece(int x, int y)
    {
        mBoard[x][y].setTag(null);

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
    private void setupTile(int x, int y)
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

        if (!mIamWhite)
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

    public ImageButton[][] getBoard()
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


    private int getTileDefaultColor(int x, int y)
    {
        if ((x + y) % 2 == 0)
        {
            return LIGHT;
        }else{
            return DARK;
        }
    }

    void resetColours()
    {
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                mBoard[x][y].setBackgroundColor(getTileDefaultColor(x, y));
            }
        }
    }

    void lightUp(Location l, int color)
    {
        mBoard[l.mX][l.mY].setBackgroundColor(color);
    }

    void mbox(String s)
    {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(gaRef);
        dlgAlert.setMessage(s);
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                    }
                });
        dlgAlert.create().show();
    }
}

