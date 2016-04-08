package matheusfroes.oracode.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import matheusfroes.oracode.R;

/**
 * Created by mathe on 07/04/2016.
 */
public class AboutFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, null, false);

        TextView textView = (TextView) view.findViewById(R.id.about_message);

        textView.setMovementMethod(LinkMovementMethod.getInstance());


        return view;
    }
}
