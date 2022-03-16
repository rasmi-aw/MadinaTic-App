package com.techload.madinatic.activities;


import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.techload.madinatic.R;
import com.techload.madinatic.data.InternalDataBase.InternalDataBaseSqliteHelper;
import com.techload.madinatic.data.networking.services.NetworkingService;
import com.techload.madinatic.fragments.MainPager;
import com.techload.madinatic.fragments.UnsignedVisitorScreen;
import com.techload.madinatic.fragments.WelcomeScreen;
import com.techload.madinatic.utils.AppSettingsUtils;
import com.techload.madinatic.utils.GraphicUtils;
import com.techload.madinatic.utils.MemoryUtils;
import com.techload.madinatic.utils.UserData;

import java.io.File;
import java.util.HashMap;


public class MainActivity extends AppSettingsUtils {
    public static FragmentActivity mainFragmentActivity;
    //
    public static FragmentManager fragmentManagerMainActivity;
    public static FragmentTransaction fragmentTransactionMainActivity;
    //
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor sharedPrefEditor;
    //
    public static String token = null;
    //
    public static HashMap<String, Bitmap> allAppImages;
    //
    public static boolean appTheme;
    public static String appLanguage;
    public static int profilePictureBackgroundResource;
    public static String profilePicturePath;
    public static boolean userIsMale = true;
    public static UserData userData;
    //
    public static SQLiteDatabase readableDB, writableDB;


    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        mainFragmentActivity = this;
        allAppImages = new HashMap<>();
        loadAppSettingsBeforeShowing();
        //
        InternalDataBaseSqliteHelper dbHelper = new InternalDataBaseSqliteHelper(this);
        if (readableDB == null) readableDB = dbHelper.getReadableDatabase();
        if (writableDB == null) writableDB = dbHelper.getWritableDatabase();
        //
        startNetworkingService(getApplicationContext());
        //
        setContentView(R.layout.activity_main);
        //
        fragmentManagerMainActivity = getSupportFragmentManager();

        //
        if (token != null) {
            putFragmentInMainActivityContent(new MainPager());
        } else {
            putFragmentInMainActivityContent(new UnsignedVisitorScreen());
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        MemoryUtils.deleteCachedImages(writableDB, this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        allAppImages.clear();
    }


    /**/
    public static void putFragmentInMainActivityContent(Fragment fragment) {
        fragmentTransactionMainActivity = fragmentManagerMainActivity.beginTransaction();
        fragmentTransactionMainActivity.replace(R.id.main_activity_content, fragment);
        fragmentTransactionMainActivity.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentTransactionMainActivity.commit();

    }

    /**/
    public static void putFragmentInMainActivityContentAndAddToBackStack(Fragment fragment) {
        fragmentTransactionMainActivity = fragmentManagerMainActivity.beginTransaction();
        fragmentTransactionMainActivity.replace(R.id.main_activity_content, fragment);
        fragmentTransactionMainActivity.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentTransactionMainActivity.addToBackStack(null);
        fragmentTransactionMainActivity.commit();

    }

    /**/
    public static void removeFragmentFromBackStack(Fragment fragment) {
        fragmentTransactionMainActivity = fragmentManagerMainActivity.beginTransaction();
        fragmentTransactionMainActivity.remove(fragment);
        fragmentTransactionMainActivity.commit();
    }


    //initialize app content before showing it to the user
    private void loadAppSettingsBeforeShowing() {
        //
        sharedPreferences = this.getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        sharedPrefEditor = sharedPreferences.edit();

        /*loading app stats every time the app starts*/
        NetworkingService.checkDBTables(this, sharedPreferences,sharedPrefEditor);

        /*app language*/
        if (sharedPreferences.contains(APPLICATION_SETTINGS_LANGUAGE)) {
            appLanguage = sharedPreferences.getString(APPLICATION_SETTINGS_LANGUAGE,
                    getResources().getConfiguration().locale.getLanguage());
            AppSettingsUtils.loadAppLanguage(appLanguage);
        } else {
            appLanguage = getResources().getConfiguration().locale.getLanguage();
        }

        /*app theme*/
        if (sharedPreferences.contains(APPLICATION_SETTINGS_THEME)) {
            appTheme = sharedPreferences.getBoolean(APPLICATION_SETTINGS_THEME, false);
            if (appTheme == AppSettingsUtils.APPLICATION_DARK_THEME) {
                setTheme(R.style.DarkTheme);
            }
        }
        /*user token to use it to connect with the server*/
        if (!sharedPreferences.contains(USER_TOKEN)) {
            sharedPrefEditor.putString(USER_TOKEN, null);
            sharedPrefEditor.commit();
        } else {
            token = sharedPreferences.getString(USER_TOKEN, null);
        }

        /*user gender*/
        userIsMale = sharedPreferences.getBoolean(USER_IS_MALE, true);

        /*user data*/
        if (token != null) {
            userData = new UserData(sharedPreferences.getString(AppSettingsUtils.USER_PERSONAL_DATA, null));
            profilePicturePath = sharedPreferences.getString(AppSettingsUtils.USER_PROFILE_PICTURE_PATH, null);
            if (profilePicturePath != null)
                allAppImages.put(profilePicturePath, GraphicUtils.loadCompressedBitmap(1, profilePicturePath));
            NetworkingService.updateUserData(userData, getApplicationContext(), sharedPrefEditor);
        }


        /*user profile picture background*/
        if (sharedPreferences.contains(USER_PROFILE_PICTURE_BACKGROUND_RESOURCE)) {
            profilePictureBackgroundResource = sharedPreferences.
                    getInt(USER_PROFILE_PICTURE_BACKGROUND_RESOURCE, R.drawable.circle_gray);
        } else {
            profilePictureBackgroundResource = R.drawable.circle_gray;
        }
    }

}//