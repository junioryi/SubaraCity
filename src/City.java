import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class City extends JFrame{
	public static final int FRAME_WIDTH = 600;
	public static final int FRAME_HEIGHT = 800;
	public static final int PANEL_WIDTH = 500;
	public static final int PANEL_HEIGHT = 100;
	public static final int PANEL_X = 50;
	public static final int PANEL_Y = 50;

	public JPanel panel;
	public JLabel pointLabel;
	public JLabel moveLabel;
	public Building[][] buildings = new Building[5][5];
	private int point;
	private int move;

	public City(){
		super();
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setTitle("SubaraCity");
		setLayout(null);
		setVisible(true);
		
		for(int col = 0; col < 5; col++){
			for(int row = 0 ; row < 5 ; row++){
				buildings[col][row] = new Building(col, row);
				buildings[col][row].addMouseListener(new ActionMerge(buildings[col][row]));
				add(buildings[col][row]);
			}
		}

		panel = new JPanel();
		panel.setLocation(PANEL_X, PANEL_Y);
        panel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        panel.setLayout(new GridLayout(1, 2));

        add(panel);

        updatePoint();
        pointLabel = new JLabel();
        pointLabel.setText("Point : " + point);
        pointLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        panel.add(pointLabel);
        
        move = 0;
        moveLabel = new JLabel();
        moveLabel.setText("Moves : " + move);
        moveLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        panel.add(moveLabel);

	}
	
	public void click(Building b){
		search(b);
		merge(b);
		fall();
		fill();
		updatePoint();
		move++;
	}
	//search for same-color building to be merged
	public void search(Building b){
		searchNear(b);

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
			if( color.equals( buildings[col][row-1].getColor() ) ){
				buildings[col][row-1].toBeMerged = true;
				searchNear(buildings[col][row-1]);
			}
		}
		//search down
		if( row != 4 && buildings[col][row+1].isSearched == false ){
			buildings[col][row+1].isSearched = true;  
			if( color.equals( buildings[col][row+1].getColor() ) ){
				buildings[col][row+1].toBeMerged = true;
				searchNear(buildings[col][row+1]);
			}
		}
		//search left
		if( col != 0 && buildings[col-1][row].isSearched == false ){
			buildings[col-1][row].isSearched = true; 
			if( color.equals( buildings[col-1][row].getColor() ) ){
				buildings[col-1][row].toBeMerged = true;
				searchNear(buildings[col-1][row]);
			}
		}
		//search right
		if( col != 4 && buildings[col+1][row].isSearched == false ){
			buildings[col+1][row].isSearched = true; 
			if( color.equals( buildings[col+1][row].getColor() ) ){
				buildings[col+1][row].toBeMerged = true;
				searchNear(buildings[col+1][row]);
			}
		}
	}	
	//merge same-color building
	public void merge(Building b){
		for(int i = 0; i < 5; i++){
			for(int j = 0 ; j < 5 ; j++){
				if(buildings[i][j].toBeMerged == true){
					b.addPoint(buildings[i][j]);
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
				tmp[rowTmp].resetRow(rowTmp);
				tmp[rowTmp].resetLocation();
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
				buildings[col][row].addMouseListener(new ActionMerge(buildings[col][row]));
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


	private class ActionMerge extends MouseAdapter{
		private Building b;
		public ActionMerge(Building b){
			super();
			this.b = b;
		}
		public void mouseClicked(MouseEvent e){
			click(b);
			repaint();
			pointLabel.setText("Point : " + point);
        	moveLabel.setText("Moves : " + move);
			panel.repaint();
			// try{
			// 	Thread.sleep(2000);
			// }
			// catch(InterruptedException ex){

			// }
		}
	}
}
