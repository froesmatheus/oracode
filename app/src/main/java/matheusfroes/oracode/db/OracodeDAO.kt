package matheusfroes.oracode.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

import matheusfroes.oracode.models.Oracode

/**
 * Created by mathe on 03/04/2016.
 */
class OracodeDAO(private val context: Context) {
    private val db: SQLiteDatabase

    init {
        db = Db(context).writableDatabase
    }

    fun insert(oracode: Oracode): Boolean {
        val cv = ContentValues()

        cv.put(Db.ORACODE_ORACODE_COLUMN, oracode.oracode)
        cv.put(Db.ORACODE_CAUSE_COLUMN, oracode.cause)
        cv.put(Db.ORACODE_EXPLANATION_COLUMN, oracode.explanation)
        cv.put(Db.ORACODE_ACTION_COLUMN, oracode.action)

        val id = db.insert(Db.ORACODE_TABLE_NAME, null, cv)

        return !id.equals(-1)

    }

    fun delete(oracodeId: Long): Boolean {
        val status = db.delete(Db.ORACODE_TABLE_NAME, "${Db.ORACODE_ID_COLUMN} = ?", arrayOf(oracodeId.toString()))

        return status > 0
    }

    fun deleteAllHistory() {
        db.delete(Db.ORACODE_TABLE_NAME, "1", null)
    }

    fun getOracodeByCode(oracodeStr: String): Oracode? {
        val cursor = db.rawQuery("SELECT * FROM ${Db.ORACODE_TABLE_NAME} WHERE ${Db.ORACODE_ORACODE_COLUMN} = ?", arrayOf(oracodeStr))

        var oracode: Oracode? = null
        if (cursor.count > 0) {
            cursor.moveToFirst()

            val _id = cursor.getInt(cursor.getColumnIndex(Db.ORACODE_ID_COLUMN))
            val code = cursor.getString(cursor.getColumnIndex(Db.ORACODE_ORACODE_COLUMN))
            val explanation = cursor.getString(cursor.getColumnIndex(Db.ORACODE_EXPLANATION_COLUMN))
            val cause = cursor.getString(cursor.getColumnIndex(Db.ORACODE_CAUSE_COLUMN))
            val action = cursor.getString(cursor.getColumnIndex(Db.ORACODE_ACTION_COLUMN))

            oracode = Oracode(code, explanation, cause, action)
            oracode.id = _id
        }
        cursor.close()

        return oracode
    }


    val oracodesCursor: Cursor
        get() = db.rawQuery("SELECT * FROM " + Db.ORACODE_TABLE_NAME, null)
}
