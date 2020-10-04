package it.ap.mytemp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText

class NewTemperatureActivity : AppCompatActivity() {
    private lateinit var editTemperatureView: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_temperature)
        editTemperatureView = findViewById(R.id.edit_temperature)

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editTemperatureView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val temperature = editTemperatureView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, temperature)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}