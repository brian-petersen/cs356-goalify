package io.goalify.android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.Goal
import io.goalify.android.models.GoalDate

class OverviewViewModel : ViewModel() {

    private lateinit var goals: LiveData<List<Goal>>
    private lateinit var goalDates: LiveData<List<GoalDate>>

    fun getGoals(): LiveData<List<Goal>> {
        AppDatabase.getInstance().goalDao()?.getLiveAll()?.let {
            goals = it
        }

        return goals
    }

    fun getGoalDates(): LiveData<List<GoalDate>> {
        AppDatabase.getInstance().goalDateDao()?.getLiveAll()?.let {
            goalDates = it
        }

        return goalDates
    }
}
