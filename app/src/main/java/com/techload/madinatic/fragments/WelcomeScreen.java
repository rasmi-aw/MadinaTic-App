package com.techload.madinatic.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;

public class WelcomeScreen extends Fragment {
    private Animation blinkAnim;
    private ImageView image;
    private TextView welcome, next, afterWelcome, indicator;
    private int i = 0;


    public WelcomeScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        next = view.findViewById(R.id.next);
        welcome = view.findViewById(R.id.welcome);
        image = view.findViewById(R.id.flowers);
        afterWelcome = view.findViewById(R.id.after_welcome);
        indicator = view.findViewById(R.id.indicator);

        // preparing animations
        blinkAnim = AnimationUtils.loadAnimation(getContext(), R.anim.blink);


        final int[] images = new int[]{R.drawable.pc_user, R.drawable.cleaning, R.drawable.clean_city, R.drawable.breaking_news};

        final String[] descriptions = new String[]{
                getString(R.string.we_deal_with_problem),
                getString(R.string.clean_city),
                getString(R.string.see_news)};

        final String[] titles = new String[]{
                getString(R.string.processing),
                getString(R.string.results),
                getString(R.string.feedback)};

        final Animation moveOutSide = AnimationUtils.loadAnimation(getContext(), R.anim.move_right);
        moveOutSide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (i < 4) {
                    image.setImageResource(images[i]);
                    image.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.move_right_inside));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (i < 4) {
                    image.setImageResource(images[i]);
                }
                if (i < 3) {
                    indicator.setText((i + 2) + "/4");
                    welcome.setText(titles[i]);
                    welcome.startAnimation(blinkAnim);
                    afterWelcome.setText(descriptions[i]);
                    image.startAnimation(moveOutSide);
                    i++;
                    return;
                }
                MainActivity.putFragmentInMainActivityContent(new MainPager());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        //animating the screen

        welcome.startAnimation(blinkAnim);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        welcome = null;
        next = null;
        blinkAnim = null;

    }
}