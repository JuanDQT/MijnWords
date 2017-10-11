package com.juandqt.mijnwords.models;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by juandaniel on 19/9/17.
 */

public class PalabraSearch extends RealmObject {

    private String name;
    private String languageCode;
    private Date date;

    public PalabraSearch() {
        this.date = new Date();
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PalabraSearch{" +
                ", name='" + name + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", date=" + date +
                '}';
    }
}
