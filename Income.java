package processor;

import java.text.NumberFormat;

public class Income {
	private double value;

	public Income(int startValue) {
		value = startValue;
	}

	public void add(double value) {
		this.value += value;
	}

	public double getValue() {
		return value;
	}
	
	public String toString() {
		return NumberFormat.getCurrencyInstance().format(value);
	}
}
