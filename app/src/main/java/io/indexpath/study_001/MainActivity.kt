package io.indexpath.study_001

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import es.dmoral.toasty.Toasty
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

//아이디 5글자 이상
//패스워드 8글자 이상, 특수문자, 대,소문자 포함
class MainActivity : AppCompatActivity() {

    lateinit var realm:Realm



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Realm.init(this)
        val config = RealmConfiguration.Builder().name("person.realm").build()
        realm = Realm.getInstance(config)
        getLastMember()

        /** 버튼관련 옵저버 */
        val observableId = RxTextView.textChanges(editTextId)
                .map { t -> t.toString().isNotEmpty() }

        val observablePw = RxTextView.textChanges(editTextPassword)
                .map { t -> t.toString().isNotEmpty() }

        val signInEnabled: Observable<Boolean> = Observable.combineLatest(
                observableId, observablePw, BiFunction { i, p -> i && p }
        )

        signInEnabled.distinctUntilChanged()
                .subscribe { enabled -> buttonLogOut.isEnabled = enabled }
        signInEnabled.distinctUntilChanged()
                .map { b -> if (b) R.color.colorAccent else R.color.material_grey_600 }
                .subscribe { color -> buttonLogOut.backgroundTintList =
                        ContextCompat.getColorStateList(this, color) }

        /** 자동 로그인 셋팅 - 이전 로그인시에 자동로그인 체크 여부를 확인해서 체크박스 설정 */
        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val autoLogin = myPref.getBoolean("autoLogin", false)

        checkAutoLogin.isChecked = autoLogin


        buttonLogOut.setOnClickListener {
            //Login()
            Log.d(TAG,"로그인 클릭")
            //val config = RealmConfiguration.Builder().name("person.realm").build()
            //realm = Realm.getInstance(config)
            //realm.beginTransaction()

            val user = realm.where(Person::class.java).equalTo("userId",editTextId.text.toString().trim()).findAll()

            Log.d(TAG,"유저 카운트  : ${user.count()}")

            if (user.isEmpty()) {

                Log.d(TAG,"유저 없음")
                Toasty.error(this, "유저 아이디 없음", Toast.LENGTH_SHORT, true).show()

                editTextId.text = null
                editTextPassword.text = null

            } else {

                val lastUser = user.last()
                if (editTextPassword.text.toString() != lastUser?.password.toString()) {
                    Toast.makeText(this,"패스워드 틀림",Toast.LENGTH_SHORT).show()

                } else {

                    /** 자동 로그인 확인 */
//                    val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
                    val editor = myPref.edit()

                    if (checkAutoLogin.isChecked) {
                        editor.putBoolean("autoLogin", true)
                        editor.putString("id", editTextId.text.toString())
                        editor.putString("password", editTextPassword.text.toString())

                    } else {
                        editor.putBoolean("autoLogin", false)
                    }

                    editor.apply()

                    Toasty.success(this, "로그인 성공", Toast.LENGTH_SHORT, true).show()
                    val i = Intent(this, ResultActivity::class.java)
                    startActivity(i)
                    finish()

                }
            }

        }

        /** 회원 가입 */
        buttonSignUp.setOnClickListener {
            startActivity<JoinActivity>()
        }

    }




//    private fun setSupportActionBar(toolbar: Toolbar) {
//        val toolbar : Toolbar = findViewById(R.id.toolbar)
//    }

//    private fun checkUserInfo() {
//        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
//        val isEmpty = myPref.getBoolean("isEmpty", true)
//
//        if (!isEmpty) {
//            Log.d(TAG,"가입정보 없음")
//
//            val i = Intent(this, JoinActivity::class.java)
//            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            //i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
//            startActivity(i)
//            finish()
//
//        } else {
//            Log.d(TAG,"가입정보 있음")
//
//            /** 렘 읽기 */
//            val config = RealmConfiguration.Builder().name("person.realm").build()
//            val realm = Realm.getInstance(config)
//            realm.beginTransaction()
//            val  allPersons = realm.where(Person::class.java).findAll()
//            allPersons.forEach { person ->
//                println("Person: ${person.userId} : ${person.email} ${person.password}")
//            }
//
//            val lastPerson = allPersons.last()
//
//            val name = lastPerson?.userId
//            val email = lastPerson?.email
//            val password = lastPerson?.password
//
//            userNick.text = "User ID : ${name}"
//            userEmail.text = "User Email : ${email}"
//            userPassword.text = "User Password : ${password}"
//
//            return
//        }
//    }


    /** 마지막 가입 회원 정보 */
    private fun getLastMember() {
//        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
//        val isEmpty = myPref.getBoolean("isEmpty", true)

        /** 렘 읽기 */
//        val config = RealmConfiguration.Builder().name("person.realm").build()

//        realm.beginTransaction()
        val  allPersons = realm.where(Person::class.java).findAll()

        if (!allPersons.isEmpty()) {
            allPersons.forEach { person ->
                println("Person: ${person.userId} : ${person.email} ${person.password}")
            }

            val lastPerson = allPersons.last()

            val name = lastPerson?.userId
            val email = lastPerson?.email
            val password = lastPerson?.password

            userNick.text = "User ID : ${name}"
            userEmail.text = "User Email : ${email}"
            userPassword.text = "User Password : ${password}"
        }

    }






    companion object {
        private val TAG = "Study001"

    }
}


