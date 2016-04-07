package matheusfroes.oracode.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import matheusfroes.oracode.R;
import matheusfroes.oracode.db.OracodeDAO;
import matheusfroes.oracode.models.Oracode;
import matheusfroes.oracode.utils.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private OracodeDAO oracodeDAO;
    private Button btnPesquisar;
    private EditText edtOracode;
    private TextView tvOracodeBody, tvCauseBody, tvActionBody, tvOracode;
    private ProgressDialog progressDialog;
    private CardView cardOracode, cardCause, cardAction;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewsById();

        btnPesquisar.setOnClickListener(this);

        oracodeDAO = new OracodeDAO(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.searching_oracode));
    }

    private void findViewsById() {
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        btnPesquisar = (Button) findViewById(R.id.btn_search);
        edtOracode = (EditText) findViewById(R.id.edt_oracode);
        tvOracode = (TextView) findViewById(R.id.tv_oracode);
        tvOracodeBody = (TextView) findViewById(R.id.tv_oracode_body);
        tvCauseBody = (TextView) findViewById(R.id.tv_cause_body);
        tvActionBody = (TextView) findViewById(R.id.tv_action_body);

        cardOracode = (CardView) findViewById(R.id.card_oracode);
        cardCause = (CardView) findViewById(R.id.card_cause);
        cardAction = (CardView) findViewById(R.id.card_action);
    }

    @Override
    public void onClick(View v) {
        if (!hasInternetConnection()) {
            Toast.makeText(MainActivity.this, R.string.internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }


        if (edtOracode.getText().toString().trim().length() == 0) {
            return;
        }
        String oracodeStr = Utils.formatOracode(edtOracode.getText().toString().trim());
        Oracode oracode = getFromCache(oracodeStr);
        if (oracode != null) {
            tvOracode.setText(oracode.getOracode());
            tvOracodeBody.setText(oracode.getExplanation());
            tvCauseBody.setText(oracode.getCause());
            tvActionBody.setText(oracode.getAction());

            scrollView.setVisibility(View.VISIBLE);

            return;
        }

        btnPesquisar.setEnabled(false);
        AsyncTaskCompat.executeParallel(new AsyncTask<String, Void, String[]>() {
            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }

            @Override
            protected String[] doInBackground(String... params) {
                Document doc = null;
                try {
                    doc = Jsoup.connect("http://ora-" + params[0] + ".ora-code.com/").get();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                if (doc == null) { return null; }
                Elements elements = doc.select("table");

                final Elements oracodes = elements.select("tr");

                String result[] = new String[3];

                for (int i = 0; i < 3; i++) {
                    result[i] = oracodes.get(i).text();
                }


                return result;
            }

            @Override
            protected void onPostExecute(String[] result) {
                if (result == null) {
                    Toast.makeText(MainActivity.this, R.string.internet_connection, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                String oracode = result[0];
                if (!oracode.startsWith("ORA")) {
                    tryAnotherParseMethod();
                    return;
                }

                oracode = result[0].substring(0, 9);
                String oracodeBody = result[0].substring(11);
                String causeBody = result[1].substring(7);
                String actionBody = result[2].substring(8);

                Oracode oracodeObj = new Oracode(oracode, oracodeBody, causeBody, actionBody);
                oracodeDAO.insert(oracodeObj);


                tvOracode.setText(oracode);
                tvOracodeBody.setText(oracodeBody);
                tvCauseBody.setText(causeBody);
                tvActionBody.setText(actionBody);
                progressDialog.dismiss();

                scrollView.setVisibility(View.VISIBLE);
                btnPesquisar.setEnabled(true);
            }
        }, edtOracode.getText().toString());

    }

    private boolean hasInternetConnection() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                return true;
            }
        }

        return false;
    }

    private void tryAnotherParseMethod() {
        AsyncTaskCompat.executeParallel(new AsyncTask<String, Void, String[]>() {
            @Override
            protected String[] doInBackground(String... params) {
                Document doc = null;
                try {
                    doc = Jsoup.connect("http://ora-" + params[0] + ".ora-code.com/").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (doc == null) {return null;}
                Elements elements = doc.select("table");

                Elements oracodes = elements.select("tbody");

                if (oracodes.size() < 7) {
                    return new String[]{""};
                }
                String text = oracodes.get(7).text();

                int cause = text.indexOf("Cause");
                int action = text.indexOf("Action");

                String oracode = text.substring(0, cause - 1);
                String causeBody = text.substring(cause, action-1);
                String actionBody = text.substring(action);

                String result[] = new String[3];
                result[0] = oracode;
                result[1] = causeBody;
                result[2] = actionBody;


                return result;
            }

            @Override
            protected void onPostExecute(String[] result) {
                if (result == null) {return;}
                String oracode = result[0];
                if (!oracode.startsWith("ORA")) {
                    progressDialog.dismiss();
                    btnPesquisar.setEnabled(true);
                    oracodeNotFoundDialog();
                    return;
                }

                oracode = result[0].substring(0, 9);
                String oracodeBody = result[0].substring(11);
                String causeBody = result[1].substring(7);
                String actionBody = result[2].substring(8);

                Oracode oracodeObj = new Oracode(oracode, oracodeBody, causeBody, actionBody);
                oracodeDAO.insert(oracodeObj);


                tvOracode.setText(oracode);
                tvOracodeBody.setText(oracodeBody);
                tvCauseBody.setText(causeBody);
                tvActionBody.setText(actionBody);
                progressDialog.dismiss();

                scrollView.setVisibility(View.VISIBLE);
                btnPesquisar.setEnabled(true);
            }
        }, edtOracode.getText().toString());
    }

    private Oracode getFromCache(String oracodeStr) {
        Oracode oracode = oracodeDAO.getOracodeByCode(oracodeStr);
        return oracode;
    }

    private void oracodeNotFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.oracode_not_found))
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ic_history:
                Intent intent = new Intent(this, OracodeHistory.class);
                startActivityForResult(intent, 1);
                break;


        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String oracode = data.getStringExtra("oracode");

            String code = oracode.split("-")[1];
            edtOracode.setText(code);
            btnPesquisar.callOnClick();
        }
    }
}
