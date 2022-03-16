package com.techload.madinatic.fragments.helpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.SnapHelper;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.fragments.password.ChangePasswordScreen;
import com.techload.madinatic.fragments.password.ResetPasswordVerification;
import com.techload.madinatic.utils.AppSettingsUtils;


public class BackNavigationFragment extends Fragment {
    private static PreviousNavigationListener previousNavigationListener;
    private Fragment fragment;
    private String pageName = null;
    private int pageNameRes = R.string.previous_page;
    //


    public BackNavigationFragment(Fragment fragment, int pageNameRes) {
        this.fragment = fragment;
        this.pageNameRes= pageNameRes;
    }

    public BackNavigationFragment(Fragment fragment, String pageName) {
        this.fragment = fragment;
        this.pageName = pageName;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_back_navigation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* Back navigation part initialization */
        if (previousNavigationListener == null) {
            previousNavigationListener = new PreviousNavigationListener();
        }
        ImageView backNavigation = view.findViewById(R.id.back_navigation);
        if (backNavigation != null) {

            if (MainActivity.appLanguage.equals("ar")) backNavigation.setRotation(180);

            backNavigation.setOnClickListener(previousNavigationListener);
        }

        /*inflating a child fragment below the back navigation components & setting the new page name*/
        if (pageName == null ) pageName = getString(pageNameRes);
        ((TextView) view.findViewById(R.id.page_name)).setText(pageName);
        getChildFragmentManager().beginTransaction().replace(R.id.page_content, fragment).commit();
        //
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragment = null;
        previousNavigationListener = null;
    }

    private class PreviousNavigationListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            getChildFragmentManager().beginTransaction().remove(fragment);
            MainActivity.fragmentManagerMainActivity.popBackStack();

//            MainActivity.removeFragmentFromBackStack(fragment);
        }
    }

}