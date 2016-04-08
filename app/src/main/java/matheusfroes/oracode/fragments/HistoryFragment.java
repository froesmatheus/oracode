package matheusfroes.oracode.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import matheusfroes.oracode.R;
import matheusfroes.oracode.adapters.OracodeAdapter;
import matheusfroes.oracode.db.OracodeDAO;

/**
 * Created by mathe on 06/04/2016.
 */
public class HistoryFragment extends Fragment implements AdapterView.OnItemClickListener {
    private Context context;
    private OracodeDAO dao;
    private OracodeAdapter adapter;
    private OnHistoryItemClick callbackActivity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callbackActivity = (OnHistoryItemClick) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnHistoryItemClick");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oracode_history, null, false);
        context = getActivity();

        ListView lvHistory = (ListView) view.findViewById(R.id.list);
        lvHistory.setEmptyView(view.findViewById(R.id.empty_history_layout));

        dao = new OracodeDAO(context);

        adapter = new OracodeAdapter(context, dao.getOracodesCursor());

        lvHistory.setAdapter(adapter);

        registerForContextMenu(lvHistory);
        lvHistory.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.history_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.ic_delete_all:
                if (adapter.getCount() == 0) {
                    Toast.makeText(context, R.string.no_items, Toast.LENGTH_SHORT).show();
                } else {
                    showConfirmationDialog();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.delete_all_history))
                .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })

                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteAllHistory();
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void deleteAllHistory() {
        dao.deleteAllHistory();
        adapter.changeCursor(dao.getOracodesCursor());
        adapter.notifyDataSetChanged();
        Toast.makeText(context, R.string.deleted_history, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater menuInflater = new MenuInflater(context);
        menuInflater.inflate(R.menu.context_menu, menu);
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
                Toast.makeText(context, R.string.history_deleted, Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onContextItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        String oracode = textView.getText().toString();

        callbackActivity.onHistoryItemClick(oracode);
    }

    public void updateHistory() {
        adapter.changeCursor(dao.getOracodesCursor());
        adapter.notifyDataSetChanged();
    }

    public interface OnHistoryItemClick {
        void onHistoryItemClick(String oracode);
    }
}
