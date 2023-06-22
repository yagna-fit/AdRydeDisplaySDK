package com.adryde.driver;

import android.content.Context;

public class MediaModel {

    public enum MediaType {

        IMAGE,

        VIDEO,

        JSON,

        UNKNOWN
    }

    public String id;

    public String getLocalPath(Context context) {
        return localPath = context.getCacheDir().toString() +"/"+ id;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String localPath;
    public String size;
    public MediaType fileType;
    public String mimeType;


}
