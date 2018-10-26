package io.goalify.android.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class GoalDate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo val goalId: Long,
    @ColumnInfo val date: Long
)

@Dao
interface GoalDateDao {

    @Query("SELECT * FROM GoalDate where goalId = :goalId")
    fun getAllByGoalId(goalId: Long): List<GoalDate>

    @Query("SELECT * FROM GoalDate")
    fun getAll(): LiveData<List<GoalDate>>

    @Insert
    fun create(goalDate: GoalDate): Long

    @Update
    fun update(goalDate: GoalDate)

    @Query("DELETE FROM GoalDate where date = :date")
    fun delete(date:Long)


}
