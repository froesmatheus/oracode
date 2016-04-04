package matheusfroes.oracode.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import matheusfroes.oracode.db.Db;

/**
 * Created by mathe on 03/04/2016.
 */
public class OracodeAdapter extends CursorAdapter {

    public OracodeAdapter(Context context, Cursor cursor) {
        super(context, cursor, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(android.R.id.text1);

        String oracode = cursor.getString(cursor.getColumnIndex(Db.ORACODE_ORACODE_COLUMN));

        textView.setText(oracode);
    }
}
