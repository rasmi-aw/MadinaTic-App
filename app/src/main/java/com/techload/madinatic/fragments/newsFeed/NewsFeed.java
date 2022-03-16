package com.techload.madinatic.fragments.newsFeed;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.adapters.PostsListAdapter;
import com.techload.madinatic.data.networking.networkOperations.Posting;
import com.techload.madinatic.fragments.Sign_in;
import com.techload.madinatic.fragments.Sign_up;
import com.techload.madinatic.fragments.helpers.BackNavigationFragment;

import com.techload.madinatic.fragments.reports.ReportProblemScreen;
import com.techload.madinatic.fragments.reports.ReportsFragment;
import com.techload.madinatic.lists_items.Post;
import com.techload.madinatic.lists_items.Media;
import com.techload.madinatic.utils.AppSettingsUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class NewsFeed extends Fragment {
    //
    private AlertDialog popupWindow;
    //
    private ImageView reportButton;
    //
    private RecyclerView postsList;
    private PostsListAdapter adapter;
    //
    private DismissWindowListener dismissWindowListener;
    private CancelWindowListener cancelWindowListener;
    //
    private SwipeRefreshLayout swipeRefreshLayout;
    private Posting posting;
    //
    private ArrayList<Post> posts;
    public static Fragment thisFragment;

    public NewsFeed() {
        // Required empty public constructor
        posts = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_feed, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        thisFragment = this;
        //
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_news_feed);
        /* case we're in the unsigned visitor screen every thing needs to look
         like it's the the light mode since it's always the same page to show*/

        if (MainActivity.token == null) {
            view.setBackgroundResource(R.color.white);


        } else if (MainActivity.appTheme == AppSettingsUtils.APPLICATION_DARK_THEME) {
            swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.black);
            swipeRefreshLayout.setColorSchemeResources(R.color.white);

        } else {
            swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.extra_light_gray);
            swipeRefreshLayout.setColorSchemeResources(R.color.black);
        }

        initializePosts(view);
        //
        initializeReporting(view);
        //

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                posting.loadPosts();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        popupWindow = null;
        reportButton = null;
        postsList = null;
        adapter.updatePost(null);
        adapter = null;
        dismissWindowListener = null;
        cancelWindowListener = null;
        swipeRefreshLayout = null;
        posting = null;
    }


    /**/
    private void initializePosts(View view) {

        //
        postsList = view.findViewById(R.id.posts_list);
        postsList.setItemViewCacheSize(20);
        postsList.setHasFixedSize(true);
        postsList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostsListAdapter(null);
        //
        Post hint = new Post(-1,
                "COVID-19",
                getString(R.string.follow_guidlines),
                null,
                AppSettingsUtils.formatDate(MainActivity.appLanguage, new SimpleDateFormat("dd-MM-yy").format(new Date())),
                new Media[]{new Media(R.drawable.breaking_news, Media.TYPE_IMAGE)},
                null,
                Post.TYPE_HINT);
        if (posts.isEmpty()) posts.add(hint);
        //
        postsList.setAdapter(adapter);


        posting = new Posting(getContext(), MainActivity.readableDB, MainActivity.writableDB) {
            @Override
            public void whenOperationFinished(Object result, int flag) {
                super.whenOperationFinished(result, flag);
                if (flag == Flag.FINISHED_READING_FROM_DB) {
                    posts.addAll((ArrayList<Post>) result);
                    if (adapter != null) adapter.updatePost(posts);

                } else if (flag == Flag.FINISHED_LOADING_FROM_SERVER) {
                    readNewsFromInternalDB();
                }

                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
            }
        };
        posting.readNewsFromInternalDB();
        posting.loadPosts();


    }

    /**/
    private void initializeReporting(final View view) {
        //

        reportButton = view.findViewById(R.id.report_problem_button);
        final Animation halfRotationLeft = AnimationUtils.loadAnimation(getContext(), R.anim.half_rotation_left);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                halfRotationLeft.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {


                        if (MainActivity.token != null) MainActivity.
                                putFragmentInMainActivityContentAndAddToBackStack(new BackNavigationFragment(new ReportProblemScreen(), R.string.report_problem));
                        else showUnsignedVisitorReportWindow(view);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                v.startAnimation(halfRotationLeft);

            }
        });
        //
        if (MainActivity.token == null) {
            popupWindow = (new AlertDialog.Builder(getContext())).create();
            dismissWindowListener = new DismissWindowListener();
            cancelWindowListener = new CancelWindowListener();
            popupWindow.setOnDismissListener(new DismissWindowListener());
            popupWindow.setOnCancelListener(new CancelWindowListener());
        }
    }

    /**/
    private void showUnsignedVisitorReportWindow(View parentFragment) {
        popupWindow.setView(LayoutInflater.from(parentFragment.getContext()).inflate(R.layout.window_unsigned_visitor_report, null));
        popupWindow.getWindow().getDecorView().setBackground(null);
        popupWindow.show();
        //
        popupWindow.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        //
        popupWindow.findViewById(R.id.see_world_wide_reports).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                MainActivity.putFragmentInMainActivityContentAndAddToBackStack(
                        new BackNavigationFragment(new ReportsFragment(false), R.string.unsigned_see_world_wide_reports));

            }
        });
        //sign
        popupWindow.findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                MainActivity.putFragmentInMainActivityContentAndAddToBackStack(new Sign_in());

            }
        });
        //
        popupWindow.findViewById(R.id.sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                MainActivity.putFragmentInMainActivityContentAndAddToBackStack(new Sign_up());

            }
        });

    }

    // classes
    private class DismissWindowListener implements DialogInterface.OnDismissListener {
        Animation halfRotationRight;

        public DismissWindowListener() {
            halfRotationRight = AnimationUtils.loadAnimation(getContext(), R.anim.half_rotation_right);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            reportButton.startAnimation(halfRotationRight);
        }
    }

    //
    private class CancelWindowListener extends DismissWindowListener implements DialogInterface.OnCancelListener {
        @Override
        public void onCancel(DialogInterface dialog) {
            reportButton.startAnimation(halfRotationRight);
        }
    }

}//