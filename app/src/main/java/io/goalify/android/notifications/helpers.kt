package io.goalify.android.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import io.goalify.android.R
import io.goalify.android.models.AppDatabase
import io.goalify.android.views.DetailActivity
import java.util.*

private const val channelId = "reminders"

fun createChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "Reminders", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "Reminders for goals"
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun createNotification(context: Context, goalId: Long): Notification {
    AppDatabase.createInstance(context)

    val goal = AppDatabase.getInstance().goalDao().getById(goalId)

    val intent = DetailActivity.newIntent(context, goalId)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    val markIntent = MarkReceiver.newIntent(context, goal.id)
    val markPendingIntent = PendingIntent.getBroadcast(context, 0, markIntent, 0)

    return NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_flag)
        .setContentTitle("Goal Reminder: ${goal.name}")
        .setContentText(goal.question)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .addAction(R.drawable.ic_check, "Mark Complete", markPendingIntent)
        .setVibrate(longArrayOf(1000, 500))
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .build()

//    return NotificationCompat.Builder(context, "Goalify")
//        .setSmallIcon(R.drawable.ic_flag)
//        .setContentTitle("My notification")
//        .setContentText("Hello World!")
//        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//        .setAutoCancel(true)
//        .build()
}

fun setupNotification(context: Context, goalId: Long) {
    AppDatabase.createInstance(context)

    AppDatabase.getInstance().goalDao().getById(goalId).let {
        val pendingIntent = Publisher.newIntent(context, goalId)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)

        if (it.hasReminder()) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, it.reminderHourOfDay)
            calendar.set(Calendar.MINUTE, it.reminderMinute)
            calendar.set(Calendar.SECOND, 0)

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            val interval = when (it.reminderFrequency) {
                0 -> AlarmManager.INTERVAL_DAY
                1 -> AlarmManager.INTERVAL_DAY * 7
                2 -> AlarmManager.INTERVAL_DAY * 30
                else -> AlarmManager.INTERVAL_DAY
            }

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, interval, pendingIntent)
        }
    }
}
