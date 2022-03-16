package com.techload.madinatic.utils;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * converting json to info that can be used to contact the server
 * or to be shown to the user
 */
public class AppStats {
    private int citizensNumber = 0, usersNumber = 0,
            reportsNumber = 0, processedReportsNumber = 0, idLastReport = 0,
            postsNumber = 0, processedPostsNumber = 0, idLastPost = 0;

    private String date, json;

    //
    public AppStats(String json) {
        this.json = json;
        if (json != null && !json.isEmpty())
            try {
                JSONObject jsonObject = new JSONObject(json);
                //
                if (json.contains("date")) date = jsonObject.getString("date");
                //
                citizensNumber = jsonObject.getJSONObject("citoyens").getInt("nombre");
                usersNumber = jsonObject.getJSONObject("comptes").getInt("nombre");
                //
                JSONObject jsonObject1 = jsonObject.getJSONObject("signalements");
                reportsNumber = jsonObject1.getInt("nombre");
                processedReportsNumber = jsonObject1.getInt("traité");
                idLastReport = jsonObject1.getInt("id_dernier_signalement");
                //
                jsonObject1 = jsonObject.getJSONObject("annonces");
                postsNumber = jsonObject1.getInt("nombre");
                processedPostsNumber = jsonObject1.getInt("validé");
                idLastPost = jsonObject1.getInt("id_derniere_annonce");
            } catch (JSONException e) {

            }
    }

    public String addDateToJson(String json) {
        JSONObject jsonObject = null;
        if (json != null && !json.isEmpty())
            try {
                jsonObject = new JSONObject(json);
                date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                jsonObject.put("date", date);

            } catch (JSONException e) {

            }
        return (jsonObject != null) ? jsonObject.toString() : null;
    }


    //getters


    public int getCitizensNumber() {
        return citizensNumber;
    }

    public int getIdLastPost() {
        return idLastPost;
    }

    public int getIdLastReport() {
        return idLastReport;
    }

    public int getPostsNumber() {
        return postsNumber;
    }

    public int getProcessedPostsNumber() {
        return processedPostsNumber;
    }

    public int getProcessedReportsNumber() {
        return processedReportsNumber;
    }

    public int getReportsNumber() {
        return reportsNumber;
    }

    public int getUsersNumber() {
        return usersNumber;
    }

    public String getDate() {

        return date;
    }
}