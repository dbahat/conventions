package amai.org.conventions;

import android.os.Bundle;

import amai.org.conventions.navigation.NavigationActivity;

public class HomeActivity extends NavigationActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentInContentContainer(R.layout.activity_home, false, false);

		setToolbarAndContentContainerBackground(getResources().getDrawable(R.drawable.harucon2017_home_background));
	}
}
