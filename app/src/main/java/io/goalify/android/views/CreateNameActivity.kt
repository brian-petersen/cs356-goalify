package io.goalify.android.views

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.goalify.android.R
import kotlinx.android.synthetic.main.layout_create_name.*

class CreateNameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_create_name)

        setTitle("Create Goal")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        buttonCreateName.setOnClickListener {
            validate()
        }
    }

    private fun validate() {

        if (editTextName.text.toString().isBlank()) {
            Toast.makeText(this, "Name cannot be blank.", Toast.LENGTH_SHORT).show()
            return
        }

        startActivity(CreateAskNotificationActivity.newIntent(this, editTextName.text.toString()))
        finish()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CreateNameActivity::class.java)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }

}
