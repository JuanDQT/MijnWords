package com.juandqt.mijnwords;

import android.animation.ValueAnimator;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.juandqt.mijnwords.tools.CustomMigration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by juandaniel on 7/8/17.
 */

public class Common extends Application {

    public static Context context;
    private static final String FILE_ACCESS = "access.json";
    private static final String FILE_LANGUAGES = "languages";
    public static HashMap<String, Integer> allLanguages;

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = getApplicationContext();
        allLanguages = instanceMapLanguages();
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().
                name("palabras").
                schemaVersion(3).
                migration(new CustomMigration()).
                build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    // Almacenamos las referencias de code idiomas en un map para ser accedido mas facilmente
    private HashMap<String, Integer> instanceMapLanguages() {
        HashMap<String, Integer> map = new HashMap<>();

        // Carga desde el JSON
        // TODO: Comrpobar todo en JSON!
        try {
            for (Map.Entry<String, Object> i : toMap(new JSONObject(openJSON(FILE_LANGUAGES))).entrySet()) {
                map.put(i.getKey(), getResources().getIdentifier(i.getValue().toString().replace("R.drawable.", ""), "drawable", getPackageName()));
                Log.e("PIP", i.getValue().toString());
            }

        } catch (Exception e) {
            ;
        }
        return map;
    }

    public String getHostURL() {

        String jsonUrlFile = openJSON(FILE_ACCESS);
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

            InputStream is = this.context.getAssets().open(jsonFile);

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
    public static String getExampleLanguage() {
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

    public static String getBaseLanguage() {
        String ln = "";
        SharedPreferences sharedPreferences = context.getSharedPreferences("SP", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("BL")) {
            ln = sharedPreferences.getString("BL", "ES");
        } else {
            ln = "ES";
        }
        return ln;
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}