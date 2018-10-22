package io.goalify.android.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo val name: String = "",
    @ColumnInfo val question: String = "",
    @ColumnInfo val reminderHourOfDay: Int = -1,
    @ColumnInfo val reminderMinute: Int = -1,
    @ColumnInfo val reminderFrequency: Int = -1
) {
    fun hasReminder(): Boolean = question != "" &&
        reminderHourOfDay != -1 &&
        reminderMinute != -1 &&
        reminderFrequency != -1
}

@Dao
interface GoalDao {

    @Query("SELECT * FROM Goal where id = :id")
    fun getById(id: Long): LiveData<Goal>

    @Query("SELECT * FROM Goal")
    fun getAll(): LiveData<List<Goal>>

    @Insert
    fun create(goal: Goal): Long
}
