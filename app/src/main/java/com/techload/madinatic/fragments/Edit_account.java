package com.techload.madinatic.fragments;


import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.R;
import com.techload.madinatic.adapters.LanguagesListAdapter;
import com.techload.madinatic.data.InternalDataBase.InternalDataBaseSchemas;
import com.techload.madinatic.data.networking.RequestHeader;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.data.networking.networkOperations.LimitedNetworkOperation;
import com.techload.madinatic.fragments.helpers.BackNavigationFragment;
import com.techload.madinatic.fragments.helpers.ChangeProfilePictureFragment;
import com.techload.madinatic.fragments.password.ChangePasswordScreen;
import com.techload.madinatic.utils.AppSettingsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class Edit_account extends ChangeProfilePictureFragment {
    //
    private TextInputEditText familyName, firstName, nationalNumber, birthday, phone, email,
            password, pseudoName;
    private Switch darkMode;

    private TextView appDataText;

    //
    private AlertDialog popupWindow;
    //


    public Edit_account() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        initialization(view);
        //

    }


    @Override
    public void onResume() {
        super.onResume();
        /* i put this here cuz it needs to be updated every time the user open this page*/

        //clear app data

        File file = getContext().getCacheDir();
        float appSize = 0;
        for (File f : file.listFiles()) {
            if (f.isFile()) appSize += f.length();
            else {
                for (File f2 : f.listFiles()) {
                    if (f2.isFile()) appSize += f2.length();
                }
            }
        }
        appSize = (appSize / (1024 * 1024));
        String appSizeStr = String.valueOf(appSize);
        appSizeStr = ((appSizeStr.length() > 3) ? appSizeStr.substring(0, 4) : appSizeStr);
        appDataText.setText(Html.fromHtml(getString(R.string.clear_app_data) +
                "   <small> <b> <font color='" + ContextCompat.getColor(getContext(), R.color.green) + "'>("
                + appSizeStr + " mb)</b></small>"));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //
        familyName = null;
        firstName = null;
        nationalNumber = null;
        birthday = null;
        phone = null;
        email = null;
        password = null;
        pseudoName = null;
        darkMode = null;
        appDataText = null;
        popupWindow = null;
    }

    /*Initializing Fragment views and keyboard clossed on start*/
    private void initialization(final View view) {
        //
        familyName = view.findViewById(R.id.familyName_editText);
        firstName = view.findViewById(R.id.name_editText);
        nationalNumber = view.findViewById(R.id.nationalId_editText);
        birthday = view.findViewById(R.id.birthday_editText);
        phone = view.findViewById(R.id.phone_editText);
        email = view.findViewById(R.id.email_editText);
        password = view.findViewById(R.id.password_editText);
        pseudoName = view.findViewById(R.id.pseudoName_editText);
        //

        /*filling fields with their info after extracting them from shared prefs & doing some operations on them*/

//        familyName.setText(MainActivity.userData.getFamilyName());
//        firstName.setText(MainActivity.userData.getFirstName());
//        //
//        birthday.setText(MainActivity.userData.getBirthDay());
//        //
//        nationalNumber.setText(MainActivity.userData.getNationalId());
//        email.setText(MainActivity.userData.getEmail());
//        phone.setText(MainActivity.userData.getPhone());
//        pseudoName.setText(MainActivity.userData.getPseudo());
//        password.setText("***************");


        //
        view.findViewById(R.id.edit_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.putFragmentInMainActivityContentAndAddToBackStack(new BackNavigationFragment(new ChangePasswordScreen(), R.string.hint_password));
            }
        });

        //
        popupWindow = (new AlertDialog.Builder(getContext())).create();
        //
        view.findViewById(R.id.language_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageWindow(view);
            }
        });

        // contact us
        View contactUs = view.findViewById(R.id.contactUs_container);
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"akram.ouardas1@gmail.com"});
                startActivityForResult(Intent.createChooser(emailIntent, getResources().getString(R.string.contact_us)), 0);
            }
        });

        // dark mode
        Switch appTheme = view.findViewById(R.id.dark_mode_switch);
        appTheme.setChecked(MainActivity.appTheme);
        appTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppSettingsUtils.changeAppTheme(isChecked);

            }
        });

        /**/
        view.findViewById(R.id.clear_data_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSettingsUtils.clearApplicationData((ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE));
            }
        });

        // about us page
        view.findViewById(R.id.about_app_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.putFragmentInMainActivityContentAndAddToBackStack(new BackNavigationFragment(new About_us(), R.string.about_us));
            }
        });
        // logout
        View logoutContainer = view.findViewById(R.id.logout_container);
        View logoutImg = logoutContainer.findViewById(R.id.logout_image);
        if (!MainActivity.appLanguage.equals("ar"))
            logoutImg.setRotation(180);

        logoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.token = null;
                MainActivity.sharedPrefEditor.remove(AppSettingsUtils.USER_TOKEN);
                MainActivity.sharedPrefEditor.remove(AppSettingsUtils.USER_IS_MALE);
                MainActivity.sharedPrefEditor.remove(AppSettingsUtils.USER_PERSONAL_DATA);
                MainActivity.sharedPrefEditor.remove(AppSettingsUtils.USER_TOKEN);
                MainActivity.sharedPrefEditor.remove(MainActivity.USER_PASSWORD);
                MainActivity.sharedPrefEditor.remove(AppSettingsUtils.USER_PROFILE_PICTURE_BACKGROUND_RESOURCE);
                MainActivity.sharedPrefEditor.remove(AppSettingsUtils.USER_PROFILE_PICTURE_PATH);
                MainActivity.sharedPrefEditor.commit();
                //
                MainActivity.userIsMale = true;
                MainActivity.profilePicturePath = null;
                MainActivity.userData = null;
                //

                MainActivity.writableDB.delete(
                        InternalDataBaseSchemas.Reports.TABLE_NAME,
                        InternalDataBaseSchemas.Reports.COLUMN_STATE + " != ? AND "
                                + InternalDataBaseSchemas.Reports.COLUMN_STATE + " != ?",
                        new String[]{"valide", "traité"});

                ContentValues row = new ContentValues();
                row.put(InternalDataBaseSchemas.Reports.COLUMN_BELONGS_TO_USER, 0);
                MainActivity.writableDB.update(InternalDataBaseSchemas.Reports.TABLE_NAME,
                        row,
                        null,
                        null);
                //
                MainActivity.writableDB.delete(
                        InternalDataBaseSchemas.Medias.TABLE_NAME,
                        InternalDataBaseSchemas.Medias.COLUMN_BELONGS_TO_USER + " != ? ",
                        new String[]{"0"});


                MainActivity.putFragmentInMainActivityContent(new UnsignedVisitorScreen());
            }
        });
        //
        view.findViewById(R.id.delete_account_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordWindow(null, R.drawable.delete_account);

            }
        });
        //
        appDataText = view.findViewById(R.id.clear_data_title);

    }

    @Override
    public void doWhenPasswordReady(String password) {
        if (!AppSettingsUtils.isConnectedToInternet(getContext())) {
            Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressWindow(getString(R.string.connecting_to_server), false);

        new LimitedNetworkOperation(getContext()) {
            @Override
            protected void doInUiThread(int evolutionFlag, Object result) {
                if (evolutionFlag == EvolutionFlag.ERROR_MESSAGE) {

                    try {
                        JSONObject response = new JSONObject((String) result);
                        String msg = response.getString("message");
                        showResultsWindow(msg, RESULT_ERROR);
                    } catch (JSONException e) {

                    }


                } else if (evolutionFlag == EvolutionFlag.JSON_IS_READY) {
                    getView().findViewById(R.id.logout_container).performClick();
                    hideProgressWindow();
                    Toast.makeText(getContext(), getString(R.string.account_deleted), Toast.LENGTH_SHORT).show();
                }
            }
        }.postJson(Requests.DELETE_ACCOUNT,
                new RequestHeader(new RequestHeader.Property(RequestHeader.Field.AUTHORIZATION, "Bearer " + MainActivity.token)),
                ("{\"password\" : " + "\"" + password + "\"}"),
                LimitedNetworkOperation.ThreadPriority.LOW);
    }


    @Override
    public void doWhenResultsSeen() {

    }

    /**/
    private void showChangeLanguageWindow(View parentFragment) {
        popupWindow.setView(LayoutInflater.from(parentFragment.getContext()).inflate(R.layout.window_change_app_language, null));
        popupWindow.getWindow().getDecorView().setBackground(null);
        popupWindow.show();
        //
        RecyclerView langagesList = popupWindow.findViewById(R.id.languages_list);
        langagesList.setHasFixedSize(true);
        langagesList.setLayoutManager(new LinearLayoutManager(getContext()));
        langagesList.setAdapter(new LanguagesListAdapter(getActivity()));
        //
        popupWindow.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }

}//