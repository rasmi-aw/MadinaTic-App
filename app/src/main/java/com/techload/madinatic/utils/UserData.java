package com.techload.madinatic.utils;


import com.techload.madinatic.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class UserData {
    private String familyName, firstName, nationalId, birthDay, email, phone, pseudo, profilePicture;

    public UserData(String userDataJson) {
        if (userDataJson != null && !userDataJson.isEmpty())
            try {
                JSONObject userInfo = new JSONObject(userDataJson);
                JSONObject userObject = userInfo.getJSONObject("user");
                JSONObject citizenObject = userInfo.getJSONObject("citoyen");

                if (userInfo != null) {
                    //
                    familyName = citizenObject.getString("nom");
                    familyName = familyName.replaceFirst("" + familyName.charAt(0), "" + Character.toUpperCase(familyName.charAt(0)));

                    //
                    firstName = citizenObject.getString("prenom");
                    firstName = firstName.replaceFirst("" + firstName.charAt(0), "" + Character.toUpperCase(firstName.charAt(0)));

                    //
                    birthDay = citizenObject.getString("date_naissance");

                    if (!MainActivity.appLanguage.equals("ar")) {
                        String[] birthD = birthDay.split("-");
                        birthDay = birthD[2] + "-" + birthD[1] + "-" + birthD[0];
                    }
                    //
                    nationalId = citizenObject.getString("ncn");
                    email = userObject.getString("email");
                    phone = userObject.getString("numero_mobile");
                    pseudo = userObject.getString("pseudo");
                    profilePicture = userObject.getString("url_photo");
                }
            } catch (JSONException e) {

            }
    }

    //getters
    public String getBirthDay() {
        return birthDay;
    }

    public String getEmail() {
        return email;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public String getPhone() {
        return phone;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
