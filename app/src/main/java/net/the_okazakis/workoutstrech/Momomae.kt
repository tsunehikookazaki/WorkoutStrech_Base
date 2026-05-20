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


class Momomae : AppCompatActivity() {

    private var timeCount = 0
    private var extimes: Int = 0
    private var num: Int = 0
    private var isSaved: Boolean = false
    private var count: Boolean = false
    private var maxextimes = 15 // Initial value
    private var countVolume: Float = 1.0f
    private var _workoutId = 22
    private var workmenu: String = ""
    private var speedTime = 1000L

    lateinit var soundPool: SoundPool
    private var sounds = mutableListOf<Int>()
    private var sndstr = 0
    private var sndend =0
    private var changleg = 0
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
    private var snd20 = 0 //2０
    private var snd21 = 0 //２１
    private var snd22 = 0 //２２
    private var snd23 = 0 //２３
    private var snd24 = 0 //２４
    private var snd25 = 0 //２５
    private var snd26 = 0 //２６
    private var snd27 = 0 //２７
    private var snd28 = 0 //２８
    private var snd29 = 0 //２９
    private var snd30 = 0 //３０
    private var sndkeep30s = 0 //30秒キープ
    private var  sndhikitsuke = 0
    private var firstleg: Boolean = true  //最初の足

    private var firsttime = true  //１回目
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
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {
                        if (!firsttime) {
                            tv2.text = "足を変えて"
                            soundPool.play(changleg, countVolume, countVolume, 0, 0, 1.0f)
                        }
                        firsttime = false
                        tv.text = "${extimes}/$maxextimes セット"
                    }
                    2 ->{
                        tv2.text = "足をお尻に引き付け前ももを伸ばす"
                        soundPool.play(sndhikitsuke, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    3 -> {
                        tv2.text = getString(R.string.keep30s)
                        soundPool.play(sndkeep30s, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    in 4..33 -> {
                        tv2.text = "${num-3}"
                        soundPool.play(sounds[num-4],countVolume, countVolume, 0, 0, 1.0f)
                    }

                    34 -> {
                        num = 0
                        if(!firstleg)
                        {  //足を変えた場合
                            extimes ++
                        }
                        firstleg = !firstleg    //足を変える
                    }
                }
                // ← これ追加
                handler.postDelayed(this, 1000)

            } else {
                handler.removeCallbacks(this)
                btnstart.isEnabled = true
                btnstop.isEnabled = false
                btnrerstart.isEnabled = false
                btnChangeTimes.isEnabled = true
                btnback.isEnabled = true
                btnyoutube.isEnabled = true
                btnspeed.isEnabled = true
                speedTime = 1000L
                if (!isSaved) {
                    RecordManager.saveRecord(this@Momomae, "23${workmenu}")
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
            maxextimes =  t.toIntOrNull() ?: 1    //maxextimesをsqlで見つけたｔに DBに無ければ10
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
            "前モモ伸ばしし\n片方の足を後ろに引き、足を持ってかかとをおしりに引き付ける。前モモが伸びているのを感じる。30秒間キープ。反対の足も同様に。1回（左右1回ずつ）で1セット。1セット標準。" +
                    "足が持てない場合はタオルなどを使って引き付ける。モモを後ろに引くとより効果的。　足が横に出ないように、前に出ないように、腰が反らないように注意する。"

        btnback = findViewById(R.id.btnback)
        btnstart = findViewById(R.id.btStart)
        btnstop = findViewById(R.id.btStop)
        btnrerstart = findViewById(R.id.btnrestart)
         btnyoutube = findViewById(R.id.youtube)
        btnChangeTimes = findViewById(R.id.button2)
        btnspeed = findViewById(R.id.btspeed)

        btnChangeTimes.visibility = android.view.View.VISIBLE


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
        changleg = soundPool.load(this, R.raw.changeleg, 1)
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
        snd21 = soundPool.load(this, R.raw.v21, 1)
        snd22 = soundPool.load(this, R.raw.v22, 1)
        snd23 = soundPool.load(this, R.raw.v23, 1)
        snd24 = soundPool.load(this, R.raw.v24, 1)
        snd25 = soundPool.load(this, R.raw.v25, 1)
        snd26 = soundPool.load(this, R.raw.v26, 1)
        snd27 = soundPool.load(this, R.raw.v27, 1)
        snd28 = soundPool.load(this, R.raw.v28, 1)
        snd29 = soundPool.load(this, R.raw.v29, 1)
        snd30 = soundPool.load(this, R.raw.v30, 1)
        sndkeep30s = soundPool.load(this, R.raw.keep30s, 1)
        sndhikitsuke = soundPool.load(this, R.raw.hikitsuke, 1)
        sounds = mutableListOf(
            snd1,
            snd2,
            snd3,
            snd4,
            snd5,
            snd6,
            snd7,
            snd8,
            snd9,
            snd10,
            snd11,
            snd12,
            snd13,
            snd14,
            snd15,
            snd16,
            snd17,
            snd18,
            snd19,
            snd20,
            snd21,
            snd22,
            snd23,
            snd24,
            snd25,
            snd26,
            snd27,
            snd28,
            snd29,
            snd30
        )

        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@Momomae, MainActivity2::class.java)
            intent2.putExtra("TEXT_KEY4", workmenu)
            intent2.putExtra("TEXT_KEY5", listnum)
            startActivity(intent2)
        }

        btnyoutube.setOnClickListener {
            val intent = Intent(this@Momomae, Youtube::class.java)
            val yID = "https://youtu.be/sRvbL3eflz0"
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
            firsttime = true
            firstleg = true
            extimes = 1
            num = -3
            tv2.text = "始めます"
            soundPool.play(sndstr, countVolume, countVolume, 0, 0, 1.0f)
            loadSettingsTick()
            handler.post(runnable)
        }

        btnstop.setOnClickListener {
            btnstart.isEnabled = true
            btnstop.isEnabled = false
            btnrerstart.isEnabled = true
            btnChangeTimes.isEnabled = true
            btnback.isEnabled = true
            btnyoutube.isEnabled = true
            btnspeed.isEnabled = true
            speedTime = 1000L
            handler.removeCallbacks(runnable)
        }

        btnrerstart.setOnClickListener {
            btnstart.isEnabled = false
            btnstop.isEnabled = true
            btnrerstart.isEnabled = false
            btnChangeTimes.isEnabled = false
            btnback.isEnabled = false
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