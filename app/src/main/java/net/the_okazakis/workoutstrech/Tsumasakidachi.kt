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

class Tsumasakidachi : AppCompatActivity() {

    lateinit var soundPool: SoundPool  //SoundPool
    private var sndstr = 0  //スタート音声　”始めます”
    private var sndend = 0 //終わり音声　　”お疲れさまでした”
    private var sndkeep3s = 0 //3秒キープ
    private var snd1 = 0 //１”いち”
    private var snd2 = 0 //２”に”
    private var snd3 = 0 //３”さん”
    private var snd4 = 0 //４”よん”
    private var snd5 = 0 //５”ご”
    private var sndnon = 0 //無音
    private var slowup = 0  //最初の運動の掛け声
    private var slowdown = 0  //次の運動の掛け声
    private var sndkeepmama = 0 //そのままキープ

    private var extimes: Int = 0  //実施回数
   private var num:Int = 0  //号令のカウント
    private var isSaved: Boolean = false // ★ 保存済みフラグ

    private var maxextimes = 15 // Initial value
    private var _workoutId = 1
    private lateinit var _helper: DatabaseHelper

    lateinit var tv: TextView
    private fun loadSettingsTick() {
        val db = _helper.writableDatabase
        val sql = "SELECT times FROM workouttimes WHERE _id = $_workoutId"
        val cursor = db.rawQuery(sql, null)
        if (cursor.moveToNext()) {
            val t = cursor.getString(0)
            maxextimes =  t.toIntOrNull() ?: 15    //maxextimesをsqlで見つけたｔに DBに無ければ10

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

        tvexpla.text = "椅子または壁に手をつき、背筋を伸ばし、ゆっくりつま先立ちになる。ゆっくりかかとを下ろす。かかとは床につけないと効果的。息を止めない。15回標準。"

        val btnback: Button = findViewById(R.id.btnback)
        val btnstart: Button = findViewById(R.id.btStart)
        val btnstop: Button = findViewById(R.id.btStop)
        val btnrerstart: Button = findViewById(R.id.btnrestart)
        val btnyoutube: Button = findViewById(R.id.youtube)
        val btnChangeTimes: Button = findViewById(R.id.button2)

        btnChangeTimes.visibility = android.view.View.VISIBLE

        textmenu.text = workmenu
        btnstop.isEnabled = false
        btnrerstart.isEnabled = false

        btnChangeTimes.setOnClickListener {
            val intent2 = Intent(this@Tsumasakidachi, MainActivity2::class.java)
            intent2.putExtra("TEXT_KEY4", workmenu)
            intent2.putExtra("TEXT_KEY5", listnum)
            startActivity(intent2)
        }


        textmenu.text = workmenu

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
        sndnon = soundPool.load(this, R.raw.nosound, 1)
        sndkeep3s = soundPool.load(this, R.raw.keep3s, 1)
        sndkeepmama = soundPool.load(this, R.raw.keepmama, 1)

        tv.text = "1/$maxextimes 回"

        btnstart.setOnClickListener {
            btnstart.isEnabled = false
            btnstop.isEnabled = true
            btnrerstart.isEnabled = false
            btnback.isEnabled = false
            btnChangeTimes.isEnabled = false
            btnyoutube.isEnabled = false

            isSaved = false
            extimes = 0  //実施回数
            num = -5   //号令のカウント  始めます用に2秒
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
            btnyoutube.isEnabled = false
            btnChangeTimes.isEnabled = false
            chronometer.start()
        }

        btnback.setOnClickListener {
            soundPool.release()
            finish()
        }

        // Youtubeボタン
        btnyoutube.setOnClickListener {
            val intent = Intent(this@Tsumasakidachi, Youtube::class.java)
            val yID = "https://youtu.be/pVUqFOD_1M0?t=63"
            intent.putExtra("yID",yID ) //ボリュームの値を転送
            startActivity(intent)
        }
        loadSettingsTick()
        chronometer.setOnChronometerTickListener {


            num++
            if (extimes < maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = "${extimes+1}/$maxextimes 回"
                        tv2.text = getString(R.string.slow_up)
                        soundPool.play(slowup, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    2 -> {}
                    3 -> {
                        tv2.text = getString(R.string.keep3s)
                        soundPool.play(sndkeepmama, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    4 -> {}
                    5 ->{
                        soundPool.play(snd2, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    6 ->{
                        soundPool.play(snd3, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    7 -> {tv2.text =  getString(R.string.slow_down)
                        soundPool.play(slowdown, countVolume, countVolume, 0, 0, 1.0f)
                    }
                    8 ->  {
                        extimes ++ ;num = 0
                    }
                    else -> {}
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
                    RecordManager.saveRecord(this, "02${workmenu}")
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