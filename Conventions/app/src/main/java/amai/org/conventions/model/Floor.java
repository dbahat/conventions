package amai.org.conventions.model;

public class Floor {
	private int number;
	private String name;
	private int imageResource;

	public Floor(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Floor withName(String name) {
		setName(name);
		return this;
	}

	public int getImageResource() {
		return imageResource;
	}

	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}

	public Floor withImageResource(int imageResource) {
		setImageResource(imageResource);
		return this;
	}
}
