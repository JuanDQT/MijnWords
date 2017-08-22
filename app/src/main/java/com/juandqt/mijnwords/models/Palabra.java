package com.juandqt.mijnwords.models;

/**
 * Created by juandaniel on 7/8/17.
 */

public class Palabra {

    private Ejemplo ejemplo;

    private ModoVerbo modoIndicativo;
    private ModoVerbo modoSubjuntivo;
    private ModoVerbo modoCondicional;
    private ModoVerbo modoImperativo;

    public Palabra() {
        modoIndicativo = new ModoVerbo();
        modoSubjuntivo = new ModoVerbo();
        modoCondicional = new ModoVerbo();
        modoImperativo = new ModoVerbo();
    }



    public ModoVerbo getModoIndicativo() {
        return modoIndicativo;
    }

    public ModoVerbo getModoSubjuntivo() {
        return modoSubjuntivo;
    }

    public ModoVerbo getModoCondicional() {
        return modoCondicional;
    }

    public ModoVerbo getModoImperativo() {
        return modoImperativo;
    }

    public void setModoIndicativo(ModoVerbo modoIndicativo) {
        this.modoIndicativo = modoIndicativo;
    }

    public void setModoSubjuntivo(ModoVerbo modoSubjuntivo) {
        this.modoSubjuntivo = modoSubjuntivo;
    }

    public void setModoCondicional(ModoVerbo modoCondicional) {
        this.modoCondicional = modoCondicional;
    }

    public void setModoImperativo(ModoVerbo modoImperativo) {
        this.modoImperativo = modoImperativo;
    }

    public void setEjemplo(Ejemplo ejemplo) {
        this.ejemplo = ejemplo;
    }

    public Ejemplo getEjemplo() {
        return ejemplo;
    }

}


