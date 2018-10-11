package io.goalify.android.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.goalify.android.R
import io.goalify.android.models.Goal
import kotlinx.android.synthetic.main.fragment_overview_detail.view.*

class OverviewViewAdapter(
    private val context: Context,
    var goals: List<Goal>
) : RecyclerView.Adapter<OverviewViewAdapter.ViewHolder>() {

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
            context.startActivity(DetailActivity.newIntent(context))
        }
    }

    override fun getItemCount(): Int = goals.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.textViewGoalName
    }
}
