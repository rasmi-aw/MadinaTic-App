package com.techload.madinatic.data.networking.networkOperations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.techload.madinatic.R;
import com.techload.madinatic.data.InternalDataBase.InternalDataBaseSchemas;
import com.techload.madinatic.data.networking.RequestHeader;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.lists_items.Media;
import com.techload.madinatic.lists_items.Report;
import com.techload.madinatic.utils.AppSettingsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Reporting implements InternalDataBaseSchemas.Reports {
    private SQLiteDatabase readableDB, writableDB;
    private Context context;


    public Reporting(Context context, SQLiteDatabase readableDB, SQLiteDatabase writableDB) {
        this.context = context;
        this.readableDB = readableDB;
        this.writableDB = writableDB;

    }

    public interface Flag {
        int ERROR = -1;
        int STARTED_LOADING_FROM_SERVER = 0;
        int FINISHED_LOADING_REPORTS_FROM_SERVER = 1;
        int FINISHED_LOADING_USER_REPORTS_FROM_SERVER = 2;
        int FINISHED_READING_FROM_DB = 3;
    }


    public void whenOperationFinished(Object result, int flag) {
    }

    ;


    /* read all reports*/
    public void loadAllReports() {

        //connection to the server and reading all users reports
        new LimitedNetworkOperation(context) {

            @Override
            protected void doInUiThread(int evolutionFlag, Object result) {
                if (evolutionFlag == EvolutionFlag.ERROR_MESSAGE
                        || evolutionFlag == EvolutionFlag.REQUEST_UNACCEPTED) {
                    whenOperationFinished(null, Flag.ERROR);
                } else if (evolutionFlag == EvolutionFlag.REQUEST_ACCEPTED) {
                    whenOperationFinished(null, Flag.STARTED_LOADING_FROM_SERVER);
                } else if (evolutionFlag == EvolutionFlag.JSON_IS_READY) {

                    try {

                        /*extracting data from db*/
                        JSONObject serverResponse = new JSONObject(((String) result));
                        JSONArray signalements = serverResponse.getJSONArray("signalements");


                        //
                        int id;
                        String title, description, address, dateSent, state, video;
                        String[] medias = null;


                        for (int i = 0; i < signalements.length(); i++) {
                            JSONObject current = signalements.getJSONObject(i);

                            /**/
                            id = current.getInt("id");

                            title = current.getString("title").trim();
                            description = current.getString("description").trim();
                            address = current.getString("position").trim();
                            dateSent = current.getString("date_depot").trim();
                            state = current.getString("etat").trim();
                            video = current.getString("video_url").trim();
                            //

                            String mediaStr = current.getString("img_url").trim();
                            if (mediaStr.contains("|"))
                                medias = mediaStr.replace('|', '#').split("#");


                            //filling reports database with last content
                            ContentValues row = new ContentValues();
                            row.put(COLUMN_ID, id);
                            row.put(COLUMN_TITLE, title);
                            row.put(COLUMN_DESCRIPTION, description);
                            row.put(COLUMN_ADDRESS, address);
                            row.put(COLUMN_DATE_SENT, dateSent);
                            row.put(COLUMN_STATE, state);
                            int rowsAffected = writableDB.update(TABLE_NAME, row,
                                    InternalDataBaseSchemas.Reports.COLUMN_ID + " = ?",
                                    new String[]{"" + id});

                            if (rowsAffected < 1)
                                writableDB.insert(TABLE_NAME, null, row);

                            // inserting images and video
                            if (video != null && video.length() > 10) {
                                ContentValues reportVideo = new ContentValues();
                                reportVideo.put(InternalDataBaseSchemas.Medias.COLUMN_PATH, video);
                                reportVideo.put(InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE, TABLE_NAME);
                                reportVideo.put(InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW, id);
                                reportVideo.put(InternalDataBaseSchemas.Medias.COLUMN_TYPE, Media.guessMediaTypeFromPath(video));
                                writableDB.insert(InternalDataBaseSchemas.Medias.TABLE_NAME, null, reportVideo);
                            }

                            if (medias != null)
                                for (int j = 0; j < medias.length; j++) {
                                    int mediaType = Media.guessMediaTypeFromPath(medias[j]);
                                    if (mediaType == Media.UNKNOWN)
                                        continue;
                                    ContentValues reportImage = new ContentValues();
                                    reportImage.put(InternalDataBaseSchemas.Medias.COLUMN_PATH, medias[j]);
                                    reportImage.put(InternalDataBaseSchemas.Medias.COLUMN_TYPE, mediaType);
                                    reportImage.put(InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE, TABLE_NAME);
                                    reportImage.put(InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW, id);
                                    //
                                    writableDB.insert(InternalDataBaseSchemas.Medias.TABLE_NAME, null, reportImage);
                                }

                        }
                        whenOperationFinished(null, Flag.FINISHED_LOADING_REPORTS_FROM_SERVER);

                    } catch (JSONException e) {

                    }
                }
            }
        }.downloadText(Requests.GET_ALL_REPORTS,
                null,
                LimitedNetworkOperation.DataType.TEXT,
                false);
    }

    public void readAllReportsFromInternalDB() {
        //
        ArrayList<Report> reports = new ArrayList<>();
        //
        int id;
        String category, title, description, address, dateSent, state;
        ArrayList<Media> medias = new ArrayList<>();
        Report report;

        String[] reportsProjection = new String[]{
                COLUMN_ID,
                COLUMN_REPORT_CATEGORY,
                COLUMN_TITLE,
                COLUMN_DESCRIPTION,
                COLUMN_ADDRESS,
                COLUMN_DATE_SENT,
                COLUMN_STATE

        };

        String[] mediasProjection = new String[]{
                InternalDataBaseSchemas.Medias.COLUMN_ID,
                InternalDataBaseSchemas.Medias.COLUMN_TYPE,
                InternalDataBaseSchemas.Medias.COLUMN_PATH,
                InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE,
                InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW,
                InternalDataBaseSchemas.Medias.COLUMN_CACHED_PATH
        };

        Cursor cursor = readableDB.query(
                TABLE_NAME,
                reportsProjection,
                COLUMN_BELONGS_TO_USER + " = ?",
                new String[]{"0"},
                null,
                null,
                COLUMN_ID + " DESC");


        while (cursor.moveToNext()) {
            /*reading reports from db*/
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REPORT_CATEGORY));
            title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
            dateSent = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_SENT));
            state = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATE));
            //

            char stateChar = Report.STATE_BEING_PROCESSED;

            switch (state) {
                case "traité":
                    stateChar = Report.STATE_PROCESSED;
                    break;

                case "valide":
                    stateChar = Report.STATE_BEING_PROCESSED;
                    break;

                case "en_attente":
                    stateChar = Report.STATE_SENT;
                    break;

                case "demandé-complement":
                    stateChar = Report.STATE_INCOMPLETE;
                    break;

                case "rejeté":
                    stateChar = Report.STATE_REJECTED;
                    break;

                default:
                    stateChar = state.charAt(0);
                    break;

            }

            /*read images after clearing last report images*/
            medias.clear();

            Cursor mediasCursor = readableDB.query(
                    InternalDataBaseSchemas.Medias.TABLE_NAME,
                    mediasProjection,
                    InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE + " = ? AND "
                            + InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW + " = ? ",
                    new String[]{TABLE_NAME, "" + id},
                    null,
                    null,
                    null
            );

            while (mediasCursor.moveToNext()) {

                String path = mediasCursor.getString(mediasCursor.getColumnIndexOrThrow(InternalDataBaseSchemas.Medias.COLUMN_PATH));
                String cachedPath = mediasCursor.getString(mediasCursor.getColumnIndexOrThrow(InternalDataBaseSchemas.Medias.COLUMN_CACHED_PATH));
                int type = mediasCursor.getInt(mediasCursor.getColumnIndexOrThrow(InternalDataBaseSchemas.Medias.COLUMN_TYPE));
                int reportId = mediasCursor.getInt(mediasCursor.getColumnIndexOrThrow(InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW));
                if (reportId == id) {
                    medias.add(new Media(type, (cachedPath != null && !cachedPath.isEmpty()) ? cachedPath : path,
                            cachedPath != null && !cachedPath.isEmpty()));
                }
            }

            Media[] mediaArray = new Media[medias.size()];
            for (int i = 0; i < medias.size(); i++) {
                mediaArray[i] = medias.get(i);
            }

            /**/
            report = new Report(id,
                    stateChar,
                    category,
                    title,
                    description,
                    address,
                    dateSent,
                    mediaArray
            );

            reports.add(report);
        }

        whenOperationFinished(reports, Flag.FINISHED_READING_FROM_DB);
    }

    public void loadUserReports() {
        //connection to the server and reading user reports
        String token = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).getString(AppSettingsUtils.USER_TOKEN, "");
        if (token != null && !token.isEmpty())
            new LimitedNetworkOperation(context) {

                @Override
                protected void doInUiThread(int evolutionFlag, Object result) {
                    if (evolutionFlag == EvolutionFlag.ERROR_MESSAGE
                            || evolutionFlag == EvolutionFlag.REQUEST_UNACCEPTED) {
                        whenOperationFinished(null, Flag.ERROR);
                    } else if (evolutionFlag == EvolutionFlag.REQUEST_ACCEPTED) {
                        whenOperationFinished(null, Flag.STARTED_LOADING_FROM_SERVER);
                    }
                    if (evolutionFlag == EvolutionFlag.JSON_IS_READY) {


                        try {

                            /*extracting data from db*/
                            JSONObject serverResponse = new JSONObject(((String) result));
                            JSONArray signalements = serverResponse.getJSONArray("signalements");
                            //
                            ArrayList<Integer> allIds = new ArrayList<>();
                            int id;
                            String category, title, description, address, dateSent, state, reason, video;
                            String[] medias = null;


                            for (int i = 0; i < signalements.length(); i++) {

                                JSONObject current = signalements.getJSONObject(i);

                                /**/
                                id = current.getInt("id");
                                category = current.getString("nom_categorie").trim();
                                title = current.getString("title").trim();
                                description = current.getString("description").trim();
                                address = current.getString("address").trim();
                                dateSent = current.getString("date_depot").trim();
                                state = current.getString("etat").trim();
                                reason = current.getString("motif").trim();
                                video = current.getString("video_url");
                                //
                                String mediaStr = current.getString("img_url").trim();

                                if (mediaStr.contains("|"))
                                    medias = mediaStr.replace('|', '#').split("#");


                                //filling reports database with last content
                                ContentValues row = new ContentValues();
                                row.put(COLUMN_ID, id);
                                row.put(COLUMN_REPORT_CATEGORY, (category.equals("traité-en_attente") ? "valide" : category));
                                row.put(COLUMN_TITLE, title);
                                row.put(COLUMN_DESCRIPTION, description);
                                row.put(COLUMN_ADDRESS, address);
                                row.put(COLUMN_DATE_SENT, dateSent);
                                row.put(COLUMN_STATE, state);
                                row.put(COLUMN_BELONGS_TO_USER, 1);
                                if (reason != null && !reason.equals("null"))
                                    row.put(COLUMN_REASON_OF_REJECTION, reason);

                                writableDB.replace(TABLE_NAME, null, row);
                                allIds.add(id);

                                // inserting images and video
                                if (video != null && video.length() >= 10) {
                                    ContentValues reportVideo = new ContentValues();
                                    reportVideo.put(InternalDataBaseSchemas.Medias.COLUMN_PATH, video);
                                    reportVideo.put(InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE, TABLE_NAME);
                                    reportVideo.put(InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW, id);
                                    reportVideo.put(InternalDataBaseSchemas.Medias.COLUMN_TYPE, Media.guessMediaTypeFromPath(video));
                                    writableDB.insert(InternalDataBaseSchemas.Medias.TABLE_NAME, null, reportVideo);
                                }

                                if (medias != null)
                                    for (int j = 0; j < medias.length; j++) {
                                        ContentValues reportImage = new ContentValues();
                                        int mediaType = Media.guessMediaTypeFromPath(medias[j]);
                                        if (mediaType == Media.UNKNOWN)
                                            continue;

                                        reportImage.put(InternalDataBaseSchemas.Medias.COLUMN_PATH, medias[j]);
                                        reportImage.put(InternalDataBaseSchemas.Medias.COLUMN_TYPE, mediaType);
                                        reportImage.put(InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE, TABLE_NAME);
                                        reportImage.put(InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW, id);
                                        reportImage.put(InternalDataBaseSchemas.Medias.COLUMN_BELONGS_TO_USER, 1);
                                        //
                                        writableDB.insert(InternalDataBaseSchemas.Medias.TABLE_NAME, null, reportImage);

                                    }
                            }
                            //deleting deleted reports (except saved in device)
                            if (allIds.isEmpty()) {
                                writableDB.delete(InternalDataBaseSchemas.Medias.TABLE_NAME,
                                        InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE + " = ?",
                                        new String[]{TABLE_NAME});
                                writableDB.delete(TABLE_NAME, COLUMN_STATE + " != ?",
                                        new String[]{"s"});


                            } else {
                                Cursor cursor = readableDB.query(TABLE_NAME,
                                        new String[]{COLUMN_ID},
                                        null,
                                        null,
                                        null,
                                        null,
                                        null);

                                while (cursor.moveToNext()) {
                                    int rowId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));

                                    if (!allIds.contains(rowId)) {
                                        writableDB.delete(InternalDataBaseSchemas.Medias.TABLE_NAME,
                                                InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW
                                                        + " = ? AND "
                                                        + InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE + " = ? "
                                                , new String[]{"" + rowId, TABLE_NAME});
                                        writableDB.delete(TABLE_NAME, COLUMN_ID + " = ? AND "
                                                        + COLUMN_STATE + " != ?",

                                                new String[]{"" + rowId, "s"});


                                    }
                                }

                            }

                            whenOperationFinished(null, Flag.FINISHED_LOADING_USER_REPORTS_FROM_SERVER);
                        } catch (JSONException e) {

                        }

                    }
                }
            }.downloadText(Requests.GET_USER_REPORTS,
                    new RequestHeader(new RequestHeader.Property(RequestHeader.Field.AUTHORIZATION,
                            "Bearer " + context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE).
                                    getString(AppSettingsUtils.USER_TOKEN, ""))),
                    LimitedNetworkOperation.DataType.TEXT,
                    false);
    }

    public void readUserReportsFromInternalDB() {
        //
        ArrayList<Report> reports = new ArrayList<>();
        //
        int id;
        String category, title, description, address, dateSent, state, reason;
        ArrayList<Media> medias = new ArrayList<>();
        Report report;

        String[] reportsProjection = new String[]{
                COLUMN_ID,
                COLUMN_REPORT_CATEGORY,
                COLUMN_TITLE,
                COLUMN_DESCRIPTION,
                COLUMN_ADDRESS,
                COLUMN_DATE_SENT,
                COLUMN_STATE,
                COLUMN_REASON_OF_REJECTION

        };

        String[] mediasProjection = new String[]{
                InternalDataBaseSchemas.Medias.COLUMN_ID,
                InternalDataBaseSchemas.Medias.COLUMN_TYPE,
                InternalDataBaseSchemas.Medias.COLUMN_PATH,
                InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE,
                InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW,
                InternalDataBaseSchemas.Medias.COLUMN_CACHED_PATH
        };

        Cursor cursor = readableDB.query(
                TABLE_NAME,
                reportsProjection,
                COLUMN_BELONGS_TO_USER + " = ? ",
                new String[]{"1"},
                null,
                null,
                COLUMN_ID + " DESC");


        while (cursor.moveToNext()) {
            /*reading reports from db*/
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REPORT_CATEGORY));
            title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
            dateSent = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_SENT));
            state = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATE));
            reason = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REASON_OF_REJECTION));
            //

            char stateChar;

            switch (state) {
                case "traité":
                    stateChar = Report.STATE_PROCESSED;
                    break;

                case "valide":
                    stateChar = Report.STATE_BEING_PROCESSED;
                    break;

                case "en_attente":
                    stateChar = Report.STATE_SENT;
                    break;

                case "demandé-complement":
                    stateChar = Report.STATE_INCOMPLETE;
                    break;

                case "rejeté":
                    stateChar = Report.STATE_REJECTED;
                    break;

                default:
                    stateChar = state.charAt(0);
                    break;

            }

            /**/
            Cursor mediasCursor = readableDB.query(
                    InternalDataBaseSchemas.Medias.TABLE_NAME,
                    mediasProjection,
                    InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE + " = ? AND "
                            + InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW + " = ? ",
                    new String[]{TABLE_NAME, "" + id},
                    null,
                    null,
                    null
            );

            /*read images after clearing last report images*/
            medias.clear();

            while (mediasCursor.moveToNext()) {
                String path = mediasCursor.getString(mediasCursor.getColumnIndexOrThrow(InternalDataBaseSchemas.Medias.COLUMN_PATH));
                String cachedPath = mediasCursor.getString(mediasCursor.getColumnIndexOrThrow(InternalDataBaseSchemas.Medias.COLUMN_CACHED_PATH));
                int type = mediasCursor.getInt(mediasCursor.getColumnIndexOrThrow(InternalDataBaseSchemas.Medias.COLUMN_TYPE));
                medias.add(new Media(type, (cachedPath != null) ? cachedPath : path,
                        cachedPath != null && !cachedPath.isEmpty()));

            }


            Media[] mediaArray = new Media[medias.size()];
            for (int i = 0; i < medias.size(); i++) {
                mediaArray[i] = medias.get(i);
            }

            /**/
            report = new Report(id,
                    stateChar,
                    category,
                    title,
                    description,
                    address,
                    dateSent,
                    (mediaArray.length > 0 ? mediaArray : null),
                    true,
                    reason

            );

            reports.add(report);
        }

        whenOperationFinished(reports, Flag.FINISHED_READING_FROM_DB);
    }

}