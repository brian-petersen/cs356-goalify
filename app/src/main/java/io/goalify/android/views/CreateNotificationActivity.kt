package io.goalify.android.views

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import formatTime
import io.goalify.android.R
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.Goal
import io.goalify.android.utils.afterTextChanged
import io.goalify.android.utils.itemSelected
import io.goalify.android.viewmodels.CreateViewModel
import kotlinx.android.synthetic.main.layout_create_notification.*
import java.util.*

private const val INTENT_GOAL_NAME = "goal_name"

class CreateNotificationActivity : AppCompatActivity() {

    private val database = AppDatabase.getInstance()

    private fun getModel(): CreateViewModel = ViewModelProviders.of(this).get(CreateViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_create_notification)

        setTitle("Create Goal")

        val goalName = intent.getStringExtra(INTENT_GOAL_NAME)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.frequencies,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerCreateReminderFrequency.adapter = adapter

        val model = getModel()
        model.name = goalName
        editTextCreateQuestion.setText(model.question)
        setReminderText(model.reminderHourOfDay, model.reminderMinute)
        spinnerCreateReminderFrequency.setSelection(model.reminderFrequencyIndex)

        // TODO on app rotate this are null for some reason...
        editTextCreateQuestion.afterTextChanged { model.question = it }
        spinnerCreateReminderFrequency.itemSelected { model.reminderFrequencyIndex = it }

        buttonCreateReminderTime.setOnClickListener {
            val cal = Calendar.getInstance()

            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                setReminderText(hourOfDay, minute)
                model.reminderHourOfDay = hourOfDay
                model.reminderMinute = minute
            }

            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this)
            ).show()
        }


        buttonCreateNotification.setOnClickListener {
            attemptSave()
        }
    }

    private fun attemptSave() {
        val model = getModel()

        if (model.question.isBlank()) {
            Toast.makeText(this, "Question cannot be blank.", Toast.LENGTH_SHORT).show()
            return
        }

        if (model.reminderHourOfDay < 0 || model.reminderMinute < 0) {
            Toast.makeText(this, "Choose a reminder time.", Toast.LENGTH_SHORT).show()
            return
        }
        val goalId = database?.goalDao()?.create(Goal(
            name = model.name,
            question = model.question,
            reminderHourOfDay = model.reminderHourOfDay,
            reminderMinute = model.reminderMinute,
            reminderFrequency = model.reminderFrequencyIndex
        ))

        if (goalId == null) {
            Toast.makeText(this, "Something went wrong! Unable to save goal.", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun setReminderText(hourOfDay: Int, minute: Int) {
        if (hourOfDay < 0 || minute < 0) {
            buttonCreateReminderTime.setText(R.string.reminder_prompt)
            return
        }

        buttonCreateReminderTime.text = formatTime(hourOfDay, minute)
    }

    companion object {
        fun newIntent(context: Context, goalName: String): Intent {
            val intent = Intent(context, CreateNotificationActivity::class.java)

            intent.putExtra(INTENT_GOAL_NAME, goalName)

            return intent
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }
}
