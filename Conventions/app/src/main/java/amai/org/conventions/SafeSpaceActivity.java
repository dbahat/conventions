package amai.org.conventions;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;
import java.util.Date;

import amai.org.conventions.model.conventions.Convention;
import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.BundleBuilder;
import amai.org.conventions.utils.Dates;
import amai.org.conventions.utils.Log;
import amai.org.conventions.utils.Views;
import androidx.core.text.method.LinkMovementMethodCompat;
import fi.iki.kuitsi.listtest.ListTagHandler;

public class SafeSpaceActivity extends NavigationActivity {
	private static final String PHONE_NUMBER = "0522284458";
	private static final String EMAIL = "safe.space@amai.org.il";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_safe_space);
		setBackground(ThemeAttributes.getDrawable(this, R.attr.infoActivitiesBackground));
		setToolbarTitle(getString(R.string.safe_space_title));

		// Handle edge to edge
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, false, findViewById(R.id.safe_space_scroll));
		// The bottom padding must be applied to a view inside the scroll view, otherwise the scrolling doesn't include the padding when scrolled from the last
		// textview and it has a LinkMovementMethod
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.NONE, Views.InsetType.NONE, false, findViewById(R.id.scroll_view_bottom_padding));

		TextView contentView = findViewById(R.id.content);
		contentView.setText(Html.fromHtml(getString(R.string.safe_space_content), null, new ListTagHandler()));
		contentView.setMovementMethod(LinkMovementMethodCompat.getInstance());

		Button safeSpaceButton = findViewById(R.id.safe_space_button);
		if (isBeforeOrAfterConvention()) {
			safeSpaceButton.setText(R.string.safe_space_button_mail);
		} else if (getWhatsAppIntentIfInstalled() == null) {
			safeSpaceButton.setText(R.string.safe_space_button_phone);
		}
	}

	private boolean isBeforeOrAfterConvention() {
		Date now = Dates.now();
		if (now.before(Convention.getInstance().getStartDate().getTime())) {
			return true;
		}

		Calendar afterEndTime = Calendar.getInstance();
		afterEndTime.setTime(Convention.getInstance().getEndDate().getTime());
		afterEndTime.add(Calendar.DATE, 1);

		return now.after(afterEndTime.getTime());
	}

	public Intent getWhatsAppIntentIfInstalled() {
		Intent intent = new Intent(Intent.ACTION_VIEW,
			Uri.parse("whatsapp://send?phone=" + PHONE_NUMBER));
		if (intent.resolveActivity(getPackageManager()) == null) {
			return null;
		}
		return intent;
	}

	public void onContactSafeSpaceClicked(View view) {
		if (isBeforeOrAfterConvention()) {
			openContactByMail(view);
			return;
		}

		String contactApp = "whatsapp";
		Intent intent = getWhatsAppIntentIfInstalled();

		// If whatsapp is not available, open phone
		if (intent == null) {
			intent = new Intent(Intent.ACTION_DIAL,
				Uri.parse("tel:" + PHONE_NUMBER)
			);
			contactApp = "phone";
		}

		FirebaseAnalytics
			.getInstance(view.getContext())
			.logEvent("contact_safe_space_clicked", new BundleBuilder()
				.putString("contact_app", contactApp)
				.build()
			);
		Log.i("amai", "sent contact_safe_space_clicked");

		if (intent.resolveActivity(getPackageManager()) != null) {
			try {
				this.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, getString(R.string.no_dial_activity), Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, getString(R.string.no_dial_activity), Toast.LENGTH_LONG).show();
		}
	}

	private void openContactByMail(View view) {
		FirebaseAnalytics
			.getInstance(view.getContext())
			.logEvent("contact_safe_space_clicked", new BundleBuilder()
				.putString("contact_app", "mail")
				.build()
			);
		Log.i("amai", "sent contact_safe_space_clicked");

		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri data = Uri.parse("mailto:" + EMAIL);
		intent.setData(data);
		try {
			this.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, getString(R.string.no_mail_activity), Toast.LENGTH_LONG).show();
		}
	}
}
