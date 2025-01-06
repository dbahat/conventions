package amai.org.conventions;

import com.opencsv.CSVWriter;

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
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
 * 1. Update the source url (probably just the slug) and output file path (if necessary) at the end of the convert method
 * 2. Comment the @Ignore on the convert method and run it as a test
 */
public class EventsJsonToCsv {

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
				return html;
				// The library for html parsing doesn't work here. We don't really need it for now so it's ok.
//				try {
//					HTMLDocument doc = new HTMLDocument();
//					new HTMLEditorKit().read( new StringReader( "<html><body>" + html ), doc, 0 );
//					return doc.getText( 1, doc.getLength() ).trim();
//				} catch( Exception ex ) {
//					System.err.println("Error decoding html: " + html);
//					return html;
//				}
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

		// If you have a json file already downloaded, you can use this instead of the URL reader
//		FileReader eventsReader = new FileReader("D:\\conventions app\\convention resources\\icon 2024\\feedback\\all_events.json");
		HttpURLConnection request = HttpConnectionCreator.createConnection(new URL("https://api.sf-f.org.il/program/list_events.php?slug=icon2024"));
		request.connect();
		try (InputStreamReader eventsReader = new InputStreamReader((InputStream) request.getContent())) {
			logic.writeEventsToFile(
					logic.readEvents(eventsReader),
					"D:\\conventions app\\convention resources\\icon 2024\\feedback\\all_events.csv");
		}

	}
}
