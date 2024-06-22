package amai.org.conventions.networking;

import java.util.List;

public class AmaiEventContract {
	private int id;
	private String category;
	private String title;
	private String content;
	private List<TimetableInfoInstance> timetableInfo;
	private List<String> tags;

	public int getId() {
		return id;
	}

	public String getCategory() {
		return category;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public List<TimetableInfoInstance> getTimetableInfo() {
		return timetableInfo;
	}

	public List<String> getTags() {
		return tags;
	}

	public AmaiEventContract setId(int id) {
		this.id = id;
		return this;
	}

	public AmaiEventContract setCategory(String category) {
		this.category = category;
		return this;
	}

	public AmaiEventContract setTitle(String title) {
		this.title = title;
		return this;
	}

	public AmaiEventContract setContent(String content) {
		this.content = content;
		return this;
	}

	public AmaiEventContract setTimetableInfo(List<TimetableInfoInstance> timetableInfo) {
		this.timetableInfo = timetableInfo;
		return this;
	}

	public AmaiEventContract setTags(List<String> tags) {
		this.tags = tags;
		return this;
	}

	public static class TimetableInfoInstance {
		private String room;
		// Keeping the date values as Strings in the contract class, since transforming them into Date format requires convention information (as they
		// only contain the time portion of the date).
		private String start;
		private String end;
		private boolean hidden;
		private String lecturer;
		private String subtitle;

		public String getStart() {
			return start;
		}

		public String getEnd() {
			return end;
		}

		public String getRoom() {
			return room;
		}

		public String getLecturer() {
			return lecturer;
		}

		public String getSubtitle() {
			return subtitle;
		}

		public boolean isHidden() {
			return hidden;
		}

		public TimetableInfoInstance setStart(String start) {
			this.start = start;
			return this;
		}

		public TimetableInfoInstance setEnd(String end) {
			this.end = end;
			return this;
		}

		public TimetableInfoInstance setRoom(String room) {
			this.room = room;
			return this;
		}

		public TimetableInfoInstance setLecturer(String lecturer) {
			this.lecturer = lecturer;
			return this;
		}

		public TimetableInfoInstance setSubtitle(String subtitle) {
			this.subtitle = subtitle;
			return this;
		}

		public TimetableInfoInstance setHidden(boolean hidden) {
			this.hidden = hidden;
			return this;
		}
	}
}
