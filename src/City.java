import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.Color;
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

	private JPanel panel;
	private JLabel pointLabel;
	private JLabel moveLabel;
	private Building[][] buildings = new Building[5][5];
	private int point = 0;
	private int move = 0;
	private boolean isMoved;			//avoid add move without moving
	private Timer timer;
	private int timerCount;
	private boolean clickDisabled;	//avoid error

	public City(){
		super();
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setTitle("SubaraCity");
		setLayout(null);
		setResizable(false);
		setVisible(true);
		
		JLabel background = new JLabel(new ImageIcon("city.png"));
		setContentPane(background);
		for(int col = 0; col < 5; col++){
			for(int row = 0 ; row < 5 ; row++){
				buildings[col][row] = new Building(col, row);
				buildings[col][row].addMouseListener(new ActionClick());
				getContentPane().add(buildings[col][row]);
			}
		}
		
		panel = new JPanel();
		panel.setLocation(PANEL_X, PANEL_Y);
        panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);

        updatePoint();
        pointLabel = new JLabel("Point : " + point);
        pointLabel.setLocation(50, 50);
        pointLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        panel.add(pointLabel, BorderLayout.EAST);

        moveLabel = new JLabel("Moves : " + move);
        moveLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        panel.add(moveLabel, BorderLayout.WEST);
        
        add(panel);
        validate();
        repaint();
	}
	
	public void click(Building clicked){
		if(timerCount == 0){
			clickDisabled = true;	//disable panel during panel moving
			isMoved = false;		
			search(clicked);
			timer.start();		
		}
		else if(timerCount < 10){
			merging(clicked);
		}
		else{
			timer.stop();
			merge(clicked);
			fall();
			fill();
			updatePoint();
			updateMove();
			updatePanel();
			checkBuildings();
			repaint();
			check();
			clickDisabled = false;	//enable panel to be clicked 
		}
		timerCount++;
	}
	//search for same-color building to be merged
	public void search(Building clicked){
		searchNear(clicked);

		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				buildings[i][j].isSearched = false;	//reset
			}
		}
	}
	//search for nearby same-color building
	public void searchNear(Building b){
		int row = b.getRow();
		int col = b.getCol();
		Color color = b.getColor();
		buildings[col][row].isSearched = true;	//avoid self search
		//search up
		if( row != 0 && buildings[col][row-1].isSearched == false ){
			buildings[col][row-1].isSearched = true; 
			if( color.equals( buildings[col][row-1].getColor() ) && buildings[col][row-1].getPoint() <= Building.CHANGE_POINT ){
				buildings[col][row-1].toBeMerged = true;
				searchNear(buildings[col][row-1]);
			}
		}
		//search down
		if( row != 4 && buildings[col][row+1].isSearched == false ){
			buildings[col][row+1].isSearched = true;  
			if( color.equals( buildings[col][row+1].getColor() ) && buildings[col][row+1].getPoint() <= Building.CHANGE_POINT ){
				buildings[col][row+1].toBeMerged = true;
				searchNear(buildings[col][row+1]);
			}
		}
		//search left
		if( col != 0 && buildings[col-1][row].isSearched == false ){
			buildings[col-1][row].isSearched = true;
			if( color.equals( buildings[col-1][row].getColor() ) && buildings[col-1][row].getPoint() <= Building.CHANGE_POINT ){
				buildings[col-1][row].toBeMerged = true;
				searchNear(buildings[col-1][row]);
			}
		}
		//search right
		if( col != 4 && buildings[col+1][row].isSearched == false ){
			buildings[col+1][row].isSearched = true; 
			if( color.equals( buildings[col+1][row].getColor() ) && buildings[col+1][row].getPoint() <= Building.CHANGE_POINT ){
				buildings[col+1][row].toBeMerged = true;
				searchNear(buildings[col+1][row]);
			}
		}
	}	
	public void merging(Building clicked){
		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				if(buildings[i][j].toBeMerged == true){
					buildings[i][j].moveTo(clicked);
					repaint();
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
					remove(buildings[i][j]);
					buildings[i][j] = null;
				}
			}
		}
	}
	//fall the building
	public void fall(){
		for(int col = 0; col < 5; col++){
			colFall(buildings[col]);
		}
	}
	public void colFall(Building[] colBuildings){
		Building[] tmp = new Building[5];
		int rowTmp = 4;
		for(int row = 4; row >= 0; row--){
			if(colBuildings[row] != null){
				tmp[rowTmp] = colBuildings[row];
				tmp[rowTmp].updateRow(rowTmp);
				tmp[rowTmp].updateLocation();
				rowTmp--;
			}
		}
		for(int row = 0; row < 5; row++){
			colBuildings[row] = tmp[row];
		}
	}
	//fill the blank
	public void fill(){
		for(int col = 0; col < 5; col++){
			colFill(buildings[col], col);
		}
	}
	public void colFill(Building[] colBuildings, int col){
		for(int row = 0; row < 5; row++){
			if(colBuildings[row] == null){
				colBuildings[row] = new Building(col, row); 
				buildings[col][row].addMouseListener(new ActionClick());
				add(buildings[col][row]);			
			}
		}
	}
	public void updatePoint(){
		int tmp = 0;
		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				tmp += buildings[i][j].getPoint();	
			}
		}
		point = tmp;
	}
	public void updateMove(){
		if(isMoved == true){
			move++;
		}
		if(move == 30){
			Building.addGray();
		}
	}
	public void updatePanel(){
		pointLabel.setText("Point : " + point);
        moveLabel.setText("Moves : " + move);
	}
	public void checkBuildings(){
		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				if(buildings[i][j].getPoint() >= Building.UPGRADE_POINT && buildings[i][j].getMouseListeners().length != 0){
					buildings[i][j].upgrade();
				}
				else if(buildings[i][j].getPoint() >= Building.CHANGE_POINT && buildings[i][j].getMouseListeners().length != 0){
					buildings[i][j].change();
				}
			}
		}	
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
				click(clicked);
			}
		}
	}
}
