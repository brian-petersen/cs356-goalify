package io.goalify.android.views

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import formatTime
import io.goalify.android.R
import io.goalify.android.viewmodels.DetailViewModel
import kotlinx.android.synthetic.main.layout_detail.*

private const val INVALID_ID = -1L

private const val INTENT_GOAL_ID = "goal_id"

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val goalId = intent.getLongExtra(INTENT_GOAL_ID, INVALID_ID)
        if (goalId == INVALID_ID) {
            throw IllegalStateException("Invalid goal id")
        }

        setContentView(R.layout.layout_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val model = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        model.getGoal(goalId).observe(this, Observer {
            title = it.name
            textViewQuestion.text = it.question
            textViewReminder.text = formatReminderText(it.reminderHourOfDay, it.reminderMinute, it.reminderFrequency)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overview, menu)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }

    private fun formatReminderText(hourOfDay: Int, minute: Int, frequency: Int): String {
        val frequencyString = resources.getStringArray(R.array.frequencies)[frequency]
        val time = formatTime(hourOfDay, minute)

        return "$frequencyString @ $time"
    }

    companion object {
        fun newIntent(context: Context, goalId: Long): Intent {
            val intent = Intent(context, DetailActivity::class.java)

            intent.putExtra(INTENT_GOAL_ID, goalId)

            return intent
        }
    }
}
