package io.goalify.android.models

import androidx.lifecycle.LiveData
import androidx.room.*
import java.sql.Date

@Entity
data class GoalDate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo val date : Date
)

@Dao
interface GoalDateDao {

    @Query("SELECT * FROM GoalDate where id = :id")
    fun getById(id: Long): LiveData<GoalDate>

    @Query("SELECT * FROM GoalDate")
    fun getAll(): LiveData<List<GoalDate>>

    @Insert
    fun create(goalDate: GoalDate): Long
}
