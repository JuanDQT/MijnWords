package com.juandqt.mijnwords.models;

import java.util.ArrayList;

/**
 * Created by juandaniel on 23/9/17.
 */

public class Word {

    private Ejemplo ejemplo;
    private ArrayList<Modo> modos;

    public Word() {
        this.modos = new ArrayList<>();
    }

    public void addModo(Modo modo) {
        this.modos.add(modo);
    }

    public ArrayList<Modo> getModos() {
        return this.modos;
    }

    public void setEjemplo(Ejemplo ejemplo) {
        this.ejemplo = ejemplo;
    }

    public Ejemplo getEjemplo() {
        return ejemplo;
    }

}
