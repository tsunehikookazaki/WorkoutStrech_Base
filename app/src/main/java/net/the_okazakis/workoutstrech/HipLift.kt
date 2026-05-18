package net.the_okazakis.workoutstrech

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.os.postDelayed
import android.os.Handler
import android.view.ViewStub
import android.widget.Button

class HipLift : AppCompatActivity() {

    lateinit var soundPool: SoundPool
    private var sndstr = 0
    private var sndend = 0
    private var snd10re = 0
    private var sndup = 0
    private var snddown = 0

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

    private var sndpi = 0 //pi

    private var extimes = 1      // 現在セット
    private var num = 0
    private var nn=0
    private var isStart= true      // スタートか

    private var isSaved: Boolean = false

    private var _workoutId = -1
    private lateinit var _helper: DatabaseHelper
    private var maxextimes = 0
    private var listnumLocal = 0


    private val handler = Handler(Looper.getMainLooper())

    private var speedTime = 1000L

    // ← ここに追加
    private lateinit var runnable: Runnable

    lateinit var tv2: TextView

    private var countVolume = 1.0f

    lateinit var tv: TextView
    private fun loadSettingsTick() {
        val db = _helper.writableDatabase
        val sql = "SELECT * FROM workouttimes WHERE _id = $_workoutId"
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToNext()) {
            //val t = cursor.getString(cursor.getColumnIndex("times"))
            val t = cursor.getString(cursor.getColumnIndexOrThrow("times"))
            val idxReps = cursor.getColumnIndex("reps")
            val r = if (idxReps != -1) cursor.getString(idxReps) ?: "20" else "20"
            maxextimes = t.toIntOrNull() ?: 10    //maxextimesをsqlで見つけたｔに DBに無ければ10
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

// onCreate などの中で


        countVolume = intent.getFloatExtra("TEXT_KEY", 1.0f)
        val listnum: Int = intent.getIntExtra("TEXT_KEY2", 0)
        listnumLocal = listnum
        _workoutId = listnum

        val lvmenu = resources.getStringArray(R.array.lv_menu)
        val workmenu = lvmenu[listnum]

        tv = findViewById(R.id.tv)
        tv2=  findViewById(R.id.tv2)
        val textmenu: TextView = findViewById(R.id.textmenu)
        val tvexpla: TextView = findViewById(R.id.tvexpla)

        tvexpla.text =
            "床に寝て、膝を曲げる。足はべた足。お尻に力を入れ腰を上げ下げする。\nおしりを下げた時床に着けない。" +
                    "おしりを上げた時に足が浮かないように。腰で上げないよう、お尻を触って、力が入っていることを確認。" +
                    "\nレベル1：両手を体側につく\nレベル2：手は胸におく" +
                    "\nレベル３：片足を上げて、反対の足に載せ、手はつく\nレベル４：片足を上げ伸ばし、両手は胸におく。" +
                    "上げた足は反対の足と同じ高さをキープ。両手を伸ばし、上にあげて掌をつけるのが最高。" +
                    "\n\n左右それぞれ20回が1セット。1セットが標準　　片足を上げた場合はそれぞれ１セットずつ、合計２セット"

        val btnback: Button = findViewById(R.id.btnback)
        val btnstart: Button = findViewById(R.id.btStart)
        val btnstop: Button = findViewById(R.id.btStop)
        val btnrerstart: Button = findViewById(R.id.btnrestart)
        val btnyoutube: Button = findViewById(R.id.youtube)
        val btnChangeTimes: Button = findViewById(R.id.button2)
        val btspeed: Button = findViewById(R.id.btspeed)

        textmenu.text = workmenu
        btnstop.isEnabled = false
        btnrerstart.isEnabled = false

        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@HipLift, MainActivity2::class.java)
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
        val sounds = listOf(
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
            snd20
        )

        snd10re = soundPool.load(this, R.raw.relax10sec, 1)

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
            nn = 0

            tv.text = "1/$maxextimes セット"
            isStart = true
            tv2.text = "始めます"
            soundPool.play(sndstr, countVolume, countVolume, 0, 0, 1.0f)
            handler.post(runnable)

        }

        btnstop.setOnClickListener {
            btnstart.isEnabled = true
            btnstop.isEnabled = false
            btnrerstart.isEnabled = true
            btnback.isEnabled = true
            btnChangeTimes.isEnabled = true
            btnyoutube.isEnabled = true
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
            handler.post(runnable)
        }
        btspeed.setOnClickListener {
            btnstart.isEnabled = false
            btnstop.isEnabled = true
            btnrerstart.isEnabled = false
            btnback.isEnabled = false
            btnChangeTimes.isEnabled = false
            btnyoutube.isEnabled = false
            speedTime = 500L
            handler.removeCallbacks(runnable)
            handler.post(runnable)
        }


        btnback.setOnClickListener {
            soundPool.release()
            finish()
        }

        btnyoutube.setOnClickListener {
            val intent = Intent(this@HipLift, Youtube::class.java)
            val yID = "https://youtu.be/trf2Ph_WWPQ"
            intent.putExtra("yID", yID) //URLを転送
            startActivity(intent)
        }


        loadSettingsTick()

        runnable = object : Runnable {

            override fun run() {



                num++

                if (extimes < maxextimes) {

                    tv.text = "${extimes + 1}/$maxextimes セット"

                    when (num) {

                        -10 -> {
                            if (!isStart) {
                                tv2.text = "10秒休みです"
                                soundPool.play(snd10re, countVolume, countVolume, 0, 0, 1.0f)
                            }
                        }

                        -3 -> {
                            if (!isStart) {
                                tv2.text = "${num * (-1)}"
                                soundPool.play(snd3, countVolume, countVolume, 0, 0, 1.0f)
                            }
                        }

                        -2 -> {
                            if (!isStart) {
                                tv2.text = "${num * (-1)}"
                                soundPool.play(snd2, countVolume, countVolume, 0, 0, 1.0f)
                            }
                        }

                        -1 -> {
                            if (!isStart) {
                                tv2.text = "${num * (-1)}"
                                soundPool.play(snd1, countVolume, countVolume, 0, 0, 1.0f)
                            }
                        }

                        0 -> {
                            if (!isStart) {
                                tv2.text = "0"
                                soundPool.play(sndpi, countVolume, countVolume, 0, 0, 1.0f)
                            }
                        }

                        1 -> {
                            tv2.text = getString(R.string.up) + " ${nn + 1}回"
                            soundPool.play(sndup, countVolume, countVolume, 0, 0, 1.0f)
                        }

                        2 -> {

                            tv2.text = getString(R.string.down) + " ${nn + 1}回"

                            soundPool.play(snddown, countVolume, countVolume, 0, 0, 1.0f)

                            if (nn < 19) {

                                num = 0
                                nn++

                            } else {

                                isStart = false
                                nn = 0
                                num = -11
                                extimes++
                            }
                        }
                    }

                    handler.postDelayed(this, speedTime)

                } else {

                    btnstart.isEnabled = true
                    btnstop.isEnabled = false
                    speedTime = 1000L
                    tv2.text = getString(R.string.good_job)

                    soundPool.play(sndend, countVolume, countVolume, 0, 0, 1.0f)
                }
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

