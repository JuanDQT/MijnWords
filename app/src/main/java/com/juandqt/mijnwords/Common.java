package com.juandqt.mijnwords;

import android.animation.ValueAnimator;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.juandqt.mijnwords.tools.CustomMigration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by juandaniel on 7/8/17.
 */

public class Common extends Application {

    public static Context context;
    private static final String FILE = "access.json";
    public static HashMap<String, Integer> allLanguages;

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = getApplicationContext();
        allLanguages = instanceMapLanguages();
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().
                                                    name("palabras").
                                                    schemaVersion(2).
                                                    migration(new CustomMigration()).
                                                    build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    // Almacenamos las referencias de code idiomas en un map para ser accedido mas facilmente
    private HashMap<String, Integer> instanceMapLanguages() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("ES", R.drawable.es_lang);
        map.put("NL", R.drawable.nl_lang);
        map.put("EN", R.drawable.en_lang);
        return map;
    }

    // Contexto global de la aplicacion
    public static Context getContext() {
        return context;
    }

    public String getHostURL() {

        String jsonUrlFile = openJSON(FILE);
        String url = "";
        try {
            JSONObject jsonUrl = new JSONObject(jsonUrlFile);
            url = jsonUrl.getString("host");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return url.replace("\n", "").replace("\r", "");
    }

    public String openJSON(String jsonFile) {
        String json = null;
        try {

            InputStream is = getContext().getAssets().open(jsonFile);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public static void expand(final View v, int duration, int targetHeight) {

        int prevHeight = v.getHeight();

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });

        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public static void collapse(final View v, int duration, int targetHeight) {
        int prevHeight = v.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    // Lenguaje del cual el usuario guardó como configuracion para ver los ejemplos en X lenguaje
    public static String getSystemLanguage() {
        String ln = "";
        SharedPreferences sharedPreferences = context.getSharedPreferences("SP", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("LN")) {
            // cargamos del sh
            ln = sharedPreferences.getString("LN", "EN");
        } else {

            ln = "EN";
        }

        return ln;
    }

    public static String getBaseVerblanguage() {
        return "ES";
    }
}