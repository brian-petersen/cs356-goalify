package io.goalify.android.views

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import getSummaryDays
import io.goalify.android.R
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.Goal
import io.goalify.android.models.GoalDate
import io.goalify.android.viewmodels.OverviewViewModel
import kotlinx.android.synthetic.main.layout_overview.*
import normalizeDate
import java.util.*

class OverviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        AppDatabase.createInstance(this)

        setContentView(R.layout.layout_overview)

        val adapter = OverviewViewAdapter(this, listOf(), listOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buttonCreate.setOnClickListener {
            startActivity(CreateActivity.newIntent(this))
        }

        val model = ViewModelProviders.of(this).get(OverviewViewModel::class.java)
        model.getGoals().observe(this, Observer {
            adapter.goals = it
            adapter.notifyDataSetChanged()

            textViewNoGoals.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE

            layoutDays.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            recyclerView.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
        })

        model.getGoalDates().observe(this, Observer {
            adapter.goalDates = it
            adapter.notifyDataSetChanged()
        })

        setSummaryDayLabels()

        //createTestGoals(1)
    }

    private fun setSummaryDayLabels() {
        val daysOfWeek = listOf(
            "SUN",
            "MON",
            "TUE",
            "WED",
            "THU",
            "FRI",
            "SAT"
        )

        val textViewWeekDays = listOf(
            textViewDayOfWeek1,
            textViewDayOfWeek2,
            textViewDayOfWeek3,
            textViewDayOfWeek4,
            textViewDayOfWeek5
        )

        val textViewDays = listOf(
            textViewDay1,
            textViewDay2,
            textViewDay3,
            textViewDay4,
            textViewDay5
        )

        val days = getSummaryDays()

        for (i in 0..4) {
            textViewWeekDays[i].text = daysOfWeek[days[i].get(Calendar.DAY_OF_WEEK) - 1]
            textViewDays[i].text = days[i].get(Calendar.DATE).toString()
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "goalify"
            val descriptionText = "Notifications for goalify"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("goalify", name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Suppress("unused")
    private fun createTestGoals(count: Int) {
        for (i in 1..count) {
            AppDatabase.getInstance()?.goalDao()?.create(
                Goal(
                    name = "Test $i",
                    question = "Test $i?",
                    reminderHourOfDay = 15,
                    reminderMinute = 0,
                    reminderFrequency = 0
                )
            )

            AppDatabase.getInstance()?.goalDateDao()?.create(GoalDate(goalId = 1, date = normalizeDate(Date())))

            AppDatabase.getInstance()?.goalDao()?.create(
                Goal(
                    name = "Test $i Small"
                )
            )
        }
    }
}
