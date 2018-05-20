package io.indexpath.study_001

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import es.dmoral.toasty.Toasty
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_join.*
import java.util.concurrent.TimeUnit




class JoinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        Realm.init(this)
        val config = RealmConfiguration.Builder().name("person.realm").build()
        val realm = Realm.getInstance(config)
        Log.d(TAG, "path: " + realm.path)
        var passwordTemp = ""


        /** 버튼관련 옵저버 */
        val observableId = RxTextView.textChanges(editTextId)
                .map { t -> CustomPatterns.idPattern.matcher(t).matches() }

        val observableEmail = RxTextView.textChanges(editTextEmail)
                .map { t -> Patterns.EMAIL_ADDRESS.matcher(t).matches() }

        val observablePw1 = RxTextView.textChanges(editTextPassword)
                .map { t -> CustomPatterns.pwPattern.matcher(t).matches() }

        val observablePw2 = RxTextView.textChanges(editTextPasswordAgain)
                .map { t -> CustomPatterns.passwordTemp.equals(t.toString()) }
                //.map { t -> editTextPassword.toString().equals(t.toString()) }

        //val observablePwCompare = RxTextView.textChanges(editTextPassword)
                //.map { t -> CustomPatterns.passwordTemp.equals(t.toString()) }




        /** 아이디 체크 */
        RxTextView.afterTextChangeEvents(editTextId)
                .skipInitialValue()
                .map {
                    checkId.text = ""
                    it.view().text.toString()
                }
                // 1초마다 새롭게 시도
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(CustomPatterns.checkIdPattern)
                .compose(retryWhenError {
                    checkId.text = it.message
                })
                .subscribe()

        /** 이메일 체크 */
        RxTextView.afterTextChangeEvents(editTextEmail)
                .skipInitialValue()
                .map {
                    checkId.text = ""
                    it.view().text.toString()
                }
                //.debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(CustomPatterns.checkEmailPattern)
                .compose(retryWhenError {
                    checkId.text = it.message
                })
                //.subscribe( passwordTemp )
                .subscribe()

        /** 첫번째 패스워드 체크 */
        RxTextView.afterTextChangeEvents(editTextPassword)
                .skipInitialValue()
                .map {
                    checkId.text = ""
                    it.view().text.toString()
                }
                //.debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(CustomPatterns.checkPwPattern)
                //.compose(CustomPatterns.comparePw)
                //.compose(CustomPatterns.checkPwPatternRepeat)
                .compose(retryWhenError {
                    checkId.text = it.message
                })
                .subscribe {
                    CustomPatterns.passwordTemp = it
                    editTextPasswordAgain.text = null
                    Log.d(TAG,"onNext1: $it ")
                }

//        /** 첫번째 패스워드를 수정했을 경우 */
//        RxTextView.afterTextChangeEvents(editTextPassword)
//                .skipInitialValue()
//                .map {
//                    checkId.text = ""
//                    it.view().text.toString()
//                }
//                //.debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
//                //.compose(CustomPatterns.checkPwPattern)
//                .compose(CustomPatterns.comparePw)
//                //.compose(CustomPatterns.checkPwPatternRepeat)
//                .compose(retryWhenError {
//                    checkId.text = it.message
//                })
//                .subscribe {
//                    //CustomPatterns.passwordTemp = it
//                    Log.d(TAG,"onNext2: $it ")
//                }

        /** 동일한 패스워드인지 체크 */
        RxTextView.afterTextChangeEvents(editTextPasswordAgain)
                .skipInitialValue()
                .map {
                    checkId.text = ""
                    it.view().text.toString()
                }
                //.debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(CustomPatterns.comparePw)
                .compose(retryWhenError {
                    checkId.text = it.message
                })
                .subscribe()


        /** Sign In observer */

        val signInEnabled1: Observable<Boolean> = Observable.combineLatest(
                observableId, observableEmail, BiFunction { i, e -> i && e }
        )

        val signInEnabled2: Observable<Boolean> = Observable.combineLatest(
                observablePw1, observablePw2, BiFunction { p1, p2 -> p1 && p2 }
        )

        val signInEnabled: Observable<Boolean> = Observable.combineLatest(
                signInEnabled1, signInEnabled2, BiFunction { s1, s2 -> s1 && s2 }
        )

//        val signInEnabled: Observable<Boolean> = Observable.combineLatest(
//                signInEnabledWithChangePassword, observablePwCompare, BiFunction { s1, s2 -> s1 && s2 }
//        )

//        observableId, observableEmail, observablePw1, observablePw2, BiFunction { i, e, p1, p2 -> i && e && p1 && p2 }

        signInEnabled.distinctUntilChanged()
                .subscribe { enabled -> buttonLogOut.isEnabled = enabled }
        signInEnabled.distinctUntilChanged()
                .map { b -> if (b) R.color.colorAccent else R.color.material_grey_600 }
                .subscribe { color -> buttonLogOut.backgroundTintList =
                        ContextCompat.getColorStateList(this, color) }



        buttonLogOut.setOnClickListener {
            //saveData()

            /** 렘 저장 */
            realm.beginTransaction()
            val number = realm.where(Person::class.java).count() + 1

            val person = realm.createObject(Person::class.java, number)

            person.userId = editTextId.text.toString()
            person.email = editTextEmail.text.toString()
            person.password = editTextPassword.text.toString()
            realm.commitTransaction()

            Toasty.success(this, "저장 성공", Toast.LENGTH_SHORT, true).show();


            /** 렘 읽기 */
            val  allPersons = realm.where(Person::class.java).findAll()
            allPersons.forEach { person ->
                println("Person: ${person.userId} : ${person.email} ${person.password}")
            }

            val lastPerson = allPersons.last()
            println("Person: ${lastPerson?.userId} : ${lastPerson?.email} ${lastPerson?.password}")


//            /** 가입정보 있음 */
//            val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
//
//            val editor = myPref.edit()
//            editor.putBoolean("isEmpty", true)
//            editor.putString("name", editTextId.text.toString())
//            editor.putString("email", editTextEmail.text.toString())
//            editor.putString("password", editTextPassword.text.toString())
//            editor.apply()


            val i = Intent(this, MainActivity::class.java)
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
            finish()


        }









    }

    /** 패턴 오류 메세지 관련 */
    private inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> = ObservableTransformer { observable ->
        observable.retryWhen { errors ->
            errors.flatMap {
                onError(it)
                Observable.just("")
            }
        }
    }

//    private fun getNextKey(): Int {
//        try {
//            val number = realm.where(Person::class.java).max("id")
//            return if (number != null) {
//                number!!.toInt() + 1
//            } else {
//                0
//            }
//        } catch (e: ArrayIndexOutOfBoundsException) {
//            return 0
//        }
//    }

//    private fun saveData() {
//
//        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
//
//        val editor = myPref.edit()
//        editor.putBoolean("isEmpty", false)
//        editor.apply()
//
//
//
//    }



    companion object {


        private val TAG = "Study001"

    }
}

