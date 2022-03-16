package com.techload.madinatic.custom_views;


import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.techload.madinatic.R;

public class BookMarkView extends androidx.appcompat.widget.AppCompatImageView {
    private boolean bookMarked = false;
    public static Toast toast;


    public BookMarkView(final Context context, AttributeSet attr) {
        super(context, attr);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setBookMarked(!bookMarked);
            }
        });


    }


    public void setBookMarked(boolean bookMarked) {
        this.bookMarked = bookMarked;

        setAlreadyBookmarked(bookMarked);

        if (toast == null) {
            toast = new Toast(getContext());
            toast.setView(LayoutInflater.from(getContext()).inflate(R.layout.toast_text, (ViewGroup) getRootView(), false));
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        final TextView message = toast.getView().findViewById(R.id.text);

        /*preparing toast to be fired*/
        if (bookMarked) {
            message.setText(R.string.bookmarked);
            message.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        } else {
            message.setText(R.string.unbookmarked);
            message.setTextColor(ContextCompat.getColor(getContext(), R.color.orange));
        }
        toast.show();

    }


    public void setAlreadyBookmarked(boolean bookMarked) {

        if (bookMarked) {
            setBackgroundResource(R.drawable.circle_blue_gray);
        } else {
            setBackgroundResource(0);
        }
    }

}