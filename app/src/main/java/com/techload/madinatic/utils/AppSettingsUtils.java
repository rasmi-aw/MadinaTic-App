package com.techload.madinatic.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.data.networking.networkOperations.Reporting;
import com.techload.madinatic.data.networking.receivers.NetworkingCallReceiver;
import com.techload.madinatic.data.networking.services.NetworkingService;

import java.util.Locale;


public class AppSettingsUtils extends FragmentActivity {
    //
    public static final boolean APPLICATION_LIGHT_THEME = false;
    public static final boolean APPLICATION_DARK_THEME = true;
    //
    public static final String APPLICATION_SETTINGS_LANGUAGE = "appLang";
    public static final String APPLICATION_SETTINGS_THEME = "appTheme";
    public static final String APPLICATION_LAST_STATISTICS = "last_stats";
    //
    public static final String USER_PROFILE_PICTURE_PATH = "profilePicture";
    public static final String USER_PROFILE_PICTURE_BACKGROUND_RESOURCE = "profilePictureBackground";
    public static final String USER_TOKEN = "token";
    public static final String USER_PERSONAL_DATA = "user_personal_data";
    public static final String USER_IS_MALE = "user_is_male";
    public static final String USER_PASSWORD = "password";
    //
    public static final int REPORTS_NOTIFICATION_ID = 0;
    public static final int POSTS_NOTIFICATION_ID = 44;

    //
    public static void changeAppLanguage(String appLang) {
        //
        MainActivity.sharedPrefEditor.putString(APPLICATION_SETTINGS_LANGUAGE, appLang);
        MainActivity.sharedPrefEditor.commit();
    }

    //
    public static void changeAppLanguageAndRestart(String appLang) {
        changeAppLanguage(appLang);
        //
        MainActivity.mainFragmentActivity.finish();
        MainActivity.mainFragmentActivity.startActivity(MainActivity.mainFragmentActivity.getIntent());
    }

    public static void loadAppLanguage(String appLang) {
        //setting the language of the activity in case of old api
        Configuration configuration = new Configuration();
        Locale locale = new Locale(appLang);
        configuration.locale = locale;
        MainActivity.mainFragmentActivity.getResources().updateConfiguration(configuration,
                MainActivity.mainFragmentActivity.getResources().getDisplayMetrics());
    }


    //
    public static void clearApplicationData(ActivityManager activityManager) {
        activityManager.clearApplicationUserData();
    }

    //
    public static void changeAppTheme(boolean newTheme) {
        if (newTheme == APPLICATION_DARK_THEME) {
            MainActivity.sharedPrefEditor.putBoolean(APPLICATION_SETTINGS_THEME, APPLICATION_DARK_THEME);
            //
        } else {
            MainActivity.sharedPrefEditor.putBoolean(APPLICATION_SETTINGS_THEME, APPLICATION_LIGHT_THEME);
        }
        MainActivity.sharedPrefEditor.commit();
        MainActivity.mainFragmentActivity.finish();
        MainActivity.mainFragmentActivity.startActivity(MainActivity.mainFragmentActivity.getIntent());

    }

    //
    public static void changeUserProfilePictureBackground() {
        MainActivity.sharedPrefEditor.putInt(USER_PROFILE_PICTURE_BACKGROUND_RESOURCE, MainActivity.profilePictureBackgroundResource);
        MainActivity.sharedPrefEditor.commit();
    }

    //


    //checking if device is connected to the internet
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        if (manager != null)
            for (Network network : manager.getAllNetworks()) {

                networkInfo = manager.getNetworkInfo(network);
                int networkType = networkInfo.getType();

                if ((networkType == ConnectivityManager.TYPE_MOBILE || networkType == ConnectivityManager.TYPE_WIFI) && networkInfo.isConnected()) {
                    return true;
                }
            }
        return false;
    }

    /*start networking service using alarm manager & broadcast receiver
     * the alarm is set to check the server every 30 min*/

    public static void startNetworkingService(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1548,
                    new Intent(context, NetworkingCallReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    (SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR), AlarmManager.INTERVAL_HOUR, pendingIntent);
        }
    }

    /**
     * notify user with new events
     */

    public static void notifyUser(Context context, int notifId, int smallIcRes, String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.app_ic)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), smallIcRes))
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(PendingIntent.
                        getActivity(context, 0, new Intent(context, MainActivity.class), 0)).
                        setAutoCancel(true);

        if ((context instanceof NetworkingService)) {
            ((Service) context).startForeground(notifId, builder.build());
        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notifId, builder.build());
        }


    }

    public static String formatDate(String appLang, String date) {

        if (date == null || date.isEmpty()) return "";
        String formattedDate = date;
        String[] dateArr = date.split("-");

        if (appLang.equals("ar")) {

            switch (Integer.valueOf(dateArr[1])) {
                case 1:
                    dateArr[1] = " جانفي ";
                    break;

                case 2:
                    dateArr[1] = " فيفري ";
                    break;

                case 3:
                    dateArr[1] = " مارس ";
                    break;

                case 4:
                    dateArr[1] = " أبريل ";
                    break;

                case 5:
                    dateArr[1] = " ماي ";
                    break;

                case 6:
                    dateArr[1] = " جوان ";
                    break;

                case 7:
                    dateArr[1] = " جويلية ";
                    break;

                case 8:
                    dateArr[1] = " أوت ";
                    break;

                case 9:
                    dateArr[1] = " سبتمبر ";
                    break;

                case 10:
                    dateArr[1] = " أكتوبر ";
                    break;

                case 11:
                    dateArr[1] = " نوفمبر ";
                    break;

                case 12:
                    dateArr[1] = " ديسمبر ";
                    break;

            }
            return (dateArr[0].length() == 4 ? dateArr[2] : dateArr[0]) + dateArr[1] + (dateArr[0].length() == 4 ? dateArr[0] : dateArr[2]);

        } else if (appLang.equals("fr")) {

            switch (Integer.valueOf(dateArr[1])) {
                case 1:
                    dateArr[1] = " Jan ";
                    break;

                case 2:
                    dateArr[1] = " Fev ";
                    break;

                case 3:
                    dateArr[1] = " Mar ";
                    break;

                case 4:
                    dateArr[1] = " Avr ";
                    break;

                case 5:
                    dateArr[1] = " Mai ";
                    break;

                case 6:
                    dateArr[1] = " Juin ";
                    break;

                case 7:
                    dateArr[1] = " Juil ";
                    break;

                case 8:
                    dateArr[1] = " Aout ";
                    break;

                case 9:
                    dateArr[1] = " Sept ";
                    break;

                case 10:
                    dateArr[1] = " Oct ";
                    break;

                case 11:
                    dateArr[1] = " Nov ";
                    break;

                case 12:
                    dateArr[1] = " Déc ";
                    break;

            }

        } else if (appLang.equals("en")) {

            switch (Integer.valueOf(dateArr[1])) {
                case 1:
                    dateArr[1] = " Jan ";
                    break;

                case 2:
                    dateArr[1] = " Feb ";
                    break;

                case 3:
                    dateArr[1] = " Mar ";
                    break;

                case 4:
                    dateArr[1] = " Apr ";
                    break;

                case 5:
                    dateArr[1] = " Mai ";
                    break;

                case 6:
                    dateArr[1] = " Jun ";
                    break;

                case 7:
                    dateArr[1] = " July ";
                    break;

                case 8:
                    dateArr[1] = " Aug ";
                    break;

                case 9:
                    dateArr[1] = " Sept ";
                    break;

                case 10:
                    dateArr[1] = " Oct ";
                    break;

                case 11:
                    dateArr[1] = " Nov ";
                    break;

                case 12:
                    dateArr[1] = " Dec ";
                    break;
            }
        }
        return (dateArr[0].length() == 4 ? dateArr[2] : dateArr[0]) + dateArr[1] + (dateArr[0].length() == 4 ? dateArr[0] : dateArr[2]);
    }

}