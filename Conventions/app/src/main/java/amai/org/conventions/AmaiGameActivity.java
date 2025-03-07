package amai.org.conventions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import amai.org.conventions.navigation.NavigationActivity;
import fi.iki.kuitsi.listtest.ListTagHandler;

public class AmaiGameActivity extends NavigationActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_amai_game);
		setBackground(ThemeAttributes.getDrawable(this, R.attr.infoActivitiesBackground));
		setToolbarTitle(getString(R.string.amai_game_title));

		TextView contentView = findViewById(R.id.content);
		contentView.setText(Html.fromHtml(getString(R.string.amai_game_content), null, new ListTagHandler()));
		contentView.setMovementMethod(LinkMovementMethod.getInstance());
	}

	public void onGoToGameClicked(View view) {
		Uri uri = Uri.parse("https://game.amai.org.il/");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
}
