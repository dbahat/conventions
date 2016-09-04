package amai.org.conventions.model.conventions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.Hall;
import amai.org.conventions.utils.ConventionStorage;

public class Icon2016Convention extends SffConvention {
    @Override
    protected ConventionStorage initStorage() {
        return new ConventionStorage(this, R.raw.cami2016_convention_events, 1);
    }

    @Override
    protected Calendar initDate() {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(2016, Calendar.OCTOBER, 18);
        return date;
    }

    @Override
    protected String initID() {
        return "Icon2016";
    }

    @Override
    protected String initDisplayName() {
        return "אייקון 2016";
    }

    @Override
    protected String initFacebookFeedPath() {
        return "/Festival.Icon/posts";
    }

    @Override
    protected List<Hall> initHalls() {
        return null;
    }

    @Override
    protected ConventionMap initMap() {
        return null;
    }

    @Override
    protected double initLongitude() {
        return 35.202425;
    }

    @Override
    protected double initLatitude() {
        return 31.786372;
    }

    @Override
    protected String initFeedbackRecipient() {
        return "";
    }

    @Override
    protected URL initModelURL() {
        try {
            return new URL("");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
