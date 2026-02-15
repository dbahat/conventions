package amai.org.conventions.model;

import android.graphics.BlendMode;

import java.util.LinkedList;
import java.util.List;

import sff.org.conventions.R;

public class StandLocationsBuilder {
	private List<StandLocation> locations = new LinkedList<>();
	float defaultWidth = 0f;
	float defaultHeight = 0f;
	float defaultWidthSpace = 0f;
	float defaultHeightSpace = 0f;
	float defaultRotation = 0f;
	int defaultHighlightColorResource = R.color.transparent;
	BlendMode defaultHighlightBlendMode;

	/**
	 * Must be called before the other methods to take effect
	 */
	public StandLocationsBuilder setDefaults(float defaultWidth, float defaultHeight, float defaultRotation, float defaultWidthSpace, float defaultHeightSpace, int defaultHighlightColorResource, BlendMode defaultHighlightBlendMode) {
		this.defaultWidth = defaultWidth;
		this.defaultHeight = defaultHeight;
		this.defaultWidthSpace = defaultWidthSpace;
		this.defaultHeightSpace = defaultHeightSpace;
		this.defaultRotation = defaultRotation;
		this.defaultHighlightColorResource = defaultHighlightColorResource;
		this.defaultHighlightBlendMode = defaultHighlightBlendMode;
		return this;
	}

	private StandLocationsBuilder single(float left, float top, String letter, int number, String next) {
		return single(left, top, defaultWidth, defaultHeight, defaultRotation, letter, number, next);
	}

	private StandLocationsBuilder single(float left, float top, float width, float height, float rotation, String letter, int number, String next) {
		String name = letter + number;
		String sort = name;
		if (number < 10) {
			sort = letter + "00" + number;
		} else if (number < 100) {
			sort = letter + "0" + number;
		}
		StandLocation location = StandLocation.fromWidths(name, sort, next, left, width, top, height, rotation);
		location.setHighlightColorResource(defaultHighlightColorResource);
		location.setHighlightBlendMode(defaultHighlightBlendMode);
		locations.add(location);
		return this;
	}

	private interface DoForConsecutive {
		void call(int index, int currentNumber, String nextName);
	}

	private void consecutive(String letter, int firstNumber, int lastNumber, String next, DoForConsecutive action) {
		if (firstNumber <= lastNumber) {
			int index = 0;
			for (int currNumber = firstNumber; currNumber <= lastNumber; ++currNumber) {
				String nextName = (currNumber == lastNumber) ? next : letter + (currNumber + 1);
				action.call(index, currNumber, nextName);
				++index;
			}
		} else {
			int index = 0;
			for (int currNumber = firstNumber; currNumber >= lastNumber; --currNumber) {
				String nextName = (currNumber == firstNumber) ? next : letter + (currNumber + 1);
				action.call(index, currNumber, nextName);
				++index;
			}
		}
	}

	public StandLocationsBuilder diagonalFromTopLeft(float firstLocationLeft, float firstLocationTop, String letter, int firstNumber, int lastNumber, String next) {
		return diagonalFromTopLeft(firstLocationLeft, firstLocationTop, defaultWidth, defaultHeight, defaultRotation, defaultWidthSpace, defaultHeightSpace, letter, firstNumber, lastNumber, next);
	}

	// widthSpace heightSpace are between the left/top corners of both locations
	public StandLocationsBuilder diagonalFromTopLeft(float firstLocationLeft, float firstLocationTop, float width, float height, float rotation, float widthSpace, float heightSpace, String letter, int firstNumber, int lastNumber, String next) {
		consecutive(letter, firstNumber, lastNumber, next, (index, currentNumber, nextName) -> {
			single(firstLocationLeft + index * widthSpace, firstLocationTop + index * heightSpace, width, height, rotation, letter, currentNumber, nextName);
		});
		return this;
	}

	public StandLocationsBuilder diagonalFromBottomLeft(float firstLocationLeft, float firstLocationTop, String letter, int firstNumber, int lastNumber, String next) {
		return diagonalFromBottomLeft(firstLocationLeft, firstLocationTop, defaultWidth, defaultHeight, defaultRotation, defaultWidthSpace, defaultHeightSpace, letter, firstNumber, lastNumber, next);
	}

	public StandLocationsBuilder diagonalFromBottomLeft(float firstLocationLeft, float firstLocationTop, float width, float height, float rotation, float widthSpace, float heightSpace, String letter, int firstNumber, int lastNumber, String next) {
		consecutive(letter, firstNumber, lastNumber, next, (index, currentNumber, nextName) -> {
			single(firstLocationLeft + index * widthSpace, firstLocationTop - index * heightSpace, width, height, rotation, letter, currentNumber, nextName);
		});
		return this;
	}

	public StandLocationsBuilder leftToRight(float firstLocationLeft, float firstLocationTop, String letter, int firstNumber, int lastNumber, String next) {
		return leftToRight(firstLocationLeft, firstLocationTop, defaultWidth, defaultHeight, defaultRotation, defaultWidthSpace, letter, firstNumber, lastNumber, next);
	}

	// space is between the right corner of one location and the left corner of the next location
	public StandLocationsBuilder leftToRight(float firstLocationLeft, float firstLocationTop, float width, float height, float rotation, float space, String letter, int firstNumber, int lastNumber, String next) {
		consecutive(letter, firstNumber, lastNumber, next, (index, currentNumber, nextName) -> {
			single(firstLocationLeft + index * (width + space), firstLocationTop, width, height, rotation, letter, currentNumber, nextName);
		});
		return this;
	}

	public StandLocationsBuilder topToBottom(float firstLocationLeft, float firstLocationTop, String letter, int firstNumber, int lastNumber, String next) {
		return topToBottom(firstLocationLeft, firstLocationTop, defaultWidth, defaultHeight, defaultRotation, defaultHeightSpace, letter, firstNumber, lastNumber, next);
	}

	public StandLocationsBuilder topToBottom(float firstLocationLeft, float firstLocationTop, float rotation, String letter, int firstNumber, int lastNumber, String next) {
		return topToBottom(firstLocationLeft, firstLocationTop, defaultWidth, defaultHeight, rotation, defaultHeightSpace, letter, firstNumber, lastNumber, next);
	}

	// space is between the bottom corner of one location and the top corner of the next location
	public StandLocationsBuilder topToBottom(float firstLocationLeft, float firstLocationTop, float width, float height, float rotation, float space, String letter, int firstNumber, int lastNumber, String next) {
		consecutive(letter, firstNumber, lastNumber, next, (index, currentNumber, nextName) -> {
			single(firstLocationLeft, firstLocationTop + index * (height + space), width, height, rotation, letter, currentNumber, nextName);
		});
		return this;
	}

	public StandLocations build() {
		StandLocation[] locationsArray = locations.toArray(new StandLocation[0]);
		return new StandLocations(locationsArray);
	}
}
