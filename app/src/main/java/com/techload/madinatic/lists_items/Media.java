package com.techload.madinatic.lists_items;

import androidx.annotation.NonNull;

public class Media {
    //
    public static final int UNKNOWN = -1;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;
    //
    public int type;
    public String path;
    public boolean cached;
    public int mediaResource;

    public Media(int type, String path, boolean cached) {
        this.type = type;
        this.path = path;
        this.cached = cached;
    }

    public Media(int mediaResource, int type) {
        this(type, null, false);
        this.mediaResource = mediaResource;
        this.type = type;

    }

    /**
     * guessing the file type from his name
     */

    public static int guessMediaTypeFromPath(String path) {

        if (path == null || path.isEmpty()) return UNKNOWN;

        String lCPath = path.toLowerCase();

        if (lCPath.contains(".mp4") || lCPath.contains(".mpg")
                || lCPath.contains(".mpeg") || lCPath.contains(".3gp")
                || lCPath.contains(".mkv") || lCPath.contains("webmp")) return TYPE_VIDEO;

        else if (lCPath.contains(".webp") || lCPath.contains(".png")
                || lCPath.contains(".jpg") || lCPath.contains(".jpeg")
                || lCPath.contains(".gif") || lCPath.contains(".bmp")) return TYPE_IMAGE;

        else return UNKNOWN;
    }

    @NonNull
    @Override
    public String toString() {
        return "Type: " + type +
                "\npath: " + path;
    }
}
