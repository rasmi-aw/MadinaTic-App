package com.techload.madinatic.fragments.password;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.data.networking.RequestHeader;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.data.networking.networkOperations.LimitedNetworkOperation;
import com.techload.madinatic.fragments.helpers.BackNavigationFragment;
import com.techload.madinatic.fragments.helpers.TextEditableFragment;
import com.techload.madinatic.utils.AppSettingsUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class ChangePasswordScreen extends TextEditableFragment {
    private TextInputEditText oldPassword, newPassword, passwordConf;
    private TextInputLayout oldPasswordParent, newPasswordParent, passwordConfParent;
    private TextView save;
    private String valOldPassword, valNewPassword, valConfPassword;
    //
    private char resultType;

    public ChangePasswordScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        initialization(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        oldPassword = null;
        newPassword = null;
        passwordConf = null;
        oldPasswordParent = null;
        newPasswordParent = null;
        passwordConfParent = null;
        save = null;
        valOldPassword = null;
        valNewPassword = null;
        valConfPassword = null;
    }

    @Override
    public void doWhenResultsSeen() {
        if (resultType == RESULT_WELL_DONE) {
            MainActivity.fragmentManagerMainActivity.popBackStack();
        }
    }

    /**/
    private void initialization(View view) {
        //
        oldPasswordParent = view.findViewById(R.id.old_password_container);
        newPasswordParent = view.findViewById(R.id.password_container);
        passwordConfParent = view.findViewById(R.id.confirm_password_container);
        //
        oldPassword = view.findViewById(R.id.old_password_editText);
        newPassword = view.findViewById(R.id.password_editText);
        passwordConf = view.findViewById(R.id.confirm_password_editText);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                oldPasswordParent.setErrorEnabled(false);
                newPasswordParent.setErrorEnabled(false);
                passwordConfParent.setErrorEnabled(false);
            }
        };
        oldPassword.addTextChangedListener(textWatcher);
        newPassword.addTextChangedListener(textWatcher);
        passwordConf.addTextChangedListener(textWatcher);
        //
        save = view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*reset error fields to show a new type of error in case it's found*/
                oldPasswordParent.setErrorEnabled(false);
                newPasswordParent.setErrorEnabled(false);
                passwordConfParent.setErrorEnabled(false);

                /*every time the user clicks on save those fields are changed*/
                valOldPassword = oldPassword.getText().toString().trim();
                valConfPassword = passwordConf.getText().toString().trim();
                valNewPassword = newPassword.getText().toString().trim();

                if (AppSettingsUtils.isConnectedToInternet(getContext())) {

                    /*case of a syntax error*/
                    if (valOldPassword.length() < 5) {
                        oldPasswordParent.setError(getString(R.string.error_too_short));
                        return;
                    } else if (newPassword.length() < 5) {
                        newPasswordParent.setError(getString(R.string.error_too_short));
                        return;
                    } else if (valNewPassword.equals(valOldPassword)) {
                        newPasswordParent.setError(getString(R.string.error_new_password_equal_to_old));
                        return;
                    } else if (!valNewPassword.equals(valConfPassword)) {
                        passwordConfParent.setError(getString(R.string.error_confirmation_password_different));
                        return;
                    }

                    final String json = "{" +
                            "\"new_password\":" + "\"" + valNewPassword + "\"," +
                            "\"old_password\":" + "\"" + valOldPassword + "\"," +
                            "\"confirm_new_password\":" + "\"" + valConfPassword + "\"" +
                            "}";

                    Log.v("rasmi", "rasmi\n" + json + "\n");

                    /*disabling send button to prevent multiple server calls*/
                    save.setClickable(false);
                    save.setBackgroundResource(R.drawable.rectangle_rounded_gray);
                    save.setAlpha(0.6f);
                    showProgressWindow(getString(R.string.connecting_to_server), false);

                    resultType = RESULT_ERROR;

                    new LimitedNetworkOperation(getContext()) {
                        @Override
                        protected void doInUiThread(int evolutionFlag, Object result) {
                            JSONObject response;

                            if (evolutionFlag == EvolutionFlag.ERROR_MESSAGE ||
                                    evolutionFlag == EvolutionFlag.JSON_IS_READY) {
                                try {
                                    response = new JSONObject(result.toString());
                                    String message = response.getString("message").trim().toLowerCase();

                                    if (message.contains("ancien")) {
                                        oldPasswordParent.setError(message);

                                    } else if (message.contains("changer")) {
                                        /*case of a successful password reset*/
                                        resultType = RESULT_WELL_DONE;
                                        MainActivity.sharedPrefEditor.putString(AppSettingsUtils.USER_PASSWORD, valConfPassword).commit();
                                        showResultsWindow(getString(R.string.successful_password_reset), RESULT_WELL_DONE);
                                    } else {
                                        showResultsWindow(message, RESULT_ERROR);
                                    }


                                } catch (JSONException e) {

                                }

                                Log.v("rasmi", "rasmi done\n" + result.toString() + "\n");
                            }

                            save.setClickable(true);
                            save.setBackgroundResource(R.drawable.rectangle_rounded_green);
                            hideProgressWindow();
                        }
                    }.postJson(Requests.CHANGE_USER_PASSWORD,
                            new RequestHeader(new RequestHeader.Property(
                                    RequestHeader.Field.AUTHORIZATION, "Bearer " + MainActivity.token)),
                            json,
                            LimitedNetworkOperation.ThreadPriority.LOW);


                } else {
                    /*case no internet*/
                }

            }
        });
        //
        view.findViewById(R.id.password_forgotten).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.putFragmentInMainActivityContentAndAddToBackStack(
                        new BackNavigationFragment(new ResetPasswordVerification(),R.string.password_forgotten));
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//            getChildFragmentManager().popBackStack();
                MainActivity.fragmentManagerMainActivity.popBackStack();
//            MainActivity.removeFragmentFromBackStack(fragment);
            }
        });

    }//
}