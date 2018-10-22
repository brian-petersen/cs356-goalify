package io.goalify.android.views

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import formatTime
import io.goalify.android.R
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.Goal
import io.goalify.android.utils.afterTextChanged
import io.goalify.android.utils.itemSelected
import io.goalify.android.viewmodels.CreateViewModel
import kotlinx.android.synthetic.main.layout_create.*
import java.util.*

class CreateActivity : AppCompatActivity() {

    private val database = AppDatabase.getInstance()

    private fun getModel(): CreateViewModel = ViewModelProviders.of(this).get(CreateViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_create)

        setTitle("Create Goal")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.frequencies,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerReminderFrequency.adapter = adapter

        val model = getModel()
        editTextName.setText(model.name)
        editTextQuestion.setText(model.question)
        setReminderText(model.reminderHourOfDay, model.reminderMinute)
        spinnerReminderFrequency.setSelection(model.reminderFrequencyIndex)

        checkBoxUseReminder.isChecked = model.setupReminder
        linearLayoutReminderFields.visibility = if (model.setupReminder) View.VISIBLE else View.GONE

        // TODO on app rotate this are null for some reason...
        editTextName.afterTextChanged { model.name = it }
        editTextQuestion.afterTextChanged { model.question = it }
        spinnerReminderFrequency.itemSelected { model.reminderFrequencyIndex = it }

        checkBoxUseReminder.setOnCheckedChangeListener { _, isChecked ->
            model.setupReminder = isChecked
            linearLayoutReminderFields.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        buttonReminderTime.setOnClickListener {
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

        buttonSave.setOnClickListener {
            attemptSave()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }

    private fun setReminderText(hourOfDay: Int, minute: Int) {
        if (hourOfDay < 0 || minute < 0) {
            buttonReminderTime.setText(R.string.reminder_prompt)
            return
        }

        buttonReminderTime.text = formatTime(hourOfDay, minute)
    }

    private fun attemptSave() {
        val model = getModel()

        if (model.name.isBlank()) {
            Toast.makeText(this, "Name cannot be blank.", Toast.LENGTH_SHORT).show()
            return
        }

        var goalId: Long?
        if (model.setupReminder) {
            if (model.question.isBlank()) {
                Toast.makeText(this, "Reminder question cannot be blank.", Toast.LENGTH_SHORT).show()
                return
            }

            if (model.reminderHourOfDay < 0 || model.reminderMinute < 0) {
                Toast.makeText(this, "Choose a reminder time.", Toast.LENGTH_SHORT).show()
                return
            }

            goalId = database?.goalDao()?.create(Goal(
                name = model.name,
                question = model.question,
                reminderHourOfDay = model.reminderHourOfDay,
                reminderMinute = model.reminderMinute,
                reminderFrequency = model.reminderFrequencyIndex
            ))
        }
        else {
            goalId = database?.goalDao()?.create(Goal(
                name = model.name
            ))
        }

        if (goalId == null) {
            Toast.makeText(this, "Something went wrong! Unable to save goal.", Toast.LENGTH_SHORT).show()
            return
        }

        finish()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CreateActivity::class.java)
        }
    }
}
