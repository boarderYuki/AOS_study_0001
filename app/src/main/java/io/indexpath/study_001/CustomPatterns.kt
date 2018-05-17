package io.indexpath.study_001

import android.util.Patterns
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import java.util.regex.Pattern

class CustomPatterns {

    // 아이디 : 영문 대,소문자,숫자,"_" 포함 6~12자리

    companion object {
        val idPattern = Pattern.compile("^[a-zA-Z]{1}[a-zA-Z0-9_]{4,11}")
        val pwPattern = Pattern.compile("^(?=.*\\d)(?=.*[~`!@#\$%\\^&*()-])(?=.*[a-z])(?=.*[A-Z]).{8,}")
        val pwPatternRepeat = Pattern.compile("(.)\\1\\1\\1")

        var passwordTemp = " "


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

//        val checkPwPatternRepeat = ObservableTransformer<String, String> { observable ->
//            observable.flatMap {
//                Observable.just(it).map { it.trim() }
//                        .filter { pwPatternRepeat.matcher(it).matches() }
//                        //.filter { pwPatternRepeat.matcher(it).matches() }
//                        .singleOrError()
//                        .onErrorResumeNext {
//                            if (it is NoSuchElementException) {
//                                //idCheckTextView.text = "아이디 패턴 오류"
//                                Single.error(Exception("패스워드에 동일 문자를 연속 4개 이상 사용할 수 없습니다."))
//                            } else {
//                                Single.error(it)
//                            }
//                        }
//                        .toObservable()
//            }
//        }


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





//        fun retryWhenError( onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> = ObservableTransformer { observable ->
//            observable.retryWhen { errors ->
//                errors.flatMap {
//                    onError(it)
//                    Observable.just("")
//                }
//            }
//        }

    }



}