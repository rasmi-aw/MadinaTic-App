package com.techload.madinatic.fragments.newsFeed;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.adapters.MediaSlideAdapter;
import com.techload.madinatic.lists_items.Post;
import com.techload.madinatic.utils.AppSettingsUtils;


public class SeeCompletePost extends Fragment {
    private Post post;

    public SeeCompletePost(Post post) {
        this.post = post;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_see_complete_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView mediasPager = view.findViewById(R.id.media_pager);
        mediasPager.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mediasPager.setAdapter(new MediaSlideAdapter(post.medias, (TextView) view.findViewById(R.id.pageIndicator)));
        new PagerSnapHelper().attachToRecyclerView(mediasPager);
        //
        ((TextView) view.findViewById(R.id.post_title)).setText(post.title);
        ((TextView) view.findViewById(R.id.post_description)).setText(post.description);
        ((TextView) view.findViewById(R.id.post_id)).setText(getString(R.string.id) + ": " + post.id);
        ((TextView) view.findViewById(R.id.post_info)).setText(Html.fromHtml(
                "<b><font color='#546E7A'>" + getString(R.string.published_by) + "</b><font> " + post.postOwnerName
                        + "<br><b><font color='#546E7A'>" +
                        getString(R.string.published_in) + "</b></font> " + AppSettingsUtils.formatDate(MainActivity.appLanguage, post.datePosted)));
        //
        view.findViewById(R.id.back_navigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fragmentManagerMainActivity.popBackStack();
            }
        });
        //
        Context wrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenuStyle);
        final PopupMenu textMenu = new PopupMenu(wrapper, view.findViewById(R.id.post_title));
        textMenu.getMenuInflater().inflate(R.menu.text_size, textMenu.getMenu());
        textMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.text_small:
                        ((TextView) view.findViewById(R.id.post_description)).setTextSize(getResources().getDimension(R.dimen.text_small));
                        break;

                    case R.id.text_medium:
                        ((TextView) view.findViewById(R.id.post_description)).setTextSize(getResources().getDimension(R.dimen.text_medium));
                        break;

                    case R.id.text_Large:
                        ((TextView) view.findViewById(R.id.post_description)).setTextSize(getResources().getDimension(R.dimen.text_Large));
                        break;
                }
                return true;
            }
        });
        //
        (view.findViewById(R.id.post_description)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                textMenu.show();
                return true;
            }
        });
        //
    }
}