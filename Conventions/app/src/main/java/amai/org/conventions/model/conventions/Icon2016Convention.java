package amai.org.conventions.model.conventions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.EventToImageResourceIdMapper;
import amai.org.conventions.model.Hall;
import amai.org.conventions.utils.ConventionStorage;
import amai.org.conventions.utils.Dates;

public class Icon2016Convention extends SffConvention {
    @Override
    protected ConventionStorage initStorage() {
        return new ConventionStorage(this, R.raw.icon2016_convention_events, 1);
    }

    @Override
    protected Calendar initStartDate() {
	    return Dates.createDate(2016, Calendar.OCTOBER, 18);
    }

	@Override
	protected Calendar initEndDate() {
		return Dates.createDate(2016, Calendar.OCTOBER, 20);
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
        return new ArrayList<>();
    }

    @Override
    protected ConventionMap initMap() {
        return new ConventionMap();
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
    protected EventToImageResourceIdMapper initImageMapper() {
        return new EventToImageResourceIdMapper();
    }

    @Override
    protected String initFeedbackRecipient() {
        return "";
    }

    @Override
    protected URL initModelURL() {
        try {
            return new URL("https://api.sf-f.org.il/program/list_events.php?slug=icon2016");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
