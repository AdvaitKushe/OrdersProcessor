package processor;

import java.util.TreeMap;

public class AllOrder  { //Keep track of client order
private int id;

private TreeMap<String, Integer> items;
	
	public AllOrder(int id) {
		this.id= id;
		items= new TreeMap<>();
	}
	 
	public int getId() {
		return id;
	}
	public TreeMap<String, Integer> getItems(){
		return items;
	}
	
	public void addItem(String Name) {
	
		if(items.containsKey(Name)) {
			int total= items.get(Name).intValue();
			total++;
			items.put(Name, total);
		}else {
			items.put(Name, 1);
		}	
		
	}


}
