package io.goalify.android.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

private const val INVALID_ID = -1L

private const val INTENT_GOAL_ID = "goal_id"

class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        //Dismiss the notification
        val notiId = intent.getIntExtra("noti_id", -1)
        if (notiId == -1){
            throw IllegalStateException("Invalid Notification Id")
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notiId)

        val goalId = intent.getLongExtra(INTENT_GOAL_ID, INVALID_ID)
        if (goalId == INVALID_ID) {
            throw IllegalStateException("Invalid goal id")
        }

        //TODO: Mark today's date as having completed the goal
        Toast.makeText(context, "Placeholder toast. Replace this with code checking today off for goal id: " + goalId + ".", Toast.LENGTH_LONG).show()
    }
}
