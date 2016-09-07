package amai.org.conventions.model.conventions;

import java.util.Calendar;

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
}
