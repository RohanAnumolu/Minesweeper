import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

//+++++++++++++++++++++++++++++++
import java.util.Timer;
import java.util.TimerTask;
//+++++++++++++++++++++++++++++++

public class mine extends JFrame implements ActionListener, MouseListener {
    JToggleButton[][] board;
    JPanel boardPanel;
    boolean firstClick, gameOn;
    int numMines, clickCount, timePassed, flags;

    int countif = 1;

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    ImageIcon flagIcon, mineIcon, smile, lose, win, wait, incorrect;
    JButton reset;
    ImageIcon[] nums;

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    Timer timer;
    JTextField timeField;

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    JMenuBar menuBar;
	JMenu difficulty;
	JMenuItem beginner, inter, expert, random;

	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	Font font;
	GraphicsEnvironment ge;

	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	boolean waiting = false;

	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public mine() {

		flags = 0;


		smile = new ImageIcon("smile0.png");
		smile = new ImageIcon(smile.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

		lose = new ImageIcon("dead0.png");
		lose = new ImageIcon(lose.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

		win = new ImageIcon("win0.png");
		win = new ImageIcon(win.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

		wait = new ImageIcon("wait0.png");
		wait = new ImageIcon(wait.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

		/*
		incorrect = new ImageIcon("incorrect0.png");
		incorrect = new ImageIcon(incorrect.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
		*/

		try
		{
			ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			font = Font.createFont(Font.TRUETYPE_FONT, new File("digital-7.ttf"));
			ge.registerFont(font);
			font = font.deriveFont(15f);

		}catch(IOException|FontFormatException e)
		{


		}

		menuBar = new JMenuBar();
		difficulty = new JMenu("Difficulty");
		difficulty.setFont(font.deriveFont(15f));
		beginner = new JMenuItem("Beginner");
		beginner.setFont(font.deriveFont(15f));
		inter = new JMenuItem("Intermediate");
		inter.setFont(font.deriveFont(15f));
		expert = new JMenuItem("Expert");
		expert.setFont(font.deriveFont(15f));
		random = new JMenuItem("Random");
		random.setFont(font.deriveFont(15f));
		beginner.addActionListener(this);
		inter.addActionListener(this);
		expert.addActionListener(this);
		random.addActionListener(this);

		difficulty.add(beginner);
		difficulty.add(inter);
		difficulty.add(expert);
		difficulty.add(random);


		menuBar.setLayout(new GridLayout(1, 3));

		menuBar.add(difficulty);


		reset = new JButton();
		reset.setIcon(smile);
		reset.addActionListener(this);
		reset.setFocusable(false);

		menuBar.add(reset);


		timeField = new JTextField();
		timeField.setText("   " + 0);
		timePassed=0;
		timeField.setBackground(Color.BLACK);
		timeField.setForeground(Color.GREEN);
		timeField.setFont(font.deriveFont(20f));
		timeField.setEditable(false);
		timeField.setPreferredSize(new Dimension(this.getWidth()/3, 30));
		menuBar.add(timeField);


		this.add(menuBar, BorderLayout.NORTH);

		mineIcon = new ImageIcon("mine0.png");
		mineIcon = new ImageIcon(mineIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));

		flagIcon = new ImageIcon("flag.png");
		flagIcon = new ImageIcon(flagIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));


		nums = new ImageIcon[9];
		for(int i = 1; i < 9; i++)
		{
			nums[i] = new ImageIcon(i + ".png");
			nums[i] = new ImageIcon(nums[i].getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
		}

		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");

		}catch(Exception e)
		{

		}

		UIManager.put("ToggleButton.select", new Color(191, 148, 228));


        createBoard(9, 9, 10);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void createBoard(int rows, int cols, int nm)
    {
        if(boardPanel!= null)
        {
            this.remove(boardPanel);
        }
        clickCount = 0;
        gameOn = true;
        firstClick = true;
        numMines = nm;
        board = new JToggleButton[rows][cols];
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(rows, cols));
        for(int i = 0; i < rows; i++)
        {
            for(int j = 0; j < cols; j++)
            {
                board[i][j] = new JToggleButton();

                board[i][j].putClientProperty("row", i);
               // board[i][j].setIcon(nums[1]);
                board[i][j].putClientProperty("col", j);
                board[i][j].putClientProperty("state", 0);
                board[i][j].setFocusable(false);

                board[i][j].setMargin(new Insets(0, 0, 0, 0));

                board[i][j].addMouseListener(this);
                boardPanel.add(board[i][j]);

            }
        }
        this.add(boardPanel, BorderLayout.CENTER);
        this.setSize(cols*40, rows*40);
        this.revalidate();
    }

    public void dropMines(int row, int col)
    {
        int count = 0;
        while(count < numMines)
        {
            int randR = (int)(Math.random()*board.length);
            int randC = (int)(Math.random()*board[0].length);
            int state = (int)(board[randR][randC]).getClientProperty("state");
            if(state != 10 && Math.abs(randR - row) > 1 && Math.abs(randC - col) > 1)
            {
                board[randR][randC].putClientProperty("state", 10); //add mine
                count++;
                for(int i = randR - 1; i <= randR + 1; i++)
                {
                    for(int j = randC - 1; j <= randC + 1; j++)
                    {
                        try
                        {
                            state = (int)(board[i][j]).getClientProperty("state");
                            if(state != 10)
                            {
                                board[i][j].putClientProperty("state", state+1); // changes count of surrounding spaces
                            }
                        }
                        catch(ArrayIndexOutOfBoundsException e)
                        {

                        }
                    }
                }
            }
        }
       /* for(int r = 0; r < board.length; r++)
        {
            for(int c = 0; c < board[0].length; c++)
            {
                int state = (int)(board[r][c]).getClientProperty("state");
                board[r][c].setText("" + state);
            }
        }*/
    }



    public void actionPerformed(ActionEvent e) {

		if(e.getSource() == beginner)
		{
			createBoard(9, 9, 10);
		}
		if(e.getSource() == inter)
		{
			createBoard(16, 16, 40);
		}
		if(e.getSource() == expert)
		{
			createBoard(16, 30, 99);
		}
		if(e.getSource() == random)
		{
			int row = (int)(Math.random()*12) + 9;
			int col = (int)(Math.random()*24) + 9;
			int num = (row*col) * (row*col) / 2400 + 10;
			createBoard(row, col, num);
		}

		reset.setIcon(smile);
		createBoard(board.length, board[0].length, numMines);
		timePassed = 0;
		if(timer != null)
		{
			timer.cancel();
		}
		timeField.setText("   " + 0);
		gameOn = true;
		flags = 0;

    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
		int row = (int)((JToggleButton)e.getComponent()).getClientProperty("row");
		int col = (int)((JToggleButton)e.getComponent()).getClientProperty("col");
		if(!waiting && !board[row][col].isSelected())
		{
			reset.setIcon(wait);
			waiting=true;
		}

    }

    public void mouseReleased(MouseEvent e) {
		if(gameOn)
		{
			int row = (int)((JToggleButton)e.getComponent()).getClientProperty("row");
			int col = (int)((JToggleButton)e.getComponent()).getClientProperty("col");
			int state = (int)((JToggleButton)e.getComponent()).getClientProperty("state");


			waiting = false;
			reset.setIcon(smile);


			if(e.getButton() == MouseEvent.BUTTON1 && board[row][col].isEnabled())
			{
				if(firstClick)
				{
					firstClick = false;
					dropMines(row, col);
					timer = new Timer();
					timer.schedule(new UpdateTimer(), 0, 1000);
				}

				if(state == 10)
				{
					//board[row][col].setIcon(mineIcon);
					showMines();
					//JOptionPane.showMessageDialog(null, "You are a loser!!");
					gameOn = false;
					timer.cancel();
					reset.setIcon(lose);
				}
				else
				{
					board[row][col].setEnabled(false);
					board[row][col].setSelected(true);
					expand(row, col);
					clickCount++;

					if(clickCount==((board.length)*(board[0].length)) - numMines)
					{

						//JOptionPane.showMessageDialog(null, "You are a winner!!!!!");
						timer.cancel();
						reset.setIcon(lose);
					}
				}
			}
			if(e.getButton() == MouseEvent.BUTTON3 && !firstClick)
			{
				if(!board[row][col].isSelected())
				{
					if(board[row][col].getIcon()==null)
					{
						if(flags < numMines)
						{
							board[row][col].setIcon(flagIcon);
							board[row][col].setDisabledIcon(flagIcon);
							board[row][col].setEnabled(false);
							flags++;
						}
					}
					else
					{
						flags--;
						board[row][col].setIcon(null);
						board[row][col].setDisabledIcon(null);
						board[row][col].setEnabled(true);
					}
				}

				/*
				if(countif == 1)
				{
					board[row][col].setIcon(flagIcon);
					countif++;
				}
				else if(countif == 2)
				{
					board[row][col].setIcon(null);
					countif--;
				}
				*/

			}
		}
		else
		{

		}
    }

    public void showMines()
    {
		for(int i = 0; i <board.length; i++)
		{
			for(int j = 0; j <board[0].length; j++)
            {
				board[i][j].setEnabled(false);
				try
				{
					int state = (int)(board[i][j]).getClientProperty("state");
					if(state == 10)
					{
						board[i][j].setIcon(mineIcon);
						board[i][j].setDisabledIcon(mineIcon);
					}
				}
				catch(ArrayIndexOutOfBoundsException e)
				{

				}
			}
		}
	}

    public void expand(int row, int col)
    {
		int state = (int)(board[row][col]).getClientProperty("state");

		if(!board[row][col].isSelected())
		{
			clickCount++;
			board[row][col].setEnabled(false);
			board[row][col].setSelected(true);
		}

		if(state != 0)
		{
			//board[row][col].setText("" + state);
			board[row][col].setIcon(nums[state]);
			board[row][col].setDisabledIcon(nums[state]);
		}
		else
		{
			for(int i = row - 1; i <= row + 1; i++)
			{
				for(int j = col - 1; j <= col + 1; j++)
                {
					try
					{
						if(!board[i][j].isSelected())
							expand(i, j);
					}
					catch(ArrayIndexOutOfBoundsException e)
					{

					}
				}
			}
		}
	}

	public class UpdateTimer extends TimerTask
	{
		public void run()
		{
			if(gameOn)
			{
				timePassed++;
				timeField.setText("   " +timePassed);
			}
		}
	}

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public static void main(String[] Args) {
		new mine();
    }
}