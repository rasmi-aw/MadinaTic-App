package com.techload.madinatic.data.networking.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.techload.madinatic.utils.AppSettingsUtils;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /**/
        AppSettingsUtils.startNetworkingService(context);
    }
}
