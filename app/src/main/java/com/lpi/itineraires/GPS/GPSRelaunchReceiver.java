package com.lpi.itineraires.GPS;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lpi.itineraires.utils.Preferences;
import com.lpi.itineraires.utils.Report;

import java.util.Calendar;

public class GPSRelaunchReceiver extends BroadcastReceiver
{
	private static final String MESSAGE = GPSRelaunchReceiver.class.getCanonicalName() + ".Restart service";

	public static void restartService(final Context context)
	{
//		Report.getInstance(context).log(Report.NIVEAU.DEBUG, "GPSRelaunchReceiver: envoi d'une intent pour relancer le service GPS");
//		try
//		{
//			Intent broadcastIntent = new Intent(context, GPSRelaunchReceiver.class);
//			context.sendBroadcast(broadcastIntent);
//		} catch (Exception e)
//		{
//			Report.getInstance(context).log(Report.NIVEAU.ERROR, e);
//		}


		// Creer une alarme qui nous reveille bientot
		Calendar calSet = Calendar.getInstance();
		calSet.roll(Calendar.MILLISECOND, Preferences.getInstance(context).getGPSMinTime() * 2);


		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP,
				calSet.getTimeInMillis(),
				"GPS",
				new AlarmManager.OnAlarmListener()
				{
					@Override
					public void onAlarm()
					{
						try
						{
							Report.getInstance(context).log(Report.NIVEAU.DEBUG, "OnAlarmListener");
							GPSService.update(context);
						} catch (Exception e)
						{
							Report.getInstance(context).log(Report.NIVEAU.ERROR, e);
						}
					}
				},
				null);
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			Report.getInstance(context).log(Report.NIVEAU.DEBUG, "GPSRelaunchReceiver: onReceive " + intent.getAction());
			GPSService.update(context);
		} catch (Exception e)
		{
			Report.getInstance(context).log(Report.NIVEAU.ERROR, e);
		}
	}
}
