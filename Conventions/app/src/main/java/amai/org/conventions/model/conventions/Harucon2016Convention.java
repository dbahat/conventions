package amai.org.conventions.model.conventions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import amai.org.conventions.R;
import amai.org.conventions.model.Convention;
import amai.org.conventions.model.ConventionMap;
import amai.org.conventions.model.EventToImageResourceIdMapper;
import amai.org.conventions.model.Hall;
import amai.org.conventions.utils.ConventionStorage;

public class Harucon2016Convention extends Convention {
	// Hall names
	private static final String MAIN_HALL_NAME = "אולם ראשי";
	private static final String AUDITORIUM_NAME = "אודיטוריום שוורץ";
	private static final String ESHKOL1_NAME = "אשכול 1";
	private static final String ESHKOL2_NAME = "אשכול 2";
	private static final String ESHKOL3_NAME = "אשכול 3";

	@Override
	protected ConventionStorage initStorage() {
		return new ConventionStorage(this, R.raw.harucon2016_convention_events);
	}

	@Override
	protected Calendar initDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		date.set(2016, Calendar.MARCH, 24);
		return date;
	}

	@Override
	protected String initID() {
		return "Harucon2016";
	}

	@Override
	protected String initDisplayName() {
		return "הארוקון 2016";
	}

	@Override
	protected String initFacebookFeedPath() {
		return "/harucon.org.il/posts";
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
		return "content@harucon.org.il";
	}

	@Override
	protected URL initModelURL() {
		try {
			return new URL("http://2016.harucon.org.il/wp-admin/admin-ajax.php?action=get_event_list");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected EventToImageResourceIdMapper initImageMapper() {
		EventToImageResourceIdMapper imageMapper = new EventToImageResourceIdMapper();
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/Where-do-I-sign-LOGO-300x189.png", R.drawable.event_musical_logo);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/11692794_1601889390062626_8995957057976976218_n-300x189.png", R.drawable.event_musical_group_name);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/01/cosplay2-300x168.jpg", R.drawable.event_cosplay2);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/01/cosplay3-300x168.jpg", R.drawable.event_cosplay3);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/harmony-screenshot-1.jpg", R.drawable.event_harmony1);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/harmony-screenshot-2.jpg", R.drawable.event_harmony2);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/harmony-screenshot-3.jpg", R.drawable.event_harmony3);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/6.-שתי-טיפות-של-דם-218x300.jpg", R.drawable.event_rmusical);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/1.-זוז-בשם-האהבה-טרילוגיית-מאב-לאב-מהי-1024x768.jpg", R.drawable.event_muv_luv);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2015/12/אלה-בן-יעקב.jpg", R.drawable.event_animation_workshop);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2015/12/הרן-יקיר-1024x716.jpg", R.drawable.event_world_end);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/6.-אל-ה-Plamo-ומעבר-לו.jpg", R.drawable.event_plamo);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/3.-כשאפולו-לבש-סייפוקו.jpg", R.drawable.event_apollo);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2015/12/ביאטריס-פיגרים.jpg", R.drawable.event_figures);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/5.-הנפלאות-הכאב-ובעיקר-הכוויות-מאחורי-הכנת-שריון.jpg", R.drawable.event_armor_pain);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2015/12/דוד-בהא.png", R.drawable.event_games_anime);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/8.-הנטאי-ותפיסת-המיניות-היפנית-1024x624.png", R.drawable.event_hentai);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/1.-קוספליי-חומרים-וטכניקות.jpg", R.drawable.event_cosplay_materials);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/toku.png", R.drawable.event_tokusetsu);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/ניבה-קרן-אור.png", R.drawable.event_bento);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2015/12/דני-פייגלמן.jpg", R.drawable.event_cosplay_leds);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2015/12/סבי-איזילוב-1024x701.jpg", R.drawable.event_manga_history);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/6.-שיעור-יפנית-למתחילים.jpg", R.drawable.event_japanese);
		imageMapper.addMapping("http://2016.harucon.org.il/wp-content/uploads/sites/7/2016/02/Crunchyroll-Logo-1-300x120.png", R.drawable.event_crunchyroll_logo);

		// Non-URL IDs
		imageMapper.addMapping(EventToImageResourceIdMapper.EVENT_GENERIC, R.drawable.harucon2016_cover);

		return imageMapper;
	}

	@Override
	protected List<Hall> initHalls() {
		Hall mainHall = new Hall().withName(MAIN_HALL_NAME).withOrder(1);
		Hall auditorium = new Hall().withName(AUDITORIUM_NAME).withOrder(2);
		Hall eshkol1 = new Hall().withName(ESHKOL1_NAME).withOrder(3);
		Hall eshkol2 = new Hall().withName(ESHKOL2_NAME).withOrder(4);
		Hall eshkol3 = new Hall().withName(ESHKOL3_NAME).withOrder(5);

		return Arrays.asList(mainHall, auditorium, eshkol1, eshkol2, eshkol3);
	}

	@Override
	protected ConventionMap initMap() {
		return new ConventionMap();
	}
}
