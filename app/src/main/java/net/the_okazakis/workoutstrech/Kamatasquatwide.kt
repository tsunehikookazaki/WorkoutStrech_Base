package net.the_okazakis.workoutstrech

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.WindowManager
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.TextView

class Kamatasquatwide : AppCompatActivity() {

    lateinit var soundPool: SoundPool
    private var sndstr = 0
    private var sndend = 0
    private var sndsizunde = 0
    private var sndslowup = 0
    private var snd1 = 0
    private var snd2 = 0
    private var snd3 = 0
    private var sndtaete = 0
    private var extimes: Int = 1
    private var num: Int = 0
    private var isSaved: Boolean = false

    private var maxextimes = 10 // Initial value
    private var _workoutId = 20
    private lateinit var _helper: DatabaseHelper

    lateinit var tv: TextView
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

        val chronometer = findViewById<Chronometer>(R.id.chronometer)
        val countVolume: Float = intent.getFloatExtra("TEXT_KEY", 1.0f)
        val listnum: Int = intent.getIntExtra("TEXT_KEY2", 0)

        val lvmenu = resources.getStringArray(R.array.lv_menu)
        val workmenu = lvmenu[listnum]
        //val tv: TextView = findViewById(R.id.tv)
        tv = findViewById(R.id.tv)
        val tv2: TextView = findViewById(R.id.tv2)
        val textmenu: TextView = findViewById(R.id.textmenu)
        val tvexpla: TextView = findViewById(R.id.tvexpla)

        tvexpla.text = "両手を胸で組む。足は肩幅より10cm程度広げ、つま先を45度程度外に向ける。" +
                "\n息を吐きながら(吸いながら)、真下に沈み込むように、ゆっくり腰を下げる。" +
                "\n内ももが張るのに耐える。ゆっくり息を吸いながら(吐きながら)腰を上げる。" +
                "\n太ももの内側が張るような感じが大事。背中が曲がらないように。" +
                "膝が内側に入らないように気を付ける。" +
                "呼吸を止めない\n\n10回で1セット。1セット標準"

        val btnback: Button = findViewById(R.id.btnback)
        val btnstart: Button = findViewById(R.id.btStart)
        val btnstop: Button = findViewById(R.id.btStop)
        val btnrerstart: Button = findViewById(R.id.btnrestart)
        val btnyoutube: Button = findViewById(R.id.youtube)
        val btnChangeTimes: Button = findViewById(R.id.button2)
        btnChangeTimes.visibility = android.view.View.VISIBLE

        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@Kamatasquatwide, MainActivity2::class.java)
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
        sndslowup = soundPool.load(this, R.raw.slowup, 1)
        sndsizunde = soundPool.load(this, R.raw.sizunde, 1)
        snd1 = soundPool.load(this, R.raw.v1, 1)
        snd2 = soundPool.load(this, R.raw.v2, 1)
        snd3 = soundPool.load(this, R.raw.v3, 1)
        sndtaete = soundPool.load(this, R.raw.taete, 1)

        tv.text = "1/$maxextimes 回"

        btnyoutube.setOnClickListener {
            val intent = Intent(this@Kamatasquatwide, Youtube::class.java)
            val videoId = "S3DJ0ke9624".trim()
            val startTime = 1
            val isMuted = true
            val isAutoplay = true
            intent.putExtra("yID", videoId)
            intent.putExtra("startTime", startTime)
            intent.putExtra("isMuted", isMuted)
            intent.putExtra("isAutoplay", isAutoplay)
            startActivity(intent)
        }

        btnstart.setOnClickListener {
            btnstart.isEnabled = false
            btnstop.isEnabled = true
            btnrerstart.isEnabled = false
            btnback.isEnabled = false
            btnyoutube.isEnabled = false
            btnyoutube.isEnabled = false

            isSaved = false
            extimes = 1
            num = -4
            tv.text = "1/$maxextimes 回"
            tv2.text = "始めます"
            soundPool.play(sndstr, countVolume, countVolume, 0, 0, 1.0f)
            chronometer.start()
            chronometer.base = SystemClock.elapsedRealtime()
        }

        btnstop.setOnClickListener {
            btnstart.isEnabled = true
            btnstop.isEnabled = false
            btnrerstart.isEnabled = true
            btnback.isEnabled = true
            btnyoutube.isEnabled = true
            chronometer.stop()
        }

        btnrerstart.setOnClickListener {
            btnstart.isEnabled = false
            btnstop.isEnabled = true
            btnrerstart.isEnabled = false
            btnback.isEnabled = false
            btnyoutube.isEnabled = false
            chronometer.start()
        }

        btnback.setOnClickListener {
            soundPool.release()
            finish()
        }
        //Youtubeのリンクを開く

        btnyoutube.setOnClickListener {
            val intent = Intent(this@Kamatasquatwide, Youtube::class.java)
            val yID = "https://youtu.be/S3DJ0ke9624"
            intent.putExtra("yID",yID ) //ボリュームの値を転送
            startActivity(intent)
        }




        loadSettingsTick()
        chronometer.setOnChronometerTickListener {


            num++
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "$extimes/$maxextimes 回"
                        tv2.text = "ゆっくり沈んで"
                        soundPool.play(sndsizunde, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    2 -> {
                        tv2.text = "1"
                        soundPool.play(snd1, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    3 -> {
                        tv2.text = "2"
                        soundPool.play(snd2, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    4 -> {
                        tv2.text = "3"
                        soundPool.play(snd3, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    6 -> {
                        tv2.text = "耐えて"
                        soundPool.play(sndtaete, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    7 -> {
                        tv2.text = "耐えて"
                        soundPool.play(sndtaete, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    8 -> {
                        tv2.text = getString(R.string.slow_up)
                        soundPool.play(sndslowup, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    9 -> {
                        tv2.text = "1"
                        soundPool.play(snd1, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    10 -> {
                        tv2.text = "2"
                        soundPool.play(snd2, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    11 -> {
                        tv2.text = "3"
                        soundPool.play(snd3, countVolume, countVolume, 0, 0, 1.0f)
                        num = 0; extimes++
                    }
                }
            } else {
                chronometer.stop()
                btnstart.isEnabled = true
                btnstop.isEnabled = false
                btnrerstart.isEnabled = false
                btnback.isEnabled = true
                btnyoutube.isEnabled = true
                if (!isSaved) {
                    RecordManager.saveRecord(this, "21${workmenu}")
                    isSaved = true
                }
                tv2.text = getString(R.string.good_job)
                soundPool.play(sndend, countVolume, countVolume, 0, 0, 1.0f)
                extimes = 0
            }
        }
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
