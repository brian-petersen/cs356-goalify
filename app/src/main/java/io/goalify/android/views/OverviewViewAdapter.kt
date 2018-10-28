package io.goalify.android.views

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import getSummaryDaysAsLong
import io.goalify.android.R
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.Goal
import io.goalify.android.models.GoalDate
import kotlinx.android.synthetic.main.fragment_overview_detail.view.*

class OverviewViewAdapter(
    private val context: Context,
    var goals: List<Goal>,
    var goalDates: List<GoalDate>
) : RecyclerView.Adapter<OverviewViewAdapter.ViewHolder>() {

    private val appDatabase = AppDatabase.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.fragment_overview_detail, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val goal = goals[position]

        holder.nameTextView.text = goal.name
        holder.nameTextView.setOnClickListener {
            context.startActivity(DetailActivity.newIntent(context, goal.id))
        }

        val summaryDates = getSummaryDaysAsLong()
        val filteredGoalDates = goalDates.filter {
            it.goalId == goal.id &&
                it.date >= summaryDates.first() &&
                it.date <= summaryDates.last()
        }

        holder.checkBoxes.forEach {
            it.isChecked = false
        }

        filteredGoalDates.forEach {
            holder.checkBoxes[summaryDates.indexOf(it.date)].isChecked = true
        }

        holder.checkBoxes.forEachIndexed { index, checkBox ->
            checkBox.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val dao = appDatabase?.goalDateDao()

                    if (checkBox.isChecked) {
                        checkBox.isChecked = false
                        dao?.delete(goal.id, summaryDates[index])
                    }
                    else {
                        checkBox.isChecked = true
                        dao?.create(GoalDate(goalId = goal.id, date = summaryDates[index]))
                    }
                }

                return@setOnTouchListener false
            }
        }
    }

    override fun getItemCount(): Int = goals.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.textViewGoalName

        val checkBoxes = listOf(
            view.checkboxDay1,
            view.checkboxDay2,
            view.checkboxDay3,
            view.checkboxDay4,
            view.checkboxDay5
        )
    }
}
