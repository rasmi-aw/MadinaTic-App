package com.techload.madinatic.lists_items;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.techload.madinatic.activities.MainActivity;
import com.techload.madinatic.utils.AppSettingsUtils;

public class Report {

    //
    public static final char STATE_SAVED = 's';
    public static final char STATE_SENT = 'e';
    public static final char STATE_BEING_PROCESSED = 'b';
    public static final char STATE_PROCESSED = 'p';
    public static final char STATE_REJECTED = 'r';
    public static final char STATE_INCOMPLETE = 'u';
    //
    public static final String CATEGORY_WATER_PROBLEM = "fuite d'eau";
    public static final String CATEGORY_LIGHTING_PROBLEM = "problem d'éclairage";
    public static final String CATEGORY_ROAD_PROBLEM = "problem de route";
    public static final String CATEGORY_OTHERS= "autre";

    //
    public long id;
    public char state;
    public String title, category, description, address, dateSent;
    public Media[] media;
    public boolean belongsToUser = false;
    public String reasonWhyRejected = null;

    //
    public Report(long id, char state, String category, String title, String description,
                  String address, String dateSent,
                  Media[] media) {
        //
        this.id = id;
        this.state = state;
        this.category = category;
        this.title = title;
        this.description = description;
        this.address = address;
        this.dateSent = AppSettingsUtils.formatDate(MainActivity.appLanguage, dateSent);
        this.media = media;
    }

    public Report(long id, char state, String category, String title, String description,
                  String address, String dateSent,
                  Media[] media, boolean belongsToUser, String reasonWhyRejected) {

        this(id, state, category, title, description, address, dateSent, media);
        this.belongsToUser = belongsToUser;
        this.reasonWhyRejected = reasonWhyRejected;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (((Report) obj).id == id);
    }

    /*used to search*/
    public boolean similarTo(String s) {
        String id = this.id + "";
        return title.trim().toLowerCase().contains(s.trim().toLowerCase())
                || s.trim().toLowerCase().contains(title.trim().toLowerCase())
                //date sent
                || dateSent.trim().toLowerCase().contains(s.trim().toLowerCase())
                || s.trim().toLowerCase().contains(dateSent.trim().toLowerCase())
                //id
                || id.trim().toLowerCase().contains(s.trim().toLowerCase())
                || s.trim().toLowerCase().contains(id.trim().toLowerCase())
                ;
    }

    @NonNull
    @Override
    public String toString() {
        return "id: " + id +
                "\nstate: " + state +
                "\ncategory: " + category +
                "\ntitle: " + title +
                "\ndescription: " + description +
                "\naddress: " + address +
                "\ndate_sent: " + dateSent +
                "\nmedia: " + media.toString() +
                "\nbelongs_to_user: " + belongsToUser +
                "\nreason_why_rejected: " + reasonWhyRejected;
    }

}