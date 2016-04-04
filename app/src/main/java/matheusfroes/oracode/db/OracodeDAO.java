package matheusfroes.oracode.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import matheusfroes.oracode.models.Oracode;

/**
 * Created by mathe on 03/04/2016.
 */
public class OracodeDAO {
    private Context context;
    private SQLiteDatabase db;

    public OracodeDAO(Context context) {
        this.context = context;
        db = new Db(context).getWritableDatabase();
    }

    public boolean insert(Oracode oracode) {
        ContentValues cv = new ContentValues();

        cv.put(Db.ORACODE_ORACODE_COLUMN, oracode.getOracode());
        cv.put(Db.ORACODE_CAUSE_COLUMN, oracode.getCause());
        cv.put(Db.ORACODE_EXPLANATION_COLUMN, oracode.getExplanation());
        cv.put(Db.ORACODE_ACTION_COLUMN, oracode.getAction());

        long id = db.insert(Db.ORACODE_TABLE_NAME, null, cv);

        return id != -1;
    }

    public Oracode getOracodeByCode(String oracodeStr) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + Db.ORACODE_TABLE_NAME + " WHERE " + Db.ORACODE_ORACODE_COLUMN + " = '" + oracodeStr + "'", null);

        Oracode oracode = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            int _id = cursor.getInt(cursor.getColumnIndex(Db.ORACODE_ID_COLUMN));
            String code = cursor.getString(cursor.getColumnIndex(Db.ORACODE_ORACODE_COLUMN));
            String explanation = cursor.getString(cursor.getColumnIndex(Db.ORACODE_EXPLANATION_COLUMN));
            String cause = cursor.getString(cursor.getColumnIndex(Db.ORACODE_CAUSE_COLUMN));
            String action = cursor.getString(cursor.getColumnIndex(Db.ORACODE_ACTION_COLUMN));

            oracode = new Oracode(code, explanation, cause, action);
            oracode.setId(_id);
        }
        cursor.close();

        return oracode;
    }


    public Cursor getOracodesCursor() {
        return db.rawQuery("SELECT * FROM " + Db.ORACODE_TABLE_NAME, null);
    }
}
