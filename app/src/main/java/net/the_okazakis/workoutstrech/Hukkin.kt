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

class Hukkin : AppCompatActivity() {

    lateinit var soundPool: SoundPool
    private var sndstr = 0
    private var sndend = 0
    private var sndkeep20s = 0
    private var snd10re = 0
    private var slowup = 0
    private var slowdown = 0

    private var snd1 = 0 //１”いち”
    private var snd2 = 0 //２”に”
    private var snd3 = 0 //３”さん”
    private var snd4 = 0 //４”よん”
    private var snd5 = 0 //５”ご”
    private var snd6 = 0 //６
    private var snd7 = 0 //７
    private var snd8 = 0 //８
    private var snd9 = 0 //９
    private var snd10 = 0 //１０
    private var snd11 = 0 //１１
    private var snd12 = 0 //１２
    private var snd13 = 0 //１３
    private var snd14 = 0 //１４
    private var snd15 = 0 //１５
    private var snd16 = 0 //１６
    private var snd17 = 0 //１７
    private var snd18 = 0 //１８
    private var snd19 = 0 //１９
    private var snd20 = 0 //20
    private var sndpi = 0 //pi

    private var extimes: Int = 0
    private var num: Int = 0
    private var isSaved: Boolean = false

    private var _workoutId = -1
    private lateinit var _helper: DatabaseHelper
    private var maxextimes =3
    private var listnumLocal = 0
    private var isStart : Boolean = true

    lateinit var tv: TextView
    private fun loadSettingsTick() {
        val db = _helper.writableDatabase
        val sql = "SELECT * FROM workouttimes WHERE _id = $_workoutId"
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToNext()) {
            val t = cursor.getString(cursor.getColumnIndex("times"))
            val idxReps = cursor.getColumnIndex("reps")
            val r = if (idxReps != -1) cursor.getString(idxReps) ?: "20" else "20"
            maxextimes = t.toIntOrNull() ?: 3    //maxextimesをsqlで見つけたｔに DBに無ければ10

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
        listnumLocal = listnum
        _workoutId = listnum

        val lvmenu = resources.getStringArray(R.array.lv_menu)
        val workmenu = lvmenu[listnum]
        //val tv: TextView = findViewById(R.id.tv)
        tv = findViewById(R.id.tv)
        val tv2: TextView = findViewById(R.id.tv2)
        val textmenu: TextView = findViewById(R.id.textmenu)
        val tvexpla: TextView = findViewById(R.id.tvexpla)
        tvexpla.text = "床に寝て、両足を床につかないよう10cm位上げ、20秒キープ\n\n高く上げると効果が薄い" +
                "\n\n20秒で1セット、３セット標準"

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
            val intent2 = Intent(this@Hukkin, MainActivity2::class.java)
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
        slowup = soundPool.load(this, R.raw.slowup, 1)
        slowdown = soundPool.load(this, R.raw.slowdown, 1)
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
        snd11 = soundPool.load(this, R.raw.v11, 1)
        snd12 = soundPool.load(this, R.raw.v12, 1)
        snd13 = soundPool.load(this, R.raw.v13, 1)
        snd14 = soundPool.load(this, R.raw.v14, 1)
        snd15 = soundPool.load(this, R.raw.v15, 1)
        snd16 = soundPool.load(this, R.raw.v16, 1)
        snd17 = soundPool.load(this, R.raw.v17, 1)
        snd18 = soundPool.load(this, R.raw.v18, 1)
        snd19 = soundPool.load(this, R.raw.v19, 1)
        snd20 = soundPool.load(this, R.raw.v20, 1)
        sndpi = soundPool.load(this, R.raw.pi, 1)
        val sounds = listOf(snd1, snd2, snd3, snd4, snd5, snd6, snd7, snd8, snd9, snd10, snd11, snd12, snd13, snd14, snd15, snd16, snd17, snd18, snd19, snd20)
        snd10re = soundPool.load(this, R.raw.relax10sec, 1)
        sndkeep20s = soundPool.load(this, R.raw.keep20s, 1)


        btnstart.setOnClickListener {
            btnstart.isEnabled = false
            btnstop.isEnabled = true
            btnrerstart.isEnabled = false
            btnback.isEnabled = false
            btnChangeTimes.isEnabled = false
            btnyoutube.isEnabled = false

            isSaved = false
            extimes = 1


            tv.text = "1/$maxextimes セット"
            isStart = true
            num = -3            // ← ここから「3,2,1」開始
            tv2.text = "始めます"
            soundPool.play(sndstr, countVolume, countVolume, 0, 0, 1.0f)
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()

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
            val intent = Intent(this@Hukkin, Youtube::class.java)
            val yID = "https://youtu.be/npIGK0blwyk"
            intent.putExtra("yID", yID) //URLを転送
            startActivity(intent)
        }

        loadSettingsTick()
        chronometer.setOnChronometerTickListener {
            num++
            if (extimes <= maxextimes) {
                when (num){
                    in -10..-4 -> {
                        if (!isStart) {
                            tv2.text = "${num * (-1)}"
                        }
                    }
                    in -3..-1 ->{
                        if(!isStart){
                            tv2.text = "${num * (-1)}"
                            soundPool.play(sounds[num*(-1)-1], countVolume, countVolume, 0, 0, 1.0f)
                        }
                    }
                    0 ->{
                        if(!isStart){
                            tv2.text = "${num * (-1)}"
                            soundPool.play(sndpi, countVolume, countVolume, 0, 0, 1.0f)
                        }
                    }
                     1 -> {
                        tv.text = "$extimes/$maxextimes セット"

                        tv2.text = getString(R.string.slow_up)
                        soundPool.play(slowup, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    2 -> {
                        tv2.text = "20秒キープ"
                             soundPool.play(sndkeep20s, countVolume, countVolume, 0, 0, 1.0f)

                    }
                    in 4..23 -> {  //3から23まで
                        tv2.text = "${num-3}"
                        soundPool.play(sounds[num-4], countVolume, countVolume, 0, 0, 1.0f)
                    }

                    24 -> {
                        tv2.text = getString(R.string.slow_down)
                        soundPool.play(slowdown, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    25 -> {
                        extimes++
                        if (extimes <= maxextimes) {
                            num = -(11) // 10s relax
                            tv2.text = getString(R.string.relax10)
                            soundPool.play(snd10re, countVolume, countVolume, 0, 0, 1.0f)
                            isStart = false
                        } else {
                           // Done
                        }
                    }
                    else -> {}
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
                    RecordManager.saveRecord(this, "09${workmenu}")
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
