package io.goalify.android.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(indices = [Index("goalId", "date", unique = true)])
data class GoalDate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo val goalId: Long,
    @ColumnInfo val date: Long
)

@Dao
interface GoalDateDao {

    @Insert
    fun create(goalDate: GoalDate): Long

    @Query("SELECT * FROM GoalDate")
    fun getLiveAll(): LiveData<List<GoalDate>>

    @Query("SELECT * FROM GoalDate WHERE goalId = :goalId")
    fun getLiveAllForGoal(goalId: Long): LiveData<List<GoalDate>>

    @Query("DELETE FROM GoalDate WHERE goalId = :goalId AND date = :date")
    fun delete(goalId: Long, date: Long)
}
