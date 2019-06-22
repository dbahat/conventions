package amai.org.conventions.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import amai.org.conventions.ConventionsApplication;
import sff.org.conventions.R;
import amai.org.conventions.utils.Log;

public class PlayServicesInstallation {
	private static final String TAG = PlayServicesInstallation.class.getCanonicalName();

	public static class CheckResult {
		private int result;
		private final boolean isCancelled;
		private final boolean isUserError;

		private CheckResult(int result, boolean isCancelled, boolean isUserError) {
			this.result = result;
			this.isCancelled = isCancelled;
			this.isUserError = isUserError;
		}
		public boolean isSuccess() {
			return result == ConnectionResult.SUCCESS;
		}
		public boolean isCancelled() {
			return isCancelled;
		}
		public boolean isUserError() {
			return isUserError;
		}
		public int getResult() {
			return result;
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 * The context sent can be any context object.
	 */
	public static CheckResult checkPlayServicesExist(final Context context, boolean ignoreCancelled) {
		if ((!ignoreCancelled) && ConventionsApplication.settings.wasPlayServicesInstallationCancelled()) {
			return new CheckResult(-1, true, false);
		}
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
		if (apiAvailability.isUserResolvableError(resultCode)) {
			return new CheckResult(resultCode, false, true);
		}

		return new CheckResult(resultCode, false, false);
	}

	public static void showInstallationDialog(final Context context, final CheckResult checkResult) {
		new AlertDialog.Builder(context)
				.setTitle(R.string.missing_play_services_dialog_title)
				.setMessage(R.string.missing_play_services_dialog_message)
				.setPositiveButton(R.string.missing_play_services_ok_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						resolvePlayServicesError(context, checkResult);
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

	public static void resolvePlayServicesError(Context context, CheckResult checkResult) {
		PendingIntent pendingIntent = GoogleApiAvailability.getInstance().getErrorResolutionPendingIntent(context, checkResult.getResult(), 0);
		if (pendingIntent == null) {
			Log.e(TAG, "resolvePlayServicesError: using default action for result " + checkResult.getResult());
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
			context.startActivity(intent);
		} else {
			try {
				pendingIntent.send(0);
			} catch (PendingIntent.CanceledException e) {
				// Nothing to do if it was already cancelled
			}
		}
	}
}
