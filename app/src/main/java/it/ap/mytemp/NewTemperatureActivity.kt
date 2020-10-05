package it.ap.mytemp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_temperature.*

class NewTemperatureActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_temperature)

        button_save.setOnClickListener {
            val replyIntent = Intent()
            if (edit_temperature.text!!.isEmpty()) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
                Toast.makeText(
                    applicationContext,
                    R.string.empty_not_saved,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val temperature = edit_temperature.text.toString().toDouble()
                val notes = edit_other_notes.text.toString()
                val cough = cough_check.isChecked
                val cold = cold_check.isChecked
                if (temperature > 32 && temperature.toDouble() < 41) {
                    replyIntent.putExtra(TEMP, temperature)
                    replyIntent.putExtra(COUGH, cough)
                    replyIntent.putExtra(COLD, cold)
                    replyIntent.putExtra(NOTES, notes)
                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                } else {
                    Toast.makeText(
                        applicationContext,
                        R.string.dead_message,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }
        }
    }

    companion object {
        const val TEMP = "it.ap.mytemp.temperaturelistsql.TEMP"
        const val COUGH = "it.ap.mytemp.temperaturelistsql.COUGH"
        const val COLD = "it.ap.mytemp.temperaturelistsql.COLD"
        const val NOTES = "it.ap.mytemp.temperaturelistsql.NOTES"
    }
}