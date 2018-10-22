package io.goalify.android.viewmodels

import androidx.lifecycle.ViewModel

class CreateViewModel : ViewModel() {
    var name: String = ""
    var setupReminder: Boolean = true
    var question: String = ""
    var reminderHourOfDay: Int = -1
    var reminderMinute: Int = -1
    var reminderFrequencyIndex: Int = 0
}
