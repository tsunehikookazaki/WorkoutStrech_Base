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


class HipabdactionBelt : AppCompatActivity() {

    private var timeCount = 0
    private var extimes: Int = 0
    private var num: Int = 0
    private var isSaved: Boolean = false
    private var count: Boolean = false
    private var maxextimes = 15 // Initial value

    private var nn: Int = 1
    private var isStart = true      // スタートか
    private var countVolume: Float = 1.0f
    private var _workoutId = 13
    private var workmenu: String = ""
    private var speedTime = 1000L

    lateinit var soundPool: SoundPool
    private var sndstr = 0
    private var sndend = 0
     private var sounds = mutableListOf<Int>()
    private var snd1 = 0 //１”いち”
    private var snd2 = 0 //２”に”
    private var snd3 = 0 //３”さん”
    private var snd4 = 0 //４”よん”
    private var snd5 = 0 //５”ご”
    private var sndpi = 0 //Pi
    private var sndbreak = 0
    private var sndkeep1s = 0
    private var slowclose = 0
    private var open = 0
    private var snd10re = 0

    lateinit var tv: TextView
    lateinit var tv2: TextView
    private lateinit var _helper: DatabaseHelper
    private lateinit var btnback: Button
    private lateinit var btnstart: Button
    private lateinit var btnstop: Button
    private lateinit var btnrerstart: Button
    private lateinit var btnyoutube: Button
    private lateinit var btnChangeTimes: Button
    private lateinit var btnspeed: Button
    private var listnumLocal = 0
    private val handler = Handler(Looper.getMainLooper())

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (extimes <= maxextimes) {
                when (num) {
                    -5 -> {
                        if (!isStart) {
                            tv2.text = "ちょっと休憩"
                            soundPool.play(sndbreak, countVolume, countVolume, 0, 0, 1.0f)
                        }
                    }

                    -4 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                        }
                    }

                    in -3..-1 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                            soundPool.play(
                                sounds[num * (-1) - 1],
                                countVolume,
                                countVolume,
                                0,
                                0,
                                1.0f
                            )
                        }
                    }

                    0 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                            soundPool.play(sndpi, countVolume, countVolume, 0, 0, 1.0f)
                        }
                    }


                    in 1..90 -> {    //%3
                        if ((num % 3) == 1) {
                            tv.text = "$extimes /$maxextimes セット"
                            tv2.text = getString(R.string.open) + " $nn/30"
                            soundPool.play(open, countVolume, countVolume, 1, 0, 1.0f)
                        }
                        if ((num % 3) == 2) {
                            tv2.text = getString(R.string.keep1s) + " $nn/30"
                            soundPool.play(sndkeep1s, countVolume, countVolume, 1, 0, 1.0f)
                        }
                        if ((num % 3) == 0) {
                            tv2.text = getString(R.string.slow_close) + " $nn/30"
                            soundPool.play(slowclose, countVolume, countVolume, 1, 0, 1.0f)

                            nn++
                        }
                    }

                    91 -> {
                        isStart = false
                        num = -6 // num++があるので　-5にするには -6
                        nn = 1
                        extimes++
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
                btnChangeTimes.isEnabled = true
                btnyoutube.isEnabled = true
                btnstop.isEnabled = true
                if (!isSaved) {
                    RecordManager.saveRecord(this@HipabdactionBelt, "14${workmenu}")
                    isSaved = true

                    tv2.text = getString(R.string.good_job)
                    soundPool.play(sndend, countVolume, countVolume, 0, 0, 1.0f)
                }
            }
        }
    }


    private fun loadSettingsTick() {
        val db = _helper.writableDatabase
        val sql = "SELECT * FROM workouttimes WHERE _id = $_workoutId"
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToNext()) {
            val t = cursor.getString(cursor.getColumnIndex("times"))
            val idxReps = cursor.getColumnIndex("reps")
            val r = if (idxReps != -1) cursor.getString(idxReps) ?: "30" else "30"
            maxextimes = t.toIntOrNull() ?: 10    // maxextimesをsqlで見つけたｔに DBに無ければ10

        }
        cursor.close()
    }

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _helper = DatabaseHelper(applicationContext)  // ←これ追加
        setContentView(R.layout.activity_sub)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        supportActionBar?.hide()


        countVolume = intent.getFloatExtra("TEXT_KEY", 1.0f)
        val listnum: Int = intent.getIntExtra("TEXT_KEY2", 0)
        listnumLocal = listnum
        _workoutId = listnum

        val lvmenu = resources.getStringArray(R.array.lv_menu)
        workmenu = lvmenu[listnum]
        //val tv: TextView = findViewById(R.id.tv)
        tv = findViewById(R.id.tv)   // ← これが正しい
        tv2 = findViewById(R.id.tv2)
        val textmenu: TextView = findViewById(R.id.textmenu)
        val tvexpla: TextView = findViewById(R.id.tvexpla)

        tvexpla.text =
            "椅子に座って足を少し開き、膝にゴムベルトを巻く。\n手をお尻の横に添えて、お尻に力を入れ膝をギュッと開く。" +
                    "\n1秒間キープして、ゆっくり閉じる" +
                    "\nこれを繰り返す。\n\n30回で1セット　3セット標準" +
                    "\n\nベルトは100均一で"

        btnback = findViewById(R.id.btnback)
        btnstart = findViewById(R.id.btStart)
        btnstop = findViewById(R.id.btStop)
        btnrerstart = findViewById(R.id.btnrestart)
         btnyoutube = findViewById(R.id.youtube)
        btnChangeTimes = findViewById(R.id.button2)
        btnspeed = findViewById(R.id.btspeed)

        textmenu.text = workmenu
        btnstop.isEnabled = false
        btnrerstart.isEnabled = false

        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@HipabdactionBelt, MainActivity2::class.java)
            intent2.putExtra("TEXT_KEY4", workmenu)
            intent2.putExtra("TEXT_KEY5", listnum)
            startActivity(intent2)
        }

        val aa0 = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        soundPool = SoundPool.Builder().setAudioAttributes(aa0).setMaxStreams(3).build()

        sndstr = soundPool.load(this, R.raw.start, 1)
        sndend = soundPool.load(this, R.raw.goodjob, 1)
        open = soundPool.load(this, R.raw.open, 1)
        slowclose = soundPool.load(this, R.raw.slowclose, 1)
        sndkeep1s = soundPool.load(this, R.raw.keep1sec, 1)
        sndbreak = soundPool.load(this, R.raw.takeabreak, 1)
        sndpi = soundPool.load(this, R.raw.pi, 1)
        snd1 = soundPool.load(this, R.raw.v1, 1)
        snd2 = soundPool.load(this, R.raw.v2, 1)
        snd3 = soundPool.load(this, R.raw.v3, 1)
        snd4 = soundPool.load(this, R.raw.v4, 1)
        snd5 = soundPool.load(this, R.raw.v5, 1)
        snd10re = soundPool.load(this, R.raw.relax10sec, 1)
        sounds =  mutableListOf(snd1, snd2, snd3, snd4, snd5)

        // 各種クリックリスナー
        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@HipabdactionBelt, MainActivity2::class.java)
            intent2.putExtra("TEXT_KEY4", workmenu)
            intent2.putExtra("TEXT_KEY5", listnum)
            startActivity(intent2)
        }

        btnyoutube.setOnClickListener {
            val intent = Intent(this@HipabdactionBelt, Youtube::class.java)
            val yID = "https://youtu.be/xZGLV-_eOEA"
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
            isSaved = false
            isStart = true
            extimes = 1
            num = -3
            nn = 1
            tv.text = "1/$maxextimes セット"
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



