package amai.org.conventions.networking;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.Halls;

public class AmaiModelParser implements ModelParser {
	private Halls halls;
	private Calendar conventionStartDate;

	public AmaiModelParser(Halls halls, Calendar conventionStartDate) {
		this.halls = halls;
		this.conventionStartDate = conventionStartDate;
	}

	@Override
	public List<ConventionEvent> parse(InputStreamReader reader) {

		Gson gson = new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
				.create();

		Type typeOfT = new TypeToken<List<AmaiEventContract>>(){}.getType();
		List<AmaiEventContract> eventsContract = gson.fromJson(reader, typeOfT);

		return new AmaiModelConverter(
				halls,
				conventionStartDate
		).convert(eventsContract);
	}
}
