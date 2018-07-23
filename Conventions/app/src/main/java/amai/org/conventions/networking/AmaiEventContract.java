package amai.org.conventions.networking;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AmaiEventContract {
	@SerializedName("ID")
	private int id;

	private CategoriesText categoriesText;
	private int timetableUrlPid;
	private String title;
	private String timetableDisableUrl;
	private String content;
	private List<TimetableInfoInstance> timetableInfo;
	private String timetableBg;
	private String timetableTextColor;

	public int getId() {
		return id;
	}

	public CategoriesText getCategoriesText() {
		return categoriesText;
	}

	public int getTimetableUrlPid() {
		return timetableUrlPid;
	}

	public String getTitle() {
		return title;
	}

	public String getTimetableDisableUrl() {
		return timetableDisableUrl;
	}

	public String getContent() {
		return content;
	}

	public List<TimetableInfoInstance> getTimetableInfo() {
		return timetableInfo;
	}

	public String getTimetableBg() {
		return timetableBg;
	}

	public String getTimetableTextColor() {
		return timetableTextColor;
	}

	public AmaiEventContract setId(int id) {
		this.id = id;
		return this;
	}

	public AmaiEventContract setCategoriesText(CategoriesText categoriesText) {
		this.categoriesText = categoriesText;
		return this;
	}

	public AmaiEventContract setTimetableUrlPid(int timetableUrlPid) {
		this.timetableUrlPid = timetableUrlPid;
		return this;
	}

	public AmaiEventContract setTitle(String title) {
		this.title = title;
		return this;
	}

	public AmaiEventContract setTimetableDisableUrl(String timetableDisableUrl) {
		this.timetableDisableUrl = timetableDisableUrl;
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

	public AmaiEventContract setTimetableBg(String timetableBg) {
		this.timetableBg = timetableBg;
		return this;
	}

	public AmaiEventContract setTimetableTextColor(String timetableTextColor) {
		this.timetableTextColor = timetableTextColor;
		return this;
	}

	public static class TimetableInfoInstance {
		private String tooltip;
		private String room;
		@SerializedName("before_hour_text")
		private String beforeHourText;

		// Keeping the date values as Strings in the contract class, since transforming them into Date format requires convention information (as they
		// only contain the time portion of the date).
		private String start;
		private String end;

		public String getTooltip() {
			return tooltip;
		}

		public String getStart() {
			return start;
		}

		public String getEnd() {
			return end;
		}

		public String getRoom() {
			return room;
		}

		public String getBeforeHourText() {
			return beforeHourText;
		}

		public TimetableInfoInstance setTooltip(String tooltip) {
			this.tooltip = tooltip;
			return this;
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

		public TimetableInfoInstance setBeforeHourText(String beforeHourText) {
			this.beforeHourText = beforeHourText;
			return this;
		}
	}

	public static class CategoriesText {
		private String name;

		public String getName() {
			return name;
		}

		public CategoriesText setName(String name) {
			this.name = name;
			return this;
		}
	}
}
