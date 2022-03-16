package com.techload.madinatic.fragments.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.adapters.reports.ReportsListAdapter;
import com.techload.madinatic.data.networking.networkOperations.Reporting;
import com.techload.madinatic.fragments.helpers.BackNavigationFragment;
import com.techload.madinatic.fragments.helpers.LoadingFragment;
import com.techload.madinatic.fragments.helpers.TextEditableFragment;
import com.techload.madinatic.lists_items.Report;
import com.techload.madinatic.utils.AppSettingsUtils;
import com.techload.madinatic.utils.GraphicUtils;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsFragment extends TextEditableFragment {
    private RecyclerView reportsList;
    private ImageView profilePicture, seeWorldWideReports;
    private SearchView searchView;
    private ReportsListAdapter adapter;
    private TextView myReportsTitle, profileName, nationalId;
    private boolean userReports;
    private ArrayList<Report> reports;
    private ImageView refresh;
    private Reporting reporting;

    //
    public static LoadingFragment loadingFragment;


    public ReportsFragment() {
        userReports = true;
    }

    public ReportsFragment(boolean userReports) {
        // Required empty public constructor
        this.userReports = userReports;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reporting = new Reporting(getContext(), MainActivity.readableDB, MainActivity.writableDB) {
            @Override
            public void whenOperationFinished(Object result, int flag) {

                if (flag == Flag.FINISHED_LOADING_REPORTS_FROM_SERVER ||
                        flag == Flag.FINISHED_LOADING_USER_REPORTS_FROM_SERVER) {
                    if (userReports) {
                        readUserReportsFromInternalDB();
                    } else readAllReportsFromInternalDB();
                } else if (flag == Flag.FINISHED_READING_FROM_DB) {
                    reports = (ArrayList<Report>) result;
                    if (reportsList != null) {
                        ((ReportsListAdapter) reportsList.getAdapter()).updateList(reports);
                    }

                }
            }
        };

        if (userReports) {

            reporting.loadUserReports();

        } else {/*the user is not signed in or wants to see world wide reports*/
            reporting.loadAllReports();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        loadingFragment = this;
        //
        initialization(view);
        //
        /*case there's no internet connection*/
        if (!MainActivity.isConnectedToInternet(getContext())) {
            Toast.makeText(getContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
        //


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reportsList = null;
        profilePicture = null;
        seeWorldWideReports = null;
        searchView = null;
        adapter = null;
        myReportsTitle = null;
        profileName = null;
        nationalId = null;
        refresh = null;
    }

    /*initialize the screen*/
    private void initialization(View view) {
        //
        profileName = view.findViewById(R.id.profile_name);
        nationalId = view.findViewById(R.id.national_id);
        myReportsTitle = view.findViewById(R.id.my_reports);
        profilePicture = view.findViewById(R.id.profile_picture);
        seeWorldWideReports = view.findViewById(R.id.see_world_wide_reports);


        //
        if (!userReports) {
            ((ViewGroup) profileName.getParent()).removeView(profileName);
            profileName.setVisibility(View.GONE);
            nationalId.setVisibility(View.GONE);
            myReportsTitle.setVisibility(View.GONE);
            profilePicture.setVisibility(View.GONE);
            seeWorldWideReports.setVisibility(View.GONE);
        } else {
//            profileName.setText(MainActivity.userData.getFirstName() + " " + MainActivity.userData.getFamilyName());
//            nationalId.setText(getString(R.string.id) + ": " + MainActivity.userData.getNationalId());
        }

        //
        profilePicture.setBackgroundResource(MainActivity.profilePictureBackgroundResource);
        if (MainActivity.profilePicturePath != null && !MainActivity.profilePicturePath.isEmpty())
            GraphicUtils.toCircledImage(profilePicture, MainActivity.allAppImages.get(MainActivity.profilePicturePath));
        else if (MainActivity.userIsMale)
            GraphicUtils.toCircledImage(profilePicture, getResources(), R.drawable.user_male);
        else GraphicUtils.toCircledImage(profilePicture, getResources(), R.drawable.user_female);
        //
        reportsList = view.findViewById(R.id.reports_list);
        reportsList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReportsListAdapter(reportsList, null);
        reportsList.setAdapter(adapter);
        reportsList.setHasFixedSize(true);

        if (userReports) {
            reporting.readUserReportsFromInternalDB();

        } else {/*the user is not signed in or wants to see world wide reports*/
            reporting.readAllReportsFromInternalDB();
        }
        initializeSearchOption(view);
        //
        seeWorldWideReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.putFragmentInMainActivityContentAndAddToBackStack(
                        new BackNavigationFragment(new ReportsFragment(false), R.string.unsigned_see_world_wide_reports));
            }
        });
        //
        ((NestedScrollView) view.findViewById(R.id.nestedScrollView)).setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                adapter.resetLastClickedElement();
            }

        });
        //
        refresh = view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (!AppSettingsUtils.isConnectedToInternet(getContext())) {
                    Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                } else {

                    showProgressWindow(getString(R.string.connecting_to_server), false);
                    Reporting reporting = new Reporting(getContext(), MainActivity.readableDB, MainActivity.writableDB) {
                        @Override
                        public void whenOperationFinished(Object result, int flag) {
                            if (flag == Flag.STARTED_LOADING_FROM_SERVER) {
                                if (!isDetached())
                                    showProgressWindow(getString(R.string.connecting_to_server), false);
                            } else if (flag == Flag.FINISHED_LOADING_USER_REPORTS_FROM_SERVER
                                    || flag == Flag.FINISHED_LOADING_REPORTS_FROM_SERVER) {

                                if (userReports) readUserReportsFromInternalDB();
                                else readAllReportsFromInternalDB();
                            } else if (flag == Flag.FINISHED_READING_FROM_DB) {
                                adapter.updateList((ArrayList<Report>) result);
                                if (!isDetached())
                                    showResultsWindow(getString(R.string.reports_update), RESULT_UPDATE_DATA);

                            } else if (flag == Flag.ERROR) {
                                if (!isDetached()) hideProgressWindow();
                            }
                        }
                    };
                    if (userReports) reporting.loadUserReports();
                    else reporting.loadAllReports();
                }

            }
        });

    }

    /*providing search option*/
    private void initializeSearchOption(View view) {
        searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filterSearchingList(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterSearchingList(newText);
                return false;
            }

        });
    }


    @Override
    public void doWhenResultsSeen() {

    }
}//
