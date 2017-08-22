package com.juandqt.mijnwords;

import android.animation.ValueAnimator;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by juandaniel on 7/8/17.
 */

public class Common extends Application {

    public static Context context;
    private static final String FILE = "access.json";

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = getApplicationContext();
    }

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

    public String getUrlURL(String id) {

        String jsonUrlFile = openJSON(FILE);
        String url = "";
        try {
            JSONObject jsonUrl = new JSONObject(jsonUrlFile);
            url = jsonUrl.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        url = url.replace("XXX", id);

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

    // Test
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


    public String getUrlSuggestion() {

        String jsonUrlFile = openJSON(FILE);
        String url = "";
        try {
            JSONObject jsonUrl = new JSONObject(jsonUrlFile);
            url = jsonUrl.getString("host_suggest");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return url.replace("\n", "").replace("\r", "");
    }

    public String getUrlHostReport() {

        String jsonUrlFile = openJSON(FILE);
        String url = "";
        try {
            JSONObject jsonUrl = new JSONObject(jsonUrlFile);
            url = jsonUrl.getString("host_report");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return url.replace("\n", "").replace("\r", "");
    }
}