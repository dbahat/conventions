package amai.org.conventions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import amai.org.conventions.navigation.NavigationActivity;
import fi.iki.kuitsi.listtest.ListTagHandler;

public class AccessibilityActivity extends NavigationActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentInContentContainer(R.layout.activity_accessability);
        setToolbarTitle(getString(R.string.accessibility));

        Uri intentData = getIntent().getData();
        if (intentData != null && intentData.getHost() != null) {
            switch (intentData.getHost()) {
                case "open-accessibility":
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
            }
        }

        TextView webContentContainer = findViewById(R.id.web_content);
        if (webContentContainer != null) {
            webContentContainer.setText(Html.fromHtml(getString(R.string.accessibility_content), null, new ListTagHandler()));
            webContentContainer.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
