package io.indexpath.study_001

import android.util.Patterns
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.regex.Pattern

class CustomPatterns {

    // 아이디 : 영문 대,소문자,숫자,"_" 포함 6~12자리

    companion object {
        val idPattern = Pattern.compile("^[a-zA-Z]{1}[a-zA-Z0-9_]{4,11}")
        val pwPattern = Pattern.compile("^(?=.*\\d)(?=.*[~`!@#\$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{8,}")
        var passwordTemp = " "

        /** 아이디 패턴 검사 */
        val checkIdPattern = ObservableTransformer<String, String> { observable ->
            observable.flatMap {
                Observable.just(it).map { it.trim() }
                        .filter { idPattern.matcher(it).matches() }
                        .singleOrError()
                        .onErrorResumeNext {
                            if (it is NoSuchElementException) {
                                //idCheckTextView.text = "아이디 패턴 오류"
                                Single.error(Exception("아이디는 영문 또는 숫자로 5글자 이상만 가능합니다."))
                            } else {
                                Single.error(it)
                            }
                        }
                        .toObservable()
            }
        }

        /** 이메일 패턴 검사 */
        val checkEmailPattern = ObservableTransformer<String, String> { observable ->
            observable.flatMap {
                Observable.just(it).map { it.trim() }
                        .filter { Patterns.EMAIL_ADDRESS.matcher(it).matches() }
                        .singleOrError()
                        .onErrorResumeNext {
                            if (it is NoSuchElementException) {
                                Single.error(Exception("유효하지 않은 이메일 형식입니다."))
                            } else {
                                Single.error(it)
                            }
                        }
                        .toObservable()
            }
        }

        /** 패스워드 패턴 검사 */
        val checkPwPattern = ObservableTransformer<String, String> { observable ->
            observable.flatMap {
                Observable.just(it).map { it.trim() }
                        .filter { pwPattern.matcher(it).matches() }
                        //.filter { pwPatternRepeat.matcher(it).matches() }
                        .singleOrError()
                        .onErrorResumeNext {
                            if (it is NoSuchElementException) {
                                //idCheckTextView.text = "아이디 패턴 오류"
                                Single.error(Exception("패스워드는 영문 대,소문자, 숫자, 특수문자가 모두 포함된 8자리 이상만 가능합니다."))
                            } else {
                                Single.error(it)
                            }
                        }
                        .toObservable()
            }
        }

        /** 패스워드1과 패스워드2가 동일한지 검사*/
        val comparePw = ObservableTransformer<String, String> { observable ->
            observable.flatMap {
                Observable.just(it).map { it.trim() }
                        .filter { it -> passwordTemp.equals(it.toString()) }
                        //.filter { editTextPassword.toString() == it.toString() || editTextPasswordAgain.toString().equals(it.toString()) }
                        //.filter { it.toString().equals( editTextPassword.toString()) && editTextPasswordAgain.toString().equals(it.toString()) }
                        .singleOrError()
                        .onErrorResumeNext {
                            if (it is NoSuchElementException) {
                                //idCheckTextView.text = "아이디 패턴 오류"
                                Single.error(Exception("패스워드가 서로 다릅니다."))
                            } else {
                                Single.error(it)
                            }
                        }
                        .toObservable()
            }
        }


        /** 중복 아이디 검사 */
        val doubleId = ObservableTransformer<String, String> { observable ->
            observable.flatMap {
                Observable.just(it).map { it.trim() }
                        .filter { it -> checkDoubleId(it) }
                        .singleOrError()
                        .onErrorResumeNext{
                            if (it is NoSuchElementException) {
                                //idCheckTextView.text = "아이디 패턴 오류"
                                Single.error(Exception("중복 아이디입니다."))
                            } else {
                                Single.error(it)
                            }
                        }
                        .toObservable()
            }
        }


        /** 중복 아이디 검사
         * 입력받은 아이디로 디비에서 동일한 아이디 값이 있는지 결과 갯수로 파악 */
        private fun checkDoubleId(t: CharSequence?) : Boolean {
            val config = RealmConfiguration.Builder().name("person.realm").build()
            val realm = Realm.getInstance(config)
            var count = realm.where(Person::class.java).equalTo("userId", t.toString()).findAll().count()

            if (count == 0) { return true } else { return false }
        }

    }

}