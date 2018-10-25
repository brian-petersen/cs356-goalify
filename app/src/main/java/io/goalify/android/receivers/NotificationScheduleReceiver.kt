package io.goalify.android.receivers

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import io.goalify.android.R
import io.goalify.android.models.AppDatabase
import io.goalify.android.viewmodels.DetailViewModel
import io.goalify.android.views.DetailActivity
import java.util.*

private const val INVALID_ID = -1L

private const val INTENT_GOAL_ID = "goal_id"
private const val INTENT_GOAL_NAME = "goal_name"
private const val INTENT_GOAL_QUESTION = "goal_question"

class NotificationScheduleReceiver : BroadcastReceiver() {

    private val database = AppDatabase.getInstance()

    override fun onReceive(context: Context, intent: Intent) {
        val goalId = intent.getLongExtra(INTENT_GOAL_ID, INVALID_ID)

        if (goalId == INVALID_ID) {
            throw IllegalStateException("Invalid goal id")
        }

        val goalName = intent.getStringExtra(INTENT_GOAL_NAME)
        val goalQuestion = intent.getStringExtra(INTENT_GOAL_QUESTION)

        // TODO get this working
//        val dvm = DetailViewModel()
//        val goal = dvm.getGoal(goalId)

        // Create an explicit intent for an Activity in your app
        val intent = DetailActivity.newIntent(context, goalId)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationId = UUID.randomUUID().hashCode()
        val markCompleteIntent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
            action = "android.intent.action.GOAL_NOTI_COMPLETED"
            putExtra("goal_id", goalId)
            putExtra("noti_id", notificationId)
        }
        val markCompletePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(context, UUID.randomUUID().hashCode(), markCompleteIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val mBuilder = NotificationCompat.Builder(context, "goalify")
            .setSmallIcon(R.drawable.ic_save)
            .setContentTitle(goalName)
            .setContentText(goalQuestion)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            // TODO chang the icon to something other than save
            .addAction(R.drawable.ic_save, "Mark Complete",
                markCompletePendingIntent)

        mBuilder.setVibrate(longArrayOf(1000, 500))
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "goalify"
            val descriptionText = "Notificaitons for goalify"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("goalify", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }

        //Schedule the next notification
        database?.goalDao()?.getById(goalId).let {
            if (it == null) {
                return
            }
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, it.reminderHourOfDay)
            calendar.set(Calendar.MINUTE, it.reminderMinute)
            calendar.set(Calendar.SECOND, 0)

            when(it.reminderFrequency){
                0 -> calendar.add(Calendar.DATE, 1)
                1 -> calendar.add(Calendar.DATE, 7)
                2 -> calendar.add(Calendar.MONTH, 1)
            }
            am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }

        notificationManager.notify(notificationId, mBuilder.build())
    }

    companion object {
        fun newIntent(context: Context, goalId: Long, goalName: String, goalQuestion: String): Intent {
            val intent = Intent(context, NotificationScheduleReceiver::class.java)

            intent.putExtra(INTENT_GOAL_ID, goalId)
            intent.putExtra(INTENT_GOAL_NAME, goalName)
            intent.putExtra(INTENT_GOAL_QUESTION, goalQuestion)

            return intent
        }
    }
}
