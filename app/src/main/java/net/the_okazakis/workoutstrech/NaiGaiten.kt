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


class NaiGaiten : AppCompatActivity() {

    private var timeCount = 0
    private var extimes: Int = 0
    private var num: Int = 0
    private var isSaved: Boolean = false
    private var count: Boolean = false
    private var maxextimes = 15 // Initial value
    private var countVolume: Float = 1.0f
    private var _workoutId = 11
    private var workmenu: String = ""
    private var speedTime = 1000L

    lateinit var soundPool: SoundPool
    private var sndstr = 0
    private var sndend = 0
     private var sounds = mutableListOf<Int>()
    private var snd1 = 0
    private var snd2 = 0
    private var snd3 = 0
    private var snd4 = 0
    private var snd5 = 0

    private var sndslowopen = 0
    private var sndslowclose = 0

    private var isStartWaiting : Boolean = false
    private var startDelay: Long = 900 // 0.9秒後に開始

    private lateinit var _helper: DatabaseHelper

    lateinit var tv: TextView
    lateinit var tv2: TextView

    lateinit var btnback: Button
    lateinit var btnstart: Button
    lateinit var btnstop: Button
    lateinit var btnrerstart: Button
    lateinit var btnyoutube: Button
    lateinit var btnChangeTimes: Button
    lateinit var btnspeed: Button


    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (extimes < maxextimes) {  //続ける条件　　maxextimeまで

                when (num) {

                    2 -> {
                        tv.text = "${extimes + 1}/$maxextimes 回"
                        tv2.text = getString(R.string.slow_open)
                        soundPool.play(sndslowopen, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    5 -> {
                        tv2.text = getString(R.string.slow_close)
                        soundPool.play(sndslowclose,countVolume,countVolume,0,0,1.0f)
                        extimes ++ ;num = 0
                    }
                    else -> {}
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
                speedTime = 1000L
                if (!isSaved) {
                    RecordManager.saveRecord(this@NaiGaiten, "12${workmenu}")
                    isSaved = true
                }
                tv2.text = getString(R.string.good_job)
                soundPool.play(sndend, countVolume, countVolume, 0, 0, 1.0f)
                extimes = 0
            }
        }
    }

    private fun loadSettingsTick() {
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
        _helper = DatabaseHelper(applicationContext)  // ←これ追加
        setContentView(R.layout.activity_sub)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        supportActionBar?.hide()

        countVolume = intent.getFloatExtra("TEXT_KEY", 1.0f)
        val listnum: Int = intent.getIntExtra("TEXT_KEY2", 0)

        val lvmenu = resources.getStringArray(R.array.lv_menu)
        workmenu = lvmenu[listnum]
        //val tv: TextView = findViewById(R.id.tv)
        tv = findViewById(R.id.tv)
        tv2 = findViewById(R.id.tv2)
        val textmenu: TextView = findViewById(R.id.textmenu)
        val tvexpla: TextView = findViewById(R.id.tvexpla)

        tvexpla.text =
            "内もも(内転筋)のストレッチ。\n床に座り、足を前に出し足裏を合わせる。または、床に寝て両膝を曲げて揃える。" +
                    "\n両足を外に倒し足を広げる。広げた時に少し手で押さえ、力を加え足を開く。" +
                    "\n\n10回が1セット。1セットが標準"
        btnback = findViewById(R.id.btnback)
        btnstart = findViewById(R.id.btStart)
        btnstop = findViewById(R.id.btStop)
        btnrerstart = findViewById(R.id.btnrestart)
         btnyoutube = findViewById(R.id.youtube)
        btnChangeTimes = findViewById(R.id.button2)
        btnspeed = findViewById(R.id.btspeed)
        btnChangeTimes.visibility = android.view.View.VISIBLE

        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@NaiGaiten, MainActivity2::class.java)
            intent2.putExtra("TEXT_KEY4", workmenu)
            intent2.putExtra("TEXT_KEY5", listnum)
            startActivity(intent2)
        }

        //btnyoutube.visibility = android.view.View.GONE // No Youtube for this one

        textmenu.text = workmenu
        btnstop.isEnabled = false
        btnrerstart.isEnabled = false

        val aa0 = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        soundPool = SoundPool.Builder().setAudioAttributes(aa0).setMaxStreams(3).build()
        sndstr = soundPool.load(this, R.raw.start, 1)
        sndend = soundPool.load(this, R.raw.goodjob, 1)
        sndslowopen = soundPool.load(this, R.raw.slowopen, 1)
        sndslowclose = soundPool.load(this, R.raw.slowclose, 1)
        snd1 = soundPool.load(this, R.raw.v1, 1)
        snd2 = soundPool.load(this, R.raw.v2, 1)
        snd3 = soundPool.load(this, R.raw.v3, 1)
        snd4 = soundPool.load(this, R.raw.v4, 1)
        snd5 = soundPool.load(this, R.raw.v5, 1)

        tv.text = "1/$maxextimes 回"

        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@NaiGaiten, MainActivity2::class.java)
            intent2.putExtra("TEXT_KEY4", workmenu)
            intent2.putExtra("TEXT_KEY5", listnum)
            startActivity(intent2)
        }

        btnyoutube.setOnClickListener {
            val intent = Intent(this@NaiGaiten, Youtube::class.java)
            val yID = "https://youtu.be/kM_M8TeN1qY?t=155"
            intent.putExtra("yID", yID) //ボリュームの値を転送
            startActivity(intent)
        }
        btnstart.setOnClickListener {
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
            num = -2
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

    override fun onDestroy() {
        _helper.close()
        soundPool.release()
        super.onDestroy()
    }
    // 👇ここに書く（onCreateの下）
    override fun onResume() {
        super.onResume()
        loadSettingsTick()
        tv.text = "1/$maxextimes 回"   // ← UIも更新
    }
}
