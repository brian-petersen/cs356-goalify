package io.goalify.android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.Goal

class DetailViewModel : ViewModel() {

    private val database = AppDatabase.getInstance()

    var goal: LiveData<Goal>? = null

    fun setGoal(id: Long) {
        if (goal == null) {
            goal = database?.goalDao()?.getLiveById(id)
        }
    }

    fun deleteGoal() {
        goal?.let { data ->
            data.value?.let { goal ->
                database?.goalDao()?.delete(goal)
            }
        }
    }
}
