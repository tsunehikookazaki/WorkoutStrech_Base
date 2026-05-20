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


class BirdDog : AppCompatActivity() {

    private var timeCount = 0
    private var extimes: Int = 0
    private var num: Int = 0
    private var isSaved: Boolean = false
    private var isStart: Boolean = false

    private var count: Boolean = false
    private var maxextimes = 15 // Initial value
    private var maxReps = 5

    private var countVolume: Float = 1.0f
    private var _workoutId = 12
    private var workmenu: String = ""

    lateinit var soundPool: SoundPool  //SoundPool
    private var sndstr = 0  //スタート音声　”始めます”
     private var sndend = 0//終わり音声　　”お疲れさまでした”
    private var sounds = mutableListOf<Int>()
    private var keepsounds = mutableListOf<Int>()
    private var speedTime = 1000L
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
    private var sndkeep1 =0
    private var sndkeep2 =0
    private var sndkeep3 =0
    private var sndkeep4 =0
    private var sndkeep5 =0
    private var sndkeep6 =0
    private var sndkeep7 =0
    private var sndkeep8 =0
    private var sndkeep9 =0
    private var sndkeep10 =0
    private var sndup = 0
    private var sndmodo = 0
    private var sndkeep3s = 0
    private var sndkaeteagete = 0
    private var sndbreak = 0
    private var snd10re = 0
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
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {  //上げて
                        tv.text = "$extimes/$maxextimes セット"
                        if (isStart) {  //初めてなら　上げて
                            tv2.text =  getString(R.string.up)
                            soundPool.play(sndup, countVolume, countVolume, 0, 0, 1.0f)
                            isStart = false   //初めてじゃない
                        }
                        else {  //初めてじゃない　変えてあげて
                            tv2.text =  getString(R.string.change4)
                            soundPool.play(sndkaeteagete, countVolume, countVolume, 0, 0, 1.0f)
                        }
                    }
                    2 -> {     //n秒キープ
                        tv2.text = "$maxReps キープ"
                        soundPool.play(keepsounds[maxReps-1], countVolume, countVolume, 0, 0, 1.0f)
                    }
                    in 3..maxReps+2 -> {   // 1,2,3,4
                        tv2.text = "${num -2}"
                        soundPool.play(sounds[num - 3], countVolume, countVolume, 0, 0, 1.0f)
                    }
                    maxReps + 3 -> {   //戻して
                        tv2.text =  getString(R.string.modo)
                        soundPool.play(sndmodo, countVolume, countVolume, 0, 0, 1.0f)
                    }

                    maxReps + 4  -> { //変えてあげて
                        tv2.text =  getString(R.string.change4)
                        soundPool.play(sndkaeteagete, countVolume, countVolume, 0, 0, 1.0f)
                    }

                    maxReps + 5 -> {      //n秒キープ
                        tv2.text = "$maxReps キープ"
                        soundPool.play(keepsounds[maxReps-1], countVolume, countVolume, 0, 0, 1.0f)
                    }
                    in maxReps + 6..maxReps+maxReps + 5 -> {   //1,2,3
                        tv2.text ="${num - maxReps -5}"
                        soundPool.play(sounds[num - maxReps -6], countVolume, countVolume, 0, 0, 1.0f)
                    }
                    maxReps+maxReps + 6  -> {   //戻して
                        tv2.text = getString(R.string.modo)
                        soundPool.play(sndmodo, countVolume, countVolume, 0, 0, 1.0f)
                        num = 0
                        extimes ++
                    }

                    else -> {}
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
                btnspeed.isEnabled = true
                if (!isSaved) {
                    RecordManager.saveRecord(this@BirdDog, "13${workmenu}")
                    isSaved = true
                }
                tv2.text = getString(R.string.good_job)
                soundPool.play(sndend, countVolume, countVolume, 0, 0, 1.0f)
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
            val r = if (idxReps != -1) cursor.getString(idxReps) ?: "5" else "5"
            maxextimes = t.toIntOrNull() ?: 1    //set回数maxextimesをsqlで見つけたｔに  セット数　　DBに無ければ10
            maxReps = r.toIntOrNull() ?: 5    //１set の回数　 maxRepsをsqlで見つけたrに  セット当たり回数  DBに無ければ10
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

        _workoutId = listnum

        val lvmenu = resources.getStringArray(R.array.lv_menu)
        workmenu = lvmenu[listnum]
        //val tv: TextView = findViewById(R.id.tv)
        tv = findViewById(R.id.tv)   // ← これが正しい
        tv2 = findViewById(R.id.tv2)
        val textmenu: TextView = findViewById(R.id.textmenu)
        val tvexpla: TextView = findViewById(R.id.tvexpla)

        tvexpla.text =
            "脊柱起立筋の筋トレ（バードドッグ）\n四つ這いになり、両手と両足は肩幅に開きます。" +
                    "\n右手は肘を伸ばし、左足は膝をつたまま足先を上げる。" +
                    "そのままの姿勢をキープ（キープは３秒が標準　最高10秒）。\n手足を元に戻し、反対側も同じようにする。" +
                    "\n手足を上げるときに、腰を反ったり、姿勢が崩れないように注意する。" +
                    "\n\n左右5回ずつが1セット。1セット標準" +
                    "\n膝を付かずに水平に伸ばすのが普通のバードドックですが、運動の強度が高いので、膝をついて行います"

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
            val intent2 = Intent(this@BirdDog, MainActivity2::class.java)
            intent2.putExtra("TEXT_KEY4", workmenu)
            intent2.putExtra("TEXT_KEY5", listnum)
            startActivity(intent2)
        }

        val aa0 = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        //soundPool設定
        soundPool = SoundPool.Builder().setAudioAttributes(aa0).setMaxStreams(3).build()

        //それぞれの音をセット

        sndstr = soundPool.load(this, R.raw.start, 1)
        sndend = soundPool.load(this, R.raw.goodjob, 1)
        sndup = soundPool.load(this, R.raw.up, 1)
        sndmodo = soundPool.load(this, R.raw.modo, 1)
        sndkeep3s = soundPool.load(this, R.raw.keep3s, 1)
        sndkaeteagete = soundPool.load(this, R.raw.kaeteagete, 1)
        sndbreak = soundPool.load(this, R.raw.takeabreak, 1)
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
        sndkeep1 = soundPool.load(this, R.raw.keep1s, 1)
        sndkeep2 = soundPool.load(this, R.raw.keep2s, 1)
        sndkeep3 = soundPool.load(this, R.raw.keep3s, 1)
        sndkeep4 = soundPool.load(this, R.raw.keep4s, 1)
        sndkeep5 = soundPool.load(this, R.raw.keep5s, 1)
        sndkeep6 = soundPool.load(this, R.raw.keep6s, 1)
        sndkeep7 = soundPool.load(this, R.raw.keep7s, 1)
        sndkeep8 = soundPool.load(this, R.raw.keep8s, 1)
        sndkeep9 = soundPool.load(this, R.raw.keep9s, 1)
        sndkeep10 = soundPool.load(this, R.raw.keep10s, 1)
        sounds =  mutableListOf(snd1, snd2, snd3, snd4, snd5, snd6, snd7, snd8, snd9, snd10)
        keepsounds =  mutableListOf(
            sndkeep1,
            sndkeep2,
            sndkeep3,
            sndkeep4,
            sndkeep5,
            sndkeep6,
            sndkeep7,
            sndkeep8,
            sndkeep9,
            sndkeep10
        )
        snd10re = soundPool.load(this, R.raw.relax10sec, 1)


        // 各種クリックリスナー
        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@BirdDog, MainActivity2::class.java)
            intent2.putExtra("TEXT_KEY4", workmenu)
            intent2.putExtra("TEXT_KEY5", listnum)
            startActivity(intent2)
        }
        //Youtubeのリンクを開く
        btnyoutube.setOnClickListener {
            val intent = Intent(this@BirdDog, Youtube::class.java)
            val yID = "https://youtu.be/-GQKKD0JtMo"
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
            isStart = true
            extimes = 1
            num = -3
            tv.text = "1/$maxextimes セット"
            tv2.text = "始めます"
            soundPool.play(sndstr, countVolume, countVolume, 0, 0, 1.0f)
            handler.post(runnable) // タイマー再開

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
