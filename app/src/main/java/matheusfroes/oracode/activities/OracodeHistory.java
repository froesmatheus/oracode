package matheusfroes.oracode.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import matheusfroes.oracode.R;
import matheusfroes.oracode.adapters.OracodeAdapter;
import matheusfroes.oracode.db.OracodeDAO;
import matheusfroes.oracode.models.Oracode;
import matheusfroes.oracode.utils.Utils;

public class OracodeHistory extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private OracodeDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oracode_history);

        ListView lvHistory = (ListView) findViewById(R.id.list);

        dao = new OracodeDAO(this);
        OracodeAdapter adapter = new OracodeAdapter(this, dao.getOracodesCursor());

        lvHistory.setAdapter(adapter);

        lvHistory.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        String oracode = textView.getText().toString();

        Intent intent = new Intent();
        intent.putExtra("oracode", oracode);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
