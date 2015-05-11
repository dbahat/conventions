package amai.org.conventions.model;

import java.io.Serializable;
import java.util.Date;

public class ConventionEvent implements Serializable {
	private int id;
    private String title;
    private String lecturer;
    private Date startTime;
    private Date endTime;
    private EventType type;
    private Hall hall;

	private UserInput userInput;
//    private int peopleAttending;


	public ConventionEvent(int id) {
		this.id = id;
		userInput = new UserInput();
	}

	public int getId() {
		return id;
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

	public UserInput getUserInput() {
		return userInput;
	}

	public void setUserInput(UserInput userInput) {
		this.userInput = userInput;
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
					Objects.equals(userInput, other.userInput);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, lecturer, startTime, endTime, type, hall, userInput);
	}

	public static class UserInput implements Serializable {
		private boolean attending;
//    private Feedback feedback;


		public boolean isAttending() {
			return attending;
		}

		public void setAttending(boolean attending) {
			this.attending = attending;
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
