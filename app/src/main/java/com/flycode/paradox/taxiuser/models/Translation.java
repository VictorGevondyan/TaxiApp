package com.flycode.paradox.taxiuser.models;

/**
 * Created by anhaytananun on 28.03.16.
 */
public class Translation {
    private String id;
    private String locality;
    private String key;
    private String value;

    public Translation(String id, String locality, String key, String value) {
        this.id = id;
        this.locality = locality;
        this.key = key;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getLocality() {
        return locality;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
