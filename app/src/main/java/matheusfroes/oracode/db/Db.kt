package matheusfroes.oracode.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by mathe on 11/04/2016.
 */
class Db(context:Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION){

    companion object {
        val DB_NAME = "Oracode"
        val DB_VERSION = 1

        val ORACODE_TABLE_NAME = "Oracodes"
        val ORACODE_ID_COLUMN = "_id"
        val ORACODE_ORACODE_COLUMN = "oracode"
        val ORACODE_EXPLANATION_COLUMN = "explanation"
        val ORACODE_ACTION_COLUMN = "action"
        val ORACODE_CAUSE_COLUMN = "cause"

        val ORACODE_TABLE_DDL = """
                CREATE TABLE IF NOT EXISTS $ORACODE_TABLE_NAME
                (
                    $ORACODE_ID_COLUMN INTEGER PRIMARY KEY AUTOINCREMENT,
                    $ORACODE_ORACODE_COLUMN INTEGER NOT NULL,
                    $ORACODE_EXPLANATION_COLUMN TEXT NOT NULL,
                    $ORACODE_ACTION_COLUMN TEXT NOT NULL,
                    $ORACODE_CAUSE_COLUMN TEXT NOT NULL
                );"""

    }


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ORACODE_TABLE_DDL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE $DB_NAME;")
        onCreate(db)
    }
}