package io.indexpath.study_001

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_result.*
import org.jetbrains.anko.startActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        loginUser.text = "Login User ID : ${myPref.getString("id", "")}"
        loginPassword.text = "Login User ID : ${myPref.getString("password", "")}"

        val autoLogin = myPref.getBoolean("autoLogin", false)

        if (autoLogin) isAutoLogin.text = "자동 로그인 선택함"
        else isAutoLogin.text = "자동 로그인 선택 안 함"

        buttonLogOut.setOnClickListener {




            Toasty.success(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT, true).show();

//            val i = Intent(applicationContext, MainActivity::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            startActivity(i)
            val editor = myPref.edit()
            editor.putBoolean("autoLogin", false)
            editor.apply()

            startActivity<MainActivity>()

            finish()
        }

    }
}

