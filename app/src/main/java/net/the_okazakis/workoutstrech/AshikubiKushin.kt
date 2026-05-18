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

class AshikubiKushin : AppCompatActivity() {

    lateinit var soundPool: SoundPool
    private var sndstr = 0
    private var sndend = 0
    private var sndstre = 0
    private var sndbent = 0
    private var extimes: Int = 0
    private var num: Int = 0
    private var isSaved: Boolean = false

    private var maxextimes = 15 // Initial value
    private var _workoutId = 5
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
        tv = findViewById(R.id.tv)   // ← これが正しい
        val tv2: TextView = findViewById(R.id.tv2)
        val textmenu: TextView = findViewById(R.id.textmenu)
        val tvexpla: TextView = findViewById(R.id.tvexpla)

        tvexpla.text = "膝を伸ばして座り、足首をゆっくり手前に曲げ、次に向こう側に伸ばす。\n" +
                "ふくらはぎ、足の甲が伸びているのを感じる。\n\n15回標準。"

        val btnback: Button = findViewById(R.id.btnback)
        val btnstart: Button = findViewById(R.id.btStart)
        val btnstop: Button = findViewById(R.id.btStop)
        val btnrerstart: Button = findViewById(R.id.btnrestart)
        val btnyoutube: Button = findViewById(R.id.youtube)
        val btnChangeTimes: Button = findViewById(R.id.button2)
        btnChangeTimes.visibility = android.view.View.VISIBLE

        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@AshikubiKushin, MainActivity2::class.java)
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
        sndstre = soundPool.load(this, R.raw.stretch, 1)
        sndbent = soundPool.load(this, R.raw.bend, 1)

        tv.text = "1/$maxextimes 回"

        btnstart.setOnClickListener {
            btnstart.isEnabled = false
            btnstop.isEnabled = true
            btnrerstart.isEnabled = false
            btnback.isEnabled = false
            btnyoutube.isEnabled = false
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

        btnyoutube.setOnClickListener {

            val intent = Intent(this@AshikubiKushin, Youtube::class.java)
            val yID = "https://youtu.be/IxC41pWD2Iw"
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
                        tv2.text = getString(R.string.foot_bent)
                        soundPool.play(sndbent, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    2 -> {
                        tv2.text = getString(R.string.foot_stre)
                        soundPool.play(sndstre, countVolume, countVolume, 0, 0, 1.0f)
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
                    RecordManager.saveRecord(this, "06${workmenu}")
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