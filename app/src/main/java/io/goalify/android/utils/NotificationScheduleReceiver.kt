package io.goalify.android.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProviders
import io.goalify.android.R
import io.goalify.android.models.AppDatabase
import io.goalify.android.viewmodels.DetailViewModel
import io.goalify.android.views.DetailActivity
import kotlinx.android.synthetic.main.layout_detail.*
import java.util.*

private const val INVALID_ID = -1L

private const val INTENT_GOAL_ID = "goal_id"

class NotificationScheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val goalId = intent.getLongExtra(INTENT_GOAL_ID, INVALID_ID)
        if (goalId == INVALID_ID) {
            throw IllegalStateException("Invalid goal id")
        }

        val goalName = intent.getStringExtra("goal_name")
        val goalQuestion = intent.getStringExtra("goal_question")

        val dvm = DetailViewModel()
        val goal = dvm.getGoal(goalId)

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

        var mBuilder = NotificationCompat.Builder(context, "goalify")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(goalName)
            .setContentText(goalQuestion)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_save, "Mark Complete",
                markCompletePendingIntent)

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

        notificationManager.notify(notificationId, mBuilder.build())

    }
}
