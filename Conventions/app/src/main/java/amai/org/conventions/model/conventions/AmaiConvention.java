package amai.org.conventions.model.conventions;

import java.util.Calendar;

import amai.org.conventions.networking.AmaiModelParser;
import amai.org.conventions.networking.ModelParser;

public abstract class AmaiConvention extends Convention {
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
}
