package amai.org.conventions.model.conventions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.model.Stand;
import amai.org.conventions.model.StandLocation;
import amai.org.conventions.model.StandType;
import amai.org.conventions.model.StandTypes;
import amai.org.conventions.model.StandsArea;
import amai.org.conventions.networking.AmaiModelParser;
import amai.org.conventions.networking.ModelParser;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Log;
import androidx.annotation.VisibleForTesting;

public abstract class AmaiConvention extends Convention {
	private static final String TAG = AmaiConvention.class.getCanonicalName();

	@Override
	protected Calendar initStartDate() {
		return initDate();
	}

	@Override
	protected Calendar initEndDate() {
		return initDate();
	}

	protected abstract Calendar initDate();

	@Override
	public ModelParser getModelParser() {
		// Using the original start date since we don't want to take into account the old events list
		// when calculating the date of the new events list
		return new AmaiModelParser(getHalls(), this.startDate, getSpecialEventsProcessor());
	}

	@Override
	public String getGoogleSpreadsheetsApiKey() {
		return "AIzaSyAKJYwC7UeHyBpcVqvXABRxhEQmLiK2TRo";
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public List<StandType> convertStandTypes(String names) {
		String[] nameArray = names.split(", ");
		List<StandType> standTypes = new ArrayList<>();
		for (String name : nameArray) {
			if (name.trim().isEmpty()) {
				// Default stand type - general
				name = getGeneralStandType();
			}
			StandType standType = getStandTypes().findByName(name);

			if (standType == null) {
				// Add a new hall to the convention
				standType = getStandTypes().add(name);
				Log.i(TAG, "Found and added new stand type with name " + name);
			}

			standTypes.add(standType);
		}

		return standTypes;
	}

	protected abstract String getGeneralStandType();

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public List<String> convertLocationIds(String ids) {
		return CollectionUtils.map(Arrays.asList(ids.split(",")), String::toLowerCase);
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public abstract StandsArea convertStandsArea(String locationIds);
}
