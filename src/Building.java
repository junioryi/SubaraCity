import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class Building extends JPanel{
	private int point;
	private int level;
	private Color color;
	private ItemOnField item;
	
	private int col;
	private int row;
	public boolean isSearched;
	public boolean toBeMerged;

	private int xFixed;
	private int yFixed;
	private int x;
	private int y;
	private static int colors = 3;
	
	public static final int WIDTH = 100;
	public static final int HEIGHT = 100;
	public static final int LEFT_BOARDER = 50;
	public static final int UPPER_BOARDER = 200;

	public static final int SET_GOLDEN_POINT = 16;
	public static final int UPGRADE_POINT = 32;
	
	public Building(int col,int row){
		super();

		point = randomPoint();
		// level=
		// item=ItemOnField.people;	
		color = randomColor();
		isSearched = false;
		toBeMerged = false;
		
		this.col = col;
		this.row = row;
    	updateLocation();
    	setBackground(color);
        setSize(WIDTH, HEIGHT);
		setBorder(BorderFactory.createLineBorder(Color.black));
	}	
	public int getCol(){
		return col;
	}
	public int getRow(){
		return row;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public Color getColor() {
		return color;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {	//to be modified
		this.point = point;
		if (this.point==1){
			this.item=ItemOnField.people;
		}else if(this.point<4){
			this.item=ItemOnField.tree;
		}else if (this.point<6){
			this.item=ItemOnField.house;
		}else {
			this.item=ItemOnField.building;
		}
	}
	public int getLevel() {
		return level;
	}
	public void updateRow(int row){
		this.row = row;
	}
	public void updateLocation(){
		x = LEFT_BOARDER + col*WIDTH;
    	y = UPPER_BOARDER + row*HEIGHT;
    	xFixed = x;
    	yFixed = y;
    	setLocation(x, y);
	}
	public void addPoint(Building merged){
		point += merged.getPoint();
		level = (int) ( Math.log(point) / Math.log(2) ) ;	//to be modified
	}
	public static void addGray(){
		colors = 4;
	}
	public void setGolden(){
		color = new Color(255, 215, 0);			//golden
    	setBackground(color);
		point = SET_GOLDEN_POINT;
	}
	public void upgrade(){
		setBorder(BorderFactory.createLineBorder(Color.white));
		removeMouseListener(getMouseListeners()[0]);
	}
	public void moveTo(Building target){
		x += ((target.x - xFixed)/10);
		y += ((target.y - yFixed)/10);
	}
	//generate random color
	private static Color randomColor(){
		int r = (int) (Math.random() * colors);
		switch (r){
			case 0:
				return new Color(50, 205, 50);	//bright green				
			case 1:
				return new Color(0, 100, 0);	//dark green
			case 2:
				return new Color(139, 69, 19);	//brown
			case 3:
				return Color.gray;				
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
        g.setFont(new Font("Monospaced", Font.BOLD, 72));
        g.drawString(Integer.toString(point), 10, 80);
    } 
}
