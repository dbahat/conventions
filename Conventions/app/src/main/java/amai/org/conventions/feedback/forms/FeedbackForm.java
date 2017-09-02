package amai.org.conventions.feedback.forms;

import amai.org.conventions.model.Survey;

public class FeedbackForm extends SurveyForm {
    private String conventionNameEntry;
    private String deviceIdEntry;
    private String testEntry;

    public String getConventionNameEntry() {
        return conventionNameEntry;
    }

    public void setConventionNameEntry(String conventionNameEntry) {
        this.conventionNameEntry = conventionNameEntry;
    }

    public FeedbackForm withConventionNameEntry(String conventionNameEntry) {
        setConventionNameEntry(conventionNameEntry);
        return this;
    }

    public String getDeviceIdEntry() {
        return deviceIdEntry;
    }

    public void setDeviceIdEntry(String deviceIdEntry) {
        this.deviceIdEntry = deviceIdEntry;
    }

    public FeedbackForm withDeviceIdEntry(String deviceIdEntry) {
        setDeviceIdEntry(deviceIdEntry);
        return this;
    }

    public String getTestEntry() {
        return testEntry;
    }

    public void setTestEntry(String testEntry) {
        this.testEntry = testEntry;
    }

    public FeedbackForm withTestEntry(String testEntry) {
        setTestEntry(testEntry);
        return this;
    }

    public boolean canFillFeedback(Survey feedback) {
        if (getSendUrl() == null) {
            return false;
        }
        if (getConventionNameEntry() == null) {
            return false;
        }
        if (getDeviceIdEntry() == null) {
            return false;
        }
        if (getTestEntry() == null) {
            return false;
        }
        return super.canFillFeedback(feedback);
    }
}
