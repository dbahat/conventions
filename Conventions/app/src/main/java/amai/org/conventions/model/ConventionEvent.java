package amai.org.conventions.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Objects;

public class ConventionEvent implements Serializable {
	private String id;
    private int serverId;
    private String title;
    private String lecturer;
    private Date startTime;
    private Date endTime;
    private EventType type;
    private Hall hall;
	private List<Integer> images;

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

    private String description;

	private UserInput userInput;
//    private int peopleAttending;


	public ConventionEvent() {
		images = new ArrayList<>();
		userInput = new UserInput();
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
        return userInput.isAttending();
    }

    public void setAttending(boolean attending) {
        this.userInput.setAttending(attending);
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

	public List<Integer> getImages() {
		return images;
	}

	public void setImages(List<Integer> images) {
		this.images = images;
	}

	public ConventionEvent withImages(List<Integer> images) {
		setImages(images);
		return this;
	}

	public UserInput getUserInput() {
		return userInput;
	}

	public void setUserInput(UserInput userInput) {
		this.userInput = userInput;
	}

	public boolean hasStarted() {
		return startTime.before(Dates.now());
	}

	public boolean hasEnded() {
		return endTime.before(Dates.now());
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
					Objects.equals(userInput, other.userInput) &&
                    Objects.equals(serverId, other.serverId);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, lecturer, startTime, endTime, type, hall, userInput, serverId);
	}

	public static class UserInput implements Serializable {
		private boolean attending;
        private Feedback feedback;

		public UserInput() {
			feedback = new Feedback().withQuestions(
					new Feedback.Question(R.string.question_enjoyment, Feedback.Question.AnswerType.SMILEY_3_POINTS),
					new Feedback.Question(R.string.question_lecturer_quality, Feedback.Question.AnswerType.SMILEY_3_POINTS),
					new Feedback.Question(R.string.question_similar_events, Feedback.Question.AnswerType.SMILEY_3_POINTS),
					new Feedback.Question(R.string.question_additional_info, Feedback.Question.AnswerType.TEXT)
			);
		}

		public boolean isAttending() {
			return attending;
		}

		public void setAttending(boolean attending) {
			this.attending = attending;
		}

		public Feedback getFeedback() {
			return feedback;
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
