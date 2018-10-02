package io.goalify.android

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_overview.*

class OverviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_overview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = OverviewViewAdapter(listOf(
            GoalOverview("Read Scriptures"),
            GoalOverview("Eat food")
        ))
    }
}
