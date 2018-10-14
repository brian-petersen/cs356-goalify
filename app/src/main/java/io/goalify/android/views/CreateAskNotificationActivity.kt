package io.goalify.android.views

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import io.goalify.android.R
import io.goalify.android.models.AppDatabase
import io.goalify.android.models.Goal
import io.goalify.android.viewmodels.CreateViewModel
import kotlinx.android.synthetic.main.layout_create_asknotification.*


private const val INTENT_GOAL_NAME = "goal_name"

class CreateAskNotificationActivity : AppCompatActivity() {

    private val database = AppDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_create_asknotification)

        setTitle("Create Goal")

        val goalName = intent.getStringExtra(INTENT_GOAL_NAME)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        buttonYesNotification.setOnClickListener {
            startActivity(CreateNotificationActivity.newIntent(this, goalName))
            finish()
        }

        buttonNoNotification.setOnClickListener {
            val goalId = database?.goalDao()?.create(Goal(
                name = goalName
            ))

            if (goalId == null) {
                Toast.makeText(this, "Something went wrong! Unable to save goal.", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }

    companion object {
        fun newIntent(context: Context, goalName: String): Intent {
            val intent = Intent(context, CreateAskNotificationActivity::class.java)

            intent.putExtra(INTENT_GOAL_NAME, goalName)

            return intent
        }
    }
}
