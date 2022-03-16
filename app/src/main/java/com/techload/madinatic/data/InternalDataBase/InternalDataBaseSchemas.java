package com.techload.madinatic.data.InternalDataBase;

import android.provider.BaseColumns;

public class InternalDataBaseSchemas {

    private InternalDataBaseSchemas() {
    }

    //
    private interface SharedColumns {
        String COLUMN_ID = "_id";
        String COLUMN_TITLE = "title";
        String COLUMN_DESCRIPTION = "description";
    }


    //
    public interface Posts extends SharedColumns {
        String TABLE_NAME = "last_posts";
        String COLUMN_POST_OWNER_NAME = "post_owner_name";
        String COLUMN_POST_OWNER_PROFILE_PICTURE_PATH = "post_owner_logo_path";
        String COLUMN_POST_OWNER_PROFILE_PICTURE_CACHED_PATH="cached_path";
        String COLUMN_DATE_POSTED = "date_posted";
    }


    public interface Reports extends SharedColumns {
        String TABLE_NAME = "reports";
        String COLUMN_STATE = "state";
        String COLUMN_ADDRESS = "address";
        String COLUMN_DATE_SENT = "date_sent";
        String COLUMN_REPORT_CATEGORY = "category";
        String COLUMN_REASON_OF_REJECTION = "why_rejected";
        String COLUMN_BELONGS_TO_USER = "belongs_to_user";
    }

    public interface Medias {
        String TABLE_NAME = "medias";
        String COLUMN_ID = "_id";
        String COLUMN_PATH = "path";
        String COLUMN_CACHED_PATH="cached_path";
        String COLUMN_TYPE = "type";
        String COLUMN_RELATED_TO_WHICH_TABLE = "related_to";
        String COLUMN_RELATED_TO_WHICH_ROW = "row_id";
        String COLUMN_BELONGS_TO_USER = "belongs_to_user";
    }


}//