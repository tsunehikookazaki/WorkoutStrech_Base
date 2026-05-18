package net.the_okazakis.workoutstrech

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // クラス内のprivate定数を宣言するためにcompanion objectブロックとする。
    companion object {
        /**
         * データベースファイル名の定数フィールド。
         */
        private const val DATABASE_NAME = "workouttime.db"
        /**
         * バージョン情報の定数フィールド。
         */
        private const val DATABASE_VERSION =12
    }

    override fun onCreate(db: SQLiteDatabase) {
        // テーブル作成用SQL文字列の作成。
        val sb = StringBuilder()
        sb.append("CREATE TABLE workouttimes (")
        sb.append("_id INTEGER PRIMARY KEY,")
        sb.append("times TEXT,")
        sb.append("reps TEXT")
        sb.append(");")
        val sql = sb.toString()

        // SQLの実行。
        db.execSQL(sql)


        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (0,'2', '15')") //腓腹筋のばし
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (1,'10', '15')") //つま先立ち
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (2,'3', '15')") //膝裏のばし(立）
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (3,'10', '15')") //ボールつぶし
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (4,'16', '15')") //じゃんけん
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (5,'15', '15')") //足首屈伸
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (6,'1', '15')") //膝裏のばし(寝）
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (7,'10', '15')") //膝抱え
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (8,'3', '15')") //足上げ腹筋
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (9,'3', '15')") //レッグライズ
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (10,'3', '15')") //ヒップリフト
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (11,'10', '15')") //内外転
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (12,'2', '15')") //バードドッグ
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (13,'3', '15')") //ヒップアブダクション(Belt)
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (14,'3', '15')") //ヒップブダクション(Ball)
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (15,'15', '15')") //スケボー屈伸
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (16,'3', '15')") //足上げ
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (17,'10', '15')") //椅子スクワット
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (18,'10', '15')") //椅子スクワット
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (19,'20', '15')") //フロントランジ
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (20,'10', '15')") //鎌田式ワイドスクワット
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (21,'10', '15')") //鎌田式かかと落とし
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (22,'1', '15')")  //前モモのばし
        db.execSQL( "INSERT INTO workouttimes (_id, times, reps) VALUES (23,'3', '15')")  //膝裏のばし
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 12) {
            db.execSQL("ALTER TABLE workouttimes ADD COLUMN reps TEXT DEFAULT '15'")
        }
    }
}
