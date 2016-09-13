package amai.org.conventions.model.conventions;

import amai.org.conventions.networking.ModelParser;
import amai.org.conventions.networking.SffModelParser;

public abstract class SffConvention extends Convention {
	@Override
	public ModelParser getModelParser() {
		return new SffModelParser();
	}
}
