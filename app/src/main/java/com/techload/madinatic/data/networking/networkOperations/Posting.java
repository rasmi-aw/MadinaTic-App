package com.techload.madinatic.data.networking.networkOperations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.techload.madinatic.data.InternalDataBase.InternalDataBaseSchemas;
import com.techload.madinatic.data.networking.Requests;
import com.techload.madinatic.lists_items.Media;
import com.techload.madinatic.lists_items.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Posting implements InternalDataBaseSchemas.Posts {
    private SQLiteDatabase readableDB, writableDB;
    private Context context;


    public Posting(Context context, SQLiteDatabase readableDB, SQLiteDatabase writableDB) {
        this.context = context;
        this.readableDB = readableDB;
        this.writableDB = writableDB;

    }



    public interface Flag {
        int ERROR = -1;
        int STARTED_LOADING_FROM_SERVER = 0;
        int FINISHED_LOADING_FROM_SERVER = 1;
        int FINISHED_READING_FROM_DB = 2;
    }


    public void whenOperationFinished(Object result, int flag) {
    }


    /* read all reports*/
    public void loadPosts() {

        //connection to the server and reading all users reports
        new LimitedNetworkOperation(context) {

            @Override
            protected void doInUiThread(int evolutionFlag, Object result) {
                if (evolutionFlag == EvolutionFlag.REQUEST_ACCEPTED) {
                    whenOperationFinished(null, Flag.STARTED_LOADING_FROM_SERVER);
                } else if (evolutionFlag == EvolutionFlag.JSON_IS_READY) {

                    try {

                        /*extracting data from db*/
                        JSONObject serverResponse = new JSONObject(((String) result));
                        JSONArray news = serverResponse.getJSONArray("articles");
                        //
                        int id;
                        String title, description, dateSent, postOwnerName, postOwnerPicture;
                        String[] medias;


                        for (int i = 0; i < news.length(); i++) {

                            JSONObject current = news.getJSONObject(i);

                            /**/
                            id = current.getInt("id");
                            title = current.getString("title").trim();
                            description = current.getString("description").trim();
                            dateSent = current.getString("date_depot").trim();
                            postOwnerName = current.getString("nom_service").trim();
                            postOwnerPicture = current.getString("url_photo_service").trim();

                            //
                            String mediaStr = current.getString("img_url").trim();
                            if (mediaStr.contains("images"))
                                medias = mediaStr.replace('|', '#').split("#");
                            else medias = null;


                            //filling reports database with last content version
                            ContentValues row = new ContentValues();
                            row.put(COLUMN_ID, id);
                            row.put(COLUMN_POST_OWNER_NAME, postOwnerName);
                            row.put(COLUMN_TITLE, title);
                            row.put(COLUMN_DESCRIPTION, description);
                            row.put(COLUMN_DATE_POSTED, dateSent);
                            row.put(COLUMN_POST_OWNER_PROFILE_PICTURE_PATH, postOwnerPicture);

                            //profile picture if changed or not
                            Cursor cursor = readableDB.query(TABLE_NAME,
                                    new String[]{COLUMN_POST_OWNER_PROFILE_PICTURE_PATH},
                                    COLUMN_ID + " = ? ",
                                    new String[]{id + ""},
                                    null,
                                    null,
                                    null);

                            if (cursor.getCount() != 1) writableDB.insert(TABLE_NAME, null, row);
                            else {
                                cursor.moveToFirst();
                                String oldProfilePic = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_OWNER_PROFILE_PICTURE_PATH));

                                if (!oldProfilePic.equals(postOwnerPicture)) {
                                    /*post owner changed its profile picture*/
                                    row.put(COLUMN_POST_OWNER_PROFILE_PICTURE_CACHED_PATH, (String) null);
                                    writableDB.replace(TABLE_NAME, null, row);
                                }
                            }


                            /*putting images into a fifo the download them after loading all reports*/
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
                                    //
                                    writableDB.insert(InternalDataBaseSchemas.Medias.TABLE_NAME, null, reportImage);
                                }

                        }
                        whenOperationFinished(null, Flag.FINISHED_LOADING_FROM_SERVER);

                    } catch (JSONException e) {

                    }
                }
            }
        }.downloadText(Requests.NEWS,
                null,
                LimitedNetworkOperation.DataType.TEXT,
                false);
    }


    public void readNewsFromInternalDB() {
        //
        ArrayList<Post> posts = new ArrayList<>();
        //
        int id;
        String title, description, datePosted, postOwnerName, postProfilePicture, postCachedProfilePicture;
        ArrayList<Media> medias = new ArrayList<>();
        Post post;

        String[] reportsProjection = new String[]{
                COLUMN_ID,
                COLUMN_TITLE,
                COLUMN_DESCRIPTION,
                COLUMN_POST_OWNER_NAME,
                COLUMN_DATE_POSTED,
                COLUMN_POST_OWNER_PROFILE_PICTURE_PATH,
                COLUMN_POST_OWNER_PROFILE_PICTURE_CACHED_PATH

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
                null,
                null,
                null,
                null,
                COLUMN_ID + " DESC");


        while (cursor.moveToNext()) {
            /*reading reports from db*/
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            postOwnerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_OWNER_NAME));
            postProfilePicture = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_OWNER_PROFILE_PICTURE_PATH));
            postCachedProfilePicture = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_POST_OWNER_PROFILE_PICTURE_CACHED_PATH));
            datePosted = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_POSTED));


            /*read images after clearing last post images*/
            medias.clear();

            Cursor mediasCursor = readableDB.query(
                    InternalDataBaseSchemas.Medias.TABLE_NAME,
                    mediasProjection,
                    InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE + " = ? AND "
                            + InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW + " = ? ",
                    new String[]{TABLE_NAME, "" + id},
                    null,
                    null, null
            );

            while (mediasCursor.moveToNext()) {

                String path = mediasCursor.getString(mediasCursor.getColumnIndexOrThrow(InternalDataBaseSchemas.Medias.COLUMN_PATH));
                String cachedPath = mediasCursor.getString(mediasCursor.getColumnIndexOrThrow(InternalDataBaseSchemas.Medias.COLUMN_CACHED_PATH));
                int type = mediasCursor.getInt(mediasCursor.getColumnIndexOrThrow(InternalDataBaseSchemas.Medias.COLUMN_TYPE));
                medias.add(new Media(type, (cachedPath != null && !cachedPath.isEmpty()) ? cachedPath : path,
                        (cachedPath != null && !cachedPath.isEmpty())));
            }

            Media[] mediaArray = new Media[medias.size()];
            for (int i = 0; i < medias.size(); i++) {
                mediaArray[i] = medias.get(i);
            }

            /**/
            post = new Post(id,
                    title,
                    description,
                    postOwnerName,
                    datePosted,
                    mediaArray,
                    (postCachedProfilePicture != null ? postCachedProfilePicture : postProfilePicture),
                    Post.TYPE_POST
            );

            posts.add(post);
        }

        //
        cursor.close();
        cursor = null;
        //


        whenOperationFinished(posts, Flag.FINISHED_READING_FROM_DB);
    }

}