package io.goalify.android.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.Goal

class DetailViewModel : ViewModel() {

    private lateinit var goal: LiveData<Goal>

    fun getGoal(id: Int): LiveData<Goal> {
        AppDatabase.getInstance()?.goalDao()?.getById(id)?.let {
            goal = it
        }

        return goal
    }
}
