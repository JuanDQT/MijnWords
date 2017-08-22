package com.juandqt.mijnwords.models;

import java.util.ArrayList;

/**
 * Created by juandaniel on 17/8/17.
 */

public class Ejemplo {

    private ArrayList<String> ejemplosEs;
    private ArrayList<String> ejemplosNl;

    public Ejemplo() {
        ejemplosEs = new ArrayList<>();
        ejemplosNl = new ArrayList<>();
    }

    public ArrayList<String> getEjemplosEs() {
        return ejemplosEs;
    }

    public void setEjemplosEs(ArrayList<String> ejemplosEs) {
        this.ejemplosEs = ejemplosEs;
    }

    public ArrayList<String> getEjemplosNl() {
        return ejemplosNl;
    }

    public void setEjemplosNl(ArrayList<String> ejemplosNl) {
        this.ejemplosNl = ejemplosNl;
    }


}