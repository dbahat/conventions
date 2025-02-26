package amai.org.conventions.model;

import java.util.LinkedList;
import java.util.List;

public class StandLocationsBuilder {
	private List<StandLocation> locations = new LinkedList<>();
	float defaultWidth = 0f;
	float defaultHeight = 0f;
	float defaultWidthSpace = 0f;
	float defaultHeightSpace = 0f;

	/**
	 * Must be called before the other methods to take effect
	 */
	public StandLocationsBuilder setDefaults(float defaultWidth, float defaultHeight, float defaultWidthSpace, float defaultHeightSpace) {
		this.defaultWidth = defaultWidth;
		this.defaultHeight = defaultHeight;
		this.defaultWidthSpace = defaultWidthSpace;
		this.defaultHeightSpace = defaultHeightSpace;
		return this;
	}

	private StandLocationsBuilder single(float left, float top, String letter, int number, String next) {
		return single(left, top, defaultWidth, defaultHeight, letter, number, next);
	}

	private StandLocationsBuilder single(float left, float top, float width, float height, String letter, int number, String next) {
		String name = letter + number;
		String sort = (number < 10) ? letter + "0" + number : name;
		locations.add(StandLocation.fromWidths(name, sort, next, left, width, top, height));
		return this;
	}

	public StandLocationsBuilder leftToRight(float firstLocationLeft, float firstLocationTop, String letter, int firstNumber, int lastNumber, String next) {
		return leftToRight(firstLocationLeft, firstLocationTop, defaultWidth, defaultHeight, defaultWidthSpace, letter, firstNumber, lastNumber, next);
	}

	public StandLocationsBuilder leftToRight(float firstLocationLeft, float firstLocationTop, float width, float height, float space, String letter, int firstNumber, int lastNumber, String next) {
		if (firstNumber <= lastNumber) {
			int index = 0;
			for (int currNumber = firstNumber; currNumber <= lastNumber; ++currNumber) {
				String nextName = (currNumber == lastNumber) ? next : letter + (currNumber + 1);
				single(firstLocationLeft + index * (width + space), firstLocationTop, width, height, letter, currNumber, nextName);
				++index;
			}
		} else {
			int index = 0;
			for (int currNumber = firstNumber; currNumber >= lastNumber; --currNumber) {
				String nextName = (currNumber == firstNumber) ? next : letter + (currNumber + 1);
				single(firstLocationLeft + index * (width + space), firstLocationTop, width, height, letter, currNumber, nextName);
				++index;
			}
		}
		return this;
	}

	public StandLocationsBuilder topToBottom(float firstLocationLeft, float firstLocationTop, String letter, int firstNumber, int lastNumber, String next) {
		return topToBottom(firstLocationLeft, firstLocationTop, defaultWidth, defaultHeight, defaultHeightSpace, letter, firstNumber, lastNumber, next);
	}

	public StandLocationsBuilder topToBottom(float firstLocationLeft, float firstLocationTop, float width, float height, float space, String letter, int firstNumber, int lastNumber, String next) {
		if (firstNumber <= lastNumber) {
			int index = 0;
			for (int currNumber = firstNumber; currNumber <= lastNumber; ++currNumber) {
				String nextName = (currNumber == lastNumber) ? next : letter + (currNumber + 1);
				single(firstLocationLeft, firstLocationTop + index * (height + space), width, height, letter, currNumber, nextName);
				++index;
			}
		} else {
			int index = 0;
			for (int currNumber = firstNumber; currNumber >= lastNumber; --currNumber) {
				String nextName = (currNumber == firstNumber) ? next : letter + (currNumber + 1);
				single(firstLocationLeft, firstLocationTop + index * (height + space), width, height, letter, currNumber, nextName);
				++index;
			}
		}
		return this;
	}

	public StandLocations build() {
		StandLocation[] locationsArray = locations.toArray(new StandLocation[0]);
		return new StandLocations(locationsArray);
	}
}
