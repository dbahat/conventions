package amai.org.conventions.networking;

import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.Stand;

public interface StandsParser {
	List<Stand> parse(InputStreamReader reader);
}
