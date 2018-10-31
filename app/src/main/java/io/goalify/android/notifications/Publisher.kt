package io.goalify.android.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

private const val INTENT_NOTIFICATION_ID = "notification_id"
private const val INTENT_NOTIFICATION = "notification"

class Publisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = intent.getParcelableExtra<Notification>(INTENT_NOTIFICATION)
        val id = intent.getLongExtra(INTENT_NOTIFICATION_ID, 0)

        notificationManager.notify(id.toInt(), notification)
    }

    companion object {
        fun newIntent(context: Context, goalId: Long): PendingIntent {
            val notification = createNotification(context, goalId)

            val notificationIntent = Intent(context, Publisher::class.java)
            notificationIntent.putExtra(INTENT_NOTIFICATION_ID, goalId)
            notificationIntent.putExtra(INTENT_NOTIFICATION, notification)

            return PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}
