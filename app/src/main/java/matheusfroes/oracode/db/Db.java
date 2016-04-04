package matheusfroes.oracode.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mathe on 03/04/2016.
 */
public class Db extends SQLiteOpenHelper {
    public static final String DB_NAME = "Oracode";
    public static final int DB_VERSION = 1;

    public static final String ORACODE_TABLE_NAME = "Oracodes";
    public static final String ORACODE_ID_COLUMN = "_id";
    public static final String ORACODE_ORACODE_COLUMN = "oracode";
    public static final String ORACODE_EXPLANATION_COLUMN = "explanation";
    public static final String ORACODE_ACTION_COLUMN = "action";
    public static final String ORACODE_CAUSE_COLUMN = "cause";


    public static final String ORACODE_TABLE_DDL = "CREATE TABLE IF NOT EXISTS " + ORACODE_TABLE_NAME + "( " +
            ORACODE_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ORACODE_ORACODE_COLUMN + " INTEGER NOT NULL," +
            ORACODE_EXPLANATION_COLUMN + " TEXT NOT NULL, " +
            ORACODE_ACTION_COLUMN + " TEXT NOT NULL, " +
            ORACODE_CAUSE_COLUMN + " TEXT NOT NULL );";


    public Db(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ORACODE_TABLE_DDL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + DB_NAME + ";");
        onCreate(db);
    }
}
