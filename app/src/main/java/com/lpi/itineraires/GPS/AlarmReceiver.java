package com.lpi.itineraires.GPS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.lpi.itineraires.utils.Report;

public class AlarmReceiver extends BroadcastReceiver
{
	/***
	 * Reception des evenements
	 *
	 * @param context
	 * @param intent
	 */
	public void onReceive(@NonNull Context context, @NonNull Intent intent)
	{
		Report.getInstance(context).log(Report.NIVEAU.DEBUG, "Alarmreceiver");
		GPSService.refresh(context);
	}
}