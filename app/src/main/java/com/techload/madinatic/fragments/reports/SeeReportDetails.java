package com.techload.madinatic.fragments.reports;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.adapters.MediaSlideAdapter;
import com.techload.madinatic.adapters.reports.ReportsListAdapter;
import com.techload.madinatic.data.InternalDataBase.InternalDataBaseSchemas;
import com.techload.madinatic.data.networking.RequestHeader;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.data.networking.networkOperations.LimitedNetworkOperation;
import com.techload.madinatic.fragments.MediasPagerFragment;
import com.techload.madinatic.fragments.helpers.BackNavigationFragment;
import com.techload.madinatic.fragments.helpers.LoadingFragment;
import com.techload.madinatic.lists_items.Report;
import com.techload.madinatic.utils.AppSettingsUtils;
import com.techload.madinatic.utils.MemoryUtils;

public class SeeReportDetails extends LoadingFragment {
    private Report report;


    public SeeReportDetails(Report report) {
        this.report = report;
    }


    @Override
    public void doWhenResultsSeen() {
        MainActivity.fragmentManagerMainActivity.popBackStack();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_see_report_details, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        report = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*initializing page indicator*/
        final TextView pageIndicator = view.findViewById(R.id.pageIndicator);

        /* initializing mediaPager */
        RecyclerView mediasPager = view.findViewById(R.id.media_pager);
        mediasPager.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mediasPager.setAdapter(new MediaSlideAdapter(report.media, pageIndicator));
        new PagerSnapHelper().attachToRecyclerView(mediasPager);

        if (report.media == null || report.media.length <= 1) {
            pageIndicator.setVisibility(View.GONE);
        } else {
            pageIndicator.setVisibility(View.VISIBLE);

        }


        /*filling report details*/
        String textColor = (MainActivity.appTheme == AppSettingsUtils.APPLICATION_DARK_THEME) ? "#ffffff" : "#000000";

        ((TextView) view.findViewById(R.id.report_title)).setText(Html.fromHtml(
                "<strong>"
                        + getString(R.string.title)
                        + "</strong>"
                        + ": <font color=\"" +
                        textColor
                        + "\"> " + report.description));

        ((TextView) view.findViewById(R.id.report_category)).setText(Html.fromHtml(
                "<strong>"
                        + getString(R.string.category)
                        + "</strong>"
                        + ": <font color=\"" +
                        textColor
                        + "\"> " + report.category));

        ((TextView) view.findViewById(R.id.report_description)).setText(Html.fromHtml(
                "<strong>"
                        + getString(R.string.description)
                        + "</strong>"
                        + ": <font color=\"" +
                        textColor
                        + "\"> " +
                        "<br>" + report.description));

        /*report rest info*/

        ((TextView) view.findViewById(R.id.report_id)).setText(getString(R.string.id)+": "+report.id);

        ((TextView) view.findViewById(R.id.date_sent)).setText(Html.fromHtml(
                "<small>" + report.dateSent + "</small>"));


        ((TextView) view.findViewById(R.id.address)).setText(Html.fromHtml(
                "<b>"
                        + getResources().getString(R.string.address)
                        + "</b>"
                        + ": <font color=\"" +
                        textColor
                        + "\">" + report.address));

        String state = null;

        switch (report.state) {
            case Report.STATE_SAVED:
                state = getString(R.string.state_saved);
                break;

            case Report.STATE_SENT:
                state = getString(R.string.state_sent);
                break;

            case Report.STATE_BEING_PROCESSED:
                state = getString(R.string.state_being_processed);
                break;

            case Report.STATE_PROCESSED:
                state = getString(R.string.state_processed);
                break;

            case Report.STATE_REJECTED:
                state = getString(R.string.state_rejected);
                break;

            case Report.STATE_INCOMPLETE:
                state = getString(R.string.state_incomplete);
                break;
        }

        ((TextView) view.findViewById(R.id.report_state)).setText(Html.fromHtml(
                "<b>"
                        + getResources().getString(R.string.state)
                        + "</b>"
                        + ": "
                        + state));

        TextView reason = view.findViewById(R.id.rejection_reason);
        if (report.reasonWhyRejected == null) {
            reason.setVisibility(View.GONE);
        } else {
            reason.setText(Html.fromHtml(
                    "<b>"
                            + getResources().getString(R.string.reason_of_rejection)
                            + "</b>"
                            + ": <font color=\"" +
                            textColor
                            + "\">" + report.reasonWhyRejected));
        }

        /**/
        final View restReportInfo = view.findViewById(R.id.report_rest_info);
        restReportInfo.setVisibility(View.GONE);


        /**/
        view.findViewById(R.id.show_more_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restReportInfo.getVisibility() == View.GONE) {
                    restReportInfo.setVisibility(View.VISIBLE);
                    restReportInfo.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.move_up));
                } else {

                    restReportInfo.setVisibility(View.GONE);


                }
            }
        });

        /*Actions*/
        View editReport = view.findViewById(R.id.edit_report_container),
                completeReport = view.findViewById(R.id.complete_report_container),
                deleteReport = view.findViewById(R.id.delete_report_container);

        if (report.state != Report.STATE_INCOMPLETE) {
            completeReport.setVisibility(View.GONE);
        }

        if (report.state != Report.STATE_SENT
                && report.state != Report.STATE_SAVED) {
            deleteReport.setVisibility(View.GONE);
            editReport.setVisibility(View.GONE);
        }

        if (report.state != Report.STATE_SENT
                && report.state != Report.STATE_SAVED
                && report.state != Report.STATE_INCOMPLETE) {
            view.findViewById(R.id.actions_empty_indicator).setVisibility(View.VISIBLE);
        }


        /*making actions*/
        if (report.state == Report.STATE_SENT || report.state == Report.STATE_INCOMPLETE
                || report.state == Report.STATE_SAVED)


        deleteReport.setVisibility(View.VISIBLE);
        deleteReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (report.state == Report.STATE_SAVED) {
                    MainActivity.writableDB.delete(InternalDataBaseSchemas.Reports.TABLE_NAME, " _id = ? ", new String[]{"" + report.id});
                    MainActivity.fragmentManagerMainActivity.popBackStack();
                    return;
                }
                if (!AppSettingsUtils.isConnectedToInternet(getContext())) {
                    showResultsWindow(getString(R.string.no_internet), LoadingFragment.RESULT_ERROR);

                } else
                    showProgressWindow(getString(R.string.deleting_report) + " " + report.id, false);
                new LimitedNetworkOperation(getContext()) {
                    @Override
                    protected void doInUiThread(int evolutionFlag, Object result) {
                        hideProgressWindow();
                        if (evolutionFlag == EvolutionFlag.ERROR_MESSAGE) {

                        }
                        if (evolutionFlag == EvolutionFlag.REQUEST_ACCEPTED) {
                            showResultsWindow(getString(R.string.report_deleted), LoadingFragment.RESULT_WELL_DONE);

                            /*deleting medias related to this report*/
                            MainActivity.writableDB.delete(InternalDataBaseSchemas.Medias.TABLE_NAME,
                                    InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE + " = ? AND "
                                            + InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW + " = ? ",
                                    new String[]{InternalDataBaseSchemas.Reports.TABLE_NAME, "" + report.id});

                            /*deleting cached images*/
                            MemoryUtils.deleteCachedImages(report.media);

                            /*deleting the report itself*/
                            MainActivity.writableDB.delete(InternalDataBaseSchemas.Reports.TABLE_NAME,
                                    InternalDataBaseSchemas.Reports.COLUMN_ID + " = ? ",
                                    new String[]{"" + report.id});

                            /*updating recycler view, reports list , and adapter*/
                            ReportsListAdapter.reports.remove(report);
                            ReportsListAdapter.filteredReportsList.remove(report);

                        }
                    }
                }.delete(Requests.DELETE_REPORT.replace("id", "" + report.id),
                        new RequestHeader(new RequestHeader.Property(
                                RequestHeader.Field.AUTHORIZATION,
                                "Bearer " + MainActivity.token)));

            }

        });

        if (editReport.getVisibility() == View.VISIBLE) {
            view.findViewById(R.id.edit_report_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.putFragmentInMainActivityContentAndAddToBackStack(
                            new BackNavigationFragment(new ReportProblemScreen(report), R.string.edit));

                }
            });
        }
        //
        if (completeReport.getVisibility() == View.VISIBLE)
            completeReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.putFragmentInMainActivityContentAndAddToBackStack(
                            new BackNavigationFragment(new ReportProblemScreen(report), R.string.complete));
                }
            });


        view.findViewById(R.id.back_navigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.fragmentManagerMainActivity.popBackStack();
            }
        });


    }
}//