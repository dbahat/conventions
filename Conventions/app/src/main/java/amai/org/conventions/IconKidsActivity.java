package amai.org.conventions;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import amai.org.conventions.navigation.NavigationActivity;
import fi.iki.kuitsi.listtest.ListTagHandler;
import sff.org.conventions.R;


public class IconKidsActivity extends NavigationActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_icon_kids);
		setToolbarTitle(getString(R.string.icon_kids));

		TextView iconKidsText = findViewById(R.id.icon_kids_content);
		if (iconKidsText != null) {
			iconKidsText.setText(Html.fromHtml(getString(R.string.icon_kids_content), null, new ListTagHandler()));
			iconKidsText.setMovementMethod(LinkMovementMethod.getInstance());
		}
	}
}
