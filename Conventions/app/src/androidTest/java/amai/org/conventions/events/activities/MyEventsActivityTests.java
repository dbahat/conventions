package amai.org.conventions.events.activities;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import amai.org.conventions.TestConvention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MyEventsActivityTests {
    @Before
    public void setup() {
        Convention convention = new TestConvention();
        convention.setEvents(new ArrayList<>(Arrays.asList(
                generateEvent(), generateEvent(), generateEvent()
        )));
        convention.setUserInput(new HashMap<String, ConventionEvent.UserInput>());
        convention.load(InstrumentationRegistry.getTargetContext());
        Convention.setConvention(convention);
    }

    @Rule
    public ActivityTestRule<MyEventsActivity> myEventsActivityActivityTestRule = new ActivityTestRule<>(MyEventsActivity.class, false, false);

    @Test
    public void MyEvents_Shows_Single_Event_When_User_Marked_Single_Event_As_Favorite() {
        ConventionEvent attendingEvent = Convention.getInstance().getEvents().get(0);
        ConventionEvent nonAttendingEvent = Convention.getInstance().getEvents().get(1);

        attendingEvent.setAttending(true);
        myEventsActivityActivityTestRule.launchActivity(new Intent());

        onView(withText(attendingEvent.getTitle())).check(matches(isDisplayed()));
        onView(withText(nonAttendingEvent.getTitle())).check(doesNotExist());
    }

    private ConventionEvent generateEvent() {
        return new ConventionEvent()
                .withStartTime(Dates.now())
                .withEndTime(Dates.now())
                .withType(new EventType("test"))
                .withHall(new Hall().withName("testHall"))
                .withTitle("event_" + UUID.randomUUID())
                .withLecturer("testLecturer")
                .withId(UUID.randomUUID().toString());
    }
}