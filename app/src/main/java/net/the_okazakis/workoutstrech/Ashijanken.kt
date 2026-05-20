package net.the_okazakis.workoutstrech

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView


class Ashijanken : AppCompatActivity() {

    // 秒数カウント
    private var timeCount = 0
    private var extimes: Int = 0
    private var num: Int = 0

    private var isSaved: Boolean = false
    private var choki: Boolean = true
    private var maxextimes = 15 // Initial value
    private var countVolume: Float = 1.0f
    private var _workoutId = 4  //Ashijanken
    private var workmenu: String = ""
    private var speedTime = 1000L

    lateinit var soundPool: SoundPool
    private var sndstr = 0
    private var sndend = 0
     private var sounds = mutableListOf<Int>()
    private var sndgo = 0
    private var sndchoki = 0
    private var sndurachoki = 0
    private var sndpa = 0


    private lateinit var _helper: DatabaseHelper

    lateinit var tv: TextView
    lateinit var tv2: TextView

    private lateinit var textmenu: TextView
    private lateinit var tvexpla: TextView
    private lateinit var btnback: Button
    private lateinit var btnstart: Button
    private lateinit var btnstop: Button
    private lateinit var btnrerstart: Button
    private lateinit var btnyoutube: Button
    private lateinit var btnChangeTimes: Button
    private lateinit var btnspeed: Button

    // Handler & Runnable（タイマー処理）
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {

            timeCount++
            num++
            if (extimes < maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "${extimes + 1}/$maxextimes 回"
                        tv2.text = getString(R.string.goo)
                        soundPool.play(sndgo, countVolume, countVolume, 0, 0, 1.0f)
                    }

                    2 -> {
                        if (choki) {
                            choki = false
                            tv2.text = getString(R.string.choki)
                            soundPool.play(sndchoki, countVolume, countVolume, 0, 0, 1.0f)
                        } else {
                            tv2.text = getString(R.string.urachoki)
                            soundPool.play(sndurachoki, countVolume, countVolume, 0, 0, 1.0f)
                            choki = true
                        }
                    }

                    3 -> {
                        tv2.text = getString(R.string.pa)
                        soundPool.play(sndpa, countVolume, countVolume, 0, 0, 1.0f)
                        num = 0; extimes++
                    }
                }
                // ← これ追加
                handler.postDelayed(this, 1000)

            } else {
                handler.removeCallbacks(this)
                btnstart.isEnabled = true
                btnstop.isEnabled = false
                btnrerstart.isEnabled = false
                btnback.isEnabled = true
                btnyoutube.isEnabled = true
                btnChangeTimes.isEnabled = true
                btnspeed.isEnabled = true
                if (!isSaved) {
                    RecordManager.saveRecord(this@Ashijanken, "05${workmenu}")
                    isSaved = true
                }
                speedTime = 1000L
                tv2.text = getString(R.string.good_job)
                soundPool.play(sndend, countVolume, countVolume, 0, 0, 1.0f)
                extimes = 0
            }
        }
    }
    private fun loadSettingsTick() {   //データベースから読み込む
        val db = _helper.writableDatabase
        val sql = "SELECT times FROM workouttimes WHERE _id = $_workoutId"
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToNext()) {
            val t = cursor.getString(0)
            maxextimes =  t.toIntOrNull() ?: 10    //maxextimesをsqlで見つけたｔに DBに無ければ10

        }
        cursor.close()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _helper = DatabaseHelper(applicationContext)
        setContentView(R.layout.activity_sub)

        //画面をスリープさせない
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //タイトルバー非表示
        supportActionBar?.hide()

        //ボリューム
        countVolume = intent.getFloatExtra("TEXT_KEY", 1.0f)

        //ワークアウト番号
        val listnum: Int = intent.getIntExtra("TEXT_KEY2", 0)
        //ワークアウト名のリスト
        val lvmenu = resources.getStringArray(R.array.lv_menu)
        workmenu = lvmenu[listnum]      // ワークアウト名をクラス変数へ代入

        // UIパーツの取得（クラス変数に代入）
        tv = findViewById(R.id.tv)
        tv2 = findViewById(R.id.tv2)
        textmenu = findViewById(R.id.textmenu)
        tvexpla = findViewById(R.id.tvexpla)
        btnback = findViewById(R.id.btnback)
        btnstart = findViewById(R.id.btStart)
        btnstop = findViewById(R.id.btStop)
        btnrerstart = findViewById(R.id.btnrestart)
        btnyoutube = findViewById(R.id.youtube)
        btnChangeTimes = findViewById(R.id.button2)
        btnspeed = findViewById(R.id.btspeed)

        //最初はStop　Restartを押せないように
        btnstop.isEnabled = false
        btnrerstart.isEnabled = false

        //ワークアウト名
        textmenu.text = workmenu
        //説明文
        tvexpla.text = "足の指でグー、チョキ(裏)　パーをする。" +
                "\nグー指はなるべく深く曲げる。チョキ、パーはなるべく大きく開く" +
                "\n\nグー　チョキ　パーを15回標準"


        // サウンド初期化
        val aa0 = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        soundPool = SoundPool.Builder().setAudioAttributes(aa0).setMaxStreams(3).build()
        sndstr = soundPool.load(this, R.raw.start, 1)
        sndend = soundPool.load(this, R.raw.goodjob, 1)
        sndgo = soundPool.load(this, R.raw.gu, 1)
        sndchoki = soundPool.load(this, R.raw.choki, 1)
        sndurachoki = soundPool.load(this, R.raw.urachoki, 1)
        sndpa = soundPool.load(this, R.raw.pa, 1)

        // 各種クリックリスナー
        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@Ashijanken, MainActivity2::class.java)
            intent2.putExtra("TEXT_KEY4", workmenu)
            intent2.putExtra("TEXT_KEY5", listnum)
            startActivity(intent2)
        }

        btnyoutube.setOnClickListener {
            val intent = Intent(this@Ashijanken, Youtube::class.java)
            val yID = "p6agyQN2gco".trim()
            intent.putExtra("yID", yID) //URLを転送
            startActivity(intent)
        }

        btnstart.setOnClickListener {
            loadSettingsTick()
            btnstart.isEnabled = false
            btnstop.isEnabled = true
            btnrerstart.isEnabled = false
            btnback.isEnabled = false
            btnChangeTimes.isEnabled = false
            btnyoutube.isEnabled = false
            btnspeed.isEnabled = false
            speedTime = 1000L
            isSaved = false
            extimes = 0
            num = -3
            tv.text = "1/$maxextimes 回"
            tv2.text = "始めます"
            soundPool.play(sndstr, countVolume, countVolume, 0, 0, 1.0f)
            loadSettingsTick()
            handler.post(runnable)
        }

        btnstop.setOnClickListener {
            btnstart.isEnabled = true
            btnstop.isEnabled = false
            btnrerstart.isEnabled = true
            btnback.isEnabled = true
            btnChangeTimes.isEnabled = true
            btnyoutube.isEnabled = true
            btnspeed.isEnabled = true
            speedTime = 1000L
            handler.removeCallbacks(runnable)
        }

        btnrerstart.setOnClickListener {
            btnstart.isEnabled = false
            btnstop.isEnabled = true
            btnrerstart.isEnabled = false
            btnback.isEnabled = false
            btnChangeTimes.isEnabled = false
            btnyoutube.isEnabled = false
            btnspeed.isEnabled = false
            handler.post(runnable) // タイマー再開
        }

        btnspeed.setOnClickListener {
            btnstart.isEnabled = false
            btnstop.isEnabled = true
            btnrerstart.isEnabled = false
            btnback.isEnabled = false
            btnChangeTimes.isEnabled = false
            btnyoutube.isEnabled = false
            btnspeed.isEnabled = false
            speedTime = 500L
            handler.removeCallbacks(runnable)
            handler.post(runnable)
        }

        btnback.setOnClickListener {
            soundPool.release()
            finish()
        }
        loadSettingsTick()
    }

     override fun onResume() {
        super.onResume()
        loadSettingsTick()
        tv.text = "1/$maxextimes 回"   // ← UIも更新
    }

    override fun onDestroy() {
            super.onDestroy()
            // 停止
            handler.removeCallbacks(runnable)
    }
}
