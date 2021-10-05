package mx.itesm.testbasicapi.controller.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import mx.itesm.testbasicapi.R
import mx.itesm.testbasicapi.Utils
import mx.itesm.testbasicapi.controller.adapter.FragmentAdapter
import mx.itesm.testbasicapi.model.Model
import mx.itesm.testbasicapi.model.entities.JwtToken
import mx.itesm.testbasicapi.model.entities.User
import mx.itesm.testbasicapi.model.repository.RemoteRepository
import mx.itesm.testbasicapi.model.repository.responseinterface.ILogin

class Forms : AppCompatActivity() {
    lateinit var viewPager: ViewPager2
    lateinit var fragmentAdapter: FragmentAdapter

//    lateinit var email: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_forms)
        viewPager = findViewById(R.id.view_pager)
        val supportFragmentManager = supportFragmentManager
        val lifecycle = lifecycle
        fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)
        viewPager.setAdapter(fragmentAdapter)

        if (Utils.isUserLoggedIn(this)) advanceToMainActivity()

    }



    fun redirectLogin(view: View?) {
        viewPager!!.currentItem = 0
    }

    fun redirectRegister(view: View?) {
        viewPager!!.currentItem = 1
    }

    fun redirectRecover(view: View?) {
        viewPager!!.currentItem = 2
    }

    fun login(view: View?) {
        // Check valid inputs(email, password)

        // Check Valid user with data base
        val loginEmailInput = findViewById<TextInputLayout>(R.id.login_email_input)
        val email = "${loginEmailInput.editText?.text}"

        val loginPasswordInput = findViewById<TextInputLayout>(R.id.login_password_input)
        val password = "${loginPasswordInput.editText?.text}"

        Toast.makeText(
            this@Forms,
            "email: $email \n pass: $password \n ",
            Toast.LENGTH_LONG
        ).show()


        val user = User("anyname", email, password)
            Model(Utils.getToken(this)).login(user, object : ILogin {

                override fun onSuccess(token: JwtToken?) {
                    Toast.makeText(this@Forms, "Welcome", Toast.LENGTH_SHORT).show()

                    if (token != null) {
                        Utils.saveToken(token, this@Forms.applicationContext)
                        // This updates the HttpClient that at this moment might not have a valid token!
                        RemoteRepository.updateRemoteReferences(token.token, this@Forms);
                        advanceToMainActivity()
                    } else {
                        // do not advance, an error occurred
                        Toast.makeText(
                            this@Forms,
                            "Something weird happened, login was ok but token was not given...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onNoSuccess(code: Int, message: String) {
                    Toast.makeText(
                        this@Forms,
                        "Problem detected $code $message",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("addProduct", "$code: $message")
                }

                override fun onFailure(t: Throwable) {
                    Toast.makeText(
                        this@Forms,
                        "Network or server error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("addProduct", t.message.toString())
                }
            })

    }

    fun register(view: View?) {
        val registerNameInput = findViewById<TextInputLayout>(R.id.register_name_input)
        val name = "${registerNameInput.editText?.text}"

        val registerEmailInput = findViewById<TextInputLayout>(R.id.register_email_input)
        val email = "${registerEmailInput.editText?.text}"

        val registerPasswordInput = findViewById<TextInputLayout>(R.id.register_password_input)
        val password = "${registerPasswordInput.editText?.text}"

        val registerConfirmInput = findViewById<TextInputLayout>(R.id.register_password_confirm_input)
        val confirmPassword = "${registerConfirmInput.editText?.text}"

        Toast.makeText(
            this@Forms,
            "name: $name \n email: $email \n pass: $password \n conf: $confirmPassword \n",
            Toast.LENGTH_LONG
        ).show()
        advanceToMainActivity()


        // Check valid inputs(name, email, password, confirm password)

        // Check Valid user with data base(Create User)

        // Call Login from here

    }

    fun recover(view: View?) {
        val recoverEmailInput = findViewById<TextInputLayout>(R.id.recover_email_input)
        val email = "${recoverEmailInput.editText?.text}"

        Toast.makeText(
            this@Forms,
            "email: $email",
            Toast.LENGTH_LONG
        ).show()


        // Check valid inputs (email in database already)

        // Check Valid user with data base

    }

    private fun advanceToMainActivity() {
        val mainActivityIntent =
            Intent(applicationContext, MainActivity::class.java)
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainActivityIntent)
        finish()
    }
}