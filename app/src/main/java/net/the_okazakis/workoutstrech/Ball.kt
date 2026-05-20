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


class Ball : AppCompatActivity() {

    private var timeCount = 0
    private var extimes: Int = 0
    private var num: Int = 0
    private var isSaved: Boolean = false
    private var count: Boolean = false
    private var maxextimes = 15 // Initial value
    private var countVolume: Float = 1.0f
    private var _workoutId = 3
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
    private var snd6 = 0
    private var snd7 = 0
    private var snd8 = 0
    private var snd9 = 0
    private var snd10 = 0
    private var sndkeep = 0
    private var sndloosen = 0
    private var sndnon = 0
    private var sndtsubu = 0

    private lateinit var _helper: DatabaseHelper

    lateinit var tv: TextView
    lateinit var tv2: TextView

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
                tv.text = "${extimes+1}/$maxextimes 回"//表示用回数 extimesは0から
                when(num) {
                    0->{tv2.text = "つぶして"
                        soundPool.play(sndtsubu, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    in 0..10-> {
                        if(num in 1..10) {
                            soundPool.play(sounds[num - 1], countVolume, countVolume, 0, 0, 1.0f)
                        }

                        tv2.text = "$num 秒"
                    }
                    11 -> {
                        soundPool.play(sndloosen, countVolume, countVolume, 0, 0, 1.0f)
                        //tv2.text = "$extimes extimes"
                        extimes++   //回数を増やす
                        tv2.text = "ゆるめて"
                        num = -1
                    }
                    else ->{}
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
                    RecordManager.saveRecord(this@Ball, "04${workmenu}")
                    isSaved = true

                    tv2.text = getString(R.string.good_job)
                    soundPool.play(sndend, countVolume, countVolume, 0, 0, 1.0f)
                }
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
        tv = findViewById(R.id.tv)   // ← これが正しい
        tv2 = findViewById(R.id.tv2)
        val textmenu: TextView = findViewById(R.id.textmenu)
        val tvexpla: TextView = findViewById(R.id.tvexpla)

        tvexpla.text =
            "膝裏ボール潰し\n椅子に座り、膝の裏に小さめのボール(または丸めたタオル)を挟み、ぎゅーっと5〜10秒間押しつぶす。10秒3回で1セット。1セット標準。"

        btnback = findViewById(R.id.btnback)
        btnstart = findViewById(R.id.btStart)
        btnstop = findViewById(R.id.btStop)
        btnrerstart = findViewById(R.id.btnrestart)
         btnyoutube = findViewById(R.id.youtube)
        btnChangeTimes = findViewById(R.id.button2)
        btnspeed = findViewById(R.id.btspeed)
        btnChangeTimes.visibility = android.view.View.VISIBLE

        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@Ball, MainActivity2::class.java)
            intent2.putExtra("TEXT_KEY4", workmenu)
            intent2.putExtra("TEXT_KEY5", listnum)
            startActivity(intent2)
        }



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
        snd1 = soundPool.load(this, R.raw.v1, 1)
        snd2 = soundPool.load(this, R.raw.v2, 1)
        snd3 = soundPool.load(this, R.raw.v3, 1)
        snd4 = soundPool.load(this, R.raw.v4, 1)
        snd5 = soundPool.load(this, R.raw.v5, 1)
        snd6 = soundPool.load(this, R.raw.v6, 1)
        snd7 = soundPool.load(this, R.raw.v7, 1)
        snd8 = soundPool.load(this, R.raw.v8, 1)
        snd9 = soundPool.load(this, R.raw.v9, 1)
        snd10 = soundPool.load(this, R.raw.v10, 1)
        sndkeep = soundPool.load(this, R.raw.v10, 1) // keep10sec was missing, using v10 as fallback
        sndloosen = soundPool.load(this, R.raw.loosen, 1)
        sndnon = soundPool.load(this, R.raw.nosound, 1)
        sndtsubu = soundPool.load(this, R.raw.tsubushite, 1)
        sounds = mutableListOf(snd1, snd2, snd3, snd4, snd5, snd6, snd7, snd8, snd9, snd10)

        // 各種クリックリスナー
        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@Ball, MainActivity2::class.java)
            intent2.putExtra("TEXT_KEY4", workmenu)
            intent2.putExtra("TEXT_KEY5", listnum)
            startActivity(intent2)
        }

        // Youtubeボタン
        btnyoutube.setOnClickListener {
            val intent = Intent(this@Ball, Youtube::class.java)
            val yID = "https://youtu.be/MVzREF4j1lI?t=4"
            intent.putExtra("yID", yID) //URLを転送
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
            num = -5
            tv2.text = "始めます"
            soundPool.play(sndstr, countVolume, countVolume, 0, 0, 1.0f)
            loadSettingsTick()
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
