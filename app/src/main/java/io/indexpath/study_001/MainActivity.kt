package io.indexpath.study_001

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkUserInfo()

        button.setOnClickListener {
            Login()
        }

    }

//    private fun setSupportActionBar(toolbar: Toolbar) {
//        val toolbar : Toolbar = findViewById(R.id.toolbar)
//    }

    private fun checkUserInfo() {
        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val isEmpty = myPref.getBoolean("isEmpty", true)

        if (isEmpty) {

            val i = Intent(this, JoinActivity::class.java)
            startActivity(i)
            finish()

        } else {

            val name = myPref.getString("name", "")
            val email = myPref.getString("email", "")
            val password = myPref.getString("password", "")

            userNick.text = "User Name : ${name}"
            userEmail.text = "User Email : ${email}"
            userPassword.text = "User Password : ${password}"

            return
        }
    }


    private fun Login() {
        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val name = myPref.getString("name", "")
        val password = myPref.getString("password", "")

        val id = editTextName.text.toString()
        val pw = editTextPassword.text.toString()

        if (id == name && pw == password) {
            Toasty.success(this, "로그인 성공", Toast.LENGTH_SHORT, true).show()
            val i = Intent(this, ResultActivity::class.java)
            startActivity(i)
            finish()

        } else {

            if (id == "" || pw == "") {
                Toasty.error(this, "아이디 / 패스워드를 입력하세요.", Toast.LENGTH_SHORT, true).show();

            } else if (id != name || pw != password) {
                Toasty.error(this, "로그인 실패", Toast.LENGTH_SHORT, true).show()
                return
            }

        }
    }

    companion object {
        private val TAG = "Study001"
    }
}
