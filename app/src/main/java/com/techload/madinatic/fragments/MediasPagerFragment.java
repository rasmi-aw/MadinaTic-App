package com.techload.madinatic.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.adapters.MediaSlideAdapter;
import com.techload.madinatic.lists_items.Media;

import java.util.ArrayList;

public class MediasPagerFragment extends Fragment {
    private Media[] medias;

    public MediasPagerFragment(Media[] medias) {
        //
        this.medias = medias;
    }

    public MediasPagerFragment(ArrayList<Media> medias) {
        //
        this.medias = new Media[medias.size()];
        for (int i = 0; i < medias.size(); i++) {
            this.medias[i] = medias.get(i);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medias_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        //
        RecyclerView mediaPager = view.findViewById(R.id.media_pager);
        mediaPager.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mediaPager.setAdapter(new MediaSlideAdapter(medias, ((TextView) view.findViewById(R.id.pageIndicator))));
        new PagerSnapHelper().attachToRecyclerView(mediaPager);


        //
        View backNav = view.findViewById(R.id.back_navigation);
        if (MainActivity.appLanguage.equals("ar")) backNav.setRotation(180);
        backNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                MainActivity.fragmentManagerMainActivity.popBackStack();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        medias = null;
    }
}