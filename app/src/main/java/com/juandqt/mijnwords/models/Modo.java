package com.juandqt.mijnwords.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juandaniel on 17/8/17.
 */

public class Modo {

    private String title;// Modo indicartivo
    private List<String> persons;// Yo, Tu, El...
    private ArrayList<Verbo> allVerbs;

    public Modo() {
        this.allVerbs = new ArrayList<>();
        this.persons = new ArrayList<>();
    }

    public ArrayList<Verbo> getAllVerbs() {
        return this.allVerbs;
    }

    public void addVerbo(Verbo verbo) {
        this.allVerbs.add(verbo);
    }

    public String getTitle() {
        return title;
    }

    public List<String> getPersons() {
        return persons;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPersons(List<String> persons) {
        this.persons = persons;
    }
}