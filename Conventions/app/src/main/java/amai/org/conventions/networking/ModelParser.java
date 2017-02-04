package amai.org.conventions.networking;

import java.io.InputStreamReader;
import java.util.List;

import amai.org.conventions.model.ConventionEvent;

public interface ModelParser {
	List<ConventionEvent> parse(InputStreamReader reader);
}
