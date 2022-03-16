package com.techload.madinatic.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.fragments.newsFeed.NewsFeed;


/**
 * A simple {@link Fragment} subclass.
 */
public class UnsignedVisitorScreen extends Fragment {
    //
    private TextView sign_in;
    //

    public UnsignedVisitorScreen() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unsigned_visitor_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialization(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sign_in = null;
    }

    private void initialization(View view) {

        //
        sign_in = view.findViewById(R.id.sign_in);
        sign_in.setText(Html.fromHtml(getResources().getString(R.string.already_registered)
                + "<font color=\"#009966\"><b><b><b>" + getResources().getString(R.string.sign_in) + "</b></b></b></font>"));
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.putFragmentInMainActivityContentAndAddToBackStack(new Sign_in());
            }
        });
        //
        view.findViewById(R.id.sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.putFragmentInMainActivityContentAndAddToBackStack(new Sign_up());
            }
        });
        //putting news feed under the collapsing toolbar
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.news_feed_container, new NewsFeed());
        transaction.commit();

    }
}//
