package amai.org.conventions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;

import amai.org.conventions.navigation.NavigationActivity;
import sff.org.conventions.R;


public class IconKidsActivity extends NavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentInContentContainer(R.layout.activity_icon_kids);
        setToolbarTitle(getString(R.string.icon_kids_title));
    }

    public void onBuyTicketsClicked(View view) {
        ConventionsApplication.sendTrackingEvent(new HitBuilders.EventBuilder()
                .setCategory("IconKids")
                .setAction("OpenWebsiteClicked")
                .build());

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cinema.co.il/news/new.aspx?0r9VQ=MHD")));
    }
}
