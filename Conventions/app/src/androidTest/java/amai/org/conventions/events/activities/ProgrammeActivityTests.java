package amai.org.conventions.events.activities;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import amai.org.conventions.TestConvention;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.EventType;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ProgrammeActivityTests {

    @Rule
    public ActivityTestRule<ProgrammeActivity> activityTestRule = new ActivityTestRule<>(ProgrammeActivity.class, false, false);

    @Test
    public void Programme_Shows_Currently_Running_Event() {
        ConventionEvent event = generateEvent();
        Convention.setConvention(generateConvention(Arrays.asList(
                event
        )));
        activityTestRule.launchActivity(new Intent());

        onView(withText(event.getTitle())).check(matches(isDisplayed()));
    }

    private Convention generateConvention(List<ConventionEvent> events) {
        Convention convention = new TestConvention();
        convention.setEvents(events);
        convention.setUserInput(new HashMap<String, ConventionEvent.UserInput>());
        convention.load(ApplicationProvider.getApplicationContext());
        Convention.setConvention(convention);
        return convention;
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