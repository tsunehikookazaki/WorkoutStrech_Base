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

class HipaddactionBall : AppCompatActivity() {

    lateinit var soundPool: SoundPool
    private var sndstr = 0
    private var sndend = 0
    private var snd1 = 0 //１”いち”
    private var snd2 = 0 //２”に”
    private var snd3 = 0 //３”さん”
    private var snd4 = 0 //４”よん”
    private var snd5 = 0 //５”ご”
    private var sndpi = 0 //Pi
    private var sndtsubushite = 0
    private var sndbreak = 0
    private var snd10re = 0
    private var num: Int = 0
    private var isSaved: Boolean = false
    private var isStart= true      // スタートか
    private var _workoutId = -1
    private lateinit var _helper: DatabaseHelper
    private var maxextimes: Int = 3
    private var extimes: Int = 1
    private var listnumLocal = 0

    lateinit var tv: TextView
    private fun loadSettingsTick() {
        val db = _helper.writableDatabase
        val sql = "SELECT * FROM workouttimes WHERE _id = $_workoutId"
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToNext()) {
            val t = cursor.getString(cursor.getColumnIndex("times"))
            val idxReps = cursor.getColumnIndex("reps")
            val r = if (idxReps != -1) cursor.getString(idxReps) ?: "50" else "50"
            maxextimes = t.toIntOrNull() ?: 3    // maxSetsをsqlで見つけたｔに DBに無ければ10
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

        val chronometer = findViewById<Chronometer>(R.id.chronometer)
        val countVolume: Float = intent.getFloatExtra("TEXT_KEY", 1.0f)
        val listnum: Int = intent.getIntExtra("TEXT_KEY2", 0)
        listnumLocal = listnum
        _workoutId = listnum

        val lvmenu = resources.getStringArray(R.array.lv_menu)
        val workmenu = lvmenu[listnum]
        //val tv: TextView = findViewById(R.id.tv)
        tv = findViewById(R.id.tv)
        val tv2: TextView = findViewById(R.id.tv2)
        val textmenu: TextView = findViewById(R.id.textmenu)
        val tvexpla: TextView = findViewById(R.id.tvexpla)

        tvexpla.text = "椅子に座って(寝ながらやってもOK)、膝の間にボール(枕、クッションでもOK)をはさむ。\nモモに力を入れてボールを挟んでつぶす。力をぬいて緩める。再度つぶして、力を抜く" +
                "これをリズミカルに繰り返す。\n\n50回で1セット　3セット標準" +
                "\n\nボールは100均一で"

        val btnback: Button = findViewById(R.id.btnback)
        val btnstart: Button = findViewById(R.id.btStart)
        val btnstop: Button = findViewById(R.id.btStop)
        val btnrerstart: Button = findViewById(R.id.btnrestart)
        val btnyoutube: Button = findViewById(R.id.youtube)
        val btnChangeTimes: Button = findViewById(R.id.button2)

        textmenu.text = workmenu
        btnstop.isEnabled = false
        btnrerstart.isEnabled = false

        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@HipaddactionBall, MainActivity2::class.java)
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
        sndtsubushite = soundPool.load(this, R.raw.tsubushite, 1)
        sndbreak = soundPool.load(this, R.raw.takeabreak, 1)
        sndpi = soundPool.load(this, R.raw.pi, 1)
        snd1 = soundPool.load(this, R.raw.v1, 1)
        snd2 = soundPool.load(this, R.raw.v2, 1)
        snd3 = soundPool.load(this, R.raw.v3, 1)
        snd4 = soundPool.load(this, R.raw.v4, 1)
        snd5 = soundPool.load(this, R.raw.v5, 1)
        snd10re = soundPool.load(this, R.raw.relax10sec, 1)
        val sounds = listOf(snd1, snd2, snd3, snd4, snd5)

        snd10re = soundPool.load(this, R.raw.relax10sec, 1)


        btnstart.setOnClickListener {
            btnstart.isEnabled = false
            btnstop.isEnabled = true
            btnrerstart.isEnabled = false
            btnback.isEnabled = false
            btnChangeTimes.isEnabled = false
            btnyoutube.isEnabled = false

            isSaved = false
            isStart = true
            extimes = 1
            num = -4
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
            val intent = Intent(this@HipaddactionBall, Youtube::class.java)
            val yID = "https://youtu.be/n7Munc_C_Xo?t=3"
            intent.putExtra("yID", yID) //URLを転送
            startActivity(intent)
        }

        loadSettingsTick()
        chronometer.setOnChronometerTickListener {

        if (extimes <= maxextimes) {
                num++
                tv.text = "${extimes}/$maxextimes セット"
                when (num) {
                    -5 -> {
                        if(!isStart){
                        tv2.text = "ちょっと休憩"
                        soundPool.play(sndbreak, countVolume, countVolume, 0, 0, 1.0f)
                        }
                    }
                    -4 ->{
                        if(!isStart){
                            tv2.text = "${num * (-1)}"
                        }
                    }
                    in -3..-1 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                            soundPool.play(sounds[num * (-1) - 1],countVolume, countVolume, 0, 0, 1.0f)
                        }
                    }

                    0 ->{
                        if(!isStart){
                            tv2.text = "${num * (-1)}"
                            soundPool.play(sndpi, countVolume, countVolume, 0, 0, 1.0f)
                        }
                    }
                    in 1..50 -> {
                        tv2.text =  getString(R.string.tubu) +"  $num/50 回 "
                        soundPool.play(sndtsubushite, countVolume, countVolume, 1, 0, 1.0f)
                    }

                    51 -> {
                        isStart = false
                        num = -6
                        extimes ++
                    }


                    else ->{}
                }



            } else {
                chronometer.stop()
                btnstart.isEnabled = true
                btnstop.isEnabled = false
                btnrerstart.isEnabled = false
                btnback.isEnabled = true
                btnChangeTimes.isEnabled = true
                btnyoutube.isEnabled = true
                if (!isSaved) {
                    RecordManager.saveRecord(this, "15${workmenu}")
                    isSaved = true
                }
                tv2.text = getString(R.string.good_job)
                soundPool.play(sndend, countVolume, countVolume, 0, 0, 1.0f)
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