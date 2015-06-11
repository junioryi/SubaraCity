
public enum ItemOnField {
	people(1),tree(3),house(5),building(10);
	private int value = 0;

    private ItemOnField(int value) {    
        this.value = value;
    }
    public static ItemOnField valueOf(int value) {    
        switch (value) {
        case 1:
            return people;
        case 3:
            return tree;
        case 5:
        	return house;
        case 10:
        	return building;
        default:
            return null;
        }
    }
    public int value() {
        return this.value;
    }
}
