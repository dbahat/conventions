package amai.org.conventions.events;

import amai.org.conventions.model.conventions.Convention;

public class SearchCategory {
	private String name;
	private int color;
	private int count;

	public SearchCategory(String name, int color) {
		this.name = name;
		this.color = color;
		this.count = 1;
	}

	public String getName() {
		return name;
	}

	public int getColor() {
		return color;
	}

	public boolean hasColor() {
		return color != Convention.NO_COLOR;
	}

	public void increase() {
		count++;
	}

	public int getCount() {
		return count;
	}
}
