package amai.org.conventions.feedback.forms;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import amai.org.conventions.model.Feedback;
import amai.org.conventions.model.FeedbackQuestion;

public class FeedbackForm {
    private URL sendUrl;
    private String conventionNameEntry;
    private String deviceIdEntry;
    private String testEntry;
    private Map<Integer, String> questionIdToFormEntry = new HashMap<>();

    public URL getSendUrl() {
        return sendUrl;
    }

    public void setSendUrl(URL sendUrl) {
        this.sendUrl = sendUrl;
    }

    public FeedbackForm withSendUrl(URL sendUrl) {
        setSendUrl(sendUrl);
        return this;
    }

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

    public FeedbackForm withQuestionEntry(int questionId, String entry) {
        questionIdToFormEntry.put(questionId, entry);
        return this;
    }

    public String getQuestionEntry(int questionId) {
        return questionIdToFormEntry.get(questionId);
    }

    public boolean canFillFeedback(Feedback feedback) {
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
        List<FeedbackQuestion> questions = feedback.getQuestions();
        for (FeedbackQuestion question : questions) {
            if (getQuestionEntry(question.getQuestionId()) == null) {
                return false;
            }
        }
        return true;
    }
}
