package io.indexpath.study_001

import android.content.Context
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


class MainActivity : AppCompatActivity() {

    lateinit var realm:Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Realm.init(this)
        val config = RealmConfiguration.Builder().name("person.realm").build()
        realm = Realm.getInstance(config)
        getLastMember()

        /** 로그인 버튼관련 옵저버
         * 아이디와 패스워드 필드가 비워져있지만 않으면 버튼이 활성화 됨*/
        val observableId = RxTextView.textChanges(editTextId)
                .map { t -> t.toString().isNotEmpty() }

        val observablePw = RxTextView.textChanges(editTextPassword)
                .map { t -> t.toString().isNotEmpty() }

        // 아이디, 패스워드 필드에 모두 값이 있는 경우 버튼 컬러 변경하고 클릭가능하게 한다
        val signInEnabled: Observable<Boolean> = Observable.combineLatest(
                observableId, observablePw, BiFunction { i, p -> i && p }
        )

        signInEnabled.distinctUntilChanged()
                .subscribe { enabled -> buttonLogOut.isEnabled = enabled }
        signInEnabled.distinctUntilChanged()
                .map { b -> if (b) R.color.colorAccent else R.color.material_grey_600 }
                .subscribe { color -> buttonLogOut.backgroundTintList =
                        ContextCompat.getColorStateList(this, color) }


        /** 자동 로그인 셋팅
         * 이전 로그인시에 자동로그인 체크 여부를 확인해서 체크박스 설정 */
        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val autoLogin = myPref.getBoolean("autoLogin", false)

        checkAutoLogin.isChecked = autoLogin

        /** 자동 로그인으로 로그인 하기*/
        if (autoLogin) {

            val getIdFromMyPref:String = myPref.getString("id", "")
            val getPasswordFromMyPref = myPref.getString("password", "")
            val user = realm.where(Person::class.java).equalTo("userId",getIdFromMyPref).findAll()

            if (!user.isEmpty() && user.last()?.password == getPasswordFromMyPref){
                // 결과 화면으로 넘기고 종료
                startActivity<ResultActivity>()
                finish()
            }

        }

        /** 로그인 버튼 클릭
         * 아이디 값으로 디비 검색해서 패스워드 비교 */
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
                    Toasty.error(this, "패스워드 틀림", Toast.LENGTH_SHORT, true).show()

                } else {

                    /** 자동 로그인
                     * 체크되어 있으면 SharedPreferences에서 체크여부와 아이디, 패스워드 정보 가져와서 자동으로 로그인 하고 결과 화면으로 이동 */
                    val editor = myPref.edit()

                    if (checkAutoLogin.isChecked) {
                        editor.putBoolean("autoLogin", true)


                    } else {
                        editor.putBoolean("autoLogin", false)
                    }

                    editor.putString("id", editTextId.text.toString())
                    editor.putString("password", editTextPassword.text.toString())
                    editor.apply()

                    Toasty.success(this, "로그인 성공", Toast.LENGTH_SHORT, true).show()
                    startActivity<ResultActivity>()
                    finish()

                }
            }

        }

        /** 회원 가입 버튼 클릭 */
        buttonSignUp.setOnClickListener {
            startActivity<JoinActivity>()
        }

    }

    /** 가장 최근에 가입한 회원 정보 */
    private fun getLastMember() {

        /** 렘 읽기 */
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