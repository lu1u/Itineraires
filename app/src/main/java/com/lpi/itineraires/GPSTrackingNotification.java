package com.lpi.itineraires;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Helper class for showing and canceling gpstracking
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class GPSTrackingNotification
{
	/**
	 * The unique identifier for this type of notification.
	 */
	private static final String NOTIFICATION_TAG = "GPSTracking";

	/**
	 * Shows the notification, or updates a previously shown notification of
	 * this type, with the given parameters.
	 * <p>
	 * TODO: Customize this method's arguments to present relevant content in
	 * the notification.
	 * <p>
	 * TODO: Customize the contents of this method to tweak the behavior and
	 * presentation of gpstracking notifications. Make
	 * sure to follow the
	 * <a href="https://developer.android.com/design/patterns/notifications.html">
	 * Notification design guidelines</a> when doing so.
	 *
	 * @see #cancel(Context)
	 */
	public static void notify(final Context context,
	                          final String exampleString, final int number)
	{
//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder( context);
//		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//
//		inboxStyle.addLine("message");
//		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
//		Notification notification;
//		notification = mBuilder.setSmallIcon(R.drawable.ic_check).setTicker("titre").setWhen(0)
//				.setAutoCancel(true)
//				.setContentTitle("titre")
//				.setContentIntent(pendingIntent)
//				.setStyle(inboxStyle)
//				.setSmallIcon(R.drawable.ic_notification)
//				//.setLargeIcon(R.drawable.ic_check)
//				.setContentText("content text")
//				.build();
//
//		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//		notificationManager.notify(0, notification);
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	private static void notify(final Context context, final Notification notification)
	{
		final NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR)
		{
			nm.notify(NOTIFICATION_TAG, 0, notification);
		}
		else
		{
			nm.notify(NOTIFICATION_TAG.hashCode(), notification);
		}
	}

	/**
	 * Cancels any notifications of this type previously shown using
	 * {@link #notify(Context, String, int)}.
	 */
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public static void cancel(final Context context)
	{
		final NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR)
		{
			nm.cancel(NOTIFICATION_TAG, 0);
		}
		else
		{
			nm.cancel(NOTIFICATION_TAG.hashCode());
		}
	}
}
