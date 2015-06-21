import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class City extends JFrame{
	public static final int FRAME_WIDTH = 600;
	public static final int FRAME_HEIGHT = 800;
	public static final int PANEL_WIDTH = 500;
	public static final int PANEL_HEIGHT = 100;
	public static final int PANEL_X = 50;
	public static final int PANEL_Y = 50;
	public static int colors = 3;

	private JPanel scoreBoard;
	private JLabel background;
	private JLabel pointLabel;
	private JLabel moveLabel;
	private Building[][] buildings = new Building[5][5];
	private int point = 0;
	private int move = 0;
	private boolean isMoved;			//avoid add move without moving
	private Timer timer;
	private int timerCount;
	private boolean clickDisabled;		//avoid clicking during moving

	public City(){
		super();
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setTitle("SubaraCity");
		setLayout(null);
		setResizable(false);
		setVisible(true);
		
		JLabel background = new JLabel(new ImageIcon("img/city.png"));
		setContentPane(background);

		for(int col = 0; col < 5; col++){
			for(int row = 0 ; row < 5 ; row++){
				buildings[col][row] = new Building(col, row);
				buildings[col][row].addMouseListener(new ActionClick());
				add(buildings[col][row]);
			}
		}

		scoreBoard = new JPanel();
		scoreBoard.setLocation(PANEL_X, PANEL_Y);
        scoreBoard.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        scoreBoard.setLayout(new BorderLayout());
        scoreBoard.setOpaque(false);

        pointLabel = new JLabel();
        pointLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        scoreBoard.add(pointLabel, BorderLayout.EAST);

        moveLabel = new JLabel();
        moveLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        scoreBoard.add(moveLabel, BorderLayout.WEST);
        
        updatePanel();
        add(scoreBoard);
	}
	
	public void click(Building clicked){
		if(timerCount == 0){
			clickDisabled = true;	//disable panel during panel moving
			timer.start();

			search(clicked);
		}
		else if(timerCount < 10)
			merging(clicked);
		else if(timerCount == 10)
			merge(clicked);
		else if(timerCount < 26)
			falling();
		else{
			fall();
			fill();
			updatePanel();
			repaint();
			check();
			
			timer.stop();
			clickDisabled = false;	//enable panel to be clicked 
		}
		timerCount++;
	}
	//search for same-color buildings to be merged
	public void search(Building clicked){
		searchNear(clicked);

		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				buildings[i][j].isSearched = false;	//reset
			}
		}
	}
	//search for nearby same-color buildings to be merged
	private void searchNear(Building center){
		int row = center.getRow();
		int col = center.getCol();
		Colors color = center.getColor();
		center.isSearched = true;	//avoid self search
		//search up
		if( row != 0 && buildings[col][row-1].isSearched == false ){
			Building up = buildings[col][row-1];
			up.isSearched = true; 
			if( color.equals(up.getColor()) ){
				up.toBeMerged = true;
				searchNear(up);
			}
		}
		//search down
		if( row != 4 && buildings[col][row+1].isSearched == false ){
			Building down = buildings[col][row+1];
			down.isSearched = true;  
			if( color.equals(down.getColor()) ){
				down.toBeMerged = true;
				searchNear(down);
			}
		}
		//search left
		if( col != 0 && buildings[col-1][row].isSearched == false ){
			Building left = buildings[col-1][row];
			left.isSearched = true;
			if( color.equals( left.getColor()) ){
				left.toBeMerged = true;
				searchNear(left);
			}
		}
		//search right
		if( col != 4 && buildings[col+1][row].isSearched == false ){
			Building right = buildings[col+1][row];
			right.isSearched = true; 
			if( color.equals( right.getColor()) ){
				right.toBeMerged = true;
				searchNear(right);
			}
		}
	}	
	public void merging(Building clicked){
		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				if(buildings[i][j].toBeMerged == true){
					buildings[i][j].mergeTo(clicked);
				}
			}
		}
	}
	//merge same-color building
	public void merge(Building clicked){
		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				if(buildings[i][j].toBeMerged == true){
					isMoved = true;
					
					clicked.addPoint(buildings[i][j]);
					clicked.check();

					remove(buildings[i][j]);
					buildings[i][j] = null;
				}
			}
		}
		repaint();
	}
	public void falling(){
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
	public void fall(){
		Building[][] tmp = new Building[5][5];
		for(int col = 0; col < 5; col++){
			int rowTmp = 4;
			for(int row = 4; row >= 0; row--){
				if(buildings[col][row] != null){
					tmp[col][rowTmp] = buildings[col][row];
					tmp[col][rowTmp].setRow(rowTmp);
					tmp[col][rowTmp].updateLocation();
					rowTmp--;
				}
			}
		}
		for(int col = 0; col < 5; col++){
			for(int row = 0; row < 5; row++){
				buildings[col][row] = tmp[col][row];
			}
		}
	}
	//fill the blank
	public void fill(){
		for(int col = 0; col < 5; col++){
			for(int row = 0; row < 5; row++){
				if(buildings[col][row] == null){
					buildings[col][row] = new Building(col, row); 
					buildings[col][row].addMouseListener(new ActionClick());
					add(buildings[col][row]);			
				}
			}
		}
	}
	public void updatePanel(){
		updatePoint();
		updateMove();
		pointLabel.setText("Point : " + point);
        moveLabel.setText("Moves : " + move);
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
	private void updateMove(){
		if(isMoved == true){
			move++;
		}
		if(move == 30){
			// Building.addGray();
		}
	}
	public static void addGray(){
		colors = 4;
	}	
	public void check(){
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
		if(isOver == false){
			for(int i = 0; i < 5; i++){
				for(int j = 0 ; j < 5 ; j++){
					if(buildings[i][j].toBeMerged == true){
						buildings[i][j].toBeMerged = false;
					}
				}
			}
		}
		else{
			JOptionPane.showMessageDialog(null, "Game Over", null, JOptionPane.INFORMATION_MESSAGE );
			dispose();
		}
	}
	private class ActionClick extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			if(!clickDisabled){
				Building clicked = (Building)e.getComponent();
				timer = new Timer(50, new ActionListener(){
					public void actionPerformed(ActionEvent e){
						click(clicked);
					}
				});
				timerCount = 0;
				isMoved = false;					
				click(clicked);
			}
		}
	}
}
