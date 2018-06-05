package io.indexpath.study_001

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import es.dmoral.toasty.Toasty
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.dialog_custom.view.*
import org.jetbrains.anko.startActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        Realm.init(this)
//        val config = RealmConfiguration.Builder().name("person.realm").build()
//        val realm = Realm.getInstance(config)



        /** 로그인 한 유저 정보 출력 */
        val myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        loginUser.text = "ID : ${myPref.getString("id", "")}"
        loginPassword.text = "PW : ${myPref.getString("password", "")}"

        val autoLogin = myPref.getBoolean("autoLogin", false)

        if (autoLogin) isAutoLogin.text = "자동 로그인 사용중"
        else isAutoLogin.text = "자동 로그인 사용안함"

        /** 투두리스트 가져오기 */
        todoLists = realm.where(TodoList::class.java).equalTo("owner", "${myPref.getString("id", "")}" ).findAll()


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

        btnAddTodo.setOnClickListener {

            val dialog = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null)
            val todoText = dialogView.textViewTodo.text

            dialog.setView(dialogView)
            dialog.setCancelable(false)
            dialog.setPositiveButton("OK", { dialogInterface: DialogInterface, i: Int -> })
            dialog.setNegativeButton("CANCEL", { dialogInterface: DialogInterface, i: Int -> })

            val customDialog = dialog.create()
            customDialog.show()


            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener({
                if (todoText.isNotBlank()) {
                    val finalTodoText = removeExtraWhiteSpaces(todoText.toString())

                    // 타임스탬프 미니멈 API 26 필요함
                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                    val formatted = current.format(formatter)

                    /** 렘에 투두 목록을 유저아이디 owner로 저장 */
                    loginUserName = myPref.getString("id", "")

                    realm.beginTransaction()
                    val number = realm.where(TodoList::class.java).count() + 1
                    val todoDB = realm.createObject(TodoList::class.java)
                    todoDB.id = number
                    todoDB.owner = loginUserName
                    todoDB.cDate = formatted
                    todoDB.content = finalTodoText
                    todoDB.isFinish = false

                    realm.commitTransaction()

                    Toasty.success(this, "Current : $formatted :: $loginUserName :: $finalTodoText", Toast.LENGTH_SHORT, true).show()
                    customDialog.dismiss()

                } else {
                    Toasty.error(this, "내용이 없습니다.", Toast.LENGTH_SHORT, true).show()
                }
            })
        }

        recyclerView.adapter = MainRecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)


    }


    class MainRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        //var todoLists = arrayOf("111", "222", "333", "444", "555")

        //var todoLists = realm.where(TodoList::class.java).equalTo("owner", "$loginUserName" ).findAll()
        //var todoBoolean = arrayOf(true, false, true, true, true)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent!!.context).inflate(R.layout.cell_layout, parent,false)
            return CustomViewHolder(view)
        }

        class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view) {
            var createDateText : TextView? = null
            var textview : TextView? = null
            var cellCheckBox : CheckBox? = null

            init {
                createDateText = view!!.findViewById(R.id.createDate)
                textview = view!!.findViewById(R.id.todoContent)
                cellCheckBox = view.findViewById(R.id.cellCheckBox)

            }



        }


        override fun getItemCount(): Int {


            return todoLists!!.count()
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var view = holder as CustomViewHolder
            view.createDateText!!.text = todoLists!![position]!!.cDate
            view.textview!!.text = todoLists!![position]!!.content.toString()
            view.cellCheckBox!!.setChecked(todoLists!![position]!!.isFinish)

            var cb = view.cellCheckBox
            cb!!.setOnClickListener {
                realm.beginTransaction()
                if (cb.isChecked) {
                    todoLists!![position]!!.isFinish = true
                    d(TAG, " 체크박스 선택 : ")
                } else {
                    todoLists!![position]!!.isFinish = false
                    d(TAG, " 체크박스 해제 : ")
                }
                realm.commitTransaction()
            }

        }



    }


    fun removeExtraWhiteSpaces(value: String): String {

        var result = ""
        var prevChar = ""

        for ( char in value ) {

            if ( (prevChar == " " && char == ' ').not() ) {
                result += char
            }
            prevChar = char.toString()
        }

        return result
    }


    companion object {
        private val TAG = "Study001"

        var loginUserName : String = ""

        val config = RealmConfiguration.Builder().name("person.realm").build()
        val realm = Realm.getInstance(config)
        //Realm.init(this)
        var todoLists : RealmResults<TodoList>? = null
    }

}







