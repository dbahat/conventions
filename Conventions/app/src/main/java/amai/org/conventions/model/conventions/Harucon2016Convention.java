package amai.org.conventions.model.conventions;

import java.util.Calendar;

import amai.org.conventions.R;
import amai.org.conventions.utils.ConventionStorage;

public class Harucon2016Convention extends Cami2015Convention {
	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.convention_events);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2016, Calendar.MARCH, 24);
		return date;
	}

	@Override
	protected String initID() {
		return "Harucon2016";
	}

	@Override
	protected String initDisplayName() {
		return "הארוקון 2016";
	}

	@Override
	protected String initFacebookFeedPath() {
		return super.initFacebookFeedPath();
	}

	@Override
	protected double initLongitude() {
		return 35.202425;
	}

	@Override
	protected double initLatitude() {
		return 31.786372;
	}
}
