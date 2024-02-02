package amai.org.conventions;

import android.content.Context;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.networking.ModelRefresher;
import amai.org.conventions.utils.Dates;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class ModelRefresherTest {
	// Help data for event creation
	private static Date todayAt10;
	private static Date todayAt12;
	private static Date tomorrowAt12;
	private static Date tomorrowAt14;
	private static List<ConventionEvent.EventLocationType> physical = Collections.singletonList(ConventionEvent.EventLocationType.PHYSICAL);
	private static List<ConventionEvent.EventLocationType> virtual = Collections.singletonList(ConventionEvent.EventLocationType.VIRTUAL);
	private static List<ConventionEvent.EventLocationType> hybrid1 = Arrays.asList(ConventionEvent.EventLocationType.PHYSICAL, ConventionEvent.EventLocationType.VIRTUAL);
	private static List<ConventionEvent.EventLocationType> hybrid2 = Arrays.asList(ConventionEvent.EventLocationType.VIRTUAL, ConventionEvent.EventLocationType.PHYSICAL);
	private static List<ConventionEvent.EventLocationType> empty = Collections.emptyList();
	private static String hall1 = "אולם 1";
	private static String hall2 = "אולם 2";

	private static Context context = ApplicationProvider.getApplicationContext();
	private ModelRefresher modelRefresher = ModelRefresher.getInstance();

	@BeforeClass
	public static void setup() {
		Convention.setConvention(new TestConvention());
		Dates.setInitialDate(getDate(2022, Calendar.MARCH, 30, 9, 0, 0));
		// Wednesday
		todayAt10 = getDate(2022, Calendar.MARCH, 30, 10, 0, 0);
		todayAt12 = getDate(2022, Calendar.MARCH, 30, 12, 0, 0);
		// Thursday
		tomorrowAt12 = getDate(2022, Calendar.MARCH, 31, 12, 0, 0);
		tomorrowAt14 = getDate(2022, Calendar.MARCH, 31, 14, 0, 0);
	}

	// Cancelled event
	@Test
	public void getCancelledEventMessage_returns_message_for_event_today() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		String result = modelRefresher.getCancelledEventMessage(context, event);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להתקיים בשעה 10:00 באולם 1 בוטל.", result);
	}

	@Test
	public void getCancelledEventMessage_returns_message_for_event() {
		ConventionEvent event = createEvent(tomorrowAt12, 1, physical, hall1);
		String result = modelRefresher.getCancelledEventMessage(context, event);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להתקיים ביום חמישי בשעה 12:00 באולם 1 בוטל.", result);
	}

	// Nothing changed
	@Test
	public void getChangedEventMessage_returns_null_when_not_changed() {
		ConventionEvent event = createEvent(tomorrowAt12, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(tomorrowAt12, 1, physical, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertNull(result);
	}

	// Time changed
	@Test
	public void getChangedEventMessage_returns_message_when_time_changed_same_date() {
		ConventionEvent event = createEvent(tomorrowAt12, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(tomorrowAt14, 1, physical, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות ביום חמישי בשעה 12:00 באולם 1 עבר לשעה 14:00.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_time_changed_same_date_today() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt12, 1, physical, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות בשעה 10:00 באולם 1 עבר לשעה 12:00.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_time_changed_different_date() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(tomorrowAt12, 1, physical, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות בשעה 10:00 באולם 1 עבר ליום חמישי בשעה 12:00.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_time_changed_different_date_today() {
		ConventionEvent event = createEvent(tomorrowAt12, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt10, 1, physical, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות ביום חמישי בשעה 12:00 באולם 1 עבר להיום בשעה 10:00.", result);
	}

	// Hall changed
	@Test
	public void getChangedEventMessage_returns_message_when_hall_changed() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt10, 1, physical, hall2);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות בשעה 10:00 באולם 1 עבר לאולם 2.", result);
	}

	// Duration changed
	@Test
	public void getChangedEventMessage_returns_message_when_duration_changed() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt10, 2, physical, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות בשעה 10:00 באולם 1 באורך שעה יארך שעתיים.", result);
	}

	// Location type changed
	@Test
	public void getChangedEventMessage_returns_message_when_location_type_changed_physical_to_virtual() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt10, 1, virtual, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות אירוע פיזי בשעה 10:00 באולם 1 עבר להיות אירוע וירטואלי.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_location_type_changed_virtual_to_physical() {
		ConventionEvent event = createEvent(todayAt10, 1, virtual, hall1);
		ConventionEvent newEvent = createEvent(todayAt10, 1, physical, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות אירוע וירטואלי בשעה 10:00 באולם 1 עבר להיות אירוע פיזי.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_location_type_changed_hybrid_to_virtual() {
		ConventionEvent event = createEvent(todayAt10, 1, hybrid1, hall1);
		ConventionEvent newEvent = createEvent(todayAt10, 1, virtual, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות אירוע היברידי בשעה 10:00 באולם 1 עבר להיות אירוע וירטואלי.", result);

		event = createEvent(todayAt10, 1, hybrid2, hall1);
		result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות אירוע היברידי בשעה 10:00 באולם 1 עבר להיות אירוע וירטואלי.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_location_type_changed_hybrid_to_physical() {
		ConventionEvent event = createEvent(todayAt10, 1, hybrid1, hall1);
		ConventionEvent newEvent = createEvent(todayAt10, 1, physical, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות אירוע היברידי בשעה 10:00 באולם 1 עבר להיות אירוע פיזי.", result);

		event = createEvent(todayAt10, 1, hybrid2, hall1);
		result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות אירוע היברידי בשעה 10:00 באולם 1 עבר להיות אירוע פיזי.", result);
	}
	
	
	@Test
	public void getChangedEventMessage_returns_null_when_location_type_changed_without_affecting_user() {
		List<List<ConventionEvent.EventLocationType>> all = Arrays.asList(physical, virtual, hybrid1, hybrid2, empty, null);
		// Changing from one of these to any others doesn't affect the user (since we don't know what the type was before)
		List<List<ConventionEvent.EventLocationType>> from = Arrays.asList(empty, null);
		// Changing from any type to one of these doesn't affect the user (since it keeps the original or we don't know what it was changed to)
		List<List<ConventionEvent.EventLocationType>> to = Arrays.asList(hybrid1, hybrid2, empty, null);

		for (List<ConventionEvent.EventLocationType> fromType : from) {
			for (List<ConventionEvent.EventLocationType> anyType : all) {
				ConventionEvent event = createEvent(todayAt10, 1, fromType, hall1);
				ConventionEvent newEvent = createEvent(todayAt10, 1, anyType, hall1);
				String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
				Assert.assertNull(result);
			}
		}

		for (List<ConventionEvent.EventLocationType> anyType : all) {
			for (List<ConventionEvent.EventLocationType> toType : to) {
				ConventionEvent event = createEvent(todayAt10, 1, anyType, hall1);
				ConventionEvent newEvent = createEvent(todayAt10, 1, toType, hall1);
				String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
				Assert.assertNull(result);
			}
		}
	}

	// 2 things changed
	@Test
	public void getChangedEventMessage_returns_message_when_time_and_hall_changed() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt12, 1, physical, hall2);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות בשעה 10:00 באולם 1 עבר לשעה 12:00 באולם 2.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_time_and_duration_changed() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt12, 2, physical, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות בשעה 10:00 באולם 1 באורך שעה עבר לשעה 12:00 ויארך שעתיים.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_time_and_location_type_changed() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt12, 1, virtual, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות אירוע פיזי בשעה 10:00 באולם 1 עבר לשעה 12:00 ויהיה אירוע וירטואלי.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_hall_and_duration_changed() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt10, 2, physical, hall2);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות בשעה 10:00 באולם 1 באורך שעה עבר לאולם 2 ויארך שעתיים.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_hall_and_location_type_changed() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt10, 1, virtual, hall2);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות אירוע פיזי בשעה 10:00 באולם 1 עבר לאולם 2 ויהיה אירוע וירטואלי.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_duration_and_location_type_changed() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt10, 2, virtual, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות אירוע פיזי בשעה 10:00 באולם 1 באורך שעה עבר להיות אירוע וירטואלי באורך שעתיים.", result);
	}

	// 3 things changed
	@Test
	public void getChangedEventMessage_returns_message_when_hall_duration_and_location_type_changed() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt10, 2, virtual, hall2);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות אירוע פיזי בשעה 10:00 באולם 1 באורך שעה עבר לאולם 2 ויהיה אירוע וירטואלי באורך שעתיים.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_time_duration_and_location_type_changed() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt12, 2, virtual, hall1);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות אירוע פיזי בשעה 10:00 באולם 1 באורך שעה עבר לשעה 12:00 ויהיה אירוע וירטואלי באורך שעתיים.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_time_hall_and_location_type_changed() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt12, 1, virtual, hall2);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות אירוע פיזי בשעה 10:00 באולם 1 עבר לשעה 12:00 באולם 2 ויהיה אירוע וירטואלי.", result);
	}

	@Test
	public void getChangedEventMessage_returns_message_when_time_hall_and_duration_changed() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt12, 2, physical, hall2);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות בשעה 10:00 באולם 1 באורך שעה עבר לשעה 12:00 באולם 2 ויארך שעתיים.", result);
	}

	// 4 things changed
	@Test
	public void getChangedEventMessage_returns_message_when_hall_time_duration_and_location_type_changed() {
		ConventionEvent event = createEvent(todayAt10, 1, physical, hall1);
		ConventionEvent newEvent = createEvent(todayAt12, 2, virtual, hall2);
		String result = modelRefresher.getChangedEventMessage(context, event, newEvent);
		Assert.assertEquals("האירוע 'שם האירוע' שהיה אמור להיות אירוע פיזי בשעה 10:00 באולם 1 באורך שעה עבר לשעה 12:00 באולם 2 ויהיה אירוע וירטואלי באורך שעתיים.", result);
	}

	// Helper methods

	private static Date getDate(int year, int month, int day, int hours, int minutes, int seconds) {
		Calendar newTime = Calendar.getInstance();
		newTime.set(year, month, day, hours, minutes, seconds);
		return newTime.getTime();
	}

	private ConventionEvent createEvent(Date startTime, int durationInHours, List<ConventionEvent.EventLocationType> locationTypes, String hall) {
		Date endTime = new Date(startTime.getTime() + durationInHours * Dates.MILLISECONDS_IN_HOUR);
		return new ConventionEvent()
				.withStartTime(startTime)
				.withEndTime(endTime)
				.withType(new EventType("test"))
				.withHall(new Hall().withName(hall))
				.withTitle("שם האירוע")
				.withLecturer("testLecturer")
				.withId("1");
	}
}
