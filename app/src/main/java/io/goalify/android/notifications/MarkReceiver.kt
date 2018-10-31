package io.goalify.android.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.GoalDate
import normalizeDate
import java.util.*

private const val INTENT_NOTIFICATION_ID = "notification_id"
private const val INTENT_GOAL_ID = "goal_id"

class MarkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        AppDatabase.createInstance(context)
        val database = AppDatabase.getInstance()

        val notificationId = intent.getIntExtra(INTENT_NOTIFICATION_ID, 0)
        val goalId = intent.getLongExtra(INTENT_GOAL_ID, 0)

        try {
            database.goalDateDao().create(GoalDate(
                goalId = goalId,
                date = normalizeDate(Date())
            ))
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        } // swallow error where it's already marked as completed

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)

        Toast.makeText(context, "Goal marked as completed for today.", Toast.LENGTH_LONG).show()
    }

    companion object {
        fun newIntent(context: Context, notificationId: Int, goalId: Long): Intent {
            val intent = Intent(context, MarkReceiver::class.java)

            intent.putExtra(INTENT_NOTIFICATION_ID, notificationId)
            intent.putExtra(INTENT_GOAL_ID, goalId)

            return intent
        }
    }
}
