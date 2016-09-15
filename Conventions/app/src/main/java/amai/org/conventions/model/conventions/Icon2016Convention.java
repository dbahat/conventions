package amai.org.conventions.model.conventions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import sff.org.conventions.R;
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
        return Arrays.asList(
		        new Hall().withName("סינמטק 1").withOrder(1),
		        new Hall().withName("סינמטק 2").withOrder(2),
		        new Hall().withName("אשכול 1").withOrder(3),
		        new Hall().withName("אשכול 2").withOrder(4),
		        new Hall().withName("אשכול 3").withOrder(5),
		        new Hall().withName("אשכול 4").withOrder(6),
		        new Hall().withName("אשכול 5").withOrder(7),
		        new Hall().withName("אשכול 6").withOrder(8),
		        new Hall().withName("חדר סדנאות 1").withOrder(9),
		        new Hall().withName("חדר סדנאות 2").withOrder(10),
		        new Hall().withName("ארועים מיוחדים").withOrder(11),
		        new Hall().withName("עירוני 1").withOrder(12),
		        new Hall().withName("עירוני 2").withOrder(13),
		        new Hall().withName("עירוני 3").withOrder(14),
		        new Hall().withName("עירוני 4").withOrder(15),
		        new Hall().withName("עירוני 5").withOrder(16),
		        new Hall().withName("עירוני 6").withOrder(17),
		        new Hall().withName("עירוני 7").withOrder(18),
		        new Hall().withName("עירוני 8").withOrder(19),
		        new Hall().withName("עירוני 9").withOrder(20),
		        new Hall().withName("עירוני 10").withOrder(21),
		        new Hall().withName("עירוני 11").withOrder(22),
		        new Hall().withName("עירוני 12").withOrder(23)
        );
    }

    @Override
    protected ConventionMap initMap() {
        return new ConventionMap();
    }

    @Override
    protected double initLongitude() {
        return 34.7845003;
    }

    @Override
    protected double initLatitude() {
        return 32.0707265;
    }

    @Override
    protected EventToImageResourceIdMapper initImageMapper() {
	    EventToImageResourceIdMapper imageMapper = new EventToImageResourceIdMapper();

	    imageMapper.addMapping(EventToImageResourceIdMapper.EVENT_GENERIC, R.drawable.icon2016_event_background);

	    return imageMapper;
    }

    @Override
    protected String initFeedbackRecipient() {
        return "feedback@iconfestival.org.il";
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
