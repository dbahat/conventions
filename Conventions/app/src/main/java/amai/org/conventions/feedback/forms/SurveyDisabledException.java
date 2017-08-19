package amai.org.conventions.feedback.forms;

public class SurveyDisabledException extends RuntimeException {
	private String disabledErrorMessage;

	public SurveyDisabledException() { super(); }

	public SurveyDisabledException(String disabledErrorMessage) {
		super();
		this.disabledErrorMessage = disabledErrorMessage;
	}

	public String getDisabledErrorMessage() {
		return disabledErrorMessage;
	}
}
