package com.example.projecta.protoalpha.adapater;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projecta.protoalpha.R;
import com.example.projecta.protoalpha.model.AppUsageStats;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ISR on 08-Feb-17.
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private List<AppUsageStats> mUsageStatsList = new ArrayList<>();
    private PackageManager packageManager;

    public static class ViewHolder extends  RecyclerView.ViewHolder {
        private final TextView mPackageName;
        private final TextView mTimeUsed;
        private final ImageView mAppIcon;


        public ViewHolder(View itemView) {
            super(itemView);
            this.mPackageName = (TextView) itemView.findViewById(R.id.textview_package_name);
            this.mTimeUsed = (TextView) itemView.findViewById(R.id.textview_time_used);
            this.mAppIcon = (ImageView) itemView.findViewById(R.id.app_icon);
        }

        public TextView getPackageName() {
            return mPackageName;
        }

        public TextView getTimeUsed() {
            return mTimeUsed;
        }

        public ImageView getAppIcon() {
            return mAppIcon;
        }
    }

    public AppAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        packageManager = parent.getContext().getPackageManager();
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_item, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ApplicationInfo applicationInfo = null;
        String packageName = mUsageStatsList.get(position).usageStats.getPackageName();
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {}
        final String title = (String)((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : packageName);

        /*holder.getPackageName().setText(
                mUsageStatsList.get(position).usageStats.getPackageName());*/

        holder.getPackageName().setText(title);
        long timeUsed = mUsageStatsList.get(position).usageStats.getTotalTimeInForeground();
        String time = String.format("%d hours, %d min, %d sec",
                TimeUnit.MILLISECONDS.toHours(timeUsed),
                TimeUnit.MILLISECONDS.toMinutes(timeUsed) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeUsed)),
                TimeUnit.MILLISECONDS.toSeconds(timeUsed) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeUsed))
        );
        holder.getTimeUsed().setText(time);
        holder.getAppIcon().setImageDrawable(mUsageStatsList.get(position).appIcon);
    }

    @Override
    public int getItemCount() {
        return mUsageStatsList.size();
    }

    public void setUsageStatsList(List<AppUsageStats> mUsageStatsList) {
        this.mUsageStatsList = mUsageStatsList;
    }
}
