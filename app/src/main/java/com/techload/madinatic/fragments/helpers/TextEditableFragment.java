package com.techload.madinatic.fragments.helpers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.techload.madinatic.R;

/* in this fragment we look always for the container view
that holds R.id.parent and Makeit close the keyBoard when it's clicked */

public abstract class TextEditableFragment extends LoadingFragment {
    public static CloseKeyBoardListener closeKeyBoardListener;
    public NestedScrollView nestedScrollView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (closeKeyBoardListener == null) closeKeyBoardListener = new CloseKeyBoardListener();
        /**/
        View parent = view.findViewById(R.id.parent);
        if (parent != null) parent.setOnClickListener(closeKeyBoardListener);
        /**/
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        if (nestedScrollView != null)
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int diff = (scrollY - oldScrollY);
                    if (diff > 50 || diff < -50) closeKeyboard(v);
                }
            });

    }

    @Override
    public void onPause() {
        closeKeyboard(getView());
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeKeyBoardListener = null;
        nestedScrollView = null;
    }

    /*Close keyboard*/
    public static void closeKeyboard(View v) {
        InputMethodManager manager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    public class CloseKeyBoardListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            closeKeyboard(v);
        }
    }
}
