
public class Building {
	private int point;
	private int level;
	private Color color;
	private ItemOnField item;
	
	public Building(int point,Color color){
		this.point=point;
		this.item=ItemOnField.people;
		this.color=color;
	}	
	public Building(){
		this(1,Color.green);		
	}


	public int getLevel() {
		return level;
	}

	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
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
	public void combinebuilding(Building[] buildings){
		int p=this.point;
		for (Building b:buildings){
			p+=b.getPoint();
		}
		this.setPoint(p);
	}	
}
