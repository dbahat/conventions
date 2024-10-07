package amai.org.conventions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import amai.org.conventions.events.activities.EventsTimeSlot;
import amai.org.conventions.events.activities.MyEventsDayFragment;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;

@RunWith(MockitoJUnitRunner.class)
public class MyEventsDayFragmentGetNonConflictingGroupsTests {

	@Before
	public void setup() {
		Convention convention = new TestConvention();
		convention.setEvents(new ArrayList<>());
		convention.setUserInput(new HashMap<>());
		Convention.setConvention(convention);
	}

	// ======================================
	// Prev, next are null (full events list)
	// ======================================

	@Test
	public void getNonConflictingGroups_returns_empty_list_when_prev_next_are_null_and_events_list_is_empty() {
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, new ArrayList<>(), null);
		assertGroups(result);
	}

	@Test
	public void getNonConflictingGroups_returns_one_non_conflicting_group_when_prev_next_are_null_and_events_list_has_one_event() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1), null);
		assertGroups(result, group(event1));
	}

	@Test
	public void getNonConflictingGroups_returns_two_non_conflicting_group_when_prev_next_are_null_and_events_list_has_two_consecutive_event() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "11:00", "12:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2), null);
		assertGroups(result,
			group(event1),
			group(event2)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_three_non_conflicting_group_when_prev_next_are_null_and_events_list_has_three_consecutive_event() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "11:00", "12:00");
		ConventionEvent event3 = createEvent("3", "12:00", "14:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3), null);
		assertGroups(result,
			group(event1),
			group(event2),
			group(event3)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_three_non_conflicting_group_and_two_free_time_slots_when_prev_next_are_null_and_events_list_has_three_non_consecutive_event() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "12:00", "14:00");
		ConventionEvent event3 = createEvent("3", "17:00", "18:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3), null);
		assertGroups(result,
			group(event1),
			new EventsTimeSlot(getDate("11:00"), getDate("12:00")),
			group(event2),
			new EventsTimeSlot(getDate("14:00"), getDate("17:00")),
			group(event3)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_conflicting_and_non_conflicting_groups_when_prev_next_are_null_and_events_list_has_two_conflicting_and_one_consecutive_event() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "10:00", "12:00");
		ConventionEvent event3 = createEvent("3", "12:00", "14:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3), null);
		assertGroups(result,
			group(event1, event2),
			group(event3)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_conflicting_free_time_and_non_conflicting_groups_when_prev_next_are_null_and_events_list_has_two_conflicting_and_one_non_consecutive_event() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "10:00", "12:00");
		ConventionEvent event3 = createEvent("3", "15:00", "14:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3), null);
		assertGroups(result,
			group(event1, event2),
			new EventsTimeSlot(getDate("12:00"), getDate("15:00")),
			group(event3)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_non_conflicting_and_conflicting_groups_when_prev_next_are_null_and_events_list_has_event_and_consecutive_two_conflicting_events() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "11:00", "12:00");
		ConventionEvent event3 = createEvent("3", "11:10", "14:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3), null);
		assertGroups(result,
			group(event1),
			group(event2, event3)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_non_conflicting_free_time_and_conflicting_groups_when_prev_next_are_null_and_events_list_has_event_and_non_consecutive_two_conflicting_events() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "12:00", "13:00");
		ConventionEvent event3 = createEvent("3", "12:50", "14:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3), null);
		assertGroups(result,
			group(event1),
			new EventsTimeSlot(getDate("11:00"), getDate("12:00")),
			group(event2, event3)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_conflicting_group_when_prev_next_are_null_and_events_list_has_two_non_conflicting_events_and_one_which_conflicts_with_both() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "10:30", "11:30");
		ConventionEvent event3 = createEvent("3", "11:10", "14:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3), null);
		assertGroups(result,
			group(event1, event2, event3)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_two_conflicting_groups_when_prev_next_are_null_and_events_list_has_two_conflicting_events_and_consecutive_two_more_conflicting_events() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "10:30", "11:30");
		ConventionEvent event3 = createEvent("3", "11:30", "15:00");
		ConventionEvent event4 = createEvent("4", "14:00", "15:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3, event4), null);
		assertGroups(result,
			group(event1, event2),
			group(event3, event4)
		);
	}

	// =================================================================================
	// prev, next are non-null, events list is empty (removal of non-conflicting event)
	// =================================================================================

	@Test
	public void getNonConflictingGroups_returns_empty_list_when_next_is_not_null_and_events_list_is_empty() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, new ArrayList<>(), group(event1));
		assertGroups(result);
	}

	@Test
	public void getNonConflictingGroups_returns_empty_list_when_prev_is_not_null_and_events_list_is_empty() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(group(event1), new ArrayList<>(), null);
		assertGroups(result);
	}

	@Test
	public void getNonConflictingGroups_returns_empty_list_when_prev_next_are_consecutive_and_events_list_is_empty() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "11:00", "12:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(group(event1), new ArrayList<>(), group(event2));
		assertGroups(result);
	}

	@Test
	public void getNonConflictingGroups_returns_free_time_slot_when_prev_next_are_non_consecutive_and_events_list_is_empty() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "14:00", "15:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(group(event1), new ArrayList<>(), group(event2));
		assertGroups(result, new EventsTimeSlot(getDate("11:00"), getDate("14:00")));
	}

	// =================================================================================
	// prev, next are non-null, events list is not empty (removal of conflicting event)
	// =================================================================================

	@Test
	public void getNonConflictingGroups_returns_non_conflicting_group_when_prev_next_are_consecutive_and_events_list_has_one_event() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "11:00", "13:00");
		ConventionEvent event3 = createEvent("3", "13:00", "14:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(group(event1), Arrays.asList(event2), group(event3));
		assertGroups(result,
			group(event2)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_two_non_conflicting_groups_when_prev_next_are_consecutive_and_events_list_has_two_non_conflicting_events() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "11:00", "13:00");
		ConventionEvent event3 = createEvent("3", "13:00", "14:00");
		ConventionEvent event4 = createEvent("4", "14:00", "15:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(group(event1), Arrays.asList(event2, event3), group(event4));
		assertGroups(result,
			group(event2),
			group(event3)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_conflicting_group_when_prev_next_are_consecutive_and_events_list_has_two_conflicting_events() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "11:00", "13:00");
		ConventionEvent event3 = createEvent("3", "12:00", "14:00");
		ConventionEvent event4 = createEvent("4", "14:00", "15:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(group(event1), Arrays.asList(event2, event3), group(event4));
		assertGroups(result,
			group(event2, event3)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_two_conflicting_group_when_prev_next_are_consecutive_and_events_list_has_two_conflicting_events_and_consecutive_two_more_conflicting_events() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "11:00", "13:00");
		ConventionEvent event3 = createEvent("3", "12:00", "14:00");
		ConventionEvent event4 = createEvent("4", "14:00", "15:00");
		ConventionEvent event5 = createEvent("5", "14:30", "16:00");
		ConventionEvent event6 = createEvent("6", "16:00", "17:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(group(event1), Arrays.asList(event2, event3, event4, event5), group(event6));
		assertGroups(result,
			group(event2, event3),
			group(event4, event5)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_free_time_and_conflicting_group_when_prev_is_non_consecutive_and_events_list_has_two_conflicting_events() {
		ConventionEvent event1 = createEvent("1", "10:00", "10:30");
		ConventionEvent event2 = createEvent("2", "11:00", "13:00");
		ConventionEvent event3 = createEvent("3", "12:00", "14:00");
		ConventionEvent event4 = createEvent("4", "14:00", "15:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(group(event1), Arrays.asList(event2, event3), group(event4));
		assertGroups(result,
			new EventsTimeSlot(getDate("10:30"), getDate("11:00")),
			group(event2, event3)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_free_time_and_conflicting_group_when_next_is_non_consecutive_and_events_list_has_two_conflicting_events() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "11:00", "13:00");
		ConventionEvent event3 = createEvent("3", "12:00", "14:00");
		ConventionEvent event4 = createEvent("4", "14:30", "15:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(group(event1), Arrays.asList(event2, event3), group(event4));
		assertGroups(result,
			group(event2, event3),
			new EventsTimeSlot(getDate("14:00"), getDate("14:30"))
		);
	}

	// ===========================
	// Ongoing conflicting events
	// ===========================

	@Test
	public void getNonConflictingGroups_returns_ongoing_event_in_separate_group_when_it_is_first_in_group_of_conflicting_events() {
		ConventionEvent event1 = createOngoingEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "10:00", "14:00");
		ConventionEvent event3 = createEvent("3", "12:00", "14:00");
		ConventionEvent event4 = createEvent("4", "13:00", "15:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3, event4), null);
		assertGroups(result,
			group(event1),
			group(event2, event3, event4)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_ongoing_event_in_separate_group_and_first_event_in_own_group_when_first_event_is_not_conflicting_without_the_ongoing_event() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createOngoingEvent("2", "10:00", "14:00");
		ConventionEvent event3 = createEvent("3", "11:00", "14:00");
		ConventionEvent event4 = createEvent("4", "13:00", "15:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3, event4), null);
		assertGroups(result,
			group(event1),
			group(event2),
			group(event3, event4)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_free_time_before_ongoing_event_disregarding_ongoing_event_end_time() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createOngoingEvent("2", "10:00", "14:00");
		ConventionEvent event3 = createEvent("3", "12:00", "14:00");
		ConventionEvent event4 = createEvent("4", "13:00", "15:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3, event4), null);
		assertGroups(result,
			group(event1),
			new EventsTimeSlot(getDate("11:00"), getDate("12:00")),
			group(event2),
			group(event3, event4)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_ongoing_event_in_conflicting_group_when_events_before_and_after_it_are_conflicting() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "10:00", "14:00");
		ConventionEvent event3 = createOngoingEvent("3", "11:00", "14:00");
		ConventionEvent event4 = createEvent("4", "13:00", "15:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3, event4), null);
		assertGroups(result,
			group(event1, event2, event3, event4)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_ongoing_event_in_separate_group_when_it_is_last_in_group_of_conflicting_events() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createEvent("2", "10:00", "14:00");
		ConventionEvent event3 = createEvent("3", "12:00", "14:00");
		ConventionEvent event4 = createOngoingEvent("4", "13:00", "15:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3, event4), null);
		assertGroups(result,
			group(event1, event2, event3),
			group(event4)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_ongoing_events_in_separate_groups_when_there_are_several_in_same_confcling_group() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createOngoingEvent("2", "10:00", "14:00");
		ConventionEvent event3 = createEvent("3", "11:00", "14:00");
		ConventionEvent event4 = createOngoingEvent("4", "13:00", "15:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3, event4), null);
		assertGroups(result,
			group(event1),
			group(event2),
			group(event3),
			group(event4)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_ongoing_events_in_separate_groups_when_there_are_several_before_in_and_after_conflicting_group() {
		ConventionEvent event1 = createOngoingEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createOngoingEvent("2", "10:00", "14:00");

		ConventionEvent event3 = createEvent("3", "11:00", "14:00");
		ConventionEvent event4 = createOngoingEvent("4", "13:00", "15:00");
		ConventionEvent event5 = createOngoingEvent("5", "13:00", "15:00");
		ConventionEvent event6 = createEvent("6", "13:30", "16:00");

		ConventionEvent event7 = createOngoingEvent("7", "15:00", "16:00");
		ConventionEvent event8 = createOngoingEvent("8", "15:00", "17:00");

		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2, event3, event4, event5, event6, event7, event8), null);
		assertGroups(result,
			group(event1),
			group(event2),
			group(event3, event4, event5, event6),
			group(event7),
			group(event8)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_free_time_when_prev_next_are_not_null_and_all_events_are_ongoing() {
		ConventionEvent event1 = createEvent("1", "10:00", "11:00");
		ConventionEvent event2 = createOngoingEvent("2", "10:00", "14:00");
		ConventionEvent event3 = createOngoingEvent("3", "11:00", "14:00");
		ConventionEvent event4 = createOngoingEvent("4", "13:00", "15:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(group(event1), Arrays.asList(event2, event3), group(event4));
		assertGroups(result,
			new EventsTimeSlot(getDate("11:00"), getDate("13:00")),
			group(event2),
			group(event3)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_free_time_before_first_non_ongoing_event_when_prev_is_not_null_and_ongoing_event_is_first() {
		ConventionEvent event1 = createEvent("1", "10:00", "10:30");
		ConventionEvent event2 = createOngoingEvent("2", "10:00", "14:00");
		ConventionEvent event3 = createEvent("3", "11:00", "14:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(group(event1), Arrays.asList(event2, event3), null);
		assertGroups(result,
			new EventsTimeSlot(getDate("10:30"), getDate("11:00")),
			group(event2),
			group(event3)
		);
	}

	@Test
	public void getNonConflictingGroups_returns_free_time_after_last_non_ongoing_event_when_next_is_not_null_and_ongoing_event_is_last() {
		ConventionEvent event1 = createEvent("1", "10:00", "10:30");
		ConventionEvent event2 = createOngoingEvent("2", "10:00", "14:00");
		ConventionEvent event3 = createEvent("3", "11:00", "14:00");
		ArrayList<EventsTimeSlot> result = MyEventsDayFragment.getNonConflictingGroups(null, Arrays.asList(event1, event2), group(event3));
		assertGroups(result,
			group(event1),
			new EventsTimeSlot(getDate("10:30"), getDate("11:00")),
			group(event2)
		);
	}

	// ==========
	// Utilities
	// ==========

	private EventsTimeSlot group(ConventionEvent ...events) {
		EventsTimeSlot slot = new EventsTimeSlot();
		for (ConventionEvent event : events) {
			slot.addEvent(event);
		}
		return slot;
	}

	private void assertGroups(ArrayList<EventsTimeSlot> result, EventsTimeSlot ...expected) {
		Assert.assertEquals(expected.length, result.size());
		for (int i = 0; i < expected.length; i++) {
			EventsTimeSlot expectedSlot = expected[i];
			EventsTimeSlot resultSlot = result.get(i);
			String groupName = "group " + i;
			Assert.assertEquals(groupName + " type", expectedSlot.getType(), resultSlot.getType());

			if (expectedSlot.getType() == EventsTimeSlot.EventsTimeSlotType.NO_EVENTS) {
				Assert.assertEquals(groupName + " start time",expectedSlot.getStartTime(), resultSlot.getStartTime());
				Assert.assertEquals(groupName + " end time", expectedSlot.getEndTime(), resultSlot.getEndTime());
			} else {
				ArrayList<String> expectedNames = new ArrayList<>(expectedSlot.getEvents().size());
				for (ConventionEvent event : expectedSlot.getEvents()) {
					expectedNames.add(event.getTitle());
				}

				ArrayList<String> resultNames = new ArrayList<>(resultSlot.getEvents().size());
				for (ConventionEvent event : resultSlot.getEvents()) {
					resultNames.add(event.getTitle());
				}

				Assert.assertEquals(groupName + " event names", expectedNames, resultNames);
			}
		}
	}

	private ConventionEvent createEvent(String id, String startTime, String endTime) {
		return new ConventionEvent()
			.withStartTime(getDate(startTime))
			.withEndTime(getDate(endTime))
			.withType(new EventType("test"))
			.withHall(new Hall().withName("testHall"))
			.withTitle("event_" + id)
			.withLecturer("testLecturer")
			.withId(id);
	}

	private ConventionEvent createOngoingEvent(String id, String startTime, String endTime) {
		return createEvent(id, startTime, endTime).withOngoing(true);
	}

	private Date getDate(String time) {
		try {
			Date hourAndMinute = new SimpleDateFormat("HH:mm", Dates.getLocale()).parse(time);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(hourAndMinute);

			Calendar date = Calendar.getInstance();
			calendar.set(date.get(Calendar.YEAR),
				date.get(Calendar.MONTH),
				date.get(Calendar.DAY_OF_MONTH));

			return calendar.getTime();
		} catch (ParseException e) {
			return new Date();
		}
	}
}
