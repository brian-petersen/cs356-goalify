package io.goalify.android.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.GoalDate
import java.util.*


private const val INVALID_ID = -1L

private const val INTENT_GOAL_ID = "goal_id"

class NotificationBroadcastReceiver : BroadcastReceiver() {

    private val database = AppDatabase.getInstance()

    override fun onReceive(context: Context, intent: Intent) {

        // Dismiss the notification
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

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val goalDate: GoalDate
        // TODO: Mark today's date as having completed the goal
        goalDate = GoalDate (
            goalId = goalId,
            date = cal.timeInMillis
        )

        val goalDateId = database?.goalDateDao()?.create(goalDate)

        if (goalDateId == null) {
            Toast.makeText(context, "Something went wrong! Unable to mark goal as completed.", Toast.LENGTH_SHORT).show()
            return
        } else {
            Toast.makeText(context, "Goal marked as completed for today.", Toast.LENGTH_SHORT).show()
        }
        //Toast.makeText(context, "Placeholder toast. Replace this with code checking today off for goal id: " + goalId + ".", Toast.LENGTH_LONG).show()
    }
}
