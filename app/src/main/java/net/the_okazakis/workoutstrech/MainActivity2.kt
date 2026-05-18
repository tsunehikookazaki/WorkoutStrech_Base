package net.the_okazakis.workoutstrech

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

/**
 * 選択された各種目の目標セット数や反復回数（reps）を設定・保存するための設定画面Activity。
 * データベース（workouttimes）から現在の設定を読み込み、ユーザーの入力に応じて更新します。
 */
private lateinit var _helper: DatabaseHelper
class MainActivity2 : AppCompatActivity() {

    /**
     * 選択されたトレーニングの主キーIDを表すプロパティ。
     */
    private var _workoutId = -1

    /**
     * 選択されたトレーニング名を表すプロパティ。
     */
    //private var _workoutName = ""

    /**
     * データベースヘルパーオブジェクト。
     */
    private lateinit var _helper: DatabaseHelper

    /**
     * 画面生成時に呼ばれ、データベースから保存されている回数を読み込んで表示します。
     * また、一部の種目に関しては「1セットあたりの回数（reps）」入力を非表示にするなど、画面UIの動的調整も行います。
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _helper = DatabaseHelper(applicationContext)  // ←これ追加
        setContentView(R.layout.activity_main2)

        /**
         * 画面が表示されたらデータベースから回数を読み込み表示する
         *
         */
        val tvWorkoutName: TextView = findViewById(R.id.tvWorkoutName) //トレーニング名
        val workmenu: String? = intent.getStringExtra("TEXT_KEY4")  //元の画面からトレーニング名を受け取る
        tvWorkoutName.text = workmenu  //トレーニング名を表示

        val dbid: Int = intent.getIntExtra("TEXT_KEY5",0)  //元の画面からメニュー値を受け取る


        _workoutId=dbid     //トレーニング番号

        val db0 = _helper.writableDatabase
        val sql0 = "SELECT * FROM workouttimes WHERE _id = $_workoutId"
        // SQLの実行。
        val cursor0 = db0.rawQuery(sql0, null)


        var times0 :String
        var reps0 :String
        // SQL実行の戻り値であるカーソルオブジェクトをループさせてデータベース内のデータを取得。
        while(cursor0.moveToNext()) {
            // カラムのインデックス値を取得。
            val idxTimes = cursor0.getColumnIndex("times")
            val idxReps = cursor0.getColumnIndex("reps")
            // カラムのインデックス値を元に実際のデータを取得。
            times0 =  cursor0.getString(idxTimes)
            reps0 =  cursor0.getString(idxReps) ?: "15"
            val etTimes = findViewById<EditText>(R.id.etTimes)
            val etReps = findViewById<EditText>(R.id.etReps)
            etTimes.setText(times0)
            etReps.setText(reps0)


        }

        cursor0.close()

        val etReps = findViewById<EditText>(R.id.etReps)
        val tvRepsLabel = findViewById<TextView>(R.id.textViewRepsLabel)
        val btnSave: Button = findViewById(R.id.btnSave)

        // 動的設定に対応したインデックス（lv_menu基準） 　番号は0から　
        val dynamicIds = listOf( 12 )

        if (!dynamicIds.contains(_workoutId)) {
            // 固定動作の場合は1セットの回数入力を非表示にする
            etReps.visibility = android.view.View.GONE
            tvRepsLabel.visibility = android.view.View.GONE

            // 保存ボタンの位置を調整（etTimesの下に配置）
            val params = btnSave.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            params.topToBottom = R.id.etTimes
            btnSave.layoutParams = params
        }

        /**
         * 保存ボタンがタップされた時の処理。
         * 入力されたセット数、リピート回数を取得し、古いデータを削除した上でデータベースに新規保存（INSERT）します。
         */
        btnSave.setOnClickListener {

            val etTimes = findViewById<EditText>(R.id.etTimes)
            val etReps = findViewById<EditText>(R.id.etReps)
            // 入力された回数を取得。
            var times = etTimes.text.toString()
            if (times==""){times="2"}   //間違って消してしまった時

            var reps = etReps.text.toString()
            if (reps==""){reps="15"}

            // データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得。
            val db = _helper.writableDatabase

            // まず、リストで選択されたカクテルのメモデータを削除。その後インサートを行う。
            // 削除用SQL文字列を用意。
            val sqlDelete = "DELETE FROM workouttimes WHERE _id = ?"
            // SQL文字列を元にプリペアドステートメントを取得。
            var stmt = db.compileStatement(sqlDelete)
            // 変数のバイド。
            stmt.bindLong(1, _workoutId.toLong())

            // 削除SQLの実行。
            stmt.executeUpdateDelete()


            // インサート用SQL文字列の用意。
          //  val sqlInsert = "INSERT INTO workouttimes (_id, name, times) VALUES (?, ?, ?)"
            val sqlInsert = "INSERT INTO workouttimes (_id,  times, reps) VALUES (?, ?, ?)"
            // SQL文字列を元にプリペアドステートメントを取得。
            stmt = db.compileStatement(sqlInsert)
            // 変数のバイド。
            stmt.bindLong(1, _workoutId.toLong())
           // stmt.bindString(2, _workoutName)
           // stmt.bindString(3, times)
            stmt.bindString(2, times)
            stmt.bindString(3, reps)
            // インサートSQLの実行。
            stmt.executeInsert()


            // 保存ボタンを取得。
            val btnSave = findViewById<Button>(R.id.btnSave)
            // 保存ボタンをタップできないように変更。
            btnSave.isEnabled = false

            this.finish()
        }
    }
    /**
     * Activityが破棄される直前に呼び出されます。
     * データベースへの接続（DatabaseHelper）を安全に閉じます。
     */
    override fun onDestroy() {
        _helper.close()
        super.onDestroy()
    }
}





