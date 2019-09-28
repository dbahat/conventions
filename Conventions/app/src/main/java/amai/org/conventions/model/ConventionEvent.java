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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import sff.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.HtmlParser;
import amai.org.conventions.utils.Objects;
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
	private String category;
	private List<String> tags;
	private int price;
	private int availableTickets = -1; // No ticket info available
	private int ticketsLimit = -1; // No ticket info available
	private Date ticketsLastModifiedDate;
	private String websiteUrl;

	public ConventionEvent() {
		images = new ArrayList<>();
		plainTextDescription = "";
		tags = new LinkedList<>();
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
		if (textColor != Convention.NO_COLOR) {
			return textColor;
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
		if (backgroundColor != Convention.NO_COLOR) {
			return backgroundColor;
		}
		int eventTypeColor = getType().getBackgroundColor();
		if (eventTypeColor != Convention.NO_COLOR) {
			return eventTypeColor;
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

	public String getCategory() {
		return category;
	}

	public ConventionEvent withCategory(String category) {
		this.category = category;
		return this;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public ConventionEvent withTags(List<String> tags) {
		setTags(tags);
		return this;
	}

	public String getTagsAsString() {
		return TextUtils.join(", ", getTags());
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getPrice() {
		return price;
	}

	public int getDiscountPrice() {
		return price >= 10 ? price - 10 : 0;
	}

	public ConventionEvent withPrice(int price) {
		setPrice(price);
		return this;
	}

	public void setAvailableTickets(int availableTickets) {
		this.availableTickets = availableTickets;
	}

	public int getAvailableTickets() {
		return availableTickets;
	}

	public ConventionEvent withAvailableTickets(int availableTickets) {
		setAvailableTickets(availableTickets);
		return this;
	}

	public void setTicketsLimit(int ticketsLimit) {
		this.ticketsLimit = ticketsLimit;
	}

	public int getTicketsLimit() {
		return ticketsLimit;
	}

	public ConventionEvent withTicketsLimit(int ticketsLimit) {
		setTicketsLimit(ticketsLimit);
		return this;
	}

	public void setTicketsLastModifiedDate(Date ticketsLastModifiedDate) {
		this.ticketsLastModifiedDate = ticketsLastModifiedDate;
	}

	public Date getTicketsLastModifiedDate() {
		return ticketsLastModifiedDate;
	}

	public ConventionEvent withTicketsLastModifiedDate(Date ticketsLastModifiedDate) {
		setTicketsLastModifiedDate(ticketsLastModifiedDate);;
		return this;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public ConventionEvent withWebsiteUrl(String websiteUrl) {
		setWebsiteUrl(websiteUrl);
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
		// Check if the event started at least 30 minutes ago
		Calendar minimumTimeOfFillingFeedback = Calendar.getInstance();
		minimumTimeOfFillingFeedback.setTime(startTime);
		minimumTimeOfFillingFeedback.add(Calendar.MINUTE, 30);

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


	public Spanned getSpannedDescription() {
		String eventDescription = this.getDescription();
		final ListTagHandler listTagHandler = new ListTagHandler();
		Spanned spanned = HtmlParser.fromHtml(eventDescription, null, new HtmlParser.TagHandler() {
			private Stack<DivSpan> spans = new Stack<>();
			@Override
			public boolean handleTag(boolean opening, String tag, Editable output, Attributes attributes) {
				listTagHandler.handleTag(opening, tag, output, null);
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
						spans.push(span);
					} else {
						DivSpan span = spans.pop();
						span.setEnd(length);
						if (span instanceof GoogleFormSpan) {
							output.setSpan(span, span.getStart(), length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					}
				} else if (opening && tag.equals("form") && attributes != null) {
					// Add the url to the google form (only if this form is inside the google form,
					// meaning the form wasn't attached to the output yet so it's still in the spans stack).
					// We go over the stack in reverse order because the current form was added last.
					GoogleFormSpan span = null;
					DivSpan currentSpan = null;
					for (ListIterator<DivSpan> iter = spans.listIterator(spans.size()); iter.hasPrevious(); currentSpan = iter.previous()) {
						if (currentSpan instanceof GoogleFormSpan) {
							span = (GoogleFormSpan) currentSpan;
							break;
						}
					}
					if (span != null) {
						String url = HtmlParser.getValue(attributes, "action");
						span.setUrl(url);
					}
				}
				return false;
			}
		});

		if (spanned == null) {
			return null;
		}

		GoogleFormSpan[] forms = spanned.getSpans(0, spanned.length(), GoogleFormSpan.class);
		for (GoogleFormSpan span : forms) {
			int spanStart = span.getStart();
			int spanEnd = span.getEnd();
			SpannableStringBuilder linkToForm = new SpannableStringBuilder();
			if (!TextUtils.isEmpty(span.getUrl())) {
				linkToForm.append("לטופס");
				linkToForm.setSpan(new FormURLSpan(span.getUrl()), 0, linkToForm.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			}
			spanned = (Spanned) TextUtils.concat(spanned.subSequence(0, spanStart), linkToForm, spanned.subSequence(spanEnd, spanned.length()));
		}
		for (URLSpan urlSpan : spanned.getSpans(0, spanned.length(), URLSpan.class)) {
			if (urlSpan.getURL().startsWith("https://docs.google.com/forms") && !(urlSpan instanceof FormURLSpan)) {
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
	private static class FormURLSpan extends URLSpan {
		public FormURLSpan(String url) {
			super(url);
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
				Objects.equals(this.description, other.description) &&
				Objects.equals(this.category, other.category) &&
				Objects.equals(this.price, other.price) &&
				Objects.equals(this.websiteUrl, other.websiteUrl) &&
				Objects.equals(this.tags, other.tags);
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
					new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_ENJOYMENT_5P, FeedbackQuestion.AnswerType.SMILEY_5_POINTS),
					new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_LECTURER_QUALITY_5P, FeedbackQuestion.AnswerType.SMILEY_5_POINTS),
					new FeedbackQuestion(FeedbackQuestion.QUESTION_ID_SIMILAR_EVENTS_5P, FeedbackQuestion.AnswerType.SMILEY_5_POINTS),
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

}
