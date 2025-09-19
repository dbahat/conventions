package amai.org.conventions;

import com.opencsv.CSVWriter;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.networking.ParseUtils;
import amai.org.conventions.networking.SffModelParser;
import amai.org.conventions.utils.HttpConnectionCreator;

/**
 * This is not really a test class. It's a class used to easily convert between
 * the events json and a csv, so we can enrich the feedback answers with event data.
 * To run it:
 * 1. Update the constants at the beginning of the class (probably just the slug and convention folder)
 * 2. Comment the @Ignore on the convert method and run it as a test
 */
public class EventsJsonToCsv {
	private static final String SLUG = "icon2025";
	private static final String CONVENTION_FOLDER = "icon 2025";

	// If you have a json file already downloaded, uncomment its path below
//	private static final String INPUT_FILE = "D:\\conventions app\\convention resources\\" + CONVENTION_FOLDER + "\\feedback\\all_events.json";
	private static final String INPUT_FILE = null;
	private static final String INPUT_URL = "https://api.sf-f.org.il/program/list_events.php?slug=" + SLUG;
	private static final String OUTPUT_PATH = "D:\\conventions app\\convention resources\\" + CONVENTION_FOLDER + "\\feedback\\all_events2.csv";

	private List<ConventionEvent> readEvents(InputStreamReader reader) {
		return new SffModelParser().parse(new Date(), reader);
	}
	
	private String join(List<String> strings, String separator) {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (String string : strings) {
			if (!first) {
				result.append(separator);
			} else {
				first = false;
			}
			result.append(string);
		}
		return result.toString();
	}
	
	private void writeEventsToFile(List<ConventionEvent> events, String filePath) throws Exception {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(filePath), "UTF-8"
				));
			// BOM is necessary for opening the file correctly in Excel
			out.write('\ufeff');
			
			CSVWriter writer = new CSVWriter(out);
			
			// Write headers
			String[] line = new String[]{
					"מספר אירוע",
					"שם האירוע",
					"אולם",
					"תאריך",
					"שעת התחלה",
					"שעת סיום",
					"אורך בשעות",
					"מנחים",
					"קטגוריה",
					"מחיר",
					"כרטיסים שנותרו",
					"תגיות",
			};
			writer.writeNext(line);
			
			// Write lines
			SimpleDateFormat eventDateFormatter = new SimpleDateFormat("dd.MM.yyyy");
			SimpleDateFormat eventTimeFormatter = new SimpleDateFormat("HH:mm");
			for (ConventionEvent event : events) {
				line = new String[]{
						event.getId(),
						event.getTitle(),
						event.getHall().getName(),
						eventDateFormatter.format(event.getStartTime()),
						eventTimeFormatter.format(event.getStartTime()),
						eventTimeFormatter.format(event.getEndTime()),
						timeDiffInHours(event.getEndTime(), event.getStartTime()),
						event.getLecturer(),
						event.getCategory(),
						event.getPrice() < 0 ? "" : String.valueOf(event.getPrice()),
						event.getTicketsLimit() < 0 || event.getAvailableTickets() < 0 ? "" : String.valueOf(event.getAvailableTickets()),
						join(event.getTags(), ", "),
				};
				writer.writeNext(line);
			}
			
			writer.close();
	}
	
	private String timeDiffInHours(Date end, Date start) {
		return String.valueOf((end.getTime() - start.getTime()) / 1000 / 60 / 60);
	}

	@Test
	@Ignore("See explanation at the class doc")
	public void convert() throws Exception {
		EventsJsonToCsv logic = new EventsJsonToCsv();

		// Setup
		// Need to mock the parser so it doesn't return null
		ParseUtils.setHtmlParser(new ParseUtils.HTMLParser() {
			@Override
			public String parse(String html) {
				try {
					// The android library for parsing doesn't work here, but this should cover most cases
					return StringEscapeUtils.unescapeHtml4(html);
				} catch( Exception ex ) {
					System.err.println("Error decoding html: " + html + "\n" + ex.getMessage());
					return html;
				}
			}
		});
		ParseUtils.setTextUtils(new ParseUtils.TextUtils() {
			@Override
			public String join(String delimiter, List<String> strings) {
				return EventsJsonToCsv.this.join(strings, delimiter);
			}

			@Override
			public boolean isEmpty(String string) {
				return string == null || string.isEmpty();
			}
		});

		// Use dummy convention (can't use the context to init the real convention). The events will be fine anyway.
		TestConvention convention = new TestConvention();
		convention.initFields();
		Convention.setConvention(convention);

		try (InputStreamReader eventsReader = getEventsReader()) {
			logic.writeEventsToFile(
					logic.readEvents(eventsReader),
					OUTPUT_PATH);
		}

	}

	private InputStreamReader getEventsReader() throws Exception {
		if (INPUT_FILE != null) {
			return new FileReader(INPUT_FILE);
		}
		HttpURLConnection request = HttpConnectionCreator.createConnection(new URL(INPUT_URL));
		request.connect();
		return new InputStreamReader((InputStream) request.getContent());
	}
}
