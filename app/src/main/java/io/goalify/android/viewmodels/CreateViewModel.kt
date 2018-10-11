package io.goalify.android.viewmodels

import androidx.lifecycle.ViewModel

class CreateViewModel : ViewModel() {
    var name: String = ""
    var question: String = ""
    var frequencyIndex: Int = 0
    var reminderHourOfDay: Int = -1
    var reminderMinute: Int = -1
    var reminderFrequencyIndex: Int = 0
}
