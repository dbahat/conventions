package amai.org.conventions;

import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;

import org.junit.Ignore;
import org.junit.Test;

import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.model.Stand;
import amai.org.conventions.model.conventions.AmaiConvention;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.ConventionStorage;

/**
 * This is not really a test class. It's a class used to easily convert between
 * the stands csv and json, so we can add the initial stands list to the app.
 * To run it:
 * 1. Update the constants at the beginning of the class (probably just the convention ID and folder)
 * 2. Comment the @Ignore on the convert method and run it as a test
 */
// This class is used to convert the stands spreadsheet (downloaded as csv) to a JSON file we can read
// the stands list from
public class StandsCSVToJson {
	private static final String INPUT_FILE = "D:\\conventions app\\convention resources\\animatsuri 2026\\stands\\stands-1.csv";
	private static final String OUTPUT_FILE = "D:\\GitHub\\conventions\\Conventions\\app\\src\\main\\res\\raw\\animatsuri2026_stands.json";

	@Test
	@Ignore("See explanation at the class doc")
	public void convert() throws Exception {
		List<Stand> stands = new LinkedList<>();
		if (!(Convention.getInstance() instanceof AmaiConvention)) {
			throw new Exception("Only Amai conventions are supported");
		}
		AmaiConvention convention = (AmaiConvention) Convention.getInstance();
		convention.initFields();

		// Read input file as csv
		try (CSVReader reader = new CSVReader(new FileReader(INPUT_FILE))) {
			// The first line is the titles
			String[] titlesLine = reader.readNext();

			// Validate the titles are as expected, otherwise the order or format might have changed and we should update the logic
			if (titlesLine.length != 6 ||
				!titlesLine[0].contains("שם דוכן") ||
				!titlesLine[1].contains("תיאור הדוכן") ||
				// [2] is the logo
				!titlesLine[3].contains("כתובת אתר הדוכן") ||
				!titlesLine[4].contains("סוג הדוכן") ||
				!titlesLine[5].contains("מיקום הדוכן")) {
				System.out.println("Unexpected titles line with length: " + titlesLine.length);
				System.out.println("Content: " + String.join("\n", titlesLine));
				throw new IllegalArgumentException("Unexpected titles line, update the expected titles and make sure the rest of the logic is correct");
			}

			// Each line is an event (except the titles line)
			String[] line;
			int lineNumber = 0;
			while ((line = reader.readNext()) != null) {
				++lineNumber;
				if (line.length != 6) {
					System.out.println("Unexpected line length in line " + lineNumber + ": " + line.length);
					System.out.println("Content: " + String.join("\n", line));
					continue;
				}

				String name = line[0];
				String description = line[1];
				String website = line[3];
				String type = line[4];
				String location = line[5];

				stands.add(
					new Stand().
						withName(name).
						withDescription(description).
						withWebsite(website).
						withTypes(convention.convertStandTypes(type)).
						withLocationIds(convention.convertLocationIds(location)).
						withStandsArea(convention.convertStandsArea(location))
				);
			}

			// Write to JSON output file
			String result = ConventionStorage.createGsonSerializer().toJson(stands);
			OutputStream outputStream = Files.newOutputStream(Path.of(OUTPUT_FILE));
			OutputStreamWriter writer = new OutputStreamWriter(outputStream);
			writer.write(result);
			writer.close();
			outputStream.close();
		}
	}
}
