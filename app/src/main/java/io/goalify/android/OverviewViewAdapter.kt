package io.goalify.android

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_overview_detail.view.*

class OverviewViewAdapter(private val goals: List<GoalOverview>) : RecyclerView.Adapter<OverviewViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.fragment_overview_detail, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val goal = goals[position]

        holder.nameTextView.text = goal.name
    }

    override fun getItemCount(): Int = goals.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.textViewGoalName
    }
}
