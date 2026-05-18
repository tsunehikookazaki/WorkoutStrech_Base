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


class Ashijanken : AppCompatActivity() {

    lateinit var soundPool: SoundPool
    private var sndstr = 0
    private var sndend = 0
    private var sndgo = 0
    private var sndchoki = 0
    private var sndurachoki = 0
    private var sndpa = 0
    private var extimes: Int = 0
    private var num: Int = 0
    private var isSaved: Boolean = false
    private var choki: Boolean = true
    private var maxextimes = 15 // Initial value
    private var _workoutId = 4
    private lateinit var _helper: DatabaseHelper

    lateinit var tv: TextView
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
        tv = findViewById(R.id.tv)   // ← これが正しい
        val tv2: TextView = findViewById(R.id.tv2)
        val textmenu: TextView = findViewById(R.id.textmenu)
        val tvexpla: TextView = findViewById(R.id.tvexpla)

        tvexpla.text = "足の指でグー、チョキ(裏)　パーをする。" +
                "\nグー指はなるべく深く曲げる。チョキ、パーはなるべく大きく開く" +
                "\n\nグー　チョキ　パーを15回標準"

        val btnback: Button = findViewById(R.id.btnback)
        val btnstart: Button = findViewById(R.id.btStart)
        val btnstop: Button = findViewById(R.id.btStop)
        val btnrerstart: Button = findViewById(R.id.btnrestart)
        val btnyoutube: Button = findViewById(R.id.youtube)
        val btnChangeTimes: Button = findViewById(R.id.button2)
        btnChangeTimes.visibility = android.view.View.VISIBLE

        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@Ashijanken, MainActivity2::class.java)
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
        sndgo = soundPool.load(this, R.raw.gu, 1)
        sndchoki = soundPool.load(this, R.raw.choki, 1)
        sndurachoki = soundPool.load(this, R.raw.urachoki, 1)
        sndpa = soundPool.load(this, R.raw.pa, 1)

        tv.text = "1/$maxextimes 回"

        btnyoutube.setOnClickListener {
            val intent = Intent(this@Ashijanken, Youtube::class.java)
            val videoId = "p6agyQN2gco".trim()
            val startTime = 102
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
            btnChangeTimes.isEnabled = false
            btnyoutube.isEnabled = false

            isSaved = false
            extimes = 0
            num = -4
            tv.text = "1/$maxextimes 回"
            tv2.text = "始めます"
            soundPool.play(sndstr, countVolume, countVolume, 0, 0, 1.0f)
            loadSettingsTick()
            chronometer.start()
            chronometer.base = SystemClock.elapsedRealtime()
        }

        btnstop.setOnClickListener {
            btnstart.isEnabled = true
            btnstop.isEnabled = false
            btnrerstart.isEnabled = true
            btnback.isEnabled = true
            btnChangeTimes.isEnabled = true
            btnyoutube.isEnabled = true
            chronometer.stop()
        }

        btnrerstart.setOnClickListener {
            btnstart.isEnabled = false
            btnstop.isEnabled = true
            btnrerstart.isEnabled = false
            btnback.isEnabled = false
            btnChangeTimes.isEnabled = false
            btnyoutube.isEnabled = false
            chronometer.start()
        }

        btnback.setOnClickListener {
            soundPool.release()
            finish()
        }

        //Youtubeのリンクを開く

        btnyoutube.setOnClickListener {

            val intent = Intent(this@Ashijanken, Youtube::class.java)
            val yID = "https://youtu.be/p6agyQN2gco?t=96"
            intent.putExtra("yID", yID) //URLを転送
            startActivity(intent)
        }


        loadSettingsTick()
        chronometer.setOnChronometerTickListener {

            num++
            if (extimes < maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "${extimes+1}/$maxextimes 回"
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
            } else {
                chronometer.stop()
                btnstart.isEnabled = true
                btnstop.isEnabled = false
                btnrerstart.isEnabled = false
                btnback.isEnabled = true
                btnyoutube.isEnabled = true
                btnChangeTimes.isEnabled = true
                if (!isSaved) {
                    RecordManager.saveRecord(this, "05${workmenu}")
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