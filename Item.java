package processor;

public class Item implements Comparable<Item> {

	private String itemName;
	// private int inventory;
	private double cost;

	public Item(String itemName, double cost) {
		this.itemName = itemName;
		this.cost = cost;
	}

	public String getItemName() {
		return itemName;
	}

	public double getCost() {
		return cost;
	}

	public String toString() {
		return itemName + "(" + cost + ")";
	}

	public boolean equals(Object o) {
		if (this.equals(o)) {
			return true;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		
		Item copy = (Item)o;
		
		return copy.itemName.equals(itemName);
	}

	@Override
	public int compareTo(Item o) {
		return this.getItemName().compareTo(o.getItemName());
		
	}

}
