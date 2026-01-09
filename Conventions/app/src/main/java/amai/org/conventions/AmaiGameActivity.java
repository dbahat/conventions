package amai.org.conventions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.navigation.NavigationActivity;
import amai.org.conventions.utils.Views;
import androidx.core.text.method.LinkMovementMethodCompat;
import fi.iki.kuitsi.listtest.ListTagHandler;

public class AmaiGameActivity extends NavigationActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_amai_game);
		setBackground(ThemeAttributes.getDrawable(this, R.attr.infoActivitiesBackground));
		setToolbarTitle(getString(R.string.amai_game_title));

		// Handle edge to edge
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.PADDING, false, findViewById(R.id.amai_game_scroll));
		// The bottom padding must be applied to a view inside the scroll view, otherwise the scrolling doesn't include the padding when scrolled from the last
		// textview and it has a LinkMovementMethod
		Views.registerApplyInsets(Views.InsetType.NONE, Views.InsetType.PADDING, Views.InsetType.NONE, Views.InsetType.NONE, false, findViewById(R.id.scroll_view_bottom_padding));

		TextView contentView = findViewById(R.id.content);
		contentView.setText(Html.fromHtml(getString(R.string.amai_game_content), null, new ListTagHandler()));
		contentView.setMovementMethod(LinkMovementMethodCompat.getInstance());
	}

	public void onGoToGameClicked(View view) {
		Uri uri = Uri.parse("https://game.amai.org.il:8443/");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
}
