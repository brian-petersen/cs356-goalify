package io.goalify.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.layout_overview.*

class OverviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_overview)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = OverviewViewAdapter(this, listOf(
            GoalOverview("Read Scriptures"),
            GoalOverview("Eat food")
        ))

        buttonCreate.setOnClickListener {
            startActivity(CreateActivity.newIntent(this))
        }
    }
}
