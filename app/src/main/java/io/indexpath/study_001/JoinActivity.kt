package io.indexpath.study_001

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import android.widget.Toast
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_join.*





class JoinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        button.setOnClickListener {
            saveData()
        }

    }

    private fun saveData() {
        if (editTextName.text.isEmpty()) {
            Toasty.error(this, "이름을 입력하세요.", Toast.LENGTH_SHORT, true).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.text).matches()) {
            Toasty.error(this, "이메일 형식이 맞지 않습니다.", Toast.LENGTH_SHORT, true).show()
            return
        }

        if (editTextPassword.text.isEmpty()) {
            Toasty.error(this, "패스워드를 입력하세요.", Toast.LENGTH_SHORT, true).show()
            return
        }

        if (editTextPasswordAgain.text.isEmpty()) {
            Toasty.error(this, "패스워드를 한번 더 입력하세요.", Toast.LENGTH_SHORT, true).show()
            return
        }

        if (editTextPassword.text.toString() != editTextPasswordAgain.text.toString()) {
            Toasty.error(this, "패스워드가 서로 다릅니다.", Toast.LENGTH_SHORT, true).show()
            return
        }

        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)

        val editor = myPref.edit()
        editor.putString("name",editTextName.text.toString())
        editor.putString("email",editTextEmail.text.toString())
        editor.putString("password", editTextPassword.text.toString())
        editor.putBoolean("isEmpty", false)
        editor.apply()


        Toasty.success(this, "저장 성공", Toast.LENGTH_SHORT, true).show();

        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()

    }

}
