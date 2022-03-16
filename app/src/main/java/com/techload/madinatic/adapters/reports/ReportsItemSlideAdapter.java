package com.techload.madinatic.adapters.reports;


import android.content.ContentValues;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.data.InternalDataBase.InternalDataBaseSchemas;
import com.techload.madinatic.data.networking.RequestHeader;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.data.networking.networkOperations.LimitedNetworkOperation;
import com.techload.madinatic.fragments.helpers.BackNavigationFragment;
import com.techload.madinatic.fragments.helpers.LoadingFragment;
import com.techload.madinatic.fragments.reports.ReportProblemScreen;
import com.techload.madinatic.fragments.reports.ReportsFragment;
import com.techload.madinatic.fragments.reports.SeeReportDetails;
import com.techload.madinatic.lists_items.Report;
import com.techload.madinatic.lists_items.Media;
import com.techload.madinatic.utils.AppSettingsUtils;
import com.techload.madinatic.utils.GraphicUtils;
import com.techload.madinatic.utils.MemoryUtils;

public class ReportsItemSlideAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //
    private final int PAGE_TYPE_INFO = 0;
    private final int PAGE_TYPE_ACTIONS = 15;
    //
    public Report report;
    public RecyclerView reportPager;
    private ReportsListAdapter reportsListAdapter;


    public ReportsItemSlideAdapter(Report report, RecyclerView reportPager,
                                   ReportsListAdapter reportsListAdapter) {

        this.reportsListAdapter = reportsListAdapter;
        this.report = report;
        this.reportPager = reportPager;
    }

    private class InfoHolder extends RecyclerView.ViewHolder {
        ViewGroup container;
        ImageView repPic;
        TextView title, description, state;

        public InfoHolder(@NonNull ViewGroup container) {
            super(container);
            this.container = container;
            repPic = container.findViewById(R.id.report_rep_pic);
            title = container.findViewById(R.id.report_title);
            description = container.findViewById(R.id.report_description);
            state = container.findViewById(R.id.report_state);
            //
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportsListAdapter.resetLastClickedElement();
                    reportPager.smoothScrollToPosition(1);
                }
            });
        }
    }

    //
    private class ActionsHolder extends RecyclerView.ViewHolder {
        ViewGroup container;
        View completeReport, editReport, deleteReport, viewReport;
        TextView actionsEmptyIndicator;

        public ActionsHolder(@NonNull ViewGroup container) {
            super(container);
            this.container = container;
            completeReport = container.findViewById(R.id.complete_report_container);
            editReport = container.findViewById(R.id.edit_report_container);
            deleteReport = container.findViewById(R.id.delete_report_container);
            actionsEmptyIndicator = container.findViewById(R.id.actions_empty_indicator);
            viewReport = container.findViewById(R.id.view_report);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return PAGE_TYPE_INFO;
        } else return PAGE_TYPE_ACTIONS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == PAGE_TYPE_INFO)
            return new InfoHolder(((ViewGroup) LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.reports_item_info, parent, false)));

        else return new ActionsHolder(((ViewGroup) LayoutInflater.from(parent.getContext()).
                inflate(R.layout.reports_item_actions, parent, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof InfoHolder) {
            dealWithInfo(((InfoHolder) holder));
        } else {
            dealWithActions(((ActionsHolder) holder));
        }
    }


    public void updateReport(Report report) {
        this.report = report;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    //
    private void dealWithInfo(final InfoHolder holder) {

        if (report != null) {

            //
            holder.title.setText(report.title);
            holder.description.setText(Html.fromHtml(
                    "<font color='#666666'>" + holder.container.getResources().getString(R.string.id) + ": " + "</font>" + report.id + "<br>"
                            + "<font color='#666666'>" + holder.container.getResources().getString(R.string.date_sent) + ": " + "</font>" + report.dateSent));

            //
            switch (report.state) {
                case Report.STATE_SAVED:
                    holder.state.setText(Html.fromHtml(holder.container.getResources().getString(R.string.state_saved)));
                    holder.repPic.setBackgroundResource(R.drawable.circle_blue_gray);
                    break;

                case Report.STATE_SENT:
                    holder.state.setText(Html.fromHtml(holder.container.getResources().getString(R.string.state_sent)));
                    holder.repPic.setBackgroundResource(R.drawable.circle_mauve);
                    break;

                case Report.STATE_BEING_PROCESSED:
                    holder.state.setText(Html.fromHtml(holder.container.getResources().getString(R.string.state_being_processed)));
                    holder.repPic.setBackgroundResource(R.drawable.circle_blue);
                    break;

                case Report.STATE_PROCESSED:
                    holder.state.setText(Html.fromHtml(holder.container.getResources().getString(R.string.state_processed)));
                    holder.repPic.setBackgroundResource(R.drawable.circle_green);
                    break;

                case Report.STATE_REJECTED:
                    holder.state.setText(Html.fromHtml(holder.container.getResources().getString(R.string.state_rejected)));
                    holder.repPic.setBackgroundResource(R.drawable.circle_red);
                    break;

                case Report.STATE_INCOMPLETE:
                    holder.state.setText(Html.fromHtml(holder.container.getResources().getString(R.string.state_incomplete)));
                    holder.repPic.setBackgroundResource(R.drawable.circle_orange);
                    break;
            }

            if (report.media != null && report.media.length > 0 && report.media[0].type == Media.TYPE_IMAGE) {

                /*if already cached*/
                if (report.media[0].cached) {
                    /*if it's already in the ram*/
                    if (MainActivity.allAppImages.containsKey(report.media[0].path)) {

                        GraphicUtils.toCircledImage(holder.repPic,
                                MainActivity.allAppImages.get(report.media[0].path));
                    } else {
                        /*if not in the ram load it and put it into the ram*/
                        Bitmap repPic = GraphicUtils.loadCompressedBitmap(2, report.media[0].path);
                        MainActivity.allAppImages.put(report.media[0].path, repPic);
                        GraphicUtils.toCircledImage(holder.repPic, repPic);
                    }

                } else {
                    /*if not cached then download it from network if possible
                     * then put it into the cache, update medias table, update report first pic,
                     *  and put it in the the ram & showing it to the user*/
                    if (AppSettingsUtils.isConnectedToInternet(holder.container.getContext())) {
                        new LimitedNetworkOperation(holder.container.getContext()) {
                            @Override
                            protected void doInUiThread(int evolutionFlag, Object result) {
                                if (evolutionFlag == EvolutionFlag.IMAGE_CACHED) {

                                    /*updating medias table in DB*/
                                    ContentValues row = new ContentValues();
                                    row.put(InternalDataBaseSchemas.Medias.COLUMN_CACHED_PATH, result.toString());
                                    MainActivity.writableDB.update(InternalDataBaseSchemas.Medias.TABLE_NAME,
                                            row,
                                            InternalDataBaseSchemas.Medias.COLUMN_PATH + " = ? ",
                                            new String[]{report.media[0].path});
                                    //
                                    report.media[0].path = result.toString();
                                    report.media[0].cached = true;

                                    /*put it into the ram & showing it to the user*/
                                    Bitmap repPic = GraphicUtils.loadCompressedBitmap(2, report.media[0].path);
                                    MainActivity.allAppImages.put(report.media[0].path, repPic);
                                    GraphicUtils.toCircledImage(holder.repPic, repPic);


                                }
                            }
                        }.downloadImage(Requests.IMAGES + report.media[0].path,
                                null,
                                LimitedNetworkOperation.ThreadPriority.LOW,
                                true);
                    }
                }
            }
        }

    }

    /**/
    private void dealWithActions(final ActionsHolder holder) {
        //
        if (MainActivity.appLanguage.equals("ar"))
            holder.viewReport.setRotation(180);

        holder.viewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.putFragmentInMainActivityContentAndAddToBackStack(new SeeReportDetails(report));
            }
        });
        //

        if (report.state != Report.STATE_INCOMPLETE) {
            holder.completeReport.setVisibility(View.GONE);
        }

        if (report.state != Report.STATE_SENT
                && report.state != Report.STATE_SAVED) {
            holder.deleteReport.setVisibility(View.GONE);
            holder.editReport.setVisibility(View.GONE);
        }

        if (report.state == Report.STATE_INCOMPLETE)
            holder.deleteReport.setVisibility(View.VISIBLE);

        if (!(holder.completeReport.getVisibility() == View.GONE
                && holder.editReport.getVisibility() == View.GONE
                && holder.deleteReport.getVisibility() == View.GONE)) {

            holder.actionsEmptyIndicator.setText("");
        } else holder.container.findViewById(R.id.space_view).setVisibility(View.GONE);

        /*making actions*/
        if (report.state == Report.STATE_SENT || report.state == Report.STATE_INCOMPLETE
                || report.state == Report.STATE_SAVED) {

            //
            if (holder.completeReport.getVisibility() == View.VISIBLE) {
                holder.completeReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.putFragmentInMainActivityContentAndAddToBackStack(
                                new BackNavigationFragment(new ReportProblemScreen(report), R.string.complete));
                    }
                });
            }


            holder.deleteReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (report.state == Report.STATE_SAVED) {
                        MainActivity.writableDB.delete(InternalDataBaseSchemas.Reports.TABLE_NAME, " _id = ? ", new String[]{"" + report.id});
                        reportsListAdapter.removeReports(report);
                        return;
                    }

                    if (!AppSettingsUtils.isConnectedToInternet(holder.container.getContext())) {
                        ReportsFragment.loadingFragment.
                                showResultsWindow(holder.container.getContext().getString(R.string.no_internet),
                                        LoadingFragment.RESULT_ERROR);

                    } else
                        ReportsFragment.loadingFragment.showProgressWindow(
                                holder.container.getContext().getString(R.string.deleting_report) + " " + report.id, false);
                    new LimitedNetworkOperation(holder.container.getContext()) {
                        @Override
                        protected void doInUiThread(int evolutionFlag, Object result) {
                            ReportsFragment.loadingFragment.hideProgressWindow();
                            if (evolutionFlag == EvolutionFlag.ERROR_MESSAGE) {

                            }
                            if (evolutionFlag == EvolutionFlag.REQUEST_ACCEPTED) {
                                ReportsFragment.loadingFragment.showResultsWindow(holder.container.getContext().getString(R.string.report_deleted), LoadingFragment.RESULT_WELL_DONE);

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
                                reportsListAdapter.notifyDataSetChanged();


                            }
                        }
                    }.delete(Requests.DELETE_REPORT.replace("id", "" + report.id),
                            new RequestHeader(new RequestHeader.Property(
                                    RequestHeader.Field.AUTHORIZATION,
                                    "Bearer " + MainActivity.token)));
                    //

                }

            });
        } else if (report.state == Report.STATE_SAVED) {
//            MainActivity.writableDB.delete(InternalDataBaseSchemas.Reports.TABLE_NAME, InternalDataBaseSchemas.Reports.COLUMN_ID =?)
        }

        //
        if (holder.editReport.getVisibility() == View.VISIBLE) {
            holder.container.findViewById(R.id.edit_report_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.putFragmentInMainActivityContentAndAddToBackStack(
                            new BackNavigationFragment(new ReportProblemScreen(report), R.string.edit));
                }
            });
        }

    }

}