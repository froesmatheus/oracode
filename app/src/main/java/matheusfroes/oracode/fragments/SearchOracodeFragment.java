package matheusfroes.oracode.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

/**
 * Created by mathe on 06/04/2016.
 */
public class SearchOracodeFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private OracodeDAO oracodeDAO;
    private Button btnPesquisar;
    private EditText edtOracode;
    private TextView tvOracodeBody, tvCauseBody, tvActionBody, tvOracode;
    private ProgressDialog progressDialog;
    private ScrollView scrollView;
    private OnHistoryUpdate callbackActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callbackActivity = (OnHistoryUpdate) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnHistoryUpdate");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null, false);
        context = getActivity();
        findViewsById(view);


        btnPesquisar.setEnabled(false);
        edtOracode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    btnPesquisar.setEnabled(false);
                else
                    btnPesquisar.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnPesquisar.setOnClickListener(this);

        oracodeDAO = new OracodeDAO(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.searching_oracode));
        return view;
    }

    private void findViewsById(View view) {
        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        btnPesquisar = (Button) view.findViewById(R.id.btn_search);
        edtOracode = (EditText) view.findViewById(R.id.edt_oracode);
        tvOracode = (TextView) view.findViewById(R.id.tv_oracode);
        tvOracodeBody = (TextView) view.findViewById(R.id.tv_oracode_body);
        tvCauseBody = (TextView) view.findViewById(R.id.tv_cause_body);
        tvActionBody = (TextView) view.findViewById(R.id.tv_action_body);
    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        if (!hasInternetConnection()) {
            Toast.makeText(context, R.string.internet_connection, Toast.LENGTH_SHORT).show();
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
                Document doc;
                try {
                    doc = Jsoup.connect("http://ora-" + params[0] + ".ora-code.com/").get();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                if (doc == null) {
                    return null;
                }
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
                    Toast.makeText(context, R.string.internet_connection, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    btnPesquisar.setEnabled(false);
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
                insertOracode(oracodeObj);


                tvOracode.setText(oracode);
                tvOracodeBody.setText(oracodeBody);
                tvCauseBody.setText(causeBody);
                tvActionBody.setText(actionBody);
                progressDialog.dismiss();

                scrollView.setVisibility(View.VISIBLE);
                btnPesquisar.setEnabled(true);
                hideKeyboard();
            }
        }, edtOracode.getText().toString());

    }

    private boolean hasInternetConnection() {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                if (doc == null) {
                    return null;
                }
                Elements elements = doc.select("table");

                Elements oracodes = elements.select("tbody");

                if (oracodes.size() < 7) {
                    return new String[]{""};
                }
                String text = oracodes.get(7).text();

                int cause = text.indexOf("Cause");
                int action = text.indexOf("Action");

                String oracode = text.substring(0, cause - 1);
                String causeBody = text.substring(cause, action - 1);
                String actionBody = text.substring(action);

                String result[] = new String[3];
                result[0] = oracode;
                result[1] = causeBody;
                result[2] = actionBody;


                return result;
            }

            @Override
            protected void onPostExecute(String[] result) {
                if (result == null) {
                    return;
                }
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
                insertOracode(oracodeObj);


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
        return oracodeDAO.getOracodeByCode(oracodeStr);
    }

    private boolean insertOracode(Oracode oracode) {
        boolean inserted = oracodeDAO.insert(oracode);

        if (inserted) {
            callbackActivity.updateHistory();
        }

        return inserted;
    }

    private void oracodeNotFoundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
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

    public void updateOracodeField(String oracode) {
        edtOracode.setText(oracode);
        btnPesquisar.callOnClick();
    }


    public interface OnHistoryUpdate {
        void updateHistory();
    }

    public void hideKeyboard() {
        InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

}
