package io.goalify.android.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.GoalDate
import normalizeDate
import java.util.*


private const val INVALID_ID = -1L

private const val INTENT_GOAL_ID = "goal_id"

class NotificationBroadcastReceiver : BroadcastReceiver() {

    private val database = AppDatabase.getInstance()

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("noti_id", -1)

        if (notificationId == -1){
            throw IllegalStateException("Invalid Notification Id")
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        val goalId = intent.getLongExtra(INTENT_GOAL_ID, INVALID_ID)
        if (goalId == INVALID_ID) {
            throw IllegalStateException("Invalid goal id")
        }

        val goalDateId = database?.goalDateDao()?.create(GoalDate(
            goalId = goalId,
            date = normalizeDate(Date())
        ))

        if (goalDateId == null) {
            Toast.makeText(context, "Something went wrong! Unable to mark goal as completed.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Goal marked as completed for today.", Toast.LENGTH_SHORT).show()
        }
    }
}
