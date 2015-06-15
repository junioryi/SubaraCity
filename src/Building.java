import javax.swing.JPanel;
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

	private int x;
	private int y;

	public static final int WIDTH = 100;
	public static final int HEIGHT = 100;
	public static final int LEFT_BOARDER = 50;
	public static final int UPPER_BOARDER = 200;
	
	public Building(int col,int row){
		super();
		point=randomPoint();
		// level=
		// item=ItemOnField.people;	
		color=randomColor();
		isSearched = false;
		toBeMerged = false;
		
		this.col = col;
		this.row = row;
		x = LEFT_BOARDER + col*WIDTH;
    	y = UPPER_BOARDER + row*HEIGHT;
    	setBackground(color);
    	setLocation(x, y);
        setSize(WIDTH, HEIGHT);
	}	
	public int getCol(){
		return col;
	}
	public int getRow(){
		return row;
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
	public void resetRow(int row){
		this.row = row;
	}
	public void resetLocation(){
		x = 50 + col*100;
    	y = 200 + row*100;
    	setLocation(x, y);
	}
	public void addPoint(Building merged){
		point += merged.getPoint();
		level = (int) ( Math.log(point) / Math.log(2) ) ;	//to be modified
	}
	//generate random color
	private static Color randomColor(){
		int r = (int) (Math.random() * 3);
		switch (r){
			case 0:
				return Color.green;
			case 1:
				return Color.gray;
			case 2:
				return new Color(200, 150, 0);
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
