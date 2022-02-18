package amai.org.conventions.model;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;

import org.xml.sax.Attributes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import androidx.annotation.Nullable;
import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.networking.AmaiModelConverter;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.HtmlParser;
import amai.org.conventions.utils.Objects;
import androidx.annotation.StringRes;
import fi.iki.kuitsi.listtest.ListTagHandler;

public class ConventionEvent implements Serializable {
	private String id;
	private int serverId;
	private int backgroundColor;
	private int textColor;
	private String title;
	private String lecturer;
	private Date startTime;
	private Date endTime;
	private EventType type;
	private Hall hall;
	private List<String> images;
	private String description;
	private String plainTextDescription;

	public ConventionEvent() {
		images = new ArrayList<>();
		plainTextDescription = "";
	}

	public void setBackgroundColor(int color) {
		this.backgroundColor = color;
	}

	public ConventionEvent withBackgroundColor(int color) {
		setBackgroundColor(color);
		return this;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getTextColor(Context context) {
		if (textColor != AmaiModelConverter.NO_COLOR) {
			return textColor;
		}
		if (Convention.getInstance().getEventLocationType(this) == EventLocationType.VIRTUAL) {
			return ThemeAttributes.getColor(context, R.attr.eventTimeVirtualTextColor);
		}
		return ThemeAttributes.getColor(context, R.attr.eventTimeDefaultTextColor);
	}

	public ConventionEvent withTextColor(int textColor) {
		setTextColor(textColor);
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		// spannedDescription will be null in unit tests, since it uses Android SDK APIs underline, which aren't currently mocked in unit tests
		Spanned spannedDescription = getSpannedDescription();
		this.plainTextDescription = description.isEmpty() || spannedDescription == null ? "" : spannedDescription.toString().replace("\n", " ");
	}

	public ConventionEvent withDescription(String description) {
		setDescription(description);
		return this;
	}

	public String getPlainTextDescription() {
		return plainTextDescription;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public ConventionEvent withId(String id) {
		setId(id);
		return this;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ConventionEvent withTitle(String title) {
		setTitle(title);
		return this;
	}

	public String getLecturer() {
		return lecturer;
	}

	public void setLecturer(String lecturer) {
		this.lecturer = lecturer;
	}

	public ConventionEvent withLecturer(String lecturer) {
		setLecturer(lecturer);
		return this;
	}

	public boolean isAttending() {
		return getUserInput().isAttending();
	}

	public void setAttending(boolean attending) {
		getUserInput().setAttending(attending);
	}

	public ConventionEvent withAttending(boolean attending) {
		setAttending(attending);
		return this;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public ConventionEvent withStartTime(Date startTime) {
		setStartTime(startTime);
		return this;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public ConventionEvent withEndTime(Date endTime) {
		setEndTime(endTime);
		return this;
	}

	public EventType getType() {
		return type;
	}

	public int getBackgroundColor(Context context) {
		if (backgroundColor != AmaiModelConverter.NO_COLOR) {
			return backgroundColor;
		}
		int eventTypeColor = getType().getBackgroundColor();
		if (eventTypeColor != AmaiModelConverter.NO_COLOR) {
			return eventTypeColor;
		}
		if (Convention.getInstance().getEventLocationType(this) == EventLocationType.VIRTUAL) {
			return ThemeAttributes.getColor(context, R.attr.eventTimeVirtualBackgroundColor);
		}
		return ThemeAttributes.getColor(context, R.attr.eventTimeDefaultBackgroundColor);
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public ConventionEvent withType(EventType type) {
		setType(type);
		return this;
	}

	public Hall getHall() {
		return hall;
	}

	public void setHall(Hall hall) {
		this.hall = hall;
	}

	public ConventionEvent withHall(Hall hall) {
		setHall(hall);
		return this;
	}

	public List<Integer> getImageResources() {
		return Convention.getInstance().getImageMapper().getImageResourcesList(images);
	}

	public List<Integer> getLogoImageResources() {
		return Convention.getInstance().getImageMapper().getLogoResources(images);
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public ConventionEvent withImages(List<String> images) {
		setImages(images);
		return this;
	}

	public UserInput getUserInput() {
		return Convention.getInstance().getEventUserInput(getId());
	}

	public boolean hasStarted() {
		return startTime.before(Dates.now());
	}

	public boolean hasEnded() {
		return endTime.before(Dates.now());
	}

	public boolean canFillFeedback() {
		// Check if the event will end in 15 minutes or less
		Calendar minimumTimeOfFillingFeedback = Calendar.getInstance();
		minimumTimeOfFillingFeedback.setTime(endTime);
		minimumTimeOfFillingFeedback.add(Calendar.MINUTE, -15);

		return minimumTimeOfFillingFeedback.getTime().before(Dates.now());
	}

	/**
	 * The user should see the feedback sending option on the event under the following conditions:
	 * 1. It's already possible to send feedback (event has ended or is about to end)
	 * 2. The user didn't send feedback yet on this event
	 * 3. The user either attended the event or already answered some questions about it
	 */
	public boolean shouldUserSeeFeedback() {
		return this.canFillFeedback() &&
				(!this.getUserInput().getFeedback().isSent()) &&
				(this.getUserInput().isAttending() ||
						this.getUserInput().getFeedback().hasAnsweredQuestions());
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public ConventionEvent withServerId(int serverId) {
		setServerId(serverId);
		return this;
	}

	@Nullable
	public Date getEventAboutToStartNotificationTime() {
		Long diff = getUserInput().getEventAboutToStartNotification().getTimeDiffInMillis();
		if (diff == null) {
			return null;
		}
		return new Date(getStartTime().getTime() + diff);
	}

	@Nullable
	public Date getEventFeedbackReminderNotificationTime() {
		Long diff = getUserInput().getEventFeedbackReminderNotification().getTimeDiffInMillis();
		if (diff == null) {
			return null;
		}
		return new Date(getEndTime().getTime() + diff);
	}


	public Spanned getSpannedDescription() {
		String eventDescription = this.getDescription();
		final ListTagHandler listTagHandler = new ListTagHandler();
		Spanned spanned = HtmlParser.fromHtml(eventDescription, null, new HtmlParser.TagHandler() {
			private Stack<DivSpan> divSpans = new Stack<>();
			private Stack<IFrameSpan> iframeSpans = new Stack<>();

			@Override
			public boolean handleTag(boolean opening, String tag, Editable output, Attributes attributes) {
				listTagHandler.handleTag(opening, tag, output, null);

				// Mark embedded google form with a GoogleFormSpan:
				// An embedded google forms is a div that has a form inside it.
				// We keep a stack of div objects so we can track when the google form's div is closed,
				// and set its URL when we reach the form element inside it
				if (tag.equals("xdiv") || tag.equals("div")) {
					int length = output.length();
					if (opening) {
						DivSpan span;
						String classStyle = HtmlParser.getValue(attributes, "class");
						if ("ss-form-container".equals(classStyle)) {
							span = new GoogleFormSpan();
						} else {
							span = new DivSpan();
						}
						span.setStart(length);
						divSpans.push(span);
					} else {
						DivSpan span = divSpans.pop();
						span.setEnd(length);
						if (span instanceof GoogleFormSpan) {
							output.setSpan(span, span.getStart(), length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					}
				} else if (opening && tag.equals("form") && attributes != null) {
					// Add the url to the google form (only if this form is inside the google form,
					// meaning the form wasn't attached to the output yet so it's still in the divSpans stack).
					// We go over the stack in reverse order because the current form was added last.
					GoogleFormSpan span = null;
					DivSpan currentSpan = null;
					for (ListIterator<DivSpan> iter = divSpans.listIterator(divSpans.size()); iter.hasPrevious(); currentSpan = iter.previous()) {
						if (currentSpan instanceof GoogleFormSpan) {
							span = (GoogleFormSpan) currentSpan;
							break;
						}
					}
					if (span != null) {
						String url = HtmlParser.getValue(attributes, "action");
						span.setUrl(url);
					}
				} else if (tag.equals("iframe")) {
					// Handle iframes
					// There are currently 2 iframe types we support: youtube videos and google forms (the previous handling for google form is via an unsupported wordpress plugin so we have to support this type as well).
					// Here we just mark the iframe with its content, later on we check for each iframe type what the URL is and add a link.
					int length = output.length();
					if (opening) {
						IFrameSpan span = new IFrameSpan();
						span.setStart(length);
						// We replace all src attributes with xsrc in amai but not in sff
						String url = HtmlParser.getValue(attributes, "xsrc");
						if (url == null) {
							url = HtmlParser.getValue(attributes, "src");
						}
						span.setUrl(url);
						iframeSpans.push(span);
					} else {
						IFrameSpan span = iframeSpans.pop();
						span.setEnd(length);
						output.setSpan(span, span.getStart(), length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
					}
				}
				return false;
			}
		});

		if (spanned == null) {
			return null;
		}

		// Convert iframes to CustomURLSpans by removing the original (iframe) content and adding a link
		IFrameSpan[] iframes = spanned.getSpans(0, spanned.length(), IFrameSpan.class);
		for (IFrameSpan span : iframes) {
			int spanStart = span.getStart();
			int spanEnd = span.getEnd();
			SpannableStringBuilder link = new SpannableStringBuilder();
			if (!TextUtils.isEmpty(span.getUrl())) {
				String linkText = "למידע נוסף";
				if (span.getUrl().startsWith("https://www.youtube.com/")) {
					linkText = "לסרטון";
				} else if (span.getUrl().startsWith("https://docs.google.com/forms/")) {
					linkText = "לטופס";
				}
				link.append(linkText).append("\n");
				link.setSpan(new CustomURLSpan(span.getUrl()), 0, link.length() - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			}
			spanned = (Spanned) TextUtils.concat(spanned.subSequence(0, spanStart), link, spanned.subSequence(spanEnd, spanned.length()));
		}

		// Convert google form spans to CustomURLSpan by removing the original (div) content and adding a link to the form
		GoogleFormSpan[] forms = spanned.getSpans(0, spanned.length(), GoogleFormSpan.class);
		for (GoogleFormSpan span : forms) {
			int spanStart = span.getStart();
			int spanEnd = span.getEnd();
			SpannableStringBuilder linkToForm = new SpannableStringBuilder();
			if (!TextUtils.isEmpty(span.getUrl())) {
				linkToForm.append("לטופס").append("\n");
				linkToForm.setSpan(new CustomURLSpan(span.getUrl()), 0, linkToForm.length() - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
			}
			spanned = (Spanned) TextUtils.concat(spanned.subSequence(0, spanStart), linkToForm, spanned.subSequence(spanEnd, spanned.length()));
		}
		// Remove any links to google forms (since they were handled previously)
		for (URLSpan urlSpan : spanned.getSpans(0, spanned.length(), URLSpan.class)) {
			if (urlSpan.getURL().startsWith("https://docs.google.com/forms/") && !(urlSpan instanceof CustomURLSpan)) {
				int spanStart = spanned.getSpanStart(urlSpan);
				int spanEnd = spanned.getSpanEnd(urlSpan);
				spanned = (Spanned) TextUtils.concat(spanned.subSequence(0, spanStart), spanned.subSequence(spanEnd, spanned.length()));
			}
		}
		return spanned;
	}

	private static class DivSpan {
		int start = -1;
		int end = -1;

		public void setStart(int start) {
			this.start = start;
		}
		public int getStart() {
			return start;
		}

		public void setEnd(int end) {
			this.end = end;
		}

		public int getEnd() {
			return end;
		}
	}
	private static class GoogleFormSpan extends DivSpan {
		private String url = null;

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUrl() {
			return url;
		}
	}
	private static class CustomURLSpan extends URLSpan {
		public CustomURLSpan(String url) {
			super(url);
		}
	}
	private static class IFrameSpan extends DivSpan {
		private String url = null;

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUrl() {
			return url;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ConventionEvent) {
			ConventionEvent other = (ConventionEvent) o;
			return Objects.equals(title, other.title) &&
					Objects.equals(lecturer, other.lecturer) &&
					Objects.equals(startTime, other.startTime) &&
					Objects.equals(endTime, other.endTime) &&
					Objects.equals(type, other.type) &&
					Objects.equals(hall, other.hall) &&
					Objects.equals(serverId, other.serverId);
		}
		return false;
	}

	/**
	 * Compare all fields to see if this object contains the exact same data
	 */
	public boolean same(ConventionEvent other) {
		// equals checks all other fields except images and plain text description (but both are
		// calculated from the description)
		return this.equals(other) &&
				Objects.equals(this.id, other.id) &&
				Objects.equals(this.backgroundColor, other.backgroundColor) &&
				Objects.equals(this.textColor, other.textColor) &&
				Objects.equals(this.description, other.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, lecturer, startTime, endTime, type, hall, serverId);
	}

	public static class UserInput implements Serializable, Cloneable {
		private boolean attending;
		private Survey feedback;
		private Survey voteSurvey;
		private EventNotification eventAboutToStartNotification;
		private EventNotification eventFeedbackReminderNotification;

		public UserInput() {
			feedback = new Survey().withQuestions(
					new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_ENJOYMENT, FeedbackQuestion.AnswerType.SMILEY_3_POINTS),
					new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_LECTURER_QUALITY, FeedbackQuestion.AnswerType.SMILEY_3_POINTS),
					new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_SIMILAR_EVENTS, FeedbackQuestion.AnswerType.SMILEY_3_POINTS),
					new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_ADDITIONAL_INFO, FeedbackQuestion.AnswerType.TEXT)
			);

			eventAboutToStartNotification = new EventNotification(EventNotification.Type.AboutToStart);
			eventFeedbackReminderNotification = new EventNotification(EventNotification.Type.FeedbackReminder);
		}

		@Override
		public UserInput clone() throws CloneNotSupportedException {
			UserInput newInput = (UserInput) super.clone();
			newInput.feedback = feedback.clone();
			newInput.eventFeedbackReminderNotification = eventFeedbackReminderNotification.clone();
			newInput.eventAboutToStartNotification = eventAboutToStartNotification.clone();
			newInput.voteSurvey = voteSurvey == null ? null : voteSurvey.clone();
			return newInput;
		}

		/**
		 * Update this instance from user input loaded from file
		 *
		 * @param other the de-serialized user input
		 */
		public void updateFrom(UserInput other) {
			if (other == null) {
				return;
			}
			attending = other.attending;
			feedback.updateFrom(other.feedback);
			voteSurvey = other.voteSurvey;
			if (voteSurvey != null) {
				voteSurvey.updateFrom(voteSurvey); // Convert question format if necessary
			}
			eventAboutToStartNotification = other.eventAboutToStartNotification;
			eventFeedbackReminderNotification = other.eventFeedbackReminderNotification;
		}

		public boolean isAttending() {
			return attending;
		}

		public void setAttending(boolean attending) {
			this.attending = attending;
		}

		public UserInput withAttending(boolean attending) {
			setAttending(attending);
			return this;
		}

		public Survey getFeedback() {
			return feedback;
		}

		public EventNotification getEventAboutToStartNotification() {
			return eventAboutToStartNotification;
		}

		public EventNotification getEventFeedbackReminderNotification() {
			return eventFeedbackReminderNotification;
		}

		public Survey getVoteSurvey() {
			return voteSurvey;
		}

		public void setVoteSurvey(Survey voteSurvey) {
			this.voteSurvey = voteSurvey;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof UserInput) {
				UserInput other = (UserInput) o;
				return Objects.equals(this.isAttending(), other.isAttending());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(attending);
		}
	}

	public enum EventLocationType {
		PHYSICAL,
		VIRTUAL;

		@StringRes
		public int getDescriptionStringId() {
			switch (this) {
				case PHYSICAL:
					return R.string.physical_event;
				case VIRTUAL:
					return R.string.virtual_event;
			}
			throw new RuntimeException("missing description for event location type " + this.toString());
		}
	}
}
