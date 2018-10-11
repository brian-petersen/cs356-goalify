package io.goalify.android.views

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

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
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFrequency.adapter = adapter
        spinnerReminderFrequency.adapter = adapter

        val model = getModel()
        editTextName.setText(model.name)
        editTextQuestion.setText(model.question)
        spinnerFrequency.setSelection(model.frequencyIndex)
        setReminderText(model.reminderHourOfDay, model.reminderMinute)
        spinnerReminderFrequency.setSelection(model.reminderFrequencyIndex)

        // TODO on app rotate this are null for some reason...
        editTextName.afterTextChanged { model.name = it }
        editTextQuestion.afterTextChanged { model.question = it }
        spinnerFrequency.itemSelected { model.frequencyIndex = it }
        spinnerReminderFrequency.itemSelected { model.reminderFrequencyIndex = it }

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
            frequency =  model.frequencyIndex,
            reminderHourOfDay = model.reminderHourOfDay,
            reminderMinute = model.reminderMinute,
            reminderFrequency = model.reminderFrequencyIndex
        ))

        if (goalId == null) {
            Toast.makeText(this, "Something went wrong! Unable to save goal.", Toast.LENGTH_SHORT).show()
            return
        }

        startActivity(DetailActivity.newIntent(this, goalId))
        finish()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CreateActivity::class.java)
        }
    }
}
