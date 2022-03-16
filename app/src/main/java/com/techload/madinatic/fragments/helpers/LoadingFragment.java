package com.techload.madinatic.fragments.helpers;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.techload.madinatic.R;

public abstract class LoadingFragment extends Fragment {
    private AlertDialog progressWindow, resultsWindow, passwordWindow;

    /**/
    public static final char RESULT_WARNING = 'w';
    public static final char RESULT_ERROR = 'e';
    public static final char RESULT_EMAIL_SENT = 'm';
    public static final char RESULT_WELL_DONE = 'd';
    public static final char RESULT_UPDATE_DATA = 'u';


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //
        progressWindow = ((new AlertDialog.Builder(getContext()))).create();
        //
        resultsWindow = ((new AlertDialog.Builder(getContext()))).create();
        resultsWindow.setCancelable(false);
        //
        passwordWindow = ((new AlertDialog.Builder(getContext()))).create();

    }

    //
    public void showProgressWindow(String progressMessage, boolean cancelable) {
        if (progressWindow != null) progressWindow.dismiss();
        progressWindow.setCancelable(cancelable);
        View layout = getLayoutInflater().inflate(R.layout.window_circular_progress, null);
        TextView message = layout.findViewById(R.id.progress_message);
        progressWindow.setView(layout);
        if (progressWindow != null && isVisible()) progressWindow.show();
        message.setText(progressMessage);
        progressWindow.getWindow().getDecorView().setBackground(null);
    }


    public void hideProgressWindow() {
        progressWindow.cancel();
    }

    //
    public void showResultsWindow(String resultsMessage, char resultsType) {

        if (progressWindow != null) progressWindow.dismiss();
        if (resultsWindow != null) resultsWindow.dismiss();
        View layout = getLayoutInflater().inflate(R.layout.window_results_dialog, null);
        resultsWindow.setView(layout);
        if (resultsWindow != null && isVisible()) resultsWindow.show();
        resultsWindow.getWindow().getDecorView().setBackground(null);
        //
        TextView message = resultsWindow.findViewById(R.id.results_message);
        message.setText(resultsMessage);
        //
        ImageView icon = layout.findViewById(R.id.results_image);
        switch (resultsType) {
            case RESULT_WARNING:
                icon.setImageResource(R.drawable.warning);
                icon.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.warning));
                break;

            case RESULT_ERROR:
                icon.setImageResource(R.drawable.report_outlined);
                icon.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.error));
                break;

            case RESULT_EMAIL_SENT:
                icon.setImageTintList(ContextCompat.getColorStateList(getContext(),-1));
                icon.setImageResource(R.drawable.gmail_ic);
                break;

            case RESULT_WELL_DONE:
                icon.setImageResource(R.drawable.done);
                icon.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.well_done));
                break;

            case RESULT_UPDATE_DATA:
                icon.setImageResource(R.drawable.alert);
                icon.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.error));
                break;
        }
        //
        layout.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doWhenResultsSeen();
                if (resultsWindow != null) resultsWindow.dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        progressWindow = null;
        resultsWindow = null;
        passwordWindow = null;

    }

    public void hideResultsWindow() {
        resultsWindow.dismiss();
    }

    //
    public void showPasswordWindow(String title, int iconRes) {
        final View layout = getLayoutInflater().inflate(R.layout.window_get_user_password, null);
        passwordWindow.setView(layout);
        if (isVisible())passwordWindow.show();
        passwordWindow.getWindow().setBackgroundDrawable(null);
        //
        if (title != null && !title.isEmpty())
            ((TextView) layout.findViewById(R.id.message)).setText(title);
        //
        ((ImageView) layout.findViewById(R.id.icon)).setImageResource(iconRes);
        //
        layout.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordWindow.dismiss();
            }
        });
        //
        layout.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = ((TextInputEditText) layout.findViewById(R.id.password_editText)).getText().toString().trim();
                if (password.length() < 5) {
                    ((TextInputLayout) (layout.findViewById(R.id.password_editText)).getParent()
                            .getParent()).setError(getString(R.string.error_too_short));
                    return;
                }
                doWhenPasswordReady(password);
                passwordWindow.dismiss();

            }
        });

    }


    public abstract void doWhenResultsSeen();

    public void doWhenPasswordReady(String password) {
    }

}