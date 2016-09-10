package amai.org.conventions.model;

import sff.org.conventions.R;

public class Stand {
	private String name;
	private StandType type;
	private StandsArea standsArea;
	private String locationName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Stand withName(String name) {
		setName(name);
		return this;
	}

	public StandType getType() {
		return type;
	}

	public void setType(StandType type) {
		this.type = type;
	}

	public Stand withType(StandType type) {
		setType(type);
		return this;
	}

	public StandsArea getStandsArea() {
		return standsArea;
	}

	public void setStandsArea(StandsArea standsArea) {
		this.standsArea = standsArea;
	}

	public Stand withStandsArea(StandsArea standsArea) {
		setStandsArea(standsArea);
		return this;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public Stand withLocationName(String locationName) {
		setLocationName(locationName);
		return this;
	}

	public enum StandType {
		COMMERCIAL(R.string.commercial_stand, R.drawable.ic_shopping_basket),
		INDEPENDENT(R.string.independent_stand, R.drawable.ic_color_lens),
		DONATION(R.string.donation_stand, R.drawable.ic_monetization),
		OTHER(R.string.other_stand, R.drawable.book);

		private int title;
		private int image;

		StandType(int title, int image) {
			this.title = title;
			this.image = image;
		}

		public int getTitle() {
			return title;
		}

		public int getImage() {
			return image;
		}
	}
}
