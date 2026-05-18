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

class Legraise : AppCompatActivity() {

    lateinit var soundPool: SoundPool
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
    private var sndpi = 0 //Pi
    private var sndstr = 0
    private var sndend = 0
    private var sndCounts = IntArray(16)
    private var snd10re = 0
    private var sndup = 0
    private var snddown = 0
    private var num: Int = 0

    private var nn= 0
    private var isSaved: Boolean = false

    private var maxextimes = 3 // Initial value
    private var extimes = 0
    private var _workoutId = -1
    private lateinit var _helper: DatabaseHelper
    private var listnumLocal = 0
    private var isStart: Boolean = true
    private var isup: Boolean = true

    lateinit var tv: TextView
    private fun loadSettingsTick() {
        val db = _helper.writableDatabase
        val sql = "SELECT * FROM workouttimes WHERE _id = $_workoutId"
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToNext()) {
            val t = cursor.getString(cursor.getColumnIndex("times"))
            val idxReps = cursor.getColumnIndex("reps")
            val r = if (idxReps != -1) cursor.getString(idxReps) ?: "10" else "10"
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
        tvexpla.text =
            "仰向けに寝て、手は身体の横に置く\n足はなるべく伸ばす（きつい場合は曲げてもよい.）\n足をそろえてゆっくり上げて(出来れば90度位に)ゆっくりおろす\n" +
                    "下したとき足は床に付けない。\n\n10回で1セット、3セットが標準"

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
            val intent2 = Intent(this@Legraise, MainActivity2::class.java)
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
        sndup = soundPool.load(this, R.raw.up, 1)
        snddown = soundPool.load(this, R.raw.down, 1)
        snd10re = soundPool.load(this, R.raw.relax10sec, 1)
        sndpi = soundPool.load(this, R.raw.pi, 1)
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
        val sounds = listOf(snd1, snd2, snd3)




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
            isup = true
            extimes = 1
            num = -3
            nn = 1

            tv.text = "1/$maxextimes セット"
            isStart = true
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
            val intent = Intent(this@Legraise, Youtube::class.java)
            val yID = "https://youtu.be/JmG5MLaDS38"
            intent.putExtra("yID",yID ) //ボリュームの値を転送
            startActivity(intent)
        }



        loadSettingsTick()
        chronometer.setOnChronometerTickListener {
            num++
            if (extimes <= maxextimes) {

                tv.text = "$extimes/$maxextimes セット"

                when (num) {
                    in -10..-4 ->{
                        if(!isStart){
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

                   in 1..20 -> {
                       if(isup) {
                             tv2.text = getString(R.string.up) + "  $nn/10"
                             soundPool.play(sndup, countVolume, countVolume, 0, 0, 1.0f)
                            isup = false
                       }else{
                            tv2.text = getString(R.string.down)+ "  $nn/10"
                            soundPool.play(snddown, countVolume, countVolume, 0, 0, 1.0f)
                            isup = true
                           nn++
                       }
                    }
                   21 -> {
                        if (nn >=10) {
                            num = -(11) // 10s relax
                            tv2.text = getString(R.string.relax10)
                            soundPool.play(snd10re, countVolume, countVolume, 0, 0, 1.0f)
                            isStart = false
                            nn=1
                            extimes++
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
                    RecordManager.saveRecord(this, "10${workmenu}")
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