package io.goalify.android.views

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import io.goalify.android.R
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.Goal
import io.goalify.android.utils.afterTextChanged
import io.goalify.android.utils.itemSelected
import io.goalify.android.viewmodels.CreateViewModel
import kotlinx.android.synthetic.main.layout_create.*
import java.text.SimpleDateFormat
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
        editTextName.afterTextChanged { model.question = it }
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

        val cal = Calendar.getInstance()

        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        cal.set(Calendar.MINUTE, minute)

        buttonReminderTime.text = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(cal.time)
    }

    private fun attemptSave() {
        val model = getModel()

        if (model.name.isBlank()) {
            return
        }

        if (model.question.isBlank()) {
            return
        }

        if (model.reminderHourOfDay < 0 || model.reminderMinute < 0) {
            return
        }

        database?.goalDao()?.create(Goal(
            name = model.name,
            question = model.question,
            frequency =  model.frequencyIndex,
            reminderHourOfDay = model.reminderHourOfDay,
            reminderMinute = model.reminderMinute,
            reminderFrequency = model.reminderFrequencyIndex
        ))

        onBackPressed()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CreateActivity::class.java)
        }
    }
}
