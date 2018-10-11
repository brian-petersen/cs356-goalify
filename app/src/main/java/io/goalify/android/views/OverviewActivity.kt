package io.goalify.android.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.goalify.android.R
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.Goal
import io.goalify.android.viewmodels.OverviewViewModel
import kotlinx.android.synthetic.main.layout_overview.*

class OverviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppDatabase.createInstance(this)

        setContentView(R.layout.layout_overview)

        val adapter = OverviewViewAdapter(this, listOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buttonCreate.setOnClickListener {
            startActivity(CreateActivity.newIntent(this))
        }

        val model = ViewModelProviders.of(this).get(OverviewViewModel::class.java)
        model.getGoals().observe(this, Observer<List<Goal>>{
            adapter.goals = it
            adapter.notifyDataSetChanged()
        })
    }
}
