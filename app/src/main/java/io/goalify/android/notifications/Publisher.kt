package io.goalify.android.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

private const val INTENT_NOTIFICATION_ID = "notification_id"
private const val INTENT_NOTIFICATION = "notification"

class Publisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val id = intent.getIntExtra(INTENT_NOTIFICATION_ID, 0)
        val notification = intent.getParcelableExtra<Notification>(INTENT_NOTIFICATION)

        notificationManager.notify(id, notification)
    }

    companion object {
        fun newIntent(context: Context, goalId: Long): PendingIntent {
            val notificationId = UUID.randomUUID().hashCode()
            val notification = createNotification(context, notificationId, goalId)

            val notificationIntent = Intent(context, Publisher::class.java)
            notificationIntent.putExtra(INTENT_NOTIFICATION_ID, notificationId)
            notificationIntent.putExtra(INTENT_NOTIFICATION, notification)

            return PendingIntent.getBroadcast(
                context,
                UUID.randomUUID().hashCode(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}
