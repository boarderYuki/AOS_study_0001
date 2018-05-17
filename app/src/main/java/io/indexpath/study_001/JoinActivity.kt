package io.indexpath.study_001

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Patterns
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_join.*


class JoinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

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
                //.debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
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
                .subscribe { enabled -> buttonSave.isEnabled = enabled }
        signInEnabled.distinctUntilChanged()
                .map { b -> if (b) R.color.colorAccent else R.color.material_grey_600 }
                .subscribe { color -> buttonSave.backgroundTintList =
                        ContextCompat.getColorStateList(this, color) }
    }






    private inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> = ObservableTransformer { observable ->
        observable.retryWhen { errors ->
            errors.flatMap {
                onError(it)
                Observable.just("")
            }
        }
    }

//    private val checkIdPattern = ObservableTransformer<String, String> { observable ->
//        observable.flatMap {
//            Observable.just(it).map { it.trim() }
//                    .filter { CustomPatterns.idPattern.matcher(it).matches() }
//                    .singleOrError()
//                    .onErrorResumeNext {
//                        if (it is NoSuchElementException) {
//                            //idCheckTextView.text = "아이디 패턴 오류"
//                            Single.error(Exception("아이디 패턴 오류"))
//                        } else {
//                            Single.error(it)
//                        }
//                    }
//                    .toObservable()
//        }
//    }

//    private fun saveData() {
//        if (editTextName.text.isEmpty()) {
//            Toasty.error(this, "이름을 입력하세요.", Toast.LENGTH_SHORT, true).show()
//            return
//        }
//
//        if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.text).matches()) {
//            Toasty.error(this, "이메일 형식이 맞지 않습니다.", Toast.LENGTH_SHORT, true).show()
//            return
//        }
//
//        if (editTextPassword.text.isEmpty()) {
//            Toasty.error(this, "패스워드를 입력하세요.", Toast.LENGTH_SHORT, true).show()
//            return
//        }
//
//        if (editTextPasswordAgain.text.isEmpty()) {
//            Toasty.error(this, "패스워드를 한번 더 입력하세요.", Toast.LENGTH_SHORT, true).show()
//            return
//        }
//
//        if (editTextPassword.text.toString() != editTextPasswordAgain.text.toString()) {
//            Toasty.error(this, "패스워드가 서로 다릅니다.", Toast.LENGTH_SHORT, true).show()
//            return
//        }
//
//        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
//
//        val editor = myPref.edit()
//        editor.putString("name",editTextName.text.toString())
//        editor.putString("email",editTextEmail.text.toString())
//        editor.putString("password", editTextPassword.text.toString())
//        editor.putBoolean("isEmpty", false)
//        editor.apply()
//
//
//        Toasty.success(this, "저장 성공", Toast.LENGTH_SHORT, true).show();
//
//        val i = Intent(this, MainActivity::class.java)
//        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        startActivity(i)
//        finish()
//
//    }


    companion object {

        private val TAG = "Study001"

    }
}
