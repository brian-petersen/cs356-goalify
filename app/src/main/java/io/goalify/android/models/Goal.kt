package io.goalify.android.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo var name: String = "",
    @ColumnInfo var question: String = "",
    @ColumnInfo var reminderHourOfDay: Int = -1,
    @ColumnInfo var reminderMinute: Int = -1,
    @ColumnInfo var reminderFrequency: Int = -1,
    @ColumnInfo var notificationId: Int = 0
) {
    fun hasReminder(): Boolean = question != "" &&
        reminderHourOfDay != -1 &&
        reminderMinute != -1 &&
        reminderFrequency != -1
}

@Dao
interface GoalDao {

    @Query("SELECT * FROM Goal WHERE id = :id")
    fun getById(id: Long): Goal

    @Query("SELECT * FROM Goal WHERE id = :id")
    fun getLiveById(id: Long): LiveData<Goal>

    @Query("SELECT * FROM Goal")
    fun getLiveAll(): LiveData<List<Goal>>

    @Insert
    fun create(goal: Goal): Long

    @Update
    fun update(goal: Goal)

    @Delete
    fun delete(goal: Goal)
}
