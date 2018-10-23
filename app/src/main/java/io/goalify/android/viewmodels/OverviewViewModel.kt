package io.goalify.android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.Goal

class OverviewViewModel : ViewModel() {

    private lateinit var goals: LiveData<List<Goal>>

    fun getGoals(): LiveData<List<Goal>> {
        AppDatabase.getInstance()?.goalDao()?.getLiveAll()?.let {
            goals = it
        }

        return goals
    }
}
