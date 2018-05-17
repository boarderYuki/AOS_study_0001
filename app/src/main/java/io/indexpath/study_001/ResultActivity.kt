package io.indexpath.study_001

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        buttonSave.setOnClickListener {
            val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)

            val editor = myPref.edit()
            editor.putString("name", "")
            editor.putString("email", "")
            editor.putString("password", "")
            editor.putBoolean("isEmpty", true)
            editor.apply()

            Toasty.success(this, "로그인 정보가 삭제되었습니다.", Toast.LENGTH_SHORT, true).show();

            val i = Intent(applicationContext, MainActivity::class.java)
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            startActivity(i)
            finish()
        }

    }
}

