package com.techload.madinatic.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.techload.madinatic.R;
import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.data.networking.networkOperations.LimitedNetworkOperation;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.fragments.helpers.ChangeProfilePictureFragment;
import com.techload.madinatic.utils.AppSettingsUtils;
import com.techload.madinatic.utils.GraphicUtils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class Sign_up extends ChangeProfilePictureFragment {
    //
    private TextInputEditText familyName, firstName, nationalNumber, birthday, phone, email,
            password, passwordConfirmed, pseudoName;

    //
    private TextInputLayout familyNameParent, firstNameParent, nationalNumberParent, birthdayParent, phoneParent, emailParent,
            passwordParent, passwordConfirmedParent, pseudoNameParent;

    //
    private CheckBox policyCheckBox;
    private TextView sign_up;
    private String valFamilyName, valFirstName, valNationalNumber, valBirthday, valPhone, valEmail, valPassword, valPasswordConfirmed,
            valPseudoName;
    //
    private AlertDialog datePickerWindow;
    //
    private boolean sign_upSuccessful = false;


    public Sign_up() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //
        initialization(view);
        //

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
        passwordConfirmed = null;
        pseudoName = null;
        familyNameParent = null;
        firstNameParent = null;
        nationalNumberParent = null;
        birthdayParent = null;
        phoneParent = null;
        emailParent = null;
        passwordParent = null;
        passwordConfirmedParent = null;
        pseudoNameParent = null;
        policyCheckBox = null;
        sign_up = null;
        valFamilyName = null;
        valFirstName = null;
        valNationalNumber = null;
        valBirthday = null;
        valPhone = null;
        valEmail = null;
        valPassword = null;
        valPasswordConfirmed = null;
        valPseudoName=null;
        datePickerWindow=null;
        //
    }

    @Override
    public void doWhenResultsSeen() {
        if (sign_upSuccessful)
            MainActivity.putFragmentInMainActivityContentAndAddToBackStack(new Sign_in());
    }

    /*Initializing Fragment views and keyboard clossed on start*/
    private void initialization(final View view) {
        policyCheckBox = view.findViewById(R.id.agree_to_policy_checkbox);
        //
        familyName = view.findViewById(R.id.familyName_editText);
        firstName = view.findViewById(R.id.name_editText);
        nationalNumber = view.findViewById(R.id.nationalId_editText);
        pseudoName = view.findViewById(R.id.pseudoName_editText);
        phone = view.findViewById(R.id.phone_editText);
        email = view.findViewById(R.id.email_editText);
        password = view.findViewById(R.id.password_editText);
        passwordConfirmed = view.findViewById(R.id.confirm_password_editText);
        //
        birthday = view.findViewById(R.id.birthday_editText);
        birthday.setFocusable(false);
        //
        familyNameParent = view.findViewById(R.id.familyName_container);
        firstNameParent = view.findViewById(R.id.name_container);
        nationalNumberParent = view.findViewById(R.id.nationalId_container);
        birthdayParent = view.findViewById(R.id.birthday);
        phoneParent = view.findViewById(R.id.phone_container);
        emailParent = view.findViewById(R.id.email_container);
        passwordParent = view.findViewById(R.id.password_container);
        passwordConfirmedParent = view.findViewById(R.id.confirm_password_container);
        pseudoNameParent = view.findViewById(R.id.pseudoName_container);
        // removing error from fields when user writes a new content on it

        final View.OnFocusChangeListener errorWatcher = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) ((TextInputLayout) v.getParent().getParent()).setErrorEnabled(false);
            }
        };

        familyName.setOnFocusChangeListener(errorWatcher);
        firstName.setOnFocusChangeListener(errorWatcher);
        nationalNumber.setOnFocusChangeListener(errorWatcher);
        birthday.setOnFocusChangeListener(errorWatcher);
        pseudoName.setOnFocusChangeListener(errorWatcher);
        phone.setOnFocusChangeListener(errorWatcher);
        email.setOnFocusChangeListener(errorWatcher);
        password.setOnFocusChangeListener(errorWatcher);
        passwordConfirmed.setOnFocusChangeListener(errorWatcher);
        firstName.setOnFocusChangeListener(errorWatcher);
        //
        birthday.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboard(v);
                //

                ViewGroup view = (ViewGroup) LayoutInflater.from(getContext()).
                        inflate(R.layout.window_date_picker, null, false);

                if (datePickerWindow == null) {
                    datePickerWindow = (new AlertDialog.Builder(getContext())).create();
                    datePickerWindow.setView(view);
                }

                if (!datePickerWindow.isShowing()) datePickerWindow.show();
                //
                final DatePicker datePicker = view.findViewById(R.id.date_picker);
                datePickerWindow.getWindow().getDecorView().setBackground(null);
                //
                datePickerWindow.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePickerWindow.dismiss();
                    }
                });
                //
                view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth() + 1;
                        int day = datePicker.getDayOfMonth();
                        valBirthday = year + "-" + ((month > 9) ? "" + month : "0" + month)
                                + "-" + ((day > 9) ? "" + day : "0" + day);
                        if (MainActivity.appLanguage.equals("ar")) {
                            birthday.setText(valBirthday);
                        } else {
                            String[] bthdFields = valBirthday.split("-");/*reversing the date from lftToRt*/
                            birthday.setText(bthdFields[2] + "-" + bthdFields[1] + "-" + bthdFields[0]);
                        }
                        datePickerWindow.dismiss();
                    }
                });
                return false;
            }
        });

        //sign_up request
        sign_up = view.findViewById(R.id.sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.putFragmentInMainActivityContentAndAddToBackStack(new Sign_in());

                /*closing keyboard*/
                closeKeyboard(v);
                return;
//                if (!AppSettingsUtils.isConnectedToInternet(getContext())) {
//                    showResultsWindow(getString(R.string.no_internet), RESULT_WARNING);
//                    sign_up.requestFocus();
//                    return;
//                } else {
//
//                    /*getting text fields values*/
//                    valFamilyName = familyName.getText().toString().trim();
//                    valFirstName = firstName.getText().toString().trim();
//                    valNationalNumber = nationalNumber.getText().toString().trim();
//                    valEmail = email.getText().toString().trim();
//                    valPhone = phone.getText().toString().trim();
//                    valPassword = password.getText().toString().trim();
//                    valPasswordConfirmed = passwordConfirmed.getText().toString().trim();
//                    valPseudoName = pseudoName.getText().toString().trim();
//
//
//                    /*checking if there's an error in one of the inputs*/
//                    if (!checkInputs()) return;
//
//                    String json = "{" +
//                            "\"last_name\":" + "\"" + valFamilyName + "\"," +
//                            "\"first_name\":" + "\"" + valFirstName + "\"," +
//                            "\"username\":" + "\"" + valPseudoName + "\"," +
//                            "\"password\":" + "\"" + valPassword + "\"," +
//                            "\"confirm_password\":" + "\"" + valPassword + "\"," +
//                            "\"email\":" + "\"" + valEmail + "\"," +
//                            "\"NCN\":" + "\"" + valNationalNumber + "\"," +
//                            "\"phone_number\":" + "\"" + valPhone + "\"," +
//                            "\"date_de_naissance\":" + "\"" + valBirthday + "\"" +
//                            "}";
//                    /*cleaning error messages from text fields*/
//                    familyNameParent.setErrorEnabled(false);
//                    firstNameParent.setErrorEnabled(false);
//                    nationalNumberParent.setErrorEnabled(false);
//                    birthdayParent.setErrorEnabled(false);
//                    phoneParent.setErrorEnabled(false);
//                    emailParent.setErrorEnabled(false);
//                    passwordParent.setErrorEnabled(false);
//                    passwordConfirmedParent.setErrorEnabled(false);
//                    pseudoNameParent.setErrorEnabled(false);
//
//                    /*prevent user from clicking sign-up more than once before getting the response from the server*/
//                    sign_up.setClickable(false);
//                    sign_up.setBackgroundResource(R.drawable.rectangle_rounded_gray);
//                    sign_up.setAlpha(0.6f);
//                    showProgressWindow(getString(R.string.connecting_to_server),false);
//                    sign_up.requestFocus();
//                    /*sending sign-up request*/
//                    new LimitedNetworkOperation(getContext()) {
//                        @Override
//                        protected void doInUiThread(int evolutionFlag, Object result) {
//
//                            if (evolutionFlag == EvolutionFlag.ERROR_MESSAGE) {
//                                try {
//                                    JSONObject serverResponse = new JSONObject(((String) result));
//                                    String message = serverResponse.getString("message").toLowerCase().trim();
//
//
//                                    /*errors indication*/
//                                    if (message != null) {
//                                        if (message.contains("ncn")) {
//                                            nationalNumberParent.setError(message);
//                                            nestedScrollView.smoothScrollTo(0, nationalNumberParent.getScrollY());
//                                        } else if (message.contains("pseudo")) {
//                                            pseudoNameParent.setError(message);
//                                            nestedScrollView.smoothScrollTo(0, pseudoNameParent.getScrollY());
//                                        } else if (message.contains("email")) {
//                                            emailParent.setError(message);
//                                            nestedScrollView.smoothScrollTo(0, emailParent.getScrollY());
//                                        } else if (message.contains("naissance")) {
//                                            birthdayParent.setError(message);
//                                            nestedScrollView.smoothScrollTo(0, birthdayParent.getScrollY());
//                                        } else {
//                                            showResultsWindow(message, RESULT_ERROR);
//                                            sign_up.requestFocus();
//                                            nestedScrollView.smoothScrollTo(0, nationalNumberParent.getScrollY());
//                                            nationalNumberParent.setError(message);
//                                        }
//
//                                    }
//                                } catch (JSONException e) {
//                                }
//
//                            } else if (evolutionFlag == EvolutionFlag.JSON_IS_READY) {
//                                //Inscription réussite in message
//                                sign_upSuccessful = true;
//                                showResultsWindow(getString(R.string.confirm_your_account), RESULT_EMAIL_SENT);
//                                sign_up.requestFocus();
//                            }
//
//                            sign_up.setClickable(true);
//                            sign_up.setBackgroundResource(R.drawable.rectangle_rounded_green);
//                            sign_up.setAlpha(1);
//                            hideProgressWindow();
//                        }
//                    }.putJson(Requests.SIGN_UP, null, json, LimitedNetworkOperation.ThreadPriority.LOW);

                }

//            }
        });

    }

    private boolean checkInputs() {

        boolean everythingIsOkay = true;

        /*checking every field if contains error*/
        if (valFirstName == null || valFirstName.isEmpty()) {
            firstNameParent.setError(getString(R.string.error_is_empty));
            everythingIsOkay = false;
        }

        if (valFamilyName == null || valFamilyName.isEmpty()) {
            familyNameParent.setError(getString(R.string.error_is_empty));
            everythingIsOkay = false;
        }

        if (valPassword == null || valPassword.isEmpty()) {
            passwordParent.setError(getString(R.string.error_is_empty));
            everythingIsOkay = false;
        } else if (valPassword.length() < 5) {
            passwordParent.setError(getString(R.string.error_too_short) + " (x < 5) ");
            everythingIsOkay = false;
        }

        if (valPasswordConfirmed == null || valPasswordConfirmed.isEmpty()) {
            passwordConfirmedParent.setError(getString(R.string.error_is_empty));
            everythingIsOkay = false;
        } else if (!valPasswordConfirmed.equals(valPassword)) {
            passwordConfirmedParent.setError(getString(R.string.error_confirmation_password_different));
            everythingIsOkay = false;
        }

        if (valBirthday == null || valBirthday.isEmpty()) {
            birthdayParent.setError(getString(R.string.error_is_empty));
            everythingIsOkay = false;
        }

        if (valPseudoName == null || valPseudoName.isEmpty()) {
            pseudoNameParent.setError(getString(R.string.error_is_empty));
            everythingIsOkay = false;
        }

        if (valPhone == null || valPhone.isEmpty()) {
            phoneParent.setError(getString(R.string.error_is_empty));
            everythingIsOkay = false;
        } else if (valPhone.length() < 10) {
            phoneParent.setError(getString(R.string.error_too_short));
            everythingIsOkay = false;
        }

        if (valEmail == null || valEmail.isEmpty()) {
            emailParent.setError(getString(R.string.error_is_empty));
            everythingIsOkay = false;
        }

        if (valNationalNumber.isEmpty()) {
            nationalNumberParent.setError(getString(R.string.error_is_empty));
            everythingIsOkay = false;
        }

        if (!policyCheckBox.isChecked() && everythingIsOkay == true) {
            policyCheckBox.setTextColor(getResources().getColor(R.color.red));
            everythingIsOkay = false;
        }


        return everythingIsOkay;

    }

}//