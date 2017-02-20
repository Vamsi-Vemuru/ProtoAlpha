package com.example.projecta.protoalpha.ui;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.projecta.protoalpha.R;
import com.example.projecta.protoalpha.adapater.AppAdapter;
import com.example.projecta.protoalpha.model.AppUsageStats;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static android.app.usage.UsageStatsManager.INTERVAL_BEST;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    RecyclerView mRecyclerView;
    AppAdapter mAppAdapter;
    UsageStatsManager mUsageStatsManager;
    Button mOpenSettingsBtn;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsageStatsManager = (UsageStatsManager) getSystemService("usagestats");
        mAppAdapter = new AppAdapter();
        mRecyclerView = (RecyclerView) findViewById(R.id.rec_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mAppAdapter);
        mOpenSettingsBtn = (Button)findViewById(R.id.button_open_usage_setting);
        List<UsageStats> usageStatsList =
                getUsageStatistics(INTERVAL_BEST);
        updateAppsList(usageStatsList);

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(intervalType, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        if (queryUsageStats.size() == 0) {
            Log.i(TAG, "The user may not allow the access to apps usage. ");
            Toast.makeText(getApplicationContext(),
                    "App Usage not enabled",
                    Toast.LENGTH_LONG).show();
            mOpenSettingsBtn.setVisibility(View.VISIBLE);
            mOpenSettingsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            });
        }
        return queryUsageStats;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void updateAppsList(List<UsageStats> usageStatsList) {
        List<AppUsageStats> customUsageStatsList = new ArrayList<>();
        for (int i = 0; i < usageStatsList.size(); i++) {
            AppUsageStats customUsageStats = new AppUsageStats();
            customUsageStats.usageStats = usageStatsList.get(i);
            try {
                Drawable appIcon = getPackageManager()
                        .getApplicationIcon(customUsageStats.usageStats.getPackageName());
                customUsageStats.appIcon = appIcon;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, String.format("App Icon is not found for %s",
                        customUsageStats.usageStats.getPackageName()));
                customUsageStats.appIcon = getDrawable(R.drawable.ic_android_black_24dp);
            }
            customUsageStatsList.add(customUsageStats);
        }
        mAppAdapter.setUsageStatsList(customUsageStatsList);
        mAppAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }
}
