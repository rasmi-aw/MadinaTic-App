package com.techload.madinatic.data.networking.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.techload.madinatic.data.networking.services.NetworkingService;

public class NetworkingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, NetworkingService.class));
        } else {
            context.startService(new Intent(context, NetworkingService.class));
        }

    }
}
