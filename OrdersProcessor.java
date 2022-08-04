package processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

public class OrdersProcessor implements Runnable {
	private AllOrder order;

	private Income totalIncome;
	private TreeSet<Item> itemList;
	private TreeMap<String, Integer> totalSold;

	private TreeMap<Integer, String> sortedReport;

	public OrdersProcessor(AllOrder order, Income totalIncome, TreeSet<Item> itemList,
			TreeMap<String, Integer> totalSold, TreeMap<Integer, String> sortedReport) {
		this.order = order;
		this.totalIncome = totalIncome;
		this.itemList = itemList;
		this.totalSold = totalSold;

		this.sortedReport = sortedReport;
	}

	public void run() {
		String report = "----- Order details for client with Id: " + order.getId() + " -----\n";

		double totalOrder = 0;
		for (String itemName : order.getItems().keySet()) {
			for (Item item : itemList) {
				if (item.getItemName().equals(itemName)) {
					String description = item.getItemName();
					double cost = item.getCost();
					int quantity = order.getItems().get(itemName);
					report += "Item's name: " + description + ", Cost per item: "
							+ NumberFormat.getCurrencyInstance().format(cost) + ", Quantity: " + quantity + ", Cost: "
							+ NumberFormat.getCurrencyInstance().format(cost * quantity) + "\n";
					totalOrder += (cost * quantity);
					synchronized (totalSold) {
						int total = totalSold.get(description) + quantity;

						totalSold.put(description, total);
					}
				}
			}

		}

		report += "Order Total: " + NumberFormat.getCurrencyInstance().format(totalOrder) + "\n";
		synchronized (totalIncome) {
			totalIncome.add(totalOrder);
		}
		synchronized (sortedReport) {
			sortedReport.put(order.getId(), report);
		}

	

	}

	public static TreeSet<Item> readInventory(String fileName) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(fileName));
		TreeSet<Item> itemList = new TreeSet<>();
		while (scanner.hasNext()) {
			String itemName = scanner.next();
			double price = scanner.nextDouble();
			Item item = new Item(itemName, price);
			itemList.add(item);

		}
		scanner.close();

		return itemList;
	}

	public static ArrayList<AllOrder> readOrder(String fileName, ArrayList<AllOrder> allOrders)
			throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(fileName));
		scanner.next();
		int id = scanner.nextInt();
		AllOrder order = new AllOrder(id);
		while (scanner.hasNext()) {
			String item = scanner.next();
			order.addItem(item);
			scanner.next();
		}

		allOrders.add(order);
		return allOrders;

	}

	public static void main(String[] args) throws FileNotFoundException, InterruptedException, IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter item's data file name:  ");
		String dataFile = scanner.next();

		System.out.println("Enter 'y' for multiple threads, any other character otherwise: ");
		String multiThread = scanner.next();

		System.out.println("Enter number of orders to process: ");
		int orderNum = scanner.nextInt();

		System.out.println("Enter order's base filename: ");
		String base = scanner.next();
		System.out.println("Enter result's filename: ");
		String resultFile = scanner.next();
		scanner.close();
		long startTime = System.currentTimeMillis();
		FileWriter fileWriter = new FileWriter(resultFile, false);

		TreeSet<Item> allItem = readInventory(dataFile);
		ArrayList<AllOrder> allOrders = new ArrayList<>();
		TreeMap<String, Integer> totalSold = new TreeMap<>();
		TreeMap<Integer, String> sortedReport = new TreeMap<>();
		for (int i = 1; i <= orderNum; i++) {
			allOrders = readOrder(base + i + ".txt", allOrders);
		}
		for (Item item : allItem) {
			totalSold.put(item.getItemName(), 0);
		}
		Income totalIncome = new Income(0);

		if (multiThread.equals("y")) {

			/* Creating Threads */
			ArrayList<Thread> allThreads = new ArrayList<Thread>();

			for (AllOrder order : allOrders) {
				allThreads.add(new Thread(new OrdersProcessor(order, totalIncome, allItem, totalSold, sortedReport)));

			}

			/* Starting Threads */
			for (Thread thread : allThreads) {
				thread.start();
			}

			/* Joining (Waiting for threads to finish) */
			for (Thread thread : allThreads) {
				thread.join();
			}

			for (Integer ID : sortedReport.keySet()) {
				System.out.println("Reading order for client with id: " + ID);
				fileWriter.write(sortedReport.get(ID));
			}

		} else {
			for (AllOrder order : allOrders) {
				System.out.println("Reading order for client with id: " + order.getId());
				String report = "----- Order details for client with Id: " + order.getId() + " -----\n";

				double totalOrder = 0;
				for (String itemName : order.getItems().keySet()) {
					for (Item item : allItem) {
						if (item.getItemName().equals(itemName)) {
							String description = item.getItemName();
							double cost = item.getCost();
							int quantity = order.getItems().get(itemName);
							report += "Item's name: " + description + ", Cost per item: "
									+ NumberFormat.getCurrencyInstance().format(cost) + ", Quantity: " + quantity
									+ ", Cost: " + NumberFormat.getCurrencyInstance().format(cost * quantity) + "\n";
							totalOrder += (cost * quantity);
							int total = totalSold.get(description) + quantity;

							totalSold.put(description, total);

						}
					}

				}

				report += "Order Total: " + NumberFormat.getCurrencyInstance().format(totalOrder) + "\n";

				totalIncome.add(totalOrder);

				
				fileWriter.write(report);

			}
			for (Integer ID : sortedReport.keySet()) {
				System.out.println("Reading order for client with id: " + ID);
				fileWriter.write(sortedReport.get(ID));
			}

		}
		String summary = "***** Summary of all orders *****\n";

		for (Item item : allItem) {

			summary += "Summary - Item's name: " + item.getItemName() + ", Cost per item: "
					+ NumberFormat.getCurrencyInstance().format(item.getCost()) + ", Number sold: "
					+ totalSold.get(item.getItemName()) + ", Item's Total: "
					+ NumberFormat.getCurrencyInstance().format(totalSold.get(item.getItemName()) * item.getCost())
					+ "\n";

		}

		
		fileWriter.write(summary);
		fileWriter.write("Summary Grand Total: " + totalIncome.toString() + "\n");
		
		fileWriter.flush();
		
	
		scanner.close();
		fileWriter.close();
		
		/* TASK YOU WANT TO TIME */
		long endTime = System.currentTimeMillis();
		System.out.println("Processing time (msec): " + (endTime - startTime));
		System.out.println("Results can be found in the file: "+resultFile);
	}

}