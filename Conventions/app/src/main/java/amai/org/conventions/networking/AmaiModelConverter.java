package amai.org.conventions.networking;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.Halls;
import amai.org.conventions.model.SpecialEventsProcessor;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;

public class AmaiModelConverter {
	public static final int NO_COLOR = Color.TRANSPARENT; // Assuming we will never get this from the server...

	private static final String TAG = AmaiModelConverter.class.getCanonicalName();

	private Calendar conventionStartDate;
	private Halls halls;
	private SpecialEventsProcessor specialEventsProcessor;

	public AmaiModelConverter(Halls halls, Calendar conventionStartDate, SpecialEventsProcessor specialEventsProcessor) {
		this.conventionStartDate = conventionStartDate;
		this.halls = halls;
		this.specialEventsProcessor = specialEventsProcessor;
	}

	public List<ConventionEvent> convert(List<AmaiEventContract> eventContracts) {
		List<ConventionEvent> result = new LinkedList<>();

		// "special content" events are events with a dedicated description page, which is usually placed in a different event.
		// For example: A guest of honor is holding 3 events - Fan meeting, Q&A, and signing event.
		// For all 3 events above, both the site and the app should show the same description (explaining about the guest of honor), and therefore
		// they will all point to a common event holding that description.
		Map<ConventionEvent, Integer> eventsWithSpecialContent = new LinkedHashMap<>();

		for (AmaiEventContract eventContract : eventContracts) {
			int instanceIndex = 1;

			// In case the same event shows in multiple times, its contact will have multiple TimetableInfo objects.
			// During convection, treat each event instance as a separate ConventionEvent
			for (AmaiEventContract.TimetableInfoInstance eventInstance : eventContract.getTimetableInfo()) {
				if ("hidden".equals(eventInstance.getTooltip())) {
					continue;
				}

				int bgColor = convertColor(eventContract.getTimetableBg());

				ConventionEvent event = new ConventionEvent()
						.withId(String.format(Dates.getLocale(), "%d_%d", eventContract.getId(), instanceIndex))
						.withServerId(eventContract.getId())
						.withBackgroundColor(bgColor)
						.withTextColor(convertColor(eventContract.getTimetableTextColor()))
						.withTitle(eventContract.getTitle())
						.withLecturer(eventInstance.getBeforeHourText())
						.withType(new EventType(bgColor, eventContract.getCategoriesText().getName()))
						.withStartTime(convertEventTime(eventInstance.getStart()))
						.withEndTime(convertEventTime(eventInstance.getEnd()))
						.withHall(convertHall(eventInstance.getRoom()))
						.withImages(extractEventImageUrls(eventContract.getContent()));

				boolean stopProcessingEventDescription = specialEventsProcessor.processSpecialEvent(event);

				if (!stopProcessingEventDescription) {
					// See above of explanation about events with special content.
					boolean isEventWithSpecialContent = eventContract.getTimetableUrlPid() > 0;
					if (isEventWithSpecialContent) {
						eventsWithSpecialContent.put(event, eventContract.getTimetableUrlPid());
					}

					boolean ignoreEventDescription = isEventWithSpecialContent
							// TimetableDisableUrl marks events that have temporary or partial description, which shouldn't be shown
							|| "1".equals(eventContract.getTimetableDisableUrl());

					if (!ignoreEventDescription) {
						event = event.withDescription(convertEventDescription(eventContract.getContent()));
					}
				}

				result.add(event);
				instanceIndex++;
			}
		}

		// Add description for all special content events
		for (ConventionEvent specialEvent : eventsWithSpecialContent.keySet()) {
			final Integer eventIdToFetchContentFrom = eventsWithSpecialContent.get(specialEvent);
			ConventionEvent eventToFetchContentFrom = CollectionUtils.findFirst(result, new CollectionUtils.Predicate<ConventionEvent>() {
				@Override
				public boolean where(ConventionEvent event) {
					return eventIdToFetchContentFrom != null && event.getServerId() == eventIdToFetchContentFrom;
				}
			});

			if (eventToFetchContentFrom != null) {
				specialEvent.setDescription(eventToFetchContentFrom.getDescription());
			}
		}

		return result;
	}

	private int convertColor(String serverColor) {

		int color = NO_COLOR;
		if (serverColor != null && !serverColor.isEmpty()) {
			try {
				if (!serverColor.startsWith("#")) {
					serverColor = "#" + serverColor;
				}
				color = Color.parseColor(serverColor);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "Color from server cannot be parsed: " + serverColor);
			}
		}
		return color;
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

		// using deprecated API to support API level 17
		// noinspection deprecation
		Html.fromHtml(rawEventDescription, new Html.ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				eventImageUrls.add(source);
				return null;
			}
		}, null);

		return eventImageUrls;
	}
}
