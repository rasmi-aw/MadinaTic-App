package com.techload.madinatic.lists_items;

import androidx.annotation.Nullable;

public class Post {
    //
    public static final int TYPE_POST = 0;
    public static final int TYPE_HINT = 15;
    //
    public long id;
    public String title, description, postOwnerName, datePosted, postOwnerProfilePicture;
    public Media[] medias;
    public int type;

    //
    public Post(long id, String title, String description, String postOwnerName, String datePosted, Media[] medias,
                String postOwnerProfilePicture, int type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.postOwnerName = postOwnerName;
        this.datePosted = datePosted;
        this.medias = medias;
        this.postOwnerProfilePicture = postOwnerProfilePicture;
        this.type = type;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        return id == ((Post) obj).id;
    }
}
