package io.indexpath.study_001

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
import org.jetbrains.anko.startActivity
import java.util.concurrent.TimeUnit


class JoinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        Realm.init(this)
        val config = RealmConfiguration.Builder().name("person.realm").build()
        val realm = Realm.getInstance(config)
        Log.d(TAG, "path: " + realm.path)


        /** 가입 버튼 관련 옵저버
         * 오류메세지와는 관계없이 단순히 패턴검사만 하여 가입 버튼 활성화에 사용*/
        val observableId = RxTextView.textChanges(editTextId)
                .map { t -> CustomPatterns.idPattern.matcher(t).matches() }

        val observableDoubleId = RxTextView.textChanges(editTextId)
                .map { t -> checkDoubleIdForButton(t) }

        val observableEmail = RxTextView.textChanges(editTextEmail)
                .map { t -> Patterns.EMAIL_ADDRESS.matcher(t).matches() }

        val observablePw1 = RxTextView.textChanges(editTextPassword)
                .map { t -> CustomPatterns.pwPattern.matcher(t).matches() }

        val observablePw2 = RxTextView.textChanges(editTextPasswordAgain)
                .map { t -> CustomPatterns.passwordTemp.equals(t.toString()) }

        /** 패턴 오류 메세지 보여줌 오류내용은 CustomPatterns 에서 가져옴 */
        /** 아이디 체크 */
        RxTextView.afterTextChangeEvents(editTextId)
                .skipInitialValue()
                .map {
                    checkDoubleIdText.text = ""
                    it.view().text.toString()
                }
                // 1초마다 새롭게 시도
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(CustomPatterns.checkIdPattern)
                .compose(CustomPatterns.doubleId) /** <--- 중복아이디 검사 */
                .compose(retryWhenError {
                    checkDoubleIdText.text = it.message
                })
                .subscribe()

        /** 이메일 체크 */
        RxTextView.afterTextChangeEvents(editTextEmail)
                .skipInitialValue()
                .map {
                    checkId.text = ""
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(CustomPatterns.checkEmailPattern)
                .compose(retryWhenError {
                    checkId.text = it.message
                })
                .subscribe()

        /** 첫번째 패스워드 체크 */
        RxTextView.afterTextChangeEvents(editTextPassword)
                .skipInitialValue()
                .map {
                    checkId.text = ""
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(CustomPatterns.checkPwPattern)
                .compose(retryWhenError {
                    checkId.text = it.message
                })
                .subscribe {
                    CustomPatterns.passwordTemp = it
                    editTextPasswordAgain.text = null
                }


        /** 동일한 패스워드인지 체크 */
        RxTextView.afterTextChangeEvents(editTextPasswordAgain)
                .skipInitialValue()
                .map {
                    checkId.text = ""
                    it.view().text.toString()
                }
                .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(CustomPatterns.comparePw)
                .compose(retryWhenError {
                    checkId.text = it.message
                })
                .subscribe()


        /** 버튼 활성화 이상형 월드컵
         * 모든 항목이 트루를 받아야만 버튼이 활성화 됨 */
        val signInEnabled1: Observable<Boolean> = Observable.combineLatest(
                observableId, observableEmail, BiFunction { i, e -> i && e }
        )

        val signInEnabled2: Observable<Boolean> = Observable.combineLatest(
                observablePw1, observablePw2, BiFunction { p1, p2 -> p1 && p2 }
        )

        val signInEnabled3: Observable<Boolean> = Observable.combineLatest(
                signInEnabled1, signInEnabled2, BiFunction { s1, s2 -> s1 && s2 }
        )

        val signInEnabled: Observable<Boolean> = Observable.combineLatest(
                signInEnabled3, observableDoubleId, BiFunction { s1, s2 -> s1 && s2 }
        )

        // 버튼 활성하면서 색상도 변경
        signInEnabled.distinctUntilChanged()
                .subscribe { enabled -> buttonLogOut.isEnabled = enabled }
        signInEnabled.distinctUntilChanged()
                .map { b -> if (b) R.color.colorAccent else R.color.material_grey_600 }
                .subscribe { color -> buttonLogOut.backgroundTintList =
                        ContextCompat.getColorStateList(this, color) }

        buttonLogOut.setOnClickListener {

            /** 렘에 회원정보 저장 */
            realm.beginTransaction()
            val number = realm.where(Person::class.java).count() + 1

            val person = realm.createObject(Person::class.java, number)

            person.userId = editTextId.text.toString()
            person.email = editTextEmail.text.toString()
            person.password = editTextPassword.text.toString()
            realm.commitTransaction()

            Toasty.success(this, "저장 성공", Toast.LENGTH_SHORT, true).show();

            startActivity<MainActivity>()
            finish()
        }

    }

    /** 버튼 활성화 관련 옵저버 observableDoubleId 에 불린 값 전달 */
    private fun checkDoubleIdForButton(t: CharSequence?) : Boolean {
        val config = RealmConfiguration.Builder().name("person.realm").build()
        val realm = Realm.getInstance(config)
        var count = realm.where(Person::class.java).equalTo("userId", t.toString()).findAll().count()
        // 중복 아이디 없으면 트루 전달
        if (count == 0) { return true } else { return false }
    }


    /** 패턴들 오류 메세지 관련 */
    private inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> = ObservableTransformer { observable ->
        observable.retryWhen { errors ->
            errors.flatMap {
                onError(it)
                Observable.just("")
            }
        }
    }

    companion object {
        private val TAG = "Study001"

    }
}