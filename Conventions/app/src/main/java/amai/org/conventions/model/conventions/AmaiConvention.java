package amai.org.conventions.model.conventions;

import java.util.Calendar;

import amai.org.conventions.model.Halls;
import amai.org.conventions.networking.AmaiModelParser;
import amai.org.conventions.networking.ModelParser;

public abstract class AmaiConvention extends Convention {
	protected AmaiConvention(Halls halls) {
		super(halls);
	}

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
		return new AmaiModelParser();
	}
}
