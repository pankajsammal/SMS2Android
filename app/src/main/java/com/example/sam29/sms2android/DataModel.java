package com.example.sam29.sms2android;

/**
 * Created by sam29 on 08-Feb-17.
 */

public class DataModel {

    String name;
    String type;
    String version_number;
    String feature;

    public DataModel(String name, String type, String version_number, String feature) {
        this.name = name;
        this.type = type;
        this.version_number = version_number;
        this.feature = feature;

    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getVersion_number() {
        return version_number;
    }

    public String getFeature() {
        return feature;
    }
}
