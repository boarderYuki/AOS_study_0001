package io.indexpath.study_001

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Study_001
 *
 * Created by yuki on 2018. 5. 18.
 */

@RealmClass
open class Person(): RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var userId: String? = null
    var email: String? = null
    var password: String? = null
    //var todoList: RealmList<TodoList> = RealmList()
}


open class TodoList: RealmObject() {

    var id: Long = 0
    var owner: String? = null
    var content: String? = null
    var isFinish: Boolean? = null
}

