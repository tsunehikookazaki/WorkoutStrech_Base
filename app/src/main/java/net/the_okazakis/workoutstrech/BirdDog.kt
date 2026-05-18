package net.the_okazakis.workoutstrech

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.TextView
import java.util.Locale

class BirdDog : AppCompatActivity() {

    lateinit var soundPool: SoundPool
    private var sndstr = 0
    private var sndend = 0
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
   private var extimes: Int = 1
    private var num: Int = 0
    private var isStart: Boolean = true
    private var isSaved: Boolean = false
    private var _workoutId = -1
    private lateinit var _helper: DatabaseHelper
    private var maxextimes = 0
    private var maxReps = 0
    private var listnumLocal = 0

    lateinit var tv: TextView

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


        val chronometer = findViewById<Chronometer>(R.id.chronometer)
        val countVolume: Float = intent.getFloatExtra("TEXT_KEY", 1.0f)
        val listnum: Int = intent.getIntExtra("TEXT_KEY2", 0)
        listnumLocal = listnum
        _workoutId = listnum

        val lvmenu = resources.getStringArray(R.array.lv_menu)
        val workmenu = lvmenu[listnum]
        //val tv: TextView = findViewById(R.id.tv)
        tv = findViewById(R.id.tv)   // ← これが正しい
        val tv2: TextView = findViewById(R.id.tv2)
        val textmenu: TextView = findViewById(R.id.textmenu)
        val tvexpla: TextView = findViewById(R.id.tvexpla)

        tvexpla.text =
            "脊柱起立筋の筋トレ（バードドッグ）\n四つ這いになり、両手と両足は肩幅に開きます。" +
                    "\n右手は肘を伸ばし、左足は膝をつたまま足先を上げる。" +
                    "そのままの姿勢をキープ（キープは３秒が標準　最高10秒）。\n手足を元に戻し、反対側も同じようにする。" +
                    "\n手足を上げるときに、腰を反ったり、姿勢が崩れないように注意する。" +
                    "\n\n左右5回ずつが1セット。1セット標準" +
                    "\n膝を付かずに水平に伸ばすのが普通のバードドックですが、運動の強度が高いので、膝をついて行います"

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
            val intent2 = Intent(this@BirdDog, MainActivity2::class.java)
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

        val sounds = listOf(snd1, snd2, snd3, snd4, snd5, snd6, snd7, snd8, snd9, snd10)
        val keepsounds = listOf(sndkeep1, sndkeep2, sndkeep3, sndkeep4, sndkeep5, sndkeep6, sndkeep7, sndkeep8, sndkeep9, sndkeep10)


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
            num = -3
            tv.text = "1/$maxextimes セット"
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

        //Youtubeのリンクを開く

        btnyoutube.setOnClickListener {
            val intent = Intent(this@BirdDog, Youtube::class.java)
            val yID = "https://youtu.be/-GQKKD0JtMo"
            intent.putExtra("yID", yID) //URLを転送
            startActivity(intent)
        }



        loadSettingsTick()
        chronometer.setOnChronometerTickListener {
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

            } else {
                chronometer.stop()
                btnstart.isEnabled = true
                btnstop.isEnabled = false
                btnrerstart.isEnabled = false
                btnback.isEnabled = true
                btnChangeTimes.isEnabled = true
                btnyoutube.isEnabled = true
                if (!isSaved) {
                    RecordManager.saveRecord(this, "13${workmenu}")
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
