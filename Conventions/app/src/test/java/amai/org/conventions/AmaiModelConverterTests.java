package amai.org.conventions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.Hall;
import amai.org.conventions.model.Halls;
import amai.org.conventions.model.SpecialEventsProcessor;
import amai.org.conventions.networking.AmaiEventContract;
import amai.org.conventions.networking.AmaiModelConverter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AmaiModelConverterTests {
	private AmaiModelConverter amaiModelConverter;

	@Mock
	private Halls hallsMock;

	@Before
	public void setup() {
		amaiModelConverter = new AmaiModelConverter(hallsMock, Calendar.getInstance(), new SpecialEventsProcessor());

		// Simulate the default behavior of adding a hall, which is common for most tests
		doAnswer(new Answer<Hall>() {
			@Override
			public Hall answer(InvocationOnMock invocation) throws Throwable {
				return new Hall().withName(invocation.getArguments()[0].toString());
			}
		}).when(hallsMock).add(anyString());
	}

	@Test
	public void Convert_Returns_EmptyList_When_ContractList_Is_Empty() {
		List<ConventionEvent> result = amaiModelConverter.convert(new ArrayList<AmaiEventContract>());
		Assert.assertEquals(0, result.size());
	}

	@Test
	public void Convert_Returns_ConventionEvent_When_ContractList_Has_One_SingleInstance_EventContract() {
		AmaiEventContract contract = generateEventContract(0, Collections.singletonList(generateTimetableInfoInstance(0)));

		List<ConventionEvent> eventList = amaiModelConverter.convert(Collections.singletonList(contract));

		Assert.assertEquals(1, eventList.size());
		assertEquals(contract, contract.getTimetableInfo().get(0), eventList.get(0));
	}

	@Test
	public void Convert_Returns_Duplicated_Events_When_ContractList_Has_One_MultipleInstance_EventContract() {
		AmaiEventContract contract = generateEventContract(0, Arrays.asList(
				generateTimetableInfoInstance(0),
				generateTimetableInfoInstance(1),
				generateTimetableInfoInstance(2)));

		List<ConventionEvent> eventList = amaiModelConverter.convert(Collections.singletonList(contract));

		Assert.assertEquals(3, eventList.size());

		assertEquals(contract, contract.getTimetableInfo().get(0), eventList.get(0));
		assertEquals(contract, contract.getTimetableInfo().get(1), eventList.get(1));
		assertEquals(contract, contract.getTimetableInfo().get(2), eventList.get(2));

		Assert.assertEquals("0_1", eventList.get(0).getId());
		Assert.assertEquals("0_2", eventList.get(1).getId());
		Assert.assertEquals("0_3", eventList.get(2).getId());
	}

	@Test
	public void Convert_Returns_Multiple_Events_When_ContractList_Has_Multiple_EventContracts() {
		AmaiEventContract.TimetableInfoInstance instanceContact = generateTimetableInfoInstance(0);

		List<AmaiEventContract> contracts = Arrays.asList(
				generateEventContract(0, Collections.singletonList(instanceContact)),
				generateEventContract(1, Collections.singletonList(instanceContact)),
				generateEventContract(2, Collections.singletonList(instanceContact)));

		List<ConventionEvent> eventList = amaiModelConverter.convert(contracts);
		Assert.assertEquals(3, eventList.size());

		assertEquals(contracts.get(0), instanceContact, eventList.get(0));
		assertEquals(contracts.get(1), instanceContact, eventList.get(1));
		assertEquals(contracts.get(2), instanceContact, eventList.get(2));

		Assert.assertEquals("0_1", eventList.get(0).getId());
		Assert.assertEquals("1_1", eventList.get(1).getId());
		Assert.assertEquals("2_1", eventList.get(2).getId());
	}

	@Test
	public void Convert_Ignores_Hidden_Events() {
		AmaiEventContract.TimetableInfoInstance instanceContact = generateTimetableInfoInstance(0);

		List<AmaiEventContract> contracts = Arrays.asList(
				generateEventContract(0, Collections.singletonList(instanceContact)),
				generateEventContract(1, Collections.singletonList(generateTimetableInfoInstance(1).setTooltip("hidden"))),
				generateEventContract(2, Collections.singletonList(instanceContact)));

		List<ConventionEvent> eventList = amaiModelConverter.convert(contracts);
		Assert.assertEquals(2, eventList.size());

		assertEquals(contracts.get(0), instanceContact, eventList.get(0));
		assertEquals(contracts.get(2), instanceContact, eventList.get(1));
	}

	@Test
	public void Convert_Strips_EventDescription_When_EventContract_Has_DisableUrlFlag() {
		AmaiEventContract contract = generateEventContract(0, Collections.singletonList(generateTimetableInfoInstance(0)))
				.setTimetableDisableUrl(1);

		List<ConventionEvent> eventList = amaiModelConverter.convert(Collections.singletonList(contract));

		Assert.assertEquals(1, eventList.size());
		Assert.assertNull(eventList.get(0).getDescription());
	}

	@Test
	public void Convert_Strips_Unneeded_Html_Tags_From_EventContract_Description() {
		AmaiEventContract contract = generateEventContract(0, Collections.singletonList(generateTimetableInfoInstance(0)))
				.setContent("<p><span style=\"font-weight: 400\"></span><img src=\"http://a.com/image.png\"/></p>");

		List<ConventionEvent> eventList = amaiModelConverter.convert(Collections.singletonList(contract));

		Assert.assertEquals(1, eventList.size());
		Assert.assertEquals("<p><span ></span><ximg /></p>", eventList.get(0).getDescription());
	}

	@Test
	public void Convert_Returns_EventDescription_When_EventWithSpecialContent_Conversion_Occurs() {
		AmaiEventContract.TimetableInfoInstance instanceContact = generateTimetableInfoInstance(0);

		List<AmaiEventContract> contracts = Arrays.asList(
				generateEventContract(1, Collections.singletonList(instanceContact)),
				generateEventContract(2, Collections.singletonList(instanceContact)).setTimetableUrlPid(1));

		List<ConventionEvent> eventList = amaiModelConverter.convert(contracts);

		Assert.assertEquals(2, eventList.size());
		// The description of the 2nd (special) event should be the content of event number #1
		Assert.assertEquals(contracts.get(0).getContent(), eventList.get(1).getDescription());
	}

	@Test
	public void Convert_Returns_No_EventDescription_When_EventWithSpecialContent_Conversion_Fails() {
		AmaiEventContract.TimetableInfoInstance instanceContact = generateTimetableInfoInstance(0);

		List<AmaiEventContract> contracts = Arrays.asList(
				generateEventContract(1, Collections.singletonList(instanceContact)),
				generateEventContract(2, Collections.singletonList(instanceContact)).setTimetableUrlPid(3));

		List<ConventionEvent> eventList = amaiModelConverter.convert(contracts);

		Assert.assertEquals(2, eventList.size());
		// The description of the 2nd (special) event should be null, since it's pointing to non-existing event #3
		Assert.assertNull(eventList.get(1).getDescription());
	}

	@Test
	public void Convert_Adds_New_Hall_If_Event_Has_Undefined_Hall_Name() {
		AmaiEventContract.TimetableInfoInstance instanceContract = generateTimetableInfoInstance(0);
		amaiModelConverter.convert(Collections.singletonList(generateEventContract(0, Collections.singletonList(instanceContract))));
		verify(hallsMock, times(1)).add(eq(instanceContract.getRoom()));
	}

	@Test
	public void Convert_Uses_Existing_Hall_If_Event_Has_Predefined_Hall_Name() {
		AmaiEventContract.TimetableInfoInstance instanceContract = generateTimetableInfoInstance(0);

		// Configure an existing hall
		Hall expectedHall = new Hall();
		when(hallsMock.findByName(eq(instanceContract.getRoom()))).thenReturn(expectedHall);

		List<ConventionEvent> eventList = amaiModelConverter.convert(Collections.singletonList(generateEventContract(0, Collections.singletonList(instanceContract))));

		verify(hallsMock, times(0)).add(anyString());
		Assert.assertEquals(expectedHall, eventList.get(0).getHall());
	}

	private AmaiEventContract.TimetableInfoInstance generateTimetableInfoInstance(int index) {
		return new AmaiEventContract.TimetableInfoInstance()
				.setRoom("testRoom" + index)
				.setBeforeHourText("testLecturer" + index)
				.setStart("19:30:0" + index)
				.setEnd("20:00:0" + index);
	}

	private AmaiEventContract generateEventContract(int index, List<AmaiEventContract.TimetableInfoInstance> timetableInfoInstances) {
		return new AmaiEventContract()
				.setId(index)
				.setTitle("testEvent" + index)
				.setCategoriesText(new AmaiEventContract.CategoriesText().setName("testCategory" + index))
				.setContent("testContent" + index)
				.setTimetableInfo(timetableInfoInstances);
	}

	private void assertEquals(AmaiEventContract expectedContract, AmaiEventContract.TimetableInfoInstance expectedInstanceContract, ConventionEvent actual) {
		Assert.assertEquals(expectedContract.getId(), actual.getServerId());
		Assert.assertEquals(expectedContract.getCategoriesText().getName(), actual.getType().getDescription());
		Assert.assertEquals(expectedContract.getTitle(), actual.getTitle());
		Assert.assertEquals(expectedContract.getContent(), actual.getDescription());
		Assert.assertEquals(expectedInstanceContract.getBeforeHourText(), actual.getLecturer());
		Assert.assertEquals(expectedInstanceContract.getRoom(), actual.getHall().getName());
		Assert.assertEquals(expectedInstanceContract.getStart(), formatHoursMinutesAndSeconds(actual.getStartTime()));
		Assert.assertEquals(expectedInstanceContract.getEnd(), formatHoursMinutesAndSeconds(actual.getEndTime()));
	}

	private String formatHoursMinutesAndSeconds(Date date) {
		return new SimpleDateFormat("HH:mm:ss", Locale.US).format(date);
	}
}
