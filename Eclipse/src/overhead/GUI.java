package overhead;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import game.GameBoard;
import game.GameEngine;

public class GUI extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	static final long serialVersionUID = -2191074802078732591L;

    private GameBoard mBoard;
    private GameEngine mEngine;
    
	private final JButton btnUndo, btnSS;
	JPanel p=new JPanel();
	
	public GUI(){
		super("Chess");
		/*int length = min(getWidth(), getHeight());
		setSize(length,length);
        length = length / 8 - 28;*/
		this.setMinimumSize(new Dimension(600,650));
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		setup(65);
		
		btnUndo = new JButton("undo");
		btnSS = new JButton("switch sides");
		btnUndo.setName("*");
		btnSS.setName("*");
		
		p.add(btnUndo);
		p.add(btnSS);

		p.setBackground(new Color(220,220,220));
		getContentPane().add(p);

		btnUndo.addActionListener(this);
		btnSS.addActionListener(this);
		
		setVisible(true);
	
		/*GUI g = this;
		this.addComponentListener(new ComponentListener() {
		    public void componentResized(ComponentEvent e) {
		        int length = min(g.getWidth(), g.getHeight());
		        
		        length = length / 8 - 28;
		        
		        for (int y = 0; y < 8; y++)
		        {
		            for (int x = 0; x < 8; x++) {
		                g.mBoard.getBoard()[x][y].setPreferredSize(new Dimension(length, length));
		            }
		        }
		    }

			@Override
			public void componentMoved(ComponentEvent e)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentShown(ComponentEvent e)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentHidden(ComponentEvent e)
			{
				// TODO Auto-generated method stub
				
			}
		});*/
		
		//addTextM("memBnk location:\n" + memBnk.getLoc());
		//addTextM("\nchange by adding to input then press \"Home\"\n");
	}
	
    private int min(int i , int j)
    {
        if(i < j)
            return i;

        return j;
    }
    public void setup(int l)
    {
        //get screen
        //LinearLayout screen = (LinearLayout) findViewById(R.id.screen);

        //screen.removeAllViews();

        //get length
        //DisplayMetrics displaymetrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        //int length = 50;

        //LinearLayout statusBar = new LinearLayout(this);
        //ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
        /*progressBar.setId(R.id.progressBar);
        progressBar.setBackgroundColor(Color.BLACK);
        progressBar.setMax(1);
        progressBar.setProgress(1);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(length*8, length /2));*/

        mBoard = new GameBoard(p, l);        //uses screen and length
        mEngine = new GameEngine(mBoard);
        setupListeners(mBoard.getBoard());

        /*LinearLayout buttons = new LinearLayout(this);

        Button undo = new Button(this);
        undo.setLayoutParams(new LinearLayout.LayoutParams(length * 4, length));
        undo.setText(R.string.btnUndo);
        undo.setBackgroundColor(Color.WHITE);
        undo.seta
        undo.setTag("undo");
        buttons.addView(undo);

        Button switchSides = new Button(this);
        switchSides.setLayoutParams(new LinearLayout.LayoutParams(length * 4, length));
        switchSides.setText(R.string.btnSS);
        switchSides.setBackgroundColor(Color.WHITE);
        switchSides.setOnClickListener(buttonsClickListener);
        switchSides.setTag("switchSides");
        buttons.addView(switchSides);

        screen.addView(buttons);

        statusBar.addView(progressBar);

        screen.addView(statusBar);
        mContentView.setBackgroundColor(Color.rgb(0,0,0));*/

    }
   
    private void setupListeners(JButton[][] board)
    {
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                board[x][y].addActionListener(this);
            }
        }
    }
	
	public void actionPerformed(ActionEvent e)
	{
		mEngine.handleButton(e);
		
	}

}
