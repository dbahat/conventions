package amai.org.conventions.notifications;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.R;

public class PlayServicesInstallation {
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	public static boolean checkPlayServicesExist(final Context context, boolean showDialog) {
		if (ConventionsApplication.settings.wasPlayServicesInstallationCancelled()) {
			return false;
		}
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
		if (showDialog && apiAvailability.isUserResolvableError(resultCode)) {
			new AlertDialog.Builder(context)
					.setTitle(R.string.missing_play_services_dialog_title)
					.setMessage(R.string.missing_play_services_dialog_message)
					.setPositiveButton(R.string.missing_play_services_ok_button, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							installPlayServices(context);
							dialogInterface.dismiss();
						}
					})
					.setNegativeButton(R.string.missing_play_services_cancel_button, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							ConventionsApplication.settings.setPlayServicesInstallationCancelled();
							dialogInterface.dismiss();
						}
					})
					.setNeutralButton(R.string.missing_play_services_neutral_button, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
						}
					})
					.setCancelable(true)
					.create()
					.show();
		}

		return resultCode == ConnectionResult.SUCCESS;
	}

	public static void installPlayServices(Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
		context.startActivity(intent);
	}
}
