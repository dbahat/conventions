package amai.org.conventions.notifications;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import amai.org.conventions.ConventionsApplication;
import amai.org.conventions.utils.Log;


public class RescheduleAlarmsReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED.equals(intent.getAction())) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
				AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				if (alarmManager.canScheduleExactAlarms()) {
					ConventionsApplication.restoreAlarmConfiguration();
				}
			}
		} else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			// Restore alarms even if they are not exact
			ConventionsApplication.restoreAlarmConfiguration();
		}
	}
}
