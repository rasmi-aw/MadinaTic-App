package com.techload.madinatic.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.data.networking.networkOperations.LimitedNetworkOperation;
import com.techload.madinatic.data.networking.services.NetworkingService;
import com.techload.madinatic.fragments.helpers.BackNavigationFragment;
import com.techload.madinatic.fragments.helpers.TextEditableFragment;
import com.techload.madinatic.fragments.password.ResetPasswordVerification;
import com.techload.madinatic.utils.AppSettingsUtils;

import org.json.JSONObject;


public class Sign_in extends TextEditableFragment {
    private TextView sign_in;
    private TextInputEditText login, password;
    private TextInputLayout loginParent, passwordParent;
    //
    private String valUsername = null, valPassword = null;
    //


    public Sign_in() {
        // Required empty public constructor
    }

    @Override
    public void doWhenResultsSeen() {
        if (MainActivity.token != null && !MainActivity.token.isEmpty()) {
            MainActivity.putFragmentInMainActivityContent(new WelcomeScreen());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
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
        sign_in = null;
        login = null;
        password = null;
        loginParent = null;
        passwordParent = null;
        valUsername = null;
        valPassword = null;
    }

    /*Initializing Fragment views and keyboard clossed on start*/
    private void initialization(final View view) {
        //
        login = view.findViewById(R.id.login_editText);
        loginParent = view.findViewById(R.id.login_container);
        password = view.findViewById(R.id.password_editText);
        passwordParent = view.findViewById(R.id.password_container);
        //
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) ((TextInputLayout) v.getParent().getParent()).setErrorEnabled(false);
            }
        };
        login.setOnFocusChangeListener(focusChangeListener);
        password.setOnFocusChangeListener(focusChangeListener);
        //
        sign_in = view.findViewById(R.id.sign_in);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*cleaning error messages from text fields*/
                passwordParent.setErrorEnabled(false);
                loginParent.setErrorEnabled(false);
                MainActivity.putFragmentInMainActivityContent(new WelcomeScreen());
                //
                closeKeyboard(v);
//                /*preparing the json object to be sent to the server*/
//                valUsername = login.getText().toString().trim();
//                valPassword = password.getText().toString().trim();
//
//                if (!AppSettingsUtils.isConnectedToInternet(getContext())) {
//                    showResultsWindow(getResources().getString(R.string.no_internet), RESULT_WARNING);
//                    return;
//                }
//
//                /*checking inputs*/
//                if (!checkInputs()) return;
//
//                String json = "{\"username\": \"" + valUsername + "\", " +
//                        "\"password\": \"" + valPassword + "\" }";
//
//                /*prevent user from clicking sign-in more than once before getting the response from the server*/
//                sign_in.setClickable(false);
//                sign_in.setBackgroundResource(R.drawable.rectangle_rounded_gray);
//                sign_in.setAlpha(0.6f);
//                showProgressWindow(getString(R.string.connecting_to_server), false);
//
//
//                /*making request for authentication and receiving data like token etc...*/
//                new LimitedNetworkOperation(getContext()) {
//                    @Override
//                    protected void doInUiThread(int evolutionFlag, Object result) {
//
//                        hideProgressWindow();
//
//                         /*case the user is logged in successfully or there's
//                         an error message from the server*/
//                        if (evolutionFlag == EvolutionFlag.JSON_IS_READY || evolutionFlag == EvolutionFlag.ERROR_MESSAGE) {
//                            JSONObject serverResponse;
//                            String message = null;
//                            try {
//
//                                /*extracting information from json response*/
//                                serverResponse = new JSONObject(((String) result));
//                                message = serverResponse.getString("message");
//
//                                if (message.contains("logged")) {
//                                    /*case the user is logged-in successfully*/
//                                    MainActivity.token = serverResponse.getString(AppSettingsUtils.USER_TOKEN);
//                                    MainActivity.sharedPrefEditor.putString(AppSettingsUtils.USER_TOKEN, MainActivity.token);
//
//                                    /*saving login and password*/
//                                    MainActivity.sharedPrefEditor.putString(MainActivity.USER_PASSWORD, valPassword);
//
//                                    MainActivity.sharedPrefEditor.commit();
//                                    showResultsWindow(getString(R.string.successful_login), RESULT_WELL_DONE);
//
////                                    /*loading user info from the server using the token
//                                    NetworkingService.updateUserData(null, getContext(), MainActivity.sharedPrefEditor);
//
//
//                                } else if (message.toLowerCase().contains("pseudo")) {
//                                    loginParent.setError(message);
//                                    return;
//                                } else if (message.contains("passe"))
//                                    passwordParent.setError(message);
//                                else {
//                                    showResultsWindow(message, RESULT_ERROR);
//                                }
//
//
//                            } catch (Exception e) {
//                                /*error in json appeared*/
//                            }
//                        }
//                        /*returning sign-in button to it's clickable state*/
//                        sign_in.setClickable(true);
//                        sign_in.setBackgroundResource(R.drawable.rectangle_rounded_green);
//                        sign_in.setAlpha(1);
//                    }
//                }.postJson(Requests.AUTHENTICATION,
//                        null,
//                        json,
//                        LimitedNetworkOperation.ThreadPriority.LOW);

            }
        });
        //
        view.findViewById(R.id.password_forgotten).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.putFragmentInMainActivityContentAndAddToBackStack(
                        new BackNavigationFragment(new ResetPasswordVerification(), getString(R.string.password_forgotten)));
            }
        });
    }

    public boolean checkInputs() {
        boolean allOkey = true;

        if (valUsername.isEmpty()) {
            loginParent.setError(getResources().getString(R.string.error_too_short));
            allOkey = false;
        }

        if (valPassword.length() < 5) {
            passwordParent.setError(getResources().getString(R.string.error_too_short));
            allOkey = false;
        }
        return allOkey;
    }
}//
