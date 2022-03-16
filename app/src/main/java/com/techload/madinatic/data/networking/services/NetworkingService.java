package com.techload.madinatic.data.networking.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.data.InternalDataBase.InternalDataBaseSqliteHelper;
import com.techload.madinatic.data.networking.DataCachingOperation;
import com.techload.madinatic.data.networking.RequestHeader;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.data.networking.networkOperations.LimitedNetworkOperation;
import com.techload.madinatic.data.networking.networkOperations.Posting;
import com.techload.madinatic.data.networking.networkOperations.Reporting;
import com.techload.madinatic.utils.AppSettingsUtils;
import com.techload.madinatic.utils.AppStats;
import com.techload.madinatic.utils.GraphicUtils;
import com.techload.madinatic.utils.UserData;

import org.json.JSONObject;

/**
 * checking if there's internet connection if yes do the following else do nothing
 * checking of there's new information in every database table comparing to the internal SQLite database
 * if no don't do anything
 * else load data for a normal visitor
 * check if the user is connected (by stored token)
 * if yes load data for that user
 */

public class NetworkingService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updateDataFromServer();
        stopSelf();
        return START_STICKY;
    }

    private void updateDataFromServer() {

        //checking if the user is connected to the internet
        if (AppSettingsUtils.isConnectedToInternet(this)) {
            InternalDataBaseSqliteHelper dbHelper;
            SQLiteDatabase writableDB, readableDB;

            if (MainActivity.writableDB != null) {
                readableDB = MainActivity.readableDB;
                writableDB = MainActivity.writableDB;
            } else {
                dbHelper = new InternalDataBaseSqliteHelper(this);
                readableDB = dbHelper.getReadableDatabase();
                writableDB = dbHelper.getWritableDatabase();
            }

            //
            SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            UserData oldUserData = new UserData(preferences.getString(AppSettingsUtils.USER_PERSONAL_DATA, null));

            connectToAccount(this,
                    editor,
                    oldUserData,
                    preferences.getString(AppSettingsUtils.USER_PASSWORD, null));
            //
            updateUserData(oldUserData, this, editor);
            //
            checkDBTables(this, preferences, editor);
            Reporting reporting = new Reporting(this, readableDB, writableDB);
            reporting.loadAllReports();
            reporting.loadUserReports();
            new Posting(this, readableDB, writableDB).loadPosts();

        }
    }


    public static void checkDBTables(final Context context, final SharedPreferences sharedPrefs, final SharedPreferences.Editor editor) {
        new LimitedNetworkOperation(context) {
            @Override
            protected void doInUiThread(int evolutionFlag, Object result) {
                if (evolutionFlag == EvolutionFlag.JSON_IS_READY) {
                    String json = result.toString();

                    AppStats oldStats = new AppStats(sharedPrefs.getString(context.getString(R.string.app_name), null));
                    AppStats newStats = new AppStats(json);
                    json = newStats.addDateToJson(json);
                    editor.putString(AppSettingsUtils.APPLICATION_LAST_STATISTICS, json);
                    editor.commit();

                    //
                    int posts = newStats.getPostsNumber() - oldStats.getPostsNumber();
                    int reports = newStats.getReportsNumber() - oldStats.getReportsNumber();
                    //
                    if (posts > 0) {
                        AppSettingsUtils.notifyUser(context,
                                AppSettingsUtils.POSTS_NOTIFICATION_ID,
                                R.drawable.breaking_news,
                                context.getString(R.string.app_name),
                                context.getString(R.string.new_posts).replace("number", "" + reports));
                    }
                    //
                    if (reports > 0) {
                        AppSettingsUtils.notifyUser(context,
                                AppSettingsUtils.REPORTS_NOTIFICATION_ID, R.drawable.pc_user,
                                context.getString(R.string.app_name),
                                context.getString(R.string.new_reports).replace("number", "" + reports));
                    }

                }
            }
        }.downloadText(Requests.CHECK_DATABASE_TABLES,
                null,
                LimitedNetworkOperation.ThreadPriority.HIGH,
                false);
    }


    public static void updateUserData(final UserData oldUserData,
                                      final Context context,
                                      final SharedPreferences.Editor sharedPrefEdit) {


         /*loading user info from the server using the token
                                     then storing it to the shared preferences*/
        new LimitedNetworkOperation(context) {
            @Override
            protected void doInUiThread(int evolutionFlag, Object result) {
                if (evolutionFlag == EvolutionFlag.JSON_IS_READY) {
                    //
                    UserData lastUserData = new UserData(result.toString());
                    sharedPrefEdit.putString(AppSettingsUtils.USER_PERSONAL_DATA, result.toString());
                    sharedPrefEdit.putBoolean(AppSettingsUtils.USER_IS_MALE, MainActivity.userIsMale);
                    sharedPrefEdit.commit();

                    /*user gender to use it later to show a default profile picture*/
                    if (MainActivity.mainFragmentActivity != null) {
                        MainActivity.userIsMale = result.toString().contains("homme");
                        MainActivity.userData = lastUserData;
                    }
                    //in case of profile picture is not found or the user has changed it
                    if (oldUserData == null || !lastUserData.getProfilePicture().equals(oldUserData.getProfilePicture())) {

                        new LimitedNetworkOperation(context) {
                            @Override
                            protected void doInUiThread(int evolutionFlag, Object result) {
                                if (evolutionFlag == EvolutionFlag.IMAGE_IS_READY) {
                                    new DataCachingOperation(context) {
                                        @Override
                                        public void doWhenFileCached(String filePath) {
                                            sharedPrefEdit.putString(AppSettingsUtils.USER_PROFILE_PICTURE_PATH, filePath).commit();
                                            //updating profile pic while running
                                            if (MainActivity.fragmentManagerMainActivity != null) {
                                                MainActivity.profilePicturePath = filePath;
                                                MainActivity.allAppImages.put(filePath, GraphicUtils.loadCompressedBitmap(1, filePath));
                                            }

                                        }
                                    }.cacheFileInBackground(DataCachingOperation.DataType.IMPORTANT_IMAGE, result);
                                }
                            }
                        }.downloadImage(Requests.IMAGES + lastUserData.getProfilePicture(),
                                null, LimitedNetworkOperation.ThreadPriority.LOW,
                                false);
                    }

                }
            }
        }.downloadText(
                Requests.GET_USER_INFO,
                new RequestHeader(new RequestHeader.Property(RequestHeader.Field.AUTHORIZATION, "Bearer " + MainActivity.token))
                , LimitedNetworkOperation.ThreadPriority.LOW, false);

    }

    public static void connectToAccount(final Context context, final SharedPreferences.Editor editor, final UserData userData, String password) {

        if (userData != null && password != null && !password.isEmpty() && editor != null && context != null) {


            final String json = "{\"username\": \"" + userData.getEmail() + "\", " +
                    "\"password\": \"" + password + "\" }";

            /*making request for authentication and receiving data like token etc...*/
            new LimitedNetworkOperation(context) {
                @Override
                protected void doInUiThread(int evolutionFlag, Object result) {

                    /*case the user is logged in successfully or there's
                      an error message from the server*/
                    if (evolutionFlag == EvolutionFlag.JSON_IS_READY || evolutionFlag == EvolutionFlag.ERROR_MESSAGE) {
                        JSONObject serverResponse;
                        String message = null;
                        try {

                            /*extracting information from json response*/
                            serverResponse = new JSONObject(((String) result));
                            message = serverResponse.getString("message");

                            if (message.contains("logged")) {
                                /*case the user is logged-in successfully*/

                                editor.putString(AppSettingsUtils.USER_TOKEN, MainActivity.token);
                                MainActivity.sharedPrefEditor.commit();

                                if (MainActivity.mainFragmentActivity != null)
                                    MainActivity.token = serverResponse.getString(AppSettingsUtils.USER_TOKEN);

                            } else if (message.toLowerCase().contains("pseudo")) {
                                json.replace(userData.getEmail(), userData.getPseudo());
                                //
                                postJson(Requests.AUTHENTICATION,
                                        null,
                                        json,
                                        LimitedNetworkOperation.ThreadPriority.LOW);
                                //
                                return;
                            }

                        } catch (Exception e) {
                            /*error in json appeared*/
                        }
                    }

                }
            }.postJson(Requests.AUTHENTICATION,
                    null,
                    json,
                    LimitedNetworkOperation.ThreadPriority.LOW);
        }
    }

}