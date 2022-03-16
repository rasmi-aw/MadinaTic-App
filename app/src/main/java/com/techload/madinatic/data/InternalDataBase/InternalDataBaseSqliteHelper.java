package com.techload.madinatic.data.InternalDataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InternalDataBaseSqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app_data.db";
    private static final int DATABASE_VERSION = 1;

    public InternalDataBaseSqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*saved posts in news feed*/
        db.execSQL("CREATE TABLE IF NOT EXISTS " + InternalDataBaseSchemas.Posts.TABLE_NAME
                + "(" + InternalDataBaseSchemas.Posts.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + InternalDataBaseSchemas.Posts.COLUMN_TITLE + "  TEXT,"
                + InternalDataBaseSchemas.Posts.COLUMN_DESCRIPTION + "  TEXT,"
                + InternalDataBaseSchemas.Posts.COLUMN_POST_OWNER_NAME + "  TEXT,"
                + InternalDataBaseSchemas.Posts.COLUMN_POST_OWNER_PROFILE_PICTURE_PATH + "  TEXT DEFAULT NULL,"
                + InternalDataBaseSchemas.Posts.COLUMN_POST_OWNER_PROFILE_PICTURE_CACHED_PATH + "  TEXT DEFAULT NULL,"
                + InternalDataBaseSchemas.Posts.COLUMN_DATE_POSTED + "  TEXT"
                + ");");


        /*all reports*/

        /*my reports table*/
        db.execSQL("CREATE TABLE IF NOT EXISTS " + InternalDataBaseSchemas.Reports.TABLE_NAME
                + "(" + InternalDataBaseSchemas.Reports.COLUMN_ID + " INTEGER PRIMARY KEY, "
                + InternalDataBaseSchemas.Reports.COLUMN_STATE + "  TEXT, "
                + InternalDataBaseSchemas.Reports.COLUMN_TITLE + "  TEXT, "
                + InternalDataBaseSchemas.Reports.COLUMN_DESCRIPTION + "  TEXT, "
                + InternalDataBaseSchemas.Reports.COLUMN_ADDRESS + "  TEXT, "
                + InternalDataBaseSchemas.Reports.COLUMN_DATE_SENT + "  TEXT DEFAULT NULL, "
                + InternalDataBaseSchemas.Reports.COLUMN_REPORT_CATEGORY + "  TEXT DEFAULT NULL, "
                + InternalDataBaseSchemas.Reports.COLUMN_BELONGS_TO_USER + "  INTEGER DEFAULT 0, "
                + InternalDataBaseSchemas.Reports.COLUMN_REASON_OF_REJECTION + "  TEXT DEFAULT NULL "
                + ");");

        /*medias*/

        db.execSQL("CREATE TABLE IF NOT EXISTS " + InternalDataBaseSchemas.Medias.TABLE_NAME
                + "(" + InternalDataBaseSchemas.Medias.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + InternalDataBaseSchemas.Medias.COLUMN_PATH + "  TEXT UNIQUE,"
                + InternalDataBaseSchemas.Medias.COLUMN_CACHED_PATH + "  TEXT DEFAULT NULL, "
                + InternalDataBaseSchemas.Medias.COLUMN_TYPE + "  INTEGER, "
                + InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_TABLE + " TEXT, "
                + InternalDataBaseSchemas.Medias.COLUMN_RELATED_TO_WHICH_ROW + " INTEGER, "
                + InternalDataBaseSchemas.Medias.COLUMN_BELONGS_TO_USER + "  INTEGER DEFAULT 0 "
                + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + InternalDataBaseSchemas.Reports.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + InternalDataBaseSchemas.Posts.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + InternalDataBaseSchemas.Medias.TABLE_NAME + ";");
        onCreate(db);
    }


}
