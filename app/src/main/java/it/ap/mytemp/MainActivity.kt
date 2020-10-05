package it.ap.mytemp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import it.ap.mytemp.data.models.Temperature
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    private val newTemperatureActivityRequestCode = 0
    private lateinit var temperatureViewModel: TemperatureViewModel
    private lateinit var firebaseAuth: FirebaseAuth
    private val signInRequestCode: Int = 1
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleSignInOptions: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = TemperatureListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        temperatureViewModel = ViewModelProvider(this).get(TemperatureViewModel::class.java)
        temperatureViewModel.allTemperatures.observe(this, Observer { temps ->
            // Update the cached copy of the temperatures in the adapter.
            temps?.let { adapter.setTemperatures(it) }
        })

        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewTemperatureActivity::class.java)
            startActivityForResult(intent, newTemperatureActivityRequestCode)
        }

        NotificationUtils().setRecurrentNotification(this@MainActivity)
        configureGoogleSignIn()
    }

    override fun onStart() {
        super.onStart()
        val user = firebaseAuth.currentUser
        updateUI(user)
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun updateUI(user: FirebaseUser?) {
        google_button.visibility = if (user != null) View.GONE else View.VISIBLE
        google_account.visibility = if (user != null) View.VISIBLE else View.GONE
        if (user != null) {
            google_button.setOnClickListener {
                signIn()
            }
        } else {
            user?.let {
                google_account.text = it.displayName
            }
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, signInRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newTemperatureActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let {
                val temp = it.getDoubleExtra(NewTemperatureActivity.TEMP, 36.5)
                val notes = it.getStringExtra(NewTemperatureActivity.NOTES)
                val cough = it.getBooleanExtra(NewTemperatureActivity.COUGH, false)
                val cold =
                    it.getBooleanExtra(NewTemperatureActivity.COLD, false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val current = LocalDateTime.now()
                    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                    val date = current.format(dateFormatter)
                    val time = current.format(timeFormatter)
                    val temperature = Temperature(0, date, time, temp, cough, cold, notes!!)
                    temperatureViewModel.insert(temperature)
                } else {
                    val year = Calendar.getInstance().get(Calendar.YEAR).toString()
                    val month = Calendar.getInstance().get(Calendar.MONTH).toString()
                    val day = Calendar.getInstance().get(Calendar.DATE).toString()
                    val date = "$year $month $day"
                    val hour = Calendar.getInstance().get(Calendar.HOUR).toString()
                    val min = Calendar.getInstance().get(Calendar.MINUTE).toString()
                    val time = "${hour}:${min}"
                    val temperature = Temperature(0, date, time, temp, cough, cold, notes!!)
                    temperatureViewModel.insert(temperature)
                }
            }
        } else if (requestCode == signInRequestCode) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val newUser = it.result?.user
                Toast.makeText(
                    this,
                    "${newUser?.displayName} sign in successful :)",
                    Toast.LENGTH_LONG
                ).show()
                updateUI(newUser)
            } else {
                Toast.makeText(this, "Google sign in failed :(", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }
}