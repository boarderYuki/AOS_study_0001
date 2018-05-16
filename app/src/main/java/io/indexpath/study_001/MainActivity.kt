package io.indexpath.study_001

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

//아이디 5글자 이상
//패스워드 8글자 이상, 특수문자, 대,소문자 포함
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val i = Intent(this, JoinActivity::class.java)
        startActivity(i)

        checkUserInfo()

//        button.setOnClickListener {
//            Login()
//        }


//        Observable.just("aaaaaaaa.com")
//                .compose(CustomPatterns.lengthGreaterThanSix)
//                .compose(CustomPatterns.verifyEmailPattern)
//                .subscribe(
//                        { Log.d(TAG,"onNext: $it looks good!") },
//                        { Log.d(TAG,"onError: ${it.message}") },
//                        { Log.d(TAG,"onComplete") }
//                )

//        RxTextView.afterTextChangeEvents(editTextName)
//                .skipInitialValue()
//                .map { it.view().text.toString() }
//                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
//                .compose(CustomPatterns.checkIdPattern)
//                .subscribe(
//                        { Log.d(TAG,"onNext: $it looks good!") },
//                        { Log.d(TAG,"onError: ${it.message}") },
//                        { Log.d(TAG,"onComplete") }
//                )


    }



//    private val lengthGreaterThanSix = ObservableTransformer<String, String> { observable ->
//        observable.map { it.trim() }
//                .filter { it.length > 6 }
//                .singleOrError()
//                .onErrorResumeNext {
//                    if (it is NoSuchElementException) {
//                        Single.error(Exception("길이가 짦음"))
//                    } else {
//                        Single.error(it)
//                    }
//                }
//                .toObservable()
//    }

//    private val verifyEmailPattern = ObservableTransformer<String, String> { observable ->
//        observable.map { it.trim() }
//                .filter { Patterns.EMAIL_ADDRESS.matcher(it).matches() }
//
//                .singleOrError()
//                .onErrorResumeNext {
//                    if (it is NoSuchElementException) {
//                        Single.error(Exception("이메일패턴오류"))
//                    } else {
//                        Single.error(it)
//                    }
//                }
//                .toObservable()
//    }

//    private fun setSupportActionBar(toolbar: Toolbar) {
//        val toolbar : Toolbar = findViewById(R.id.toolbar)
//    }

    private fun checkUserInfo() {
        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val isEmpty = myPref.getBoolean("isEmpty", true)

        if (isEmpty) {
            Log.d(TAG,"JoinActivity")

            val i = Intent(this, JoinActivity::class.java)
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
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



//    private fun Login() {
//        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
//        val name = myPref.getString("name", "")
//        val password = myPref.getString("password", "")
//
//        val id = editTextName.text.toString()
//        val pw = editTextPassword.text.toString()
//
//        if (id == name && pw == password) {
//            Toasty.success(this, "로그인 성공", Toast.LENGTH_SHORT, true).show()
//            val i = Intent(this, ResultActivity::class.java)
//            startActivity(i)
//            finish()
//
//        } else {
//
//            if (id == "" || pw == "") {
//                Toasty.error(this, "아이디 / 패스워드를 입력하세요.", Toast.LENGTH_SHORT, true).show();
//
//            } else if (id != name || pw != password) {
//                Toasty.error(this, "로그인 실패", Toast.LENGTH_SHORT, true).show()
//                return
//            }
//
//        }
//    }







    companion object {
        private val TAG = "Study001"
    }
}
