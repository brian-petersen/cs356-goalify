package io.goalify.android.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.goalify.android.models.AppDatabase

class CreateViewModel : ViewModel() {

    var goalId: Long? = null

    val name = MutableLiveData<String>()
    val setupReminder = MutableLiveData<Boolean>()
    val question = MutableLiveData<String>()
    val reminderHourOfDay = MutableLiveData<Int>()
    val reminderMinute = MutableLiveData<Int>()
    val reminderFrequencyIndex = MutableLiveData<Int>()

    fun loadBlankGoal() {
        name.value = ""
        setupReminder.value = true
        question.value = ""
        reminderHourOfDay.value = -1
        reminderMinute.value = -1
        reminderFrequencyIndex.value = 0
    }

    fun loadGoal(id: Long) {
        AppDatabase.getInstance()?.goalDao()?.getById(id).let {
            goalId = id

            name.value = it?.name
            setupReminder.value = it?.hasReminder()
            question.value = it?.question
            reminderHourOfDay.value = it?.reminderHourOfDay
            reminderMinute.value = it?.reminderMinute
            reminderFrequencyIndex.value = it?.reminderFrequency
        }
    }
}
