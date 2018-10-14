package io.goalify.android.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.goalify.android.R
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.Goal
import io.goalify.android.viewmodels.OverviewViewModel
import kotlinx.android.synthetic.main.layout_overview.*
import java.util.*

class OverviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppDatabase.createInstance(this)

        setContentView(R.layout.layout_overview)

        val adapter = OverviewViewAdapter(this, listOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buttonCreate.setOnClickListener {
            startActivity(CreateNameActivity.newIntent(this))
        }

        val model = ViewModelProviders.of(this).get(OverviewViewModel::class.java)
        model.getGoals().observe(this, Observer {
            adapter.goals = it
            adapter.notifyDataSetChanged()
        })

        setSummaryDayLabels()

        createTestGoals(1)
    }

    private fun getSummaryDays(): List<Calendar> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        val days = mutableListOf<Calendar>()
        days.add(cal.clone() as Calendar)
        for (i in 1..4) {
            cal.add(Calendar.DATE, 1)
            days.add(cal.clone() as Calendar)
        }

        return days
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
        }
    }
}
