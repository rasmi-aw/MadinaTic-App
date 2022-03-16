package com.techload.madinatic.adapters.reports;


import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.techload.madinatic.R;
import com.techload.madinatic.lists_items.Report;

import java.util.ArrayList;

public class ReportsListAdapter extends RecyclerView.Adapter<ReportsListAdapter.ReportHolder> {
    public static ArrayList<Report> reports, filteredReportsList;
    private RecyclerView reportsList;
    private ReportsListAdapter thisAdapter;


    //
    class ReportHolder extends RecyclerView.ViewHolder {
        protected RecyclerView itemPager;

        public ReportHolder(View view) {
            super(view);
            //
            itemPager = view.findViewById(R.id.item_pager);
            itemPager.setLayoutManager(new LinearLayoutManager(itemPager.getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            new PagerSnapHelper().attachToRecyclerView(itemPager);
        }

    }
    //


    //
    public ReportsListAdapter(RecyclerView reportsList, ArrayList<Report> reports) {
        this.reports = reports;
        filteredReportsList = new ArrayList<>();
        if (reports != null && !reports.isEmpty()) filteredReportsList.addAll(reports);
        //
        this.reportsList = reportsList;
        thisAdapter = this;

    }


    @NonNull
    @Override
    public ReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ReportHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.reports_item_pager, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ReportHolder holder, int position) {
        //

        holder.itemPager.setAdapter(new ReportsItemSlideAdapter(filteredReportsList.get(position), holder.itemPager, thisAdapter));
    }

    @Override
    public int getItemCount() {
        return (filteredReportsList == null || filteredReportsList.isEmpty()) ? 0 : filteredReportsList.size();
    }

    /*reset the last clicked item while scrolling or when clicking and other one*/
    public void resetLastClickedElement() {
        int count = 0;
        do {
            View item = reportsList.getLayoutManager().
                    findViewByPosition(count);
            if (item != null) {
                RecyclerView itemPager = item.findViewById(R.id.item_pager);
                itemPager.smoothScrollToPosition(0);
            }
            count++;
        } while (count < reportsList.getAdapter().getItemCount());
    }

    public void updateList(ArrayList<Report> reports) {
        this.reports = reports;
        filteredReportsList.clear();
        filteredReportsList.addAll(reports);
        notifyDataSetChanged();
    }

    public void removeReports(Report... reports) {
        if (reports != null && reports.length > 0) {
            for (int i = 0; i < reports.length; i++) {
                this.reports.remove(reports[i]);
                filteredReportsList.remove(reports[i]);
            }
            notifyDataSetChanged();
        }
    }

    /**/
    public void filterSearchingList(final String researchQuery) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String query = researchQuery.trim();
                //
                Report report;
                filteredReportsList.clear();
                if (!query.isEmpty()) {

                    for (int i = 0; i < reports.size(); i++) {
                        report = reports.get(i);
                        if (report.similarTo(researchQuery))
                            filteredReportsList.add(report);
                    }

                } else if (filteredReportsList.size() < reports.size()) {
                    filteredReportsList.addAll(reports);
                }
                //
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                notifyDataSetChanged();
            }
        }.execute();
    }


}/**/