package com.juandqt.mijnwords.models;

import java.util.ArrayList;

/**
 * Created by juandaniel on 17/8/17.
 */

public class ModoVerbo {

    private String tiempo;
    private ArrayList<String> presente;
    private ArrayList<String> preteritoImperfecto;
    private ArrayList<String> preteritoIndefinido;
    private ArrayList<String> futuro;
    private ArrayList<String> condicional;
    private ArrayList<String> afirmativo;
    private ArrayList<String> negativo;
    private ArrayList<ArrayList<String>> allTimes;

    public ArrayList<ArrayList<String>> getAllTimes() {
        return allTimes;
    }

    public ModoVerbo() {
        presente = new ArrayList<>();
        preteritoImperfecto = new ArrayList<>();
        preteritoIndefinido = new ArrayList<>();
        condicional = new ArrayList<>();
        afirmativo = new ArrayList<>();
        negativo = new ArrayList<>();
        futuro = new ArrayList<>();
        allTimes = new ArrayList<>();
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public ArrayList<String> getPresente() {
        return presente;
    }

    public void setPresente(ArrayList<String> presente) {
        this.presente = presente;
        allTimes.add(presente);
    }

    public ArrayList<String> getPreteritoImperfecto() {
        return preteritoImperfecto;
    }

    public void setPreteritoImperfecto(ArrayList<String> preteritoImperfecto) {
        this.preteritoImperfecto = preteritoImperfecto;
        allTimes.add(preteritoImperfecto);
    }

    public ArrayList<String> getPreteritoIndefinido() {
        return preteritoIndefinido;
    }

    public void setPreteritoIndefinido(ArrayList<String> preteritoIndefinido) {
        this.preteritoIndefinido = preteritoIndefinido;
        allTimes.add(preteritoIndefinido);
    }

    public ArrayList<String> getCondicional() {
        return condicional;
    }

    public void setCondicional(ArrayList<String> condicional) {
        this.condicional = condicional;
        allTimes.add(condicional);
    }

    public ArrayList<String> getAfirmativo() {
        return afirmativo;
    }

    public void setAfirmativo(ArrayList<String> afirmativo) {
        this.afirmativo = afirmativo;
        allTimes.add(afirmativo);
    }

    public ArrayList<String> getNegativo() {
        return negativo;
    }

    public void setNegativo(ArrayList<String> negativo) {
        this.negativo = negativo;
        allTimes.add(negativo);
    }

    public ArrayList<String> getFuturo() {
        return futuro;
    }

    public void setFuturo(ArrayList<String> futuro) {
        this.futuro = futuro;
        allTimes.add(futuro);

    }


}