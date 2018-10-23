package io.goalify.android.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import formatTime
import io.goalify.android.R
import io.goalify.android.viewmodels.DetailViewModel
import kotlinx.android.synthetic.main.layout_detail.*

private const val INVALID_ID = -1L

private const val INTENT_GOAL_ID = "goal_id"

class DetailActivity : AppCompatActivity() {

    private fun getModel(): DetailViewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val goalId = intent.getLongExtra(INTENT_GOAL_ID, INVALID_ID)
        if (goalId == INVALID_ID) {
            throw IllegalStateException("Invalid goal id")
        }

        setContentView(R.layout.layout_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val model = getModel()
        model.setGoal(goalId)
        model.goal?.observe(this, Observer {
            title = it.name

            if (it.hasReminder()) {
                textViewQuestion.visibility = View.VISIBLE
                textViewQuestion.text = it.question

                textViewReminder.text = formatReminderText(
                    it.reminderHourOfDay,
                    it.reminderMinute,
                    it.reminderFrequency
                )
            }
            else {
                textViewQuestion.visibility = View.GONE

                textViewReminder.text = "No reminder setup. Edit the goal to add one."
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overview, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_edit -> {
                val id = getModel().goal?.value?.id

                if (id != null) {
                    startActivity(CreateActivity.newIntent(this, id))
                }
            }

            R.id.action_delete -> {
                val model = getModel()
                model.goal?.removeObservers(this)
                model.deleteGoal()

                finish()
            }

            else -> return super.onOptionsItemSelected(item)
        }

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
