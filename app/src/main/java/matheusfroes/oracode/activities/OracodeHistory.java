package matheusfroes.oracode.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import matheusfroes.oracode.R;
import matheusfroes.oracode.adapters.OracodeAdapter;
import matheusfroes.oracode.db.OracodeDAO;
import matheusfroes.oracode.models.Oracode;
import matheusfroes.oracode.utils.Utils;

public class OracodeHistory extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private OracodeDAO dao;
    private OracodeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oracode_history);

        ListView lvHistory = (ListView) findViewById(R.id.list);

        dao = new OracodeDAO(this);
        adapter = new OracodeAdapter(this, dao.getOracodesCursor());

        lvHistory.setAdapter(adapter);

        registerForContextMenu(lvHistory);
        lvHistory.setOnItemClickListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.ic_delete:
                long id = adapter.getItemId(menuInfo.position);
                dao.delete(id);
                adapter.changeCursor(dao.getOracodesCursor());
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "History deleted", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onContextItemSelected(item);
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
