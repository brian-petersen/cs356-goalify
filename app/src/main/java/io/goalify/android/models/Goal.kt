package io.goalify.android.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo val name: String = "",
    @ColumnInfo val question: String = "",
    @ColumnInfo val frequency: String = "",
    @ColumnInfo val reminderHourOfDay: Int = 0,
    @ColumnInfo val reminderMinute: Int = 0,
    @ColumnInfo val reminderFrequency: String = ""
)

@Dao
interface GoalDao {
    @Query("SELECT * FROM Goal")
    fun getAll(): LiveData<List<Goal>>

    @Insert
    fun create(goal: Goal): Long
}
