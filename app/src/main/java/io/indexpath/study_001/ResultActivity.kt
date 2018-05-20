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

        /** 로그인 한 유저 정보 출력 */
        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        loginUser.text = "Login User ID : ${myPref.getString("id", "")}"
        loginPassword.text = "Login User ID : ${myPref.getString("password", "")}"

        val autoLogin = myPref.getBoolean("autoLogin", false)

        if (autoLogin) isAutoLogin.text = "자동 로그인 선택함"
        else isAutoLogin.text = "자동 로그인 선택 안 함"

        /** 로그 아웃 버튼
         * 클릭하면 자동로그인 체크 여부 지우고 메인 화면으로 이동함
         * 자동로그인 관련 확인하려면 로그아웃버튼 클릭 없이 앱 종료한 다음 재 실행하면 됨 */
        buttonLogOut.setOnClickListener {

            val editor = myPref.edit()
            editor.putBoolean("autoLogin", false)
            editor.apply()

            startActivity<MainActivity>()
            finish()

            Toasty.success(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT, true).show()
        }

    }
}