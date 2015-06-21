import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

public class Building extends JPanel{
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
	private static Image gray;
	private static Image[] golden = new Image[6];	
	private static Image[] green = new Image[7];
	private static Image[] brown = new Image[7];
	private static Image[] darkGray = new Image[7];
	private static Image[] darkGreen = new Image[7];

	public boolean isSearched = false;
	public boolean toBeMerged = false;
	
	public static final int WIDTH = 100;
	public static final int HEIGHT = 100;
	public static final int LEFT_BOARDER = 50;
	public static final int UPPER_BOARDER = 200;
	public static final int CHANGE_POINT = 128;
	public static final int UPGRADE_POINT = 256;
	
	public Building(int col,int row){
		super();
		this.col = col;
		this.row = row;
		point = randomPoint();
		color = randomColor();
    	updateLevel();
		updateImg();
    	updateLocation();
        
        setSize(WIDTH, HEIGHT);
		setBorder(BorderFactory.createRaisedBevelBorder());
	}	
	public Building(Building original){
		super();
		col = original.col;
		row = original.row;
		point = original.point;
		color = original.color;
    	updateLevel();
		updateImg();
    	updateLocation();
        
        setSize(WIDTH, HEIGHT);
		setBorder(BorderFactory.createRaisedBevelBorder());
	}
	public int getCol(){
		return col;
	}
	public int getRow(){
		return row;
	}
	public int getPoint() {
		return point;
	}	
	public Colors getColor() {
		return color;
	}
	public void setRow(int row){
		this.row = row;
	}
	public void addPoint(Building merged){
		point += merged.getPoint();
	}	
	public void updateLevel(){
		level = (int) ( Math.log(point)/Math.log(2) );
	}
	public void updateImg(){
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
	public void check(){
		if(point >= UPGRADE_POINT && getMouseListeners().length != 0)
			upgrade();
		else if(point >= CHANGE_POINT && getMouseListeners().length != 0)
			change();
		updateLevel();
		updateImg();			
	}
	public void change(){
    	color = Colors.GRAY;
		point = CHANGE_POINT;
	}
	public void upgrade(){
		color = Colors.GOLDEN;
		setBorder(BorderFactory.createLoweredBevelBorder());
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
	//generate random color
	private static Colors randomColor(){
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
	private static int randomPoint(){
		int r = (int) (Math.random() * 2) + 1;
		return r;
	}
	//show point
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(img, 0, 0, this);
    } 
}
