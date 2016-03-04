package amai.org.conventions.model;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.ThemeAttributes;
import amai.org.conventions.networking.ModelParser;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Objects;

public class ConventionEvent implements Serializable {
	private static final String TAG = ConventionEvent.class.getCanonicalName();

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

	public ConventionEvent() {
		images = new ArrayList<>();
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
		if (textColor != ModelParser.NO_COLOR) {
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
    }

    public ConventionEvent withDescription(String description) {
        setDescription(description);
        return this;
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
		if (backgroundColor != ModelParser.NO_COLOR) {
			return backgroundColor;
		}
		return getType().getBackgroundColor();
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

	public List<String> getImages() {
		return images;
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

	@Override
	public int hashCode() {
		return Objects.hash(title, lecturer, startTime, endTime, type, hall, serverId);
	}

	public static class UserInput implements Serializable, Cloneable {
		private boolean attending;
        private Feedback feedback;
		private EventNotification eventAboutToStartNotification;
		private EventNotification eventFeedbackReminderNotification;

		public UserInput() {
			feedback = new Feedback().withQuestions(
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
			return newInput;
		}

		/**
		 * Update this instance from user input loaded from file
		 * @param other the deserialized user input
		 */
		public void updateFrom(UserInput other) {
			if (other == null) {
				return;
			}
			attending = other.attending;
			feedback.updateFrom(other.feedback);
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

		public Feedback getFeedback() {
			return feedback;
		}

		public EventNotification getEventAboutToStartNotification() {
			return eventAboutToStartNotification;
		}

		public EventNotification getEventFeedbackReminderNotification() {
			return eventFeedbackReminderNotification;
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
