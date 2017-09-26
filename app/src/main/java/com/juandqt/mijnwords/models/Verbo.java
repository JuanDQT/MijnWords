package com.juandqt.mijnwords.models;

import java.util.ArrayList;

/**
 * Created by juandaniel on 22/9/17.
 */

public class Verbo {

    private String tiempo;// Presnete, Futuro
    private ArrayList<String> verbs;

    public Verbo() {
        this.verbs = new ArrayList<>();
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public void addVerb(String verb) {
        this.verbs.add(verb);
    }

    public String getTiempo() {
        return this.tiempo;
    }

    public ArrayList<String> getVerbs() {
        return verbs;
    }

    @Override
    public String toString() {
        return "Verbo{" +
                "tiempo='" + tiempo + '\'' +
                ", verbs=" + verbs +
                '}';
    }
}
