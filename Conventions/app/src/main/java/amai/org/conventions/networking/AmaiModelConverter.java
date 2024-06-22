package amai.org.conventions.networking;

import android.graphics.Color;
import android.text.Html;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.Halls;
import amai.org.conventions.model.SpecialEventsProcessor;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;

public class AmaiModelConverter {
	public static final int NO_COLOR = Color.TRANSPARENT; // Assuming we will never get this from the server...

	private static final String TAG = AmaiModelConverter.class.getCanonicalName();

	private final Calendar conventionStartDate;
	private final Halls halls;
	private final SpecialEventsProcessor specialEventsProcessor;

	public AmaiModelConverter(Halls halls, Calendar conventionStartDate, SpecialEventsProcessor specialEventsProcessor) {
		this.conventionStartDate = conventionStartDate;
		this.halls = halls;
		this.specialEventsProcessor = specialEventsProcessor;
	}

	public List<ConventionEvent> convert(List<AmaiEventContract> eventContracts) {
		List<ConventionEvent> result = new LinkedList<>();

		for (AmaiEventContract eventContract : eventContracts) {
			int instanceIndex = 1;

			// In case the same event shows in multiple times, its contact will have multiple TimetableInfo objects.
			// During convection, treat each event instance as a separate ConventionEvent
			for (AmaiEventContract.TimetableInfoInstance eventInstance : eventContract.getTimetableInfo()) {
				if (eventInstance.isHidden()) {
					continue;
				}

				ConventionEvent event = new ConventionEvent()
						.withId(String.format(Dates.getLocale(), "%d_%d", eventContract.getId(), instanceIndex))
						.withServerId(eventContract.getId())
						.withBackgroundColor(NO_COLOR)
						.withTextColor(NO_COLOR)
						.withTitle(eventContract.getTitle())
						.withLecturer(eventInstance.getLecturer())
						.withType(new EventType(NO_COLOR, eventContract.getCategory()))
						.withStartTime(convertEventTime(eventInstance.getStart()))
						.withEndTime(convertEventTime(eventInstance.getEnd()))
						.withHall(convertHall(eventInstance.getRoom()))
						.withSubTitle(eventInstance.getSubtitle())
						.withImages(extractEventImageUrls(eventContract.getContent()))
						.withTags(eventContract.getTags());

				boolean stopProcessingEventDescription = specialEventsProcessor.processSpecialEvent(event);

				if (!stopProcessingEventDescription) {
					event = event.withDescription(convertEventDescription(eventContract.getContent()));
				}

				result.add(event);
				instanceIndex++;
			}
		}

		return result;
	}

	private Date convertEventTime(String eventTime) {
		try {
			Date hourAndMinute = new SimpleDateFormat("HH:mm:ss", Dates.getLocale()).parse(eventTime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(hourAndMinute);

			calendar.set(conventionStartDate.get(Calendar.YEAR),
					conventionStartDate.get(Calendar.MONTH),
					conventionStartDate.get(Calendar.DAY_OF_MONTH));

			return Dates.conventionToLocalTime(calendar.getTime());
		} catch (ParseException e) {
			return new Date();
		}
	}

	private Hall convertHall(String hallName) {
		Hall hall = halls.findByName(hallName);

		if (hall == null) {
			// Add a new hall to the convention
			hall = halls.add(hallName);
			Log.i(TAG, "Found and added new hall with name " + hallName);
		}

		return hall;
	}

	private String convertEventDescription(String rawEventDescription) {
		return rawEventDescription
				// Remove style, height and width attributes in tags since they make the element take
				// up more space than needed and are not supported anyway.
				// Note: class was also removed but we now need it to recognize google forms and it doesn't
				// seem to make a difference.
				.replaceAll("style=\"[^\"]*\"", "")
				.replaceAll("width=\"[^\"]*\"", "")
				.replaceAll("height=\"[^\"]*\"", "")
				// Remove scripts (multi-line and lazy)
				.replaceAll("(?s)<script>.*?</script>", "")
				// Replace divs and images with some other unsupported (and therefore ignored)
				.replace("<div", "<xdiv")
				.replace("/div>", "/xdiv>")
				// Remove tabs because they are not treated as whitespace and mess up the formatting
				.replace("\t", "    ")
				// Replace img tags and rename src attribute since we don't want images to appear in the description
				.replaceAll("src=(\"[^\"]*\")", "xsrc=$1")
				.replace("<img", "<ximg")
				.replace("/img>", "/ximg>");
	}

	private List<String> extractEventImageUrls(String rawEventDescription) {
		// Collect the images from the html. This must be done separately because we don't want to actually
		// include the images in the output.
		final List<String> eventImageUrls = new LinkedList<>();

		Html.fromHtml(rawEventDescription, source -> {
            eventImageUrls.add(source);
            return null;
        }, null);

		return eventImageUrls;
	}
}
