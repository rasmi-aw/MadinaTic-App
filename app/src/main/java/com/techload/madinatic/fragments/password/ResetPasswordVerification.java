package com.techload.madinatic.fragments.password;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.techload.madinatic.R;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.data.networking.networkOperations.LimitedNetworkOperation;
import com.techload.madinatic.fragments.helpers.TextEditableFragment;
import com.techload.madinatic.utils.AppSettingsUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class ResetPasswordVerification extends TextEditableFragment {


    public ResetPasswordVerification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextInputEditText login = view.findViewById(R.id.login_editText);
        final TextInputLayout loginParent = view.findViewById(R.id.login_container);
        //
        login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginParent.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //

        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String valLogin = login.getText().toString().trim();

                if (valLogin == null || valLogin.length() < 4) {
                    loginParent.setError(getString(R.string.error_too_short));
                    return;
                }

                if (!AppSettingsUtils.isConnectedToInternet(getContext())) {
                    Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgressWindow(getString(R.string.connecting_to_server), false);

                String json = "{ \"email\" : " + "\"" + valLogin + "\""
                        + "}";

                new LimitedNetworkOperation(getContext()) {
                    @Override
                    protected void doInUiThread(int evolutionFlag, Object result) {

                        if (evolutionFlag == EvolutionFlag.JSON_IS_READY) {

                            showResultsWindow(getString(R.string.reset_password_email), RESULT_EMAIL_SENT);

                        } else {
                            hideProgressWindow();
                            if (evolutionFlag == EvolutionFlag.ERROR_MESSAGE) {
                                try {
                                    showResultsWindow(new JSONObject(result.toString()).getString("message"), RESULT_ERROR);
                                } catch (JSONException e) {
                                }
                            }

                        }

                    }
                }.postJson(Requests.RESET_PASSWORD_EMAIL,
                        null,
                        json,
                        LimitedNetworkOperation.ThreadPriority.LOW);

            }
        });


    }


    @Override
    public void doWhenResultsSeen() {

    }
}
