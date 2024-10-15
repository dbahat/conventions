package amai.org.conventions;

import android.content.Context;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.auth.Configuration;
import amai.org.conventions.feedback.forms.EventFeedbackForm;
import amai.org.conventions.feedback.forms.FeedbackForm;
import amai.org.conventions.model.ConventionEvent;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Halls;
import amai.org.conventions.model.ImageIdToImageResourceMapper;
import amai.org.conventions.model.SecondHandItem;
import amai.org.conventions.model.Survey;
import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.networking.EventTicketsParser;
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
    protected URL initUpdatesURL() {
        return null;
    }

    @Override
    protected URL initModelURL() {
        return null;
    }

    @Override
    protected URL initTicketsLastUpdateURL() {
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
    public URL getEventTicketsNumberURL(ConventionEvent event) {
        return null;
    }

    @Override
    public EventTicketsParser getEventTicketsParser() {
        return null;
    }

    @Override
    public URL getSecondHandFormURL(String id) {
        return null;
    }

    @Override
    public URL getSecondHandFormsURL(List<String> ids) {
        return null;
    }

    @Override
    public URL getSecondHandItemsURL(SecondHandItem.Status status) {
        return null;
    }

    @Override
    public URL getSecondHandGoToCreateFormsURL() {
        return null;
    }

    @Override
    public HttpURLConnection getUserPurchasedEventsRequest(String token) throws Exception {
        return null;
    }

    @Override
    public HttpURLConnection getUserIDRequest(String token) throws Exception {
        return null;
    }

    @Override
    public HttpURLConnection getUserQRRequest(String token, String user) throws Exception {
        return null;
    }

    @Override
    public ModelParser getModelParser() {
        return null;
    }

    @Override
    public boolean canUserLogin() {
        return false;
    }

    @Override
    public ConventionStorage getStorage() {
        return mock(ConventionStorage.class);
    }

    @Override
    public List<ConventionEvent.EventLocationType> getEventLocationTypes(ConventionEvent event) {
        //noinspection deprecation - intentional
        return event.getLocationTypes();
    }

    @Override
    public Configuration getAuthConfiguration(Context context) {
        return null;
    }
}
