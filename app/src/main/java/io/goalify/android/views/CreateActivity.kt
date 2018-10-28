package io.goalify.android.views

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import formatTime
import io.goalify.android.R
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.Goal
import io.goalify.android.receivers.NotificationScheduleReceiver
import io.goalify.android.utils.afterTextChanged
import io.goalify.android.utils.itemSelected
import io.goalify.android.viewmodels.CreateViewModel
import kotlinx.android.synthetic.main.layout_create.*
import java.util.*

private const val MISSING_GOAL_ID = -1L
private const val INTENT_GOAL_ID = "goal_id"

class CreateActivity : AppCompatActivity() {

    private val database = AppDatabase.getInstance()

    private fun getModel(): CreateViewModel = ViewModelProviders.of(this).get(CreateViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_create)

        val goalId = intent.getLongExtra(INTENT_GOAL_ID, MISSING_GOAL_ID)

        title = if (goalId == MISSING_GOAL_ID) "Create Goal" else "Edit Goal"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.frequencies,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerReminderFrequency.adapter = adapter

        val model = getModel()

        if (goalId != MISSING_GOAL_ID) {
            model.loadGoal(goalId)
        }
        else {
            model.loadBlankGoal()
        }

        editTextName.afterTextChanged { model.name.value = it }
        model.name.observe(this, Observer {
            if (editTextName.text.toString() != it) {
                editTextName.setText(it)
            }
        })

        checkBoxUseReminder.setOnCheckedChangeListener { _, isChecked -> model.setupReminder.value = isChecked }
        model.setupReminder.observe(this, Observer {
            if (checkBoxUseReminder.isChecked != it) {
                checkBoxUseReminder.isChecked = it
            }

            linearLayoutReminderFields.visibility = if (it) View.VISIBLE else View.GONE
        })

        editTextQuestion.afterTextChanged { model.question.value = it }
        model.question.observe(this, Observer {
            if (editTextQuestion.text.toString() != it) {
                editTextQuestion.setText(it)
            }
        })

        buttonReminderTime.setOnClickListener {
            val cal = Calendar.getInstance()

            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                model.reminderHourOfDay.value = hourOfDay
                model.reminderMinute.value = minute
            }

            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this)
            ).show()
        }
        model.reminderHourOfDay.observe(this, Observer {
            val format = generateReminderText()
            if (buttonReminderTime.text != format) {
                buttonReminderTime.text = format
            }
        })
        model.reminderMinute.observe(this, Observer {
            val format = generateReminderText()
            if (buttonReminderTime.text != format) {
                buttonReminderTime.text = format
            }
        })

        spinnerReminderFrequency.itemSelected { model.reminderFrequencyIndex.value = it }
        model.reminderFrequencyIndex.observe(this, Observer {
            if (spinnerReminderFrequency.selectedItemPosition != it) {
                spinnerReminderFrequency.setSelection(it)
            }
        })

        buttonSave.setOnClickListener {
            attemptSave()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }

    private fun generateReminderText(): String {
        val model = getModel()

        val hourOfDay = model.reminderHourOfDay.value
        val minute = model.reminderMinute.value

        if (hourOfDay == null || minute == null || hourOfDay < 0 || minute < 0) {
            return resources.getString(R.string.reminder_prompt)
        }

        return formatTime(hourOfDay, minute)
    }

    private fun attemptSave() {
        val model = getModel()

        val name: String? = model.name.value

        if (name == null || name.isBlank()) {
            Toast.makeText(this, "Name cannot be blank.", Toast.LENGTH_SHORT).show()
            return
        }

        val goal: Goal
        if (model.setupReminder.value == true) {
            val question = model.question.value
            val hourOfDay = model.reminderHourOfDay.value
            val minute = model.reminderMinute.value
            val frequency = model.reminderFrequencyIndex.value

            if (question == null || question.isBlank()) {
                Toast.makeText(this, "Reminder question cannot be blank.", Toast.LENGTH_SHORT).show()
                return
            }

            if (hourOfDay == null || minute == null || hourOfDay < 0 || minute < 0) {
                Toast.makeText(this, "Choose a reminder time.", Toast.LENGTH_SHORT).show()
                return
            }

            if (frequency == null) {
                Toast.makeText(this, "Choose a reminder frequency.", Toast.LENGTH_SHORT).show()
                return
            }

            goal = Goal(
                name = name,
                question = question,
                reminderHourOfDay = hourOfDay,
                reminderMinute = minute,
                reminderFrequency = frequency
            )
        }
        else {
            goal = Goal(
                name = name
            )
        }

        var goalId: Long? = model.goalId

        // editing an existing goal
        if (goalId != null) {
            goal.id = goalId

            database?.goalDao()?.update(goal)
        }
        // creating a new goal
        else {
            goalId = database?.goalDao()?.create(goal)

            if (goalId == null) {
                Toast.makeText(this, "Something went wrong! Unable to save goal.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        cancelNotification(goalId)

        if (checkBoxUseReminder.isChecked) {
            setNotification(goalId)
        }

        finish()
    }

    private fun cancelNotification(goalId: Long){
        database?.goalDao()?.getById(goalId).let {
            if (it == null) {
                return
            }

            val intent = NotificationScheduleReceiver.newIntent(this, goalId, it.name, it.question)

            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val am = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            am.cancel(pendingIntent)
        }
    }

    private fun setNotification(goalId: Long) {
        database?.goalDao()?.getById(goalId).let {
            if (it == null) {
                return
            }

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, it.reminderHourOfDay)
            calendar.set(Calendar.MINUTE, it.reminderMinute)
            calendar.set(Calendar.SECOND, 0)

            val intent = NotificationScheduleReceiver.newIntent(this, goalId, it.name, it.question)

            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val am = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Notification recurrence is scheduled by the notification appearing in NotificationScheduleReceiver
            am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    companion object {
        fun newIntent(context: Context, goalId: Long = MISSING_GOAL_ID): Intent {
            val intent = Intent(context, CreateActivity::class.java)

            intent.putExtra(INTENT_GOAL_ID, goalId)

            return intent
        }
    }
}
