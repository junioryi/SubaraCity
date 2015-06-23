import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class City extends JFrame{
	private static final int FRAME_WIDTH = 600;
	private static final int FRAME_HEIGHT = 800;
	private static int colors = 3;

	private static Image gray;
	private static Image[] golden = new Image[6];	
	private static Image[] green = new Image[7];
	private static Image[] brown = new Image[7];
	private static Image[] darkGray = new Image[7];
	private static Image[] darkGreen = new Image[7];
	private static AudioPlayer mergeSound = new AudioPlayer(new File("sound/merge.wav"));	
	private static AudioPlayer overSound = new AudioPlayer(new File("sound/over.wav"));	
	private static AudioPlayer removeSound = new AudioPlayer(new File("sound/remove.wav"));	

	private ScoreBoard scoreBoard = new ScoreBoard();
	// private FunctionBoard functionBoard = new FunctionBoard();
	private JLabel background = new JLabel();
	private Building[][] buildings = new Building[5][5];

	private Timer timer;
	private int timerCount;
	private boolean clickDisabled;		//avoid clicking during moving
	
	public static void main(String[] args){
		loadImg();
		City city1 = new City();
	}
	public City(){
		super("SubaraCity");
		setVisible(true);
		setResizable(false);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setLocationRelativeTo(null);
		setContentPane(background);

		for(int col = 0; col < 5; col++){
			for(int row = 0 ; row < 5 ; row++){
				buildings[col][row] = new Building(col, row);
				add(buildings[col][row]);
			}
		}
        
        scoreBoard.update();
        add(scoreBoard);
        repaint();
	}
	//search for same-color buildings to be merged
	private void search(Building clicked){
		searchNear(clicked);
		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				buildings[i][j].isSearched = false;	//reset
			}
		}
	}
	//search for nearby same-color buildings to be merged
	private void searchNear(Building center){
		int row = center.row;
		int col = center.col;
		center.isSearched = true;	//avoid self search
		//search up
		if( row != 0 && buildings[col][row-1].isSearched == false ){
			Building up = buildings[col][row-1];
			up.isSearched = true; 
			if( center.color.equals(up.color) ){
				up.toBeMerged = true;
				searchNear(up);
			}
		}
		//search down
		if( row != 4 && buildings[col][row+1].isSearched == false ){
			Building down = buildings[col][row+1];
			down.isSearched = true;  
			if( center.color.equals(down.color) ){
				down.toBeMerged = true;
				searchNear(down);
			}
		}
		//search left
		if( col != 0 && buildings[col-1][row].isSearched == false ){
			Building left = buildings[col-1][row];
			left.isSearched = true;
			if( center.color.equals(left.color) ){
				left.toBeMerged = true;
				searchNear(left);
			}
		}
		//search right
		if( col != 4 && buildings[col+1][row].isSearched == false ){
			Building right = buildings[col+1][row];
			right.isSearched = true; 
			if( center.color.equals(right.color) ){
				right.toBeMerged = true;
				searchNear(right);
			}
		}
	}	
	//merging animation
	private void merging(Building clicked){
		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				if(buildings[i][j].toBeMerged == true){
					buildings[i][j].mergeTo(clicked);
				}
			}
		}
	}
	//merge same-color building
	private void merge(Building clicked){
		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				if(buildings[i][j].toBeMerged == true){
					clicked.addPoint(buildings[i][j]);
					clicked.updateLevel();

					remove(buildings[i][j]);
					buildings[i][j] = null;
				}
			}
		}
		repaint();
	}	
	//falling animation
	private void falling(){
		Building[][] tmp = new Building[5][5];
		for(int col = 0; col < 5; col++){
			int rowTmp = 4;
			for(int row = 4; row >= 0; row--){
				if(buildings[col][row] != null){
					tmp[col][rowTmp] = new Building(col, rowTmp);
					buildings[col][row].fallTo(tmp[col][rowTmp]);
					rowTmp--;
				}
			}
		}
	}
	//fall the building
	private void fall(){
		Building[][] tmp = new Building[5][5];
		for(int col = 0; col < 5; col++){
			int rowTmp = 4;
			for(int row = 4; row >= 0; row--){
				if(buildings[col][row] != null){
					tmp[col][rowTmp] = buildings[col][row];
					tmp[col][rowTmp].row = rowTmp;
					tmp[col][rowTmp].updateLocation();
					rowTmp--;
				}
			}
		}
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 5; j++){
				buildings[i][j] = tmp[i][j];
			}
		}
	}
	//fill the blank
	private void fill(){
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 5; j++){
				if(buildings[i][j] == null){
					buildings[i][j] = new Building(i, j); 
					add(buildings[i][j]);			
				}
			}
		}
	}
	private static void addGray(){
		colors = 4;
	}
	private void moveCheck(Building clicked){
		boolean toMove = false;
		outerLoop:
		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				if(buildings[i][j].toBeMerged == true){
					toMove = true;
					break outerLoop;		
				}
			}
		}
		if(toMove == false){
			removeSound.play();
			clicked.setBorder(BorderFactory.createLoweredBevelBorder());
			Object[] options = { "確定", "再看看"};
			if(scoreBoard.excavator > 0 && JOptionPane.showOptionDialog(null, "確定要摧毀這棟房子嗎？", "挖土機來了！", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null , options, null) == JOptionPane.OK_OPTION){
				remove(clicked);
				buildings[clicked.col][clicked.row] = null;	
				scoreBoard.excavator--;
				timerCount = 11;	
			}
			else{
				clicked.setBorder(BorderFactory.createRaisedBevelBorder());
				timer.stop();
				clickDisabled = false;	//enable panel to be clicked 
			}	
		}
		else{
			scoreBoard.move++;
		}
	}	
	private void overCheck(){
		if(scoreBoard.excavator > 0)
			return;
		boolean isOver = true;
		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				search(buildings[i][j]);	
			}
		}
		outerLoop:
		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				if(buildings[i][j].toBeMerged == true){
					isOver = false;
					break outerLoop;		
				}
			}
		}
		if(isOver == true){
			overSound.play();
			Object[] options = { "是", "放棄"};
			if(JOptionPane.showOptionDialog(null, "是否要重建城市？", "人口爆炸！", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null , options, null) == JOptionPane.OK_OPTION){			
				City city2 = new City();
			}
			dispose();
		}	
		else{
			for(int i = 0; i < 5; i++){
				for(int j = 0 ; j < 5 ; j++){
					if(buildings[i][j].toBeMerged == true){
						buildings[i][j].toBeMerged = false;				
					}
				}
			}	
		}
	}
	public static void loadImg(){
		try {
            gray = ImageIO.read(new File("img/gray8.png"));
            golden[0] = ImageIO.read(new File("img/golden9.png"));
            golden[1] = ImageIO.read(new File("img/golden10.png"));
            golden[2] = ImageIO.read(new File("img/golden11.png"));
            golden[3] = ImageIO.read(new File("img/golden12.png"));
            golden[4] = ImageIO.read(new File("img/golden13.png"));
            golden[5] = ImageIO.read(new File("img/golden14.png"));
            green[0] = ImageIO.read(new File("img/green1.png"));
            green[1] = ImageIO.read(new File("img/green2.png"));
            green[2] = ImageIO.read(new File("img/green3.png"));
            green[3] = ImageIO.read(new File("img/green4.png"));
            green[4] = ImageIO.read(new File("img/green5.png"));
            green[5] = ImageIO.read(new File("img/green6.png"));
            green[6] = ImageIO.read(new File("img/green7.png"));
            darkGreen[0] = ImageIO.read(new File("img/darkGreen1.png"));
            darkGreen[1] = ImageIO.read(new File("img/darkGreen2.png"));
            darkGreen[2] = ImageIO.read(new File("img/darkGreen3.png"));
            darkGreen[3] = ImageIO.read(new File("img/darkGreen4.png"));
            darkGreen[4] = ImageIO.read(new File("img/darkGreen5.png"));
            darkGreen[5] = ImageIO.read(new File("img/darkGreen6.png"));
            darkGreen[6] = ImageIO.read(new File("img/darkGreen7.png"));
            brown[0] = ImageIO.read(new File("img/brown1.png"));
            brown[1] = ImageIO.read(new File("img/brown2.png"));
            brown[2] = ImageIO.read(new File("img/brown3.png"));
            brown[3] = ImageIO.read(new File("img/brown4.png"));
            brown[4] = ImageIO.read(new File("img/brown5.png"));
            brown[5] = ImageIO.read(new File("img/brown6.png"));
            brown[6] = ImageIO.read(new File("img/brown7.png"));
            darkGray[0] = ImageIO.read(new File("img/darkGray1.png"));
            darkGray[1] = ImageIO.read(new File("img/darkGray2.png"));
            darkGray[2] = ImageIO.read(new File("img/darkGray3.png"));
            darkGray[3] = ImageIO.read(new File("img/darkGray4.png"));
            darkGray[4] = ImageIO.read(new File("img/darkGray5.png"));
            darkGray[5] = ImageIO.read(new File("img/darkGray6.png"));
            darkGray[6] = ImageIO.read(new File("img/darkGray7.png"));
        } 
        catch (IOException e) {
        	e.printStackTrace();
        }
	}
	private enum Colors{
		BROWN, GREEN, DARK_GREEN, GRAY, DARK_GRAY, GOLDEN
	}
	//instantiate timer
	private class ActionClick extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			if(!clickDisabled){
				Building clicked = (Building)e.getComponent();
				timer = new Timer(50, new ActionTimer(clicked));
				timerCount = 0;
				clickDisabled = true;	//disable panel during panel moving
				timer.start();
			}
		}
	}
	//initialize timer
	private class ActionTimer implements ActionListener{
		private Building clicked;
		public ActionTimer(Building clicked){
			this.clicked = clicked;
		}
		public void actionPerformed(ActionEvent e){
			if(timerCount == 0){
				search(clicked);
				moveCheck(clicked);
			}
			else if(timerCount < 10)
				merging(clicked);
			else if(timerCount == 10){
				mergeSound.play();
				merge(clicked);
			}
			else if(timerCount < 26)
				falling();
			else{
				fall();
				fill();
				scoreBoard.update();
				repaint();
				overCheck();
				
				timer.stop();
				clickDisabled = false;	//enable panel to be clicked 
			}
			timerCount++;
		}	
	}
	private class ScoreBoard extends JPanel{
		private static final int PANEL_WIDTH = 500;
		private static final int PANEL_HEIGHT = 80;
		private static final int PANEL_X = 50;
		private static final int PANEL_Y = 100;	

		private MyLabel excavatorLabel = new MyLabel(new ImageIcon("img/excavator.png"));
		private MyLabel pointLabel = new MyLabel();
		private MyLabel moveLabel = new MyLabel();	

		private int point = 0;
		private int move = 0;
		private int excavator = 1;
		
		public ScoreBoard(){
			setLocation(PANEL_X, PANEL_Y);
	        setSize(PANEL_WIDTH, PANEL_HEIGHT);
	        setLayout(new GridLayout(1, 0));
	        setOpaque(false);

	        add(moveLabel);
	        add(pointLabel);
	        add(excavatorLabel);
	    }
	    public void update(){
			updatePoint();
			updateColors();
			updateExcavator();
			pointLabel.setText("<html><center>Population<br>" + point + "</center></html>");
		    moveLabel.setText("<html><center>Year<br>" + (1900+move) + "</center></html>");
		    excavatorLabel.setText(" x " + excavator);
		}
		private void updatePoint(){
			int tmp = 0;
			for(int i = 0; i < 5; i++){
				for(int j = 0 ; j < 5 ; j++){
					tmp += buildings[i][j].getPoint();	
				}
			}
			point = tmp;
		}
		private void updateColors(){
			if(move == 30)
				addGray();
		}
		private void updateExcavator(){
			if(move%50 == 0)
				excavator++;
		}	
		private class MyLabel extends JLabel{
			public MyLabel(){
				super("", SwingConstants.CENTER);
				setFont(new Font("Monospaced", Font.BOLD, 24));
		        setBorder(BorderFactory.createRaisedBevelBorder());
		        setOpaque(true);
		        setBackground(Color.WHITE);
				}
				public MyLabel(ImageIcon i){
				super(i);
				setFont(new Font("Monospaced", Font.BOLD, 24));
		        setBorder(BorderFactory.createRaisedBevelBorder());
		        setOpaque(true);
		        setBackground(Color.WHITE);
		    }
		    public MyLabel(String i){
				super(i, SwingConstants.CENTER);
				setFont(new Font("Monospaced", Font.BOLD, 24));
		        setBorder(BorderFactory.createRaisedBevelBorder());
		        setOpaque(true);
		        setBackground(Color.WHITE);
		    }
		}
	}		
    private class Building extends JPanel implements Cloneable{
		private int col;
		private int row;	
		private int point;
		private int level;
		private Colors color;
		private Image img;
		private int x;
		private int y;
		private int xFixed;
		private int yFixed;

		public boolean isSearched = false;
		public boolean toBeMerged = false;
		
		private static final int WIDTH = 100;
		private static final int HEIGHT = 100;
		private static final int LEFT_BOARDER = 50;
		private static final int UPPER_BOARDER = 200;
		private static final int CHANGE_POINT = 128;
		private static final int UPGRADE_POINT = 256;
		
		public Building(int col,int row){
			super();
			this.col = col;
			this.row = row;
			point = randomPoint();
			color = randomColor();
	    	updateLevel();
	    	updateLocation();
			
			addMouseListener(new ActionClick());
	        setSize(WIDTH, HEIGHT);
			setBorder(BorderFactory.createRaisedBevelBorder());
		}	
		public int getPoint() {
			return point;
		}	
		public void addPoint(Building merged){
			point += merged.getPoint();
		}	
		public void updateLevel(){
			if(point >= UPGRADE_POINT && getMouseListeners().length != 0)
				upgrade();
			else if(point >= CHANGE_POINT && getMouseListeners().length != 0)
				change();	
			level = (int) ( Math.log(point)/Math.log(2) );
			updateImg();
		}
		private void updateImg(){
			switch (color){
				case GRAY:
					img = gray;
					break;
				case GREEN:
					img = green[level];
					break;
				case DARK_GREEN:
					img = darkGreen[level];	
					break;
				case BROWN:
					img = brown[level];	
					break;
				case DARK_GRAY:
					img = darkGray[level];
					break;
				case GOLDEN:
					img = golden[(point/CHANGE_POINT)-2];	
			}
		}	
		public void updateLocation(){
			x = LEFT_BOARDER + col*WIDTH;
	    	y = UPPER_BOARDER + row*HEIGHT;
	    	xFixed = x;
	    	yFixed = y;
	    	setLocation(x, y);
		}
		public void change(){
	    	color = Colors.GRAY;
			point = CHANGE_POINT;
		}
		public void upgrade(){
			color = Colors.GOLDEN;
			removeMouseListener(getMouseListeners()[0]);
		}
		public void mergeTo(Building target){
			x += ((target.x - xFixed)/10);
			y += ((target.y - yFixed)/10);
	    	setLocation(x, y);
		}
		public void fallTo(Building target){
			if(x != target.x)
				x += 25;
			if(y != target.y)
				y += 25;
	    	setLocation(x, y);
		}
		//generate random color
		private Colors randomColor(){
			int r = (int) (Math.random() * City.colors);
			switch (r){
				case 0:
					return Colors.GREEN;				
				case 1:
					return Colors.DARK_GREEN;	
				case 2:
					return Colors.BROWN;	
				case 3:
					return Colors.DARK_GRAY;			
				default:
					return null;
			}
		}
		//generate random point
		private int randomPoint(){
			int r = (int) (Math.random() * 2) + 1;
			return r;
		}
	    public void paintComponent(Graphics g){
	        super.paintComponent(g);
	        g.drawImage(img, 0, 0, this);
	    } 
	}
}
