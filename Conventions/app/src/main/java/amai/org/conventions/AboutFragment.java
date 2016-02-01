package amai.org.conventions;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class AboutFragment extends DialogFragment {
	private final static String linksText = "<p><a href=\"http://www.amai.org.il/\">לאתר אמא\"י</a></p>" +
			"<p><a href=\"http://2015.cami.org.il/\">לאתר הכנס</a></p>" +
			"<p><a href=\"http://2015.cami.org.il/%D7%A7%D7%A8%D7%93%D7%99%D7%98%D7%99%D7%9D/\">קרדיטים לאומנים</a></p>";

	private final static String aboutAppText = "האפליקציה פותחה עבור כנס כאמ\"י 2015.<br/>בקשות והצעות ניתן לכתוב <a href=\"market://details?id=amai.org.conventions\">בדף האפליקציה בחנות</a>.";

	private WindowManager.LayoutParams layoutParams = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

		View view = inflater.inflate(R.layout.fragment_about, container, false);

		TextView linksView = (TextView) view.findViewById(R.id.about_amai_links);
		linksView.setText(Html.fromHtml(linksText));
		linksView.setMovementMethod(LinkMovementMethod.getInstance());

		TextView aboutAppView = (TextView) view.findViewById(R.id.about_app_content);
		aboutAppView.setText(Html.fromHtml(aboutAppText));
		aboutAppView.setMovementMethod(LinkMovementMethod.getInstance());

		TextView aboutAppVersion = (TextView) view.findViewById(R.id.about_app_version);
		String versionName = ConventionsApplication.getVersionName();
		if (versionName == null || versionName.trim().isEmpty()) {
			aboutAppVersion.setVisibility(View.GONE);
		} else {
			aboutAppVersion.setText(getString(R.string.app_version, versionName));
		}

		Button dismissButton = (Button) view.findViewById(R.id.about_dismiss);
		dismissButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		return view;
	}

	public void setLocation(int x, int y, int width, int height) {
		this.layoutParams = new WindowManager.LayoutParams();
		this.layoutParams.x = x;
		this.layoutParams.y = y;
		this.layoutParams.width = width;
		this.layoutParams.height = height;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (layoutParams != null) {
			Window window = getDialog().getWindow();
			window.setGravity(Gravity.TOP | Gravity.LEFT);
			WindowManager.LayoutParams windowLayoutParams = window.getAttributes();
			windowLayoutParams.x = layoutParams.x;
			windowLayoutParams.y = layoutParams.y;
			windowLayoutParams.width = layoutParams.width;
			windowLayoutParams.height = layoutParams.height;
			window.setAttributes(windowLayoutParams);
		}
	}
}
