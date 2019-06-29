package amai.org.conventions;

import java.net.URL;
import java.util.Calendar;

import amai.org.conventions.feedback.forms.EventFeedbackForm;
import amai.org.conventions.feedback.forms.FeedbackForm;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Halls;
import amai.org.conventions.model.ImageIdToImageResourceMapper;
import amai.org.conventions.model.Survey;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.networking.ModelParser;
import amai.org.conventions.utils.ConventionStorage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestConvention extends Convention {
    @Override
    protected ConventionStorage initStorage() {
        return null;
    }

    @Override
    protected Calendar initStartDate() {
        return Calendar.getInstance();
    }

    @Override
    protected Calendar initEndDate() {
        return Calendar.getInstance();
    }

    @Override
    protected String initID() {
        return null;
    }

    @Override
    protected String initDisplayName() {
        return "testConvention";
    }

    @Override
    protected URL initModelURL() {
        return null;
    }

    @Override
    protected String initFacebookFeedPath() {
        return null;
    }

    @Override
    protected Halls initHalls() {
        return null;
    }

    @Override
    protected ConventionMap initMap() {
        return mock(ConventionMap.class);
    }

    @Override
    protected double initLongitude() {
        return 0;
    }

    @Override
    protected double initLatitude() {
        return 0;
    }

    @Override
    protected ImageIdToImageResourceMapper initImageMapper() {
        return null;
    }

    @Override
    protected EventFeedbackForm initEventFeedbackForm() {
        return null;
    }

    @Override
    protected FeedbackForm initConventionFeedbackForm() {
        FeedbackForm feedbackForm = mock(FeedbackForm.class);
        when(feedbackForm.canFillFeedback(any(Survey.class))).thenReturn(true);
        return feedbackForm;
    }

    @Override
    public String getGoogleSpreadsheetsApiKey() {
        return null;
    }

    @Override
    public ModelParser getModelParser() {
        return null;
    }

    @Override
    public ConventionStorage getStorage() {
        return mock(ConventionStorage.class);
    }
}
