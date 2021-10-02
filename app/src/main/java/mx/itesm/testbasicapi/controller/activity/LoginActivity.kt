package mx.itesm.testbasicapi.controller.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import mx.itesm.testbasicapi.R
import mx.itesm.testbasicapi.Utils
import mx.itesm.testbasicapi.model.Model
import mx.itesm.testbasicapi.model.entities.JwtToken
import mx.itesm.testbasicapi.model.entities.User
import mx.itesm.testbasicapi.model.repository.RemoteRepository
import mx.itesm.testbasicapi.model.repository.responseinterface.ILogin

class LoginActivity : AppCompatActivity() {
    private lateinit var btnLogin: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener(loginClickListener())

//        if (Utils.isUserLoggedIn(this)) advanceToMainActivity()
        // Check Expiration
    }

    private fun loginClickListener(): View.OnClickListener? {
        return View.OnClickListener {
            val email = findViewById<EditText>(R.id.txtUsername).text.toString()
            val password = findViewById<EditText>(R.id.txtPassword).text.toString()
            val user = User("anyname", email, password)

            Model(Utils.getToken(this)).login(user, object : ILogin {
                override fun onSuccess(token: JwtToken?) {
                    Toast.makeText(this@LoginActivity, "Welcome boi", Toast.LENGTH_SHORT).show()
                    if (token != null) {
                        Utils.saveToken(token, this@LoginActivity.applicationContext)
                        // This updates the HttpClient that at this moment might not have a valid token!
                        RemoteRepository.updateRemoteReferences(token.token, this@LoginActivity);
                        advanceToMainActivity()
                    } else {
                        // do not advance, an error occurred
                        Toast.makeText(
                            this@LoginActivity,
                            "Something weird happened, login was ok but token was not given...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onNoSuccess(code: Int, message: String) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Problem detected $code $message",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("addProduct", "$code: $message")
                }

                override fun onFailure(t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Network or server error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("addProduct", t.message.toString())
                }
            })
        }
    }

    private fun advanceToMainActivity() {
        val mainActivityIntent =
            Intent(applicationContext, MainActivity::class.java)
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainActivityIntent)
    }
}